package com.store.oneplan.Dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.store.oneplan.Notes.notesactivity;
import com.store.oneplan.R;
import com.store.oneplan.TimeTable.time_table;
import com.store.oneplan.Timer.timer;
import com.store.oneplan.ToDoList.DatabaseHandler;
import com.store.oneplan.ToDoList.DialogCloseListener;
import com.store.oneplan.ToDoList.ToDoAdapter;
import com.store.oneplan.ToDoList.ToDoModel;
import com.store.oneplan.ToDoList.todohome;
import com.store.oneplan.mymessagingapp.MainActivity_chat;
import com.store.oneplan.side_menu;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    BottomNavigationView btmView;
    CardView home_btnTt, btnTask, btnNote;
    ImageView btnReg, btnProfile;
    Button btnChat;
    RelativeLayout home_timeTable;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser fUser;
    String userId;
    Button btnPrf;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DatabaseHandler db;
    CircularImageView mView;

    private RecyclerView tasksRecyclerView1;
    private ToDoAdapter tasksAdapter1;

    private List<ToDoModel> taskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmView = findViewById(R.id.dashboard_nav);
        btmView.setSelectedItemId(R.id.dashboard);
        btnChat = findViewById(R.id.chatbox);
        btnProfile = findViewById(R.id.dashboard_profile);
        btnNote = findViewById(R.id.dashboard_notes);
        btnTask = findViewById(R.id.dashboard_tasks);
        mView = findViewById(R.id.dashboard_profile);

        home_btnTt = findViewById(R.id.home_goto_timetable);
        home_timeTable = findViewById(R.id.rl_goto_timetable);

        fAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userId = fUser.getUid();

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" +fAuth.getCurrentUser().getUid()+ "/profile.jpg");



        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull @NotNull Uri uri) {
                Picasso.get().load(uri).into(mView);

            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MainActivity_chat.class));
            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, notesactivity.class));
                finish();
            }
        });

        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, todohome.class));
                finish();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, side_menu.class));
                finish();

            }
        });


        home_timeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timetable_page();

            }
        });

        home_btnTt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timetable_page();

            }
        });


        btmView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.dashboard:
                        return true;

                    case R.id.timetable:
                        startActivity(new Intent(getApplicationContext(), time_table.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.notes:
                        startActivity(new Intent(getApplicationContext(), notesactivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.timer:
                        startActivity(new Intent(getApplicationContext(), timer.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.todolist:
                        startActivity(new Intent(getApplicationContext(), todohome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                }
                return false;
            }
        });

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView1 = findViewById(R.id.viewtasks);
        tasksRecyclerView1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        tasksAdapter1 = new ToDoAdapter(db, MainActivity.this);
        tasksRecyclerView1.setAdapter(tasksAdapter1);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter1.setTasks(taskList);


    }


    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter1.setTasks(taskList);
        tasksAdapter1.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    private void timetable_page ()
    {
        Intent intent = new Intent(MainActivity.this, time_table.class);
        startActivity(intent);
        finish();
    }


    private boolean isConnected (MainActivity mainActivity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo data = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return(wifi != null && wifi.isConnected()) || (data != null && data.isConnected());

    }

}
