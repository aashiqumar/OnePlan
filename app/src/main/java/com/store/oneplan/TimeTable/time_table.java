package com.store.oneplan.TimeTable;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.Notes.notesactivity;
import com.store.oneplan.R;
import com.store.oneplan.Timer.timer;
import com.store.oneplan.ToDoList.todohome;

public class time_table extends AppCompatActivity {

    BottomNavigationView btmView;
    LinearLayout btnAssignment, btnEvent, btnClass;
    CardView btnOther;
    SwitchCompat showDate;
    TextView displayDate, eventCount;
    RecyclerView recyclerView;
    Button btnDelete, btnAdd,btnAlert;

    FirebaseUser fUser;
    FirebaseFirestore fStore, db;
    CollectionReference countStore;
    FirebaseAuth fAuth;
    String userId;
    LinearLayout linearLayout;
    StaggeredGridLayoutManager staggeredGridLayoutManager;


    FirestoreRecyclerAdapter<TimeTableModal, NoteViewHolder> reminderAdapter;
    private Object Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        btmView = findViewById(R.id.timetable_nav);
        btmView.setSelectedItemId(R.id.timetable);
        btnAdd = findViewById(R.id.tb_btnAdd);
        displayDate = findViewById(R.id.tb_txtDate);
        recyclerView = findViewById(R.id.tb_recycler);
        btnAssignment = findViewById(R.id.assignment_tab);
        btnEvent = findViewById(R.id.events_tab);
        btnClass = findViewById(R.id.classes_tab);
        btnOther = findViewById(R.id.otherTab);
        btnAlert = findViewById(R.id.timetableAlert);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                help();
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(time_table.this, tb_other.class));
            }
        });

        btnClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(time_table.this, tb_class.class));
            }
        });

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(time_table.this, event.class));

            }
        });

        btnAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(time_table.this,  tb_assignment.class));
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(time_table.this, timetable_bottomSheet.class);
                startActivity(intent);
            }
        });

        btmView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;

                    case R.id.notes:
                        startActivity(new Intent(getApplicationContext(), notesactivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;

                    case R.id.timer:
                        startActivity(new Intent(getApplicationContext(), timer.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;

                    case R.id.todolist:
                        startActivity(new Intent(getApplicationContext(), todohome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;

                    case R.id.timetable:
                        return true;

                }
                return false;
            }
        });

        Query query = fStore.collection("Timetable").document(fUser.getUid()).collection("assignment");

        FirestoreRecyclerOptions<TimeTableModal> userReminders = new FirestoreRecyclerOptions.Builder<TimeTableModal>().setQuery(query, TimeTableModal.class).build();

        reminderAdapter = new FirestoreRecyclerAdapter<TimeTableModal, NoteViewHolder>(userReminders) {

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull TimeTableModal timeTableModal) {

                btnDelete = noteViewHolder.itemView.findViewById(R.id.delete_a_timetable);


                noteViewHolder.nTitle.setText(timeTableModal.getTitle());
                noteViewHolder.nTime.setText(timeTableModal.getTime());
                noteViewHolder.nDate.setText(timeTableModal.getDate());

                String docId = reminderAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), EditTimetable.class);
                        intent.putExtra("Title", timeTableModal.getTitle());
                        intent.putExtra("Description", timeTableModal.getDescription());
                        intent.putExtra("Date", timeTableModal.getDate());
                        intent.putExtra("Time", timeTableModal.getTime());
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

                                AlertDialog alertDialog = new AlertDialog.Builder(time_table.this)
                                        .setIcon(R.drawable.ic_aboutus)
                                        .setTitle("Are You Sure You Want To Delete an 'Assignment' Reminder?")
                                        .setMessage("Deleted Reminders Can't be undone!")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //yes
                                                DocumentReference df = fStore
                                                        .collection("Timetable")
                                                        .document(fUser.getUid())
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
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_card, parent, false);
                return new NoteViewHolder(view);


            }
        };

        recyclerView = findViewById(R.id.tb_recycler);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(reminderAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        }).attachToRecyclerView(recyclerView);





    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void help ()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.timetable_alert, null);

        Button btnDone = view.findViewById(R.id.btnDone_Alert);



        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });


        alertDialog.show();
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nTitle, nDate, nTime;
        LinearLayout linearLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            nTitle = itemView.findViewById(R.id.tb_title);
            nDate = itemView.findViewById(R.id.tb_date);
            nTime = itemView.findViewById(R.id.tb_time);
            linearLayout = itemView.findViewById(R.id.layout_viewTimetable);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(reminderAdapter != null)
        {
            reminderAdapter.startListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        reminderAdapter.startListening();
    }

    private void deleteItem()
    {

    }



}
