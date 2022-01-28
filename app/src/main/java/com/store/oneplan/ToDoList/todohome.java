package com.store.oneplan.ToDoList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.Notes.notesactivity;
import com.store.oneplan.R;
import com.store.oneplan.TimeTable.time_table;
import com.store.oneplan.Timer.timer;

import java.util.Collections;
import java.util.List;

public class todohome extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private Button fab;
    private BottomNavigationView btmView;

    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todohome);
        btmView = findViewById(R.id.todolist_nav);
        btmView.setSelectedItemId(R.id.todolist);

        btmView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.todolist:
                        return true;

                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;

                    case R.id.timetable:
                        startActivity(new Intent(getApplicationContext(), time_table.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                        return true;

                    case R.id.notes:
                        startActivity(new Intent(getApplicationContext(), notesactivity.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                        return true;

                    case R.id.timer:
                        startActivity(new Intent(getApplicationContext(), timer.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                        return true;




                }
                return false;
            }
        });

        db = new DatabaseHandler(this);
        db.openDatabase();

        tasksRecyclerView = findViewById(R.id.todolist_recyclerview);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(todohome.this));
        tasksAdapter = new ToDoAdapter(db,todohome.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();

    }
}