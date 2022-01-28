package com.store.oneplan.Login_Signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.store.oneplan.R;
import com.store.oneplan.side_menu;

import org.jetbrains.annotations.NotNull;

public class activity_view_profile extends AppCompatActivity {

    private Button btnEditProfile, btnBack;
    private TextView txtName, txtEmail;
    private FirebaseFirestore fStore;
    private CircularImageView cView;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    LoadingDialog loadingDialog = new LoadingDialog(activity_view_profile.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        btnBack = findViewById(R.id.view_profile_btnBack);
        btnEditProfile = findViewById(R.id.btn_EditProfile);
        txtEmail = findViewById(R.id.view_profile_email);
        txtName = findViewById(R.id.view_profile_name);
        cView = findViewById(R.id.view_profile_image);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" +fAuth.getCurrentUser().getUid()+ "/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull @NotNull Uri uri) {
                Picasso.get().load(uri).into(cView);

            }
        });

        String userId = fAuth.getCurrentUser().getUid();
        txtName.setEnabled(false);
        txtEmail.setEnabled(false);

        //To Read the users profile information
        DocumentReference documentReference = fStore.collection("users").document(userId);

        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 1000);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {



                if (task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    txtEmail.setText(documentSnapshot.getString("Email"));
                    txtName.setText(documentSnapshot.getString("Name"));

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

                Toast.makeText(getApplicationContext(), "Profile Failed To Load! Check Your Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(activity_view_profile.this, side_menu.class));
                finish();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(activity_view_profile.this, activity_profile.class));
            }
        });





    }
}