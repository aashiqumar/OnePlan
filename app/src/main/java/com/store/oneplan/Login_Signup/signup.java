package com.store.oneplan.Login_Signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.store.oneplan.R;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private EditText rEmail, rName, rPassword;
    private Button btnReg;
    private TextView btnLog, btnSkip;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore fStore;
    private  String fEmail, fPassword;
    String userId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

        rEmail = findViewById(R.id.regEmail);
        rName = findViewById(R.id.regName);
        rPassword = findViewById(R.id.regPassword);
        btnLog = findViewById(R.id.btn_to_login);
        btnReg = findViewById(R.id.btnSignup);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        DatabaseReference myRef = database.getReference().child("Users");

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(signup.this, login.class));
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = rEmail.getText().toString().trim();
                String password = rPassword.getText().toString().trim();
                String name = rName.getText().toString().trim();



                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }

                else if (password.length()<5)
                {
                    Toast.makeText(getApplicationContext(), "Password Should atleast Contain 6 Characters", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Sucessfully Registered!", Toast.LENGTH_SHORT).show();

                                userId = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                user.put("Name", name);
                                user.put("Email", email);

                                myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Name").setValue(name);
                                myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Email").setValue(email);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(), "Please Verify Your Email Now", Toast.LENGTH_SHORT ).show();

                                    }
                                });
                                sendEmailVerification();
                            }

                            else
                            {
                                Toast.makeText(getApplicationContext(), "Account Already Exists! Login Now...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signup.this, login.class));

                            }


                        }
                    });
                }
            }
        });

    }


    private void sendEmailVerification() {

        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();

        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getApplicationContext(), "Verification Email Sent!", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    final ProgressDialog progressDialog = new ProgressDialog(signup.this);
                    progressDialog.setTitle("Teleporting to Login!");
                    progressDialog.show();
                    startActivity(new Intent(signup.this, login.class));


                }
            });
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Failed to send Verification!", Toast.LENGTH_SHORT);
        }

    }



}
