package com.store.oneplan.mymessagingapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.Login_Signup.activity_view_profile;
import com.store.oneplan.R;

import java.util.HashMap;

public class MainActivity_chat extends AppCompatActivity {
    private TextView txtdiplayUserName;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAdapter mTabAdapter;
    private Button btnBack;
    private BottomNavigationView btmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.OnePlanOrange));
        }

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        btmView = findViewById(R.id.chat_nav);

        btmView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {

                    case R.id.gotoprofile:
                        startActivity(new Intent(getApplicationContext(), activity_view_profile.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.backtomain:
                        startActivity(new Intent(MainActivity_chat.this, MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;
                }
                return false;
            }
        });



        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        userRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //txtdiplayUserName.setText(snapshot.child("Name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_mainactivity,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.itmLogOut)
        {
            HashMap<String,Object> map = new HashMap<>();
            map.put("Offline",ServerValue.TIMESTAMP);
            userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity_chat.this,SignUpActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onDestroy();
    }



    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Online",ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Offline",ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
    }
}