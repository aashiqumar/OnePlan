package com.store.oneplan.TimeTable;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.store.oneplan.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class timetable_bottomSheet extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    SwitchCompat switchTime;
    SwitchCompat switchDate;
    SwitchCompat switchRepeat;
    TextView showDate, showTime, showRepeat;
    TextView btnDone, btnCancel;
    Button btnSave;
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    EditText txtTitle, txtDescription;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    String userId;
    Calendar now = Calendar.getInstance();
    TimePickerDialog tim;
    DatePickerDialog dpd;
    private MaterialTimePicker picker;
    private NotificationHelper notificationHelper;
    private  NotificationModel notificationModel;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mHourRepeat, mMinuteRepeat;

    ScrollView bottomSheet;

    String timeToNotify;

    Dialog dialog;
    Dialog dateDialog;

    SwitchCompat assignmentSwitch, eventSwitch, classSwitch, otherSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_bottom_sheet);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomSheet = findViewById(R.id.bottomSheet);

        switchTime = findViewById(R.id.tb_time_switch);
        showTime = findViewById(R.id.tb_txtTime);

        switchDate = findViewById(R.id.tb_date_switch);
        showDate = findViewById(R.id.tb_txtDate);

        txtTitle = findViewById(R.id.set_title);
        txtDescription = findViewById(R.id.set_description);
        btnDone = findViewById(R.id.tb_done);
        btnCancel = findViewById(R.id.tb_cancel);

        assignmentSwitch = (SwitchCompat) findViewById(R.id.assignemnt_switch);
        eventSwitch = (SwitchCompat) findViewById(R.id.events_switch);
        classSwitch = (SwitchCompat) findViewById(R.id.classes_switch);
        otherSwitch = (SwitchCompat) findViewById(R.id.other_switch);

        dialog = new Dialog(timetable_bottomSheet.this);
        notificationHelper = new NotificationHelper(timetable_bottomSheet.this);




        //-----------------------------------------------------------------------------------------------------------------
        //Date Switch - Date Picker Code

        //Date Switch - Switch Code
        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "Date Picker");

                }

            }
        });

        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b)
                {
                    TimePickerFragment timePicker = new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(),"Time Picker");

                }

            }
        });



        assignmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked)
                {
                    // When the assignment switch is checked other priority switches will be disables
                    eventSwitch.setEnabled(false);
                    classSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);

                    String value = (txtTitle.getText().toString().trim());
                    String desc = (txtDescription.getText().toString().trim());
                    String timeD = (showTime.getText().toString().trim());


                    //when button clicked done, all the data written will be stored in the Firestore
                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String title = txtTitle.getText().toString().trim();
                            String description = txtDescription.getText().toString().trim();
                            String date = showDate.getText().toString().trim();
                            String time = showTime.getText().toString().trim();

                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Timetable").document(userId).collection("assignment").document();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Title", title);
                            user.put("Description", description);
                            user.put("Date", date);
                            user.put("Time", time);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                    Toast.makeText(getApplicationContext(), "Reminder Set For " + date + " at " + time + " ", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(timetable_bottomSheet.this, time_table.class));


                                    //startAlarmAssignment(calendar);

                                    //GET NOTIFICATION 🔔


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "FAILED TO SET REMINDER", Toast.LENGTH_SHORT);

                                }
                            });


                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

                        }
                    });

                }

                else if (!isChecked)
                {
                    // When the user un-checks the assiwngment switch, all the other priority switches will be enabled to select again
                    eventSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    assignmentSwitch.setEnabled(true);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Toast.makeText(getApplicationContext(), "Please Select a Priority", Toast.LENGTH_SHORT);
                        }
                    });
                }

            }
        });

        eventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    classSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String title = txtTitle.getText().toString().trim();
                            String description = txtDescription.getText().toString().trim();
                            String date = showDate.getText().toString().trim();
                            String time = showTime.getText().toString().trim();

                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Timetable").document(userId).collection("events").document();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Title", title);
                            user.put("Description", description);
                            user.put("Date", date);
                            user.put("Time", time);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Set For " + date + " at " + time + " ", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(timetable_bottomSheet.this, time_table.class));


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "FAILED TO SET REMINDER", Toast.LENGTH_SHORT);

                                }
                            });


                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

                        }
                    });

                }

                else if (isChecked == false)
                {
                    assignmentSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Toast.makeText(getApplicationContext(), "Please Select a Priority", Toast.LENGTH_SHORT);
                        }
                    });
                }

            }
        });

        classSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    eventSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String title = txtTitle.getText().toString().trim();
                            String description = txtDescription.getText().toString().trim();
                            String date = showDate.getText().toString().trim();
                            String time = showTime.getText().toString().trim();

                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Timetable").document(userId).collection("class").document();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Title", title);
                            user.put("Description", description);
                            user.put("Date", date);
                            user.put("Time", time);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Set For " + date + " at " + time + " ", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

                                    //startAlarmClass(calendar);
                                    //GET NOTIFICATION 🔔


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "FAILED TO SET REMINDER", Toast.LENGTH_SHORT);

                                }
                            });


                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(getApplicationContext(), "Reminder Cancelled", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

                        }
                    });

                }

                else if (isChecked == false)
                {
                    classSwitch.setEnabled(true);
                    assignmentSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Toast.makeText(getApplicationContext(), "Please Select a Priority", Toast.LENGTH_SHORT);
                        }
                    });
                }

            }
        });

        otherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    eventSwitch.setEnabled(false);
                    classSwitch.setEnabled(false);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String title = txtTitle.getText().toString().trim();
                            String description = txtDescription.getText().toString().trim();
                            String date = showDate.getText().toString().trim();
                            String time = showTime.getText().toString().trim();

                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("Timetable").document(userId).collection("other").document();
                            Map<String, Object> user = new HashMap<>();
                            user.put("Title", title);
                            user.put("Description", description);
                            user.put("Date", date);
                            user.put("Time", time);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Set For " + date + " at " + time + " ", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(timetable_bottomSheet.this, time_table.class));
                                    //GET NOTIFICATION 🔔


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "FAILED TO SET REMINDER", Toast.LENGTH_SHORT);

                                }
                            });


                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

                        }
                    });

                }

                else if (isChecked == false)
                {
                    assignmentSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Toast.makeText(getApplicationContext(), "Please Select a Priority", Toast.LENGTH_SHORT);
                        }
                    });
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                startActivity(new Intent(timetable_bottomSheet.this, time_table.class));

            }
        });

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updateDateTimeText(c);
        startAlarm(c, txtTitle.getText().toString(), txtDescription.getText().toString());

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateDateTimeText(c);
        startAlarm(c, txtTitle.getText().toString(), txtDescription.getText().toString());

    }

    private void updateDateTimeText(Calendar c) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedTime = df.format(c.getTime());
        showTime.setText(formattedTime);

        SimpleDateFormat tf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = tf.format(c.getTime());
        showDate.setText(formattedDate);

    }

    private void startAlarm(Calendar c, String title, String description)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(timetable_bottomSheet.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(timetable_bottomSheet.this, 1, intent, 0);


        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private void cancelAlarm ()
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(timetable_bottomSheet.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(timetable_bottomSheet.this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }


}
