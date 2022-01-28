package com.store.oneplan;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.Login_Signup.activity_view_profile;
import com.store.oneplan.Login_Signup.login;

import org.jetbrains.annotations.NotNull;

public class side_menu extends AppCompatActivity {

    Button btnProfile, btnAboutus, btnLogout, btnBack;
    FirebaseAuth fAuth;
    CircularImageView rView;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        btnBack = findViewById(R.id.p_btnBackTo_sidemenu);
        btnProfile = findViewById(R.id.sidemenu_profile);
        btnAboutus = findViewById(R.id.sidemenu_aboutus);
        btnLogout = findViewById(R.id.sidemenu_logout);
        rView = findViewById(R.id.sidemenu_profileShow);

        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid()+ "/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull @NotNull Uri uri) {
                Picasso.get().load(uri).into(rView);

            }
        });

        fAuth = FirebaseAuth.getInstance();

        btnAboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(side_menu.this, aboutus.class));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(side_menu.this, MainActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(side_menu.this, activity_view_profile.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(side_menu.this)
                        .setIcon(R.drawable.ic_aboutus)
                        .setTitle("Are You Sure, You Want to Sign Out?")
                        .setMessage("Signing Out Will Redirect You To Login Page")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                fAuth.signOut();
                                Toast.makeText(getApplicationContext(), "Signed Out!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(side_menu.this, login.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();

                            }
                        }).show();


            }
        });


    }

}