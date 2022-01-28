package com.store.oneplan;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class aboutus extends AppCompatActivity {

    Button btnBack;
    TextView txtInstagram, txtFacebook, txtTwitter, txtYoutube, txtDiscord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        btnBack = findViewById(R.id.ab_btnBackTo_dash);

        txtInstagram = findViewById(R.id.txtInstagram);
        txtInstagram.setMovementMethod(LinkMovementMethod.getInstance());

        txtFacebook = findViewById(R.id.txtFacebook);
        txtFacebook.setMovementMethod(LinkMovementMethod.getInstance());

        txtTwitter = findViewById(R.id.txtTwitter);
        txtTwitter.setMovementMethod(LinkMovementMethod.getInstance());

        txtYoutube = findViewById(R.id.txtYoutube);
        txtYoutube.setMovementMethod(LinkMovementMethod.getInstance());

        txtDiscord = findViewById(R.id.txtDiscord);
        txtDiscord.setMovementMethod(LinkMovementMethod.getInstance());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(aboutus.this, side_menu.class));
            }
        });

    }
}