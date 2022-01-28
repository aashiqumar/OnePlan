package com.store.oneplan.Chat;

import androidx.appcompat.app.AppCompatActivity;

import com.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.R;

public class chatHome extends AppCompatActivity {

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        btnBack = findViewById(R.id.btnBackTo_chat_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(chatHome.this, MainActivity.class));
            }
        });



    }
}
