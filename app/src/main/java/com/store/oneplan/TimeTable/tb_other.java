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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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

public class tb_other extends AppCompatActivity {

    Button btnBack, btnDelete;
    TextView btnNewReminder;
    RecyclerView rView;
    SwitchCompat switchOther;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirestoreRecyclerAdapter<TimeTableModal, OtherViewHolder> fAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tb_other);

        btnBack = findViewById(R.id.o_btnBackTo_tb);
        rView = findViewById(R.id.other_recycler);
        btnNewReminder = findViewById(R.id.other_newReminder);
        switchOther = findViewById(R.id.edit_other_switch);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        btnNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(tb_other.this, timetable_bottomSheet.class));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(tb_other.this, time_table.class));
            }
        });

        //Read from Firebase
        Query query = fStore.collection("Timetable").document(fUser.getUid()).collection("other");

        FirestoreRecyclerOptions<TimeTableModal> others = new FirestoreRecyclerOptions.Builder<TimeTableModal>()
                .setQuery(query, TimeTableModal.class).build();

        fAdapter = new FirestoreRecyclerAdapter<TimeTableModal, OtherViewHolder>(others) {
            @Override
            protected void onBindViewHolder(@NonNull OtherViewHolder otherViewHolder, int i, @NonNull TimeTableModal timeTableModal) {

                btnDelete = otherViewHolder.itemView.findViewById(R.id.delete_o_timetable);
                String docId = others.getSnapshots().getSnapshot(i).getId();

                otherViewHolder.title.setText(timeTableModal.getTitle());
                otherViewHolder.time.setText(timeTableModal.getTime());
                otherViewHolder.date.setText(timeTableModal.getDate());

                otherViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

                btnDelete.setOnClickListener(new View.OnClickListener() {
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
                                intent.putExtra("Description", timeTableModal.getTime());
                                intent.putExtra("Time", timeTableModal.getTime());
                                intent.putExtra("Date", timeTableModal.getDate());
                                intent.putExtra("ttId", docId);

                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                AlertDialog alertDialog = new AlertDialog.Builder(tb_other.this)
                                        .setIcon(R.drawable.ic_aboutus)
                                        .setTitle("Are You Sure You Want To Delete an 'Other' Reminder?")
                                        .setMessage("Deleted Reminders Can't be undone!")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //yes
                                                DocumentReference df = fStore
                                                        .collection("Timetable")
                                                        .document(fUser.getUid())
                                                        .collection("other")
                                                        .document(docId);

                                                df.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(getApplicationContext(), "Other Reminder Deleted", Toast.LENGTH_SHORT).show();

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
            public OtherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_card, parent, false);
                return new OtherViewHolder(view);
            }
        };

        rView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rView.setLayoutManager(staggeredGridLayoutManager);
        rView.setAdapter(fAdapter);

        //--------------------------------------------------------------

    }

    public class OtherViewHolder extends RecyclerView.ViewHolder
    {
        TextView title, date, time;

        public OtherViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tb_title);
            date = itemView.findViewById(R.id.tb_date);
            time = itemView.findViewById(R.id.tb_time);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        fAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (fAdapter != null)
        {
            fAdapter.startListening();
        }
    }
}
