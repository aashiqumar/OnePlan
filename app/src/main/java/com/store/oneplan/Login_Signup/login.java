package com.store.oneplan.Login_Signup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.R;

public class login extends AppCompatActivity {

    private TextView foregetPasswrod, btnGoToSignup, btnLogin, txtEmail, txtName;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        foregetPasswrod = findViewById(R.id.login_forgetPassword);
        btnGoToSignup = findViewById(R.id.login_goto_signup);
        mEmail = findViewById(R.id.logEmail);
        mPassword = findViewById(R.id.logPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null)
        {
            finish();
            startActivity(new Intent(login.this, MainActivity.class));
        }

        foregetPasswrod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(login.this, forgetPassword.class));

            }
        });

        btnGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(login.this, signup.class));

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                if (email.isEmpty() || pass.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }



                else
                {
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                checkEmailVerification();
                            }

                            else if (pass.matches(String.valueOf(mAuth)))
                            {
                                Toast.makeText(getApplicationContext(), "Incorrect Password! Try Resetting or Try Again", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Account Does Not Exist! or Incorrect Email or Password", Toast.LENGTH_LONG).show();
                            }
                        }


                    });
                }
            }
        });


    }

    private void checkEmailVerification() {

        FirebaseUser firebaseUser= mAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified() == true)
        {
            Toast.makeText(getApplicationContext(), "Login Sucessfull", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(login.this, MainActivity.class));

        }

        else
        {
            Toast.makeText(getApplicationContext(), "Verify Your Email! Check Your Mailbox", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }

    }
//
//    private void createUser()
//    {
//
//
////        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email.matches())
////        {
////            if (!pass.isEmpty()){
////                mAuth.createUserWithEmailAndPassword(email, pass)
////                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
////                            @Override
////                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
////                                Toast.makeText(login.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
////                                startActivity(new Intent(login.this , SigninActivity.classes));
////                                finish();
////                            }
////                        }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////
////                        Toast.makeText(getApplicationContext(), "Registration Error! Try Again", Toast.LENGTH_SHORT).show();
////
////                    }
////                });
////
////            }else {
////                mPassword.setError("Empty Feilds Are not Allowed");
////            }
////        }
////        else if(email.isEmpty())
////        {
////            mEmail.setError("Empty Feilds Are not Allowed");
////        }
////
////        else
////            {
////            mEmail.setError("Please Enter Correct Email");
////        }
////    }


}
