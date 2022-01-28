package com.store.oneplan.Login_Signup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.store.oneplan.R;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class activity_profile extends AppCompatActivity {

    ImageView btnEditProfile;
    TextView txtEmail;
    EditText txtName;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    Intent data;
    Button btnBack, btnSave;
    String userId;
    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private CircularImageView cView;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    LoadingDialog loadingDialog = new LoadingDialog(activity_profile.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtEmail = findViewById(R.id.profile_email);
        txtName = findViewById(R.id.profile_name);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.profile_btnBack);
        cView = findViewById(R.id.c_profile_image);
        btnSave = findViewById(R.id.btnSave_profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //To get the location where the profile has been stored
        storageReference = FirebaseStorage.getInstance().getReference();

        //To find the correct profile for current email address or user id
        StorageReference profileRef = storageReference.child("users/" +fAuth.getCurrentUser().getUid()+ "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull @NotNull Uri uri) {

                Picasso.get().load(uri).into(cView);
                // The profile has been downloaded using picasso API and Circular image View to display
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().setAspectRatio(1, 1).start(activity_profile.this);

            }
        });

        data = getIntent();

        userId = fAuth.getCurrentUser().getUid();

        txtName.setEnabled(true);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(activity_profile.this, activity_view_profile.class));
                finish();

            }
        });

        DocumentReference df = fStore.collection("users").document(userId);

        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    txtName.setText(documentSnapshot.getString("Name"));
                    txtEmail.setText(documentSnapshot.getString("Email"));

                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtName.setEnabled(true);

                String nName = txtName.getText().toString().trim();
                String nEmail = txtEmail.getText().toString().trim();

                DocumentReference documentReference = fStore
                        .collection("users")
                        .document(userId);

                Map<String, Object> profile = new HashMap<>();

                profile.put("Name", nName);
                profile.put("Email", nEmail);


                documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Profile Has Been Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity_profile.this, activity_view_profile.class));
                        loadingDialog.startLoadingDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismissDialog();
                            }
                        }, 5000);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Profile Failed to Update!", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000)// The result code acts like the permission ID
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri imageUri = data.getData();
                cView.setImageURI(imageUri);// Setting the image to the ImageView

                uploadImageToFirebase(imageUri);// + Upload the image Firebase Storage
            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri)
    {
        StorageReference fileRef = storageReference.child("users/" +fAuth.getCurrentUser().getUid()+ "/profile.jpg");

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull @NotNull UploadTask.TaskSnapshot taskSnapshot) {


                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Uri uri) {

                        //Picasso.get().load(uri).into(cView);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

                Toast.makeText(getApplicationContext(), "Profile Failed to Uploaded!", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
