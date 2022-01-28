package com.store.oneplan.Notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.store.oneplan.R;

import java.util.HashMap;
import java.util.Map;

public class createnote extends AppCompatActivity {

    EditText mcreatetitleofnote,mcreatecontentofnote;
    Button msavenote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Button btnBack;

    ProgressBar mprogressbarofcreatenote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);



        msavenote=findViewById(R.id.savenote);
        mcreatecontentofnote=findViewById(R.id.createcontentofnote);
        mcreatetitleofnote=findViewById(R.id.createtitleofnote);
        btnBack = findViewById(R.id.btnBack_Note);

        mprogressbarofcreatenote=findViewById(R.id.progressbarofcreatenote);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(createnote.this, notesactivity.class));

            }
        });



        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=mcreatetitleofnote.getText().toString();
                String content=mcreatecontentofnote.getText().toString();
                if(title.isEmpty() || content.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Both field are Require",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    DocumentReference documentReference=firebaseFirestore.collection("Notes").document(firebaseUser.getUid()).collection("savednotes").document();
                    Map<String ,Object> note= new HashMap<>();
                    note.put("title",title);
                    note.put("content",content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Note Created Succesffuly",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createnote.this, notesactivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed To Create Note",Toast.LENGTH_SHORT).show();
                            finish();
                            // startActivity(new Intent(createnote.this,notesactivity.class));
                        }
                    });




                }
            }
        });






    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}