package com.store.oneplan.mymessagingapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.store.oneplan.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    private TextView txtRecieverUserName,txtLastSeen;
    private String reciverName,receiverId;
    private DatabaseReference userRef,chatRef;
    private Toolbar mToolbar;
    private EditText edtMessage;
    private ImageButton btnSend;
    private FirebaseAuth mAuth;
    private Button btnBack;
    private RecyclerView mRecyclerView;
    public static ArrayList<String> messages;
    public static ArrayList<Integer> messagePosition;
    public static ArrayList<String> messageIds;
    private MessageAdapter mMessageAdapter;

    TextView txtWrite;
    Button btnCapture, btnCopy;
    Bitmap bitmap;
    ImageView imgView;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);


        txtRecieverUserName = findViewById(R.id.txtReceiverName);
        txtLastSeen = findViewById(R.id.txtLastSeen);

        Intent intent = getIntent();
        reciverName = intent.getStringExtra("ReceiverName");
        receiverId = intent.getStringExtra("ReceiverId");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(receiverId);
        txtRecieverUserName.setText(reciverName);
        mToolbar = findViewById(R.id.toolBarMessage);
        setSupportActionBar(mToolbar);


        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = findViewById(R.id.recycularViewMessages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        messages = new ArrayList<>();
        messagePosition = new ArrayList<>();
        messageIds = new ArrayList<>();
        mMessageAdapter= new MessageAdapter(MessageActivity.this,messages);
        mRecyclerView.setAdapter(mMessageAdapter);
        btnBack = findViewById(R.id.btnBack_Chat);
        btnCapture = findViewById(R.id.scanner_chatbox);
        txtWrite = findViewById(R.id.edtMessage);
        imgView = findViewById(R.id.imageview_zero);

        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImageImportDialog();
            }
        });


        getLastSeen();
        getMessages();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MessageActivity.this, MainActivity_chat.class));
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMessage.getText().toString().equals(""))
                {
                    Toast.makeText(MessageActivity.this,"Enter Something ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("SenderId",mAuth.getCurrentUser().getUid());
                    map.put("Message",edtMessage.getText().toString());
                    map.put("ReceiverId",receiverId);
                    chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                userRef.child(mAuth.getCurrentUser().getUid())
                                        .child("ChatLists").child(receiverId).setValue(ServerValue.TIMESTAMP);
                                userRef.child(receiverId).child("ChatLists")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(ServerValue.TIMESTAMP);
                                edtMessage.setText("");
                            }
                        }
                    });
                }
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.addmenu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.addImage)
        {

            showImageImportDialog();
        }

        if (id == R.id.setting)
        {
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();

        }


        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {

        String[] items = {" Camera", "Gallery"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(MessageActivity.this);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0)
                {

                    if (!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickCamera();
                    }
                }

                if (which == 1)
                {
                    if (!checkStoragePermission())
                    {
                        requestStoragePermission();
                    }

                    else {

                        pickGallery();
                    }

                }

            }
        });
        dialog.create().show();
    }

    private void pickGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera()
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE) ;
    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(MessageActivity.this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(MessageActivity.this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted)
                    {
                        pickCamera();
                    }

                    else {

                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted)
                    {
                        pickGallery();
                    }

                    else {

                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MessageActivity.this);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MessageActivity.this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                imgView.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational())
                {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

                else {

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i=0; i < items.size(); i++)
                    {
                        TextBlock myItems = items.valueAt(i);
                        sb.append(myItems.getValue());
                        sb.append("\n");
                    }

                    txtWrite.setText(sb.toString());
                }
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();
                Toast.makeText(MessageActivity.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        }


    }


    void getLastSeen()
    {
        userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChild("Status")) {
                    if (snapshot.child("Status").hasChild("Online")) {
                        txtLastSeen.setText("Online");

                    } else {
                        Object objTimeStamp = snapshot.child("Status").child("Offline").getValue();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String lastseenString = df.format(objTimeStamp);
                        String currentDateString = df.format(new Date());
                        Date currentDate = null,lastseenDate = null;
                        try {
                            currentDate = df.parse(currentDateString);
                            lastseenDate = df.parse(lastseenString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(currentDate.compareTo(lastseenDate)==0)
                        {
                            String time =new SimpleDateFormat("h:mm a").format(objTimeStamp);
                            txtLastSeen.setText("last seen :"+time);
                        }
                        else
                        {
                            String date=  new SimpleDateFormat("yyyy-MM-dd").format(objTimeStamp);
                            txtLastSeen.setText("last seen :"+date);
                        }


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void getMessages()
    {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot!=null && snapshot.exists())
                {
                    if(snapshot.child("ReceiverId").getValue().toString().equals(receiverId)
                            && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid()))
                    {
                        messages.add(snapshot.child("Message").getValue().toString());
                        messagePosition.add(1);
                        mMessageAdapter.notifyItemChanged(messages.size()-1);
                        messageIds.add(snapshot.getKey());
                        mRecyclerView.smoothScrollToPosition(messages.size()-1);
                    }
                    else if(snapshot.child("ReceiverId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                            && snapshot.child("SenderId").getValue().toString().equals(receiverId))
                    {
                        messages.add(snapshot.child("Message").getValue().toString());
                        messagePosition.add(0);
                        mMessageAdapter.notifyItemChanged(messages.size()-1);
                        messageIds.add(snapshot.getKey());
                        mRecyclerView.smoothScrollToPosition(messages.size()-1);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("ReceiverId").getValue().toString().equals(receiverId)
                        && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid()))
                || (snapshot.child("ReceiverId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                        && snapshot.child("SenderId").getValue().toString().equals(receiverId))) {
                    int pos = messageIds.indexOf(snapshot.getKey());
                    messagePosition.remove(pos);
                    messages.remove(pos);
                    messageIds.remove(pos);
                    mMessageAdapter.notifyItemRemoved(pos);
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}