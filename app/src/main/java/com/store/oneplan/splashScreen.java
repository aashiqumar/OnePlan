package com.store.oneplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.store.oneplan.Login_Signup.login;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.OnePlanOrange));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(splashScreen.this, login.class);
                startActivity(intent);
                final ProgressDialog progressDialog = new ProgressDialog(splashScreen.this);
                progressDialog.setTitle("Ready To Launch");
                progressDialog.show();
                finish();

            }
        },2000);
    }
}