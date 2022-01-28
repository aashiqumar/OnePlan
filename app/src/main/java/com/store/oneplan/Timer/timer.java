package com.store.oneplan.Timer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.oneplan.Dashboard.MainActivity;
import com.store.oneplan.Notes.notesactivity;
import com.store.oneplan.R;
import com.store.oneplan.TimeTable.time_table;
import com.store.oneplan.ToDoList.todohome;

public class timer extends AppCompatActivity {

    ImageView imgTimer;
    Button btnStart, btnStop;
    BottomNavigationView btnView;
    Chronometer timerHere;
    SeekBar processbar;
    MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;
    Boolean counterIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        timerHere = findViewById(R.id.timerHere);
        processbar = findViewById(R.id.processbar);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        processbar.setMax(3600);
        processbar.setProgress(300);
        btnView = findViewById(R.id.timer_nav);
        btnView.setSelectedItemId(R.id.timer);

        btnStop.setAlpha(0);

        btnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.timetable:
                        startActivity(new Intent(getApplicationContext(), time_table.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.notes:
                        startActivity(new Intent(getApplicationContext(), notesactivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        return true;

                    case R.id.todolist:
                        startActivity(new Intent(getApplicationContext(), todohome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;


                    case R.id.timer:
                        return true;
                }

                return false;
            }
        });


        processbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                update(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public void start_timer(View view) {

        if (counterIsActive == false) {
            counterIsActive = true;
            processbar.setEnabled(false);
            btnStart.setText("STOP");

            countDownTimer = new CountDownTimer(processbar.getProgress() * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    update((int) millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {

                    reset();

                    if (mediaPlayer != null)
                        mediaPlayer.start();
                }
            }.start();
        } else {

            reset();
        }
    }


    private void reset() {
        timerHere.setText("05:00");
        processbar.setProgress(300);
        countDownTimer.cancel();
        btnStart.setText("START");
        processbar.setEnabled(true);
        counterIsActive = false;
    }


    private void update(int progress) {

        int minutes = progress / 60;

        int seconds = progress % 60;
        String secondsFinal = "";

        if (seconds <= 9) {
            secondsFinal = "0" + seconds;
        } else {
            secondsFinal = "" + seconds;
        }

        processbar.setProgress(progress);
        timerHere.setText("" + minutes + ":" + secondsFinal);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (counterIsActive) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (counterIsActive) {
            countDownTimer.cancel();
        }
    }
}

