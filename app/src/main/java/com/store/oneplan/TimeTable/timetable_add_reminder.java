package com.store.oneplan.TimeTable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.store.oneplan.R;

public class timetable_add_reminder extends AppCompatActivity {

    Button btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classes_card);




        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(timetable_add_reminder.this, timetable_bottomSheet.class));


            }
        });
    }
}
