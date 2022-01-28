package com.store.oneplan.Login_Signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.store.oneplan.R;

public class forgetPassword extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private Button btnEnter;
    RelativeLayout relativeLayout;

    private EditText txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btnEnter = findViewById(R.id.fp_btnEnter);
        txtEmail = findViewById(R.id.fp_email);
        ImageView btnBack = (ImageView) findViewById(R.id.fp_btnBack);
        relativeLayout = findViewById(R.id.fp_rLayout);

        firebaseAuth = FirebaseAuth.getInstance();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(forgetPassword.this, login.class));

            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString().trim();

                if (email.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter Your Registered Email!", Toast.LENGTH_SHORT).show();
                }

                else
                {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Password Reset Email Sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgetPassword.this, login.class));
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Email is Incorrect! or Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }


            }

        });
    }
}
