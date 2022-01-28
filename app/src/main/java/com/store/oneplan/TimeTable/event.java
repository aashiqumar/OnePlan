package com.store.oneplan.TimeTable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.store.oneplan.R;

public class event extends AppCompatActivity {

    Button btnBack, btnView, btnDelete;
    TextView btnNewReminder;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirestoreRecyclerAdapter <TimeTableModal, EventViewHolder> firestoreRecyclerAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        recyclerView = findViewById(R.id.event_recycler);
        btnBack = findViewById(R.id.e_btnBackTo_tb);
        btnNewReminder = findViewById(R.id.event_newReminder);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(event.this, timetable_bottomSheet.class));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(event.this, time_table.class));

            }
        });

        Query query = firebaseFirestore.collection("Timetable").document(firebaseUser.getUid()).collection("events").orderBy("Title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TimeTableModal> events = new FirestoreRecyclerOptions.Builder<TimeTableModal>().setQuery(query, TimeTableModal.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<TimeTableModal, EventViewHolder>(events) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i, @NonNull TimeTableModal timeTableModal) {

                btnView = eventViewHolder.itemView.findViewById(R.id.delete_e_timetable);


                eventViewHolder.title.setText(timeTableModal.getTitle());
                eventViewHolder.time.setText(timeTableModal.getTime());
                eventViewHolder.date.setText(timeTableModal.getDate());

                String docId = events.getSnapshots().getSnapshot(i).getId();

                eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), EditTimetable.class);
                        intent.putExtra("Title", timeTableModal.getTitle());
                        intent.putExtra("Description", timeTableModal.getDescription());
                        intent.putExtra("Time", timeTableModal.getTime());
                        intent.putExtra("Date", timeTableModal.getDate());
                        intent.putExtra("ttId", docId);
                        v.getContext().startActivity(intent);

                    }
                });

                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupMenu.setGravity(Gravity.END);
                        }
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                Intent intent = new Intent(v.getContext(), EditTimetable.class);
                                intent.putExtra("Title", timeTableModal.getTitle());
                                intent.putExtra("Description", timeTableModal.getDescription());
                                intent.putExtra("Time", timeTableModal.getTime());
                                intent.putExtra("Date", timeTableModal.getDate());
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog alertDialog = new AlertDialog.Builder(event.this)
                                        .setIcon(R.drawable.ic_aboutus)
                                        .setTitle("Are You Sure You Want To Delete an 'Event' Reminder?")
                                        .setMessage("Deleted Reminders Can't be undone!")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //yes
                                                DocumentReference df = firebaseFirestore
                                                        .collection("Timetable")
                                                        .document(firebaseUser.getUid())
                                                        .collection("events")
                                                        .document(docId);

                                                df.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(getApplicationContext(), "Event Reminder Deleted", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(getApplicationContext(), "Rminder Failed to Delete", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Toast.makeText(getApplicationContext(), "Reminder Not Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).show();
                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });

            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card, parent, false);
                return new EventViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(firestoreRecyclerAdapter);


    }

    public class EventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title, date,time;
        LinearLayout eventLayout;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tb_title);
            date = itemView.findViewById(R.id.tb_date);
            time = itemView.findViewById(R.id.tb_time);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firestoreRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firestoreRecyclerAdapter != null)
        {
            firestoreRecyclerAdapter.startListening();
        }

    }
}
