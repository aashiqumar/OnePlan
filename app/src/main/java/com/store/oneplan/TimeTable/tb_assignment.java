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
import androidx.cardview.widget.CardView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class tb_assignment extends AppCompatActivity {

    private Intent data;
    private Button btnBack, btnView;
    private TextView btnNewReminder, a_title;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    FirestoreRecyclerAdapter <TimeTableModal, AssignmentViewHolder> assignmentAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tb_assignment);

        btnBack = findViewById(R.id.btnBackTo_tb);
        recyclerView = findViewById(R.id.assignment_recycler);
        btnNewReminder = findViewById(R.id.assignemnt_newReminder);
        a_title = findViewById(R.id.assigment_title);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        data = getIntent();

        //The data will be read from the firebase using query
        Query query = firebaseFirestore
                .collection("Timetable")
                .document(firebaseUser.getUid())
                .collection("assignment");//  The path the data has been saved in the firebase

        FirestoreRecyclerOptions<TimeTableModal> assingments = new FirestoreRecyclerOptions
                .Builder<TimeTableModal>()
                .setQuery(query, TimeTableModal.class).build(); //The Firebase Recycler Adapter to fetch from the model class

        assignmentAdapter = new FirestoreRecyclerAdapter<TimeTableModal, AssignmentViewHolder>(assingments) {
            @Override
            protected void onBindViewHolder(@NonNull AssignmentViewHolder assignmentViewHolder, int i, @NonNull TimeTableModal timeTableModal) {

                btnView = assignmentViewHolder.itemView.findViewById(R.id.delete_a_timetable);

                // The title, date, time will be used to retrive data to particulat textview
                assignmentViewHolder.title.setText(timeTableModal.getTitle());
                assignmentViewHolder.date.setText(timeTableModal.getDate());
                assignmentViewHolder.time.setText(timeTableModal.getTime());

                // Will help identifying the correct document with the firestore
                String docId = assingments.getSnapshots().getSnapshot(i).getId();

                //The ViewHolder will help us to fetch the data to particular card only from the assignment priority
                assignmentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Reading data from the firestore
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
                                intent.putExtra("ttId", docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                AlertDialog alertDialog = new AlertDialog.Builder(tb_assignment.this)
                                        .setIcon(R.drawable.ic_aboutus)
                                        .setTitle("Are You Sure You Want To Delete an 'Assignment' Reminder?")
                                        .setMessage("Deleted Reminders Can't be undone!")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //yes
                                                DocumentReference df = firebaseFirestore
                                                        .collection("Timetable")
                                                        .document(firebaseUser.getUid())
                                                        .collection("assignment")
                                                        .document(docId);

                                                df.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(getApplicationContext(), "Assignment Reminder Deleted", Toast.LENGTH_SHORT).show();

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
            public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_card, parent, false);
                return new AssignmentViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager1 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager1);
        recyclerView.setAdapter(assignmentAdapter);

        btnNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(tb_assignment.this, timetable_bottomSheet.class));
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(tb_assignment.this, time_table.class));

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (assignmentAdapter != null)
        {
            assignmentAdapter.startListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        assignmentAdapter.startListening();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title, date, time;
        LinearLayout layout;
        CardView cView;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tb_title);
            date = itemView.findViewById(R.id.tb_date);
            time = itemView.findViewById(R.id.tb_time);
            layout = itemView.findViewById(R.id.layout_viewTimetable);
            cView = itemView.findViewById(R.id.cView_assignment);

        }
    }

    private int getRandomColor()
    {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);

        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);



    }
}
