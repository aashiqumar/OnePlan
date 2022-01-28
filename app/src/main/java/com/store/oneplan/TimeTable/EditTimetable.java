package com.store.oneplan.TimeTable;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.store.oneplan.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditTimetable extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private EditText txtTitle, txtDescription;
    private SwitchCompat assignmentSwitch, eventSwitch, classSwitch, otherSwitch, timeSwitch, dateSwitch;
    private TextView btnSave, showTime, showDate, btnCancel;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timetable);

        txtTitle = findViewById(R.id.edit_title);
        txtDescription = findViewById(R.id.edit_description);
        assignmentSwitch = findViewById(R.id.edit_assignemnt_switch);
        timeSwitch = findViewById(R.id.edit_time_switch);
        dateSwitch = findViewById(R.id.edit_date_switch);
        btnSave = findViewById(R.id.edit_done);
        btnCancel = findViewById(R.id.edit_cancel);
        showDate = findViewById(R.id.edit_txtDate);
        showTime = findViewById(R.id.edit_txtTime);

        assignmentSwitch = findViewById(R.id.edit_assignemnt_switch);
        eventSwitch = findViewById(R.id.edit_events_switch);
        classSwitch = findViewById(R.id.edit_classes_switch);
        otherSwitch = findViewById(R.id.edit_other_switch);


        fAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        dateSwitch.setChecked(true);
        timeSwitch.setChecked(true);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(EditTimetable.this, time_table.class));
            }
        });

        data = getIntent();

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    TimePickerFragment tf = new TimePickerFragment();
                    tf.show(getSupportFragmentManager(), "Time Picker");
                }
            }
        });

        dateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    DatePickerFragment df = new DatePickerFragment();
                    df.show(getSupportFragmentManager(), "Date Picker");
                }
            }
        });

        assignmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    eventSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);
                    eventSwitch.setEnabled(false);
                    assignmentSwitch.setEnabled(true);

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String ntitle = txtTitle.getText().toString().trim();
                            String ndescription = txtDescription.getText().toString().trim();
                            String ndate = showDate.getText().toString().trim();
                            String ntime = showTime.getText().toString().trim();

                            DocumentReference df = fStore
                                    .collection("Timetable")
                                    .document(fUser.getUid())
                                    .collection("assignment")
                                    .document(data.getStringExtra("ttId"));

                            Map<String, Object> assignement = new HashMap<>();
                            assignement.put("Title", ntitle);
                            assignement.put("Description", ndescription);
                            assignement.put("Date", ndate);
                            assignement.put("Time", ntime);

                            df.set(assignement).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Has Been Updated!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(EditTimetable.this, time_table.class));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "Reminder Failed to Update", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    });
                }

                else if (!isChecked)
                {
                    eventSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                    assignmentSwitch.setEnabled(true);
                }
            }
        });// End of assignment reminder


        eventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    classSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);
                    eventSwitch.setEnabled(true);

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                String ntitle = txtTitle.getText().toString().trim();
                                String ndescription = txtDescription.getText().toString().trim();
                                String ndate = showDate.getText().toString().trim();
                                String ntime = showTime.getText().toString().trim();

                                DocumentReference df = fStore
                                        .collection("Timetable")
                                        .document(fUser.getUid())
                                        .collection("events")
                                        .document(data.getStringExtra("ttId"));

                                Map<String, Object> assignement = new HashMap<>();
                                assignement.put("Title", ntitle);
                                assignement.put("Description", ndescription);
                                assignement.put("Date", ndate);
                                assignement.put("Time", ntime);

                                df.set(assignement).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(), "Reminder Has Been Updated!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(EditTimetable.this, event.class));

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(getApplicationContext(), "Reminder Failed to Update", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }



                        });
                }

                else if (!isChecked)
                {
                    assignmentSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);
                }
            }
        }); // End of event reminder

        classSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    eventSwitch.setEnabled(false);
                    otherSwitch.setEnabled(false);
                    classSwitch.setEnabled(true);

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String nTitle = txtTitle.getText().toString().trim();
                            String nDescription = txtDescription.getText().toString().trim();
                            String nTime = showTime.getText().toString().trim();
                            String nDate = showDate.getText().toString().trim();

                            DocumentReference df = fStore
                                    .collection("Timetable")
                                    .document(fUser.getUid())
                                    .collection("class")
                                    .document(data.getStringExtra("ttId"));

                            Map<String, Object> classes = new HashMap<>();
                            classes.put("Title", nTitle);
                            classes.put("Description", nDescription);
                            classes.put("Time", nTime);
                            classes.put("Date", nDate);

                            df.set(classes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditTimetable.this, time_table.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "Reminder Failed To Update!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }

                else if (!isChecked)
                {
                    assignmentSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                }

            }
        });// End of class reminder

        otherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    assignmentSwitch.setEnabled(false);
                    eventSwitch.setEnabled(false);
                    classSwitch.setEnabled(false);
                    otherSwitch.setEnabled(true);

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String nTitle = txtTitle.getText().toString().trim();
                            String nDescription = txtDescription.getText().toString().trim();
                            String nTime = showTime.getText().toString().trim();
                            String nDate = showDate.getText().toString().trim();

                            DocumentReference df = fStore
                                    .collection("Timetable")
                                    .document(fUser.getUid())
                                    .collection("other")
                                    .document(data.getStringExtra("ttId"));

                            HashMap<String, Object> other = new HashMap<>();
                            other.put("Title", nTitle);
                            other.put("Description", nDescription);
                            other.put("Time", nTime);
                            other.put("Date", nDate);

                            df.set(other).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(getApplicationContext(), "Reminder Updated Sucessfully!", Toast.LENGTH_SHORT);
                                    startActivity(new Intent(EditTimetable.this, time_table.class));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getApplicationContext(), "Reminder Failed To Update!", Toast.LENGTH_SHORT);
                                }
                            });

                        }
                    });

                }
                else if (!isChecked)
                {
                    assignmentSwitch.setEnabled(true);
                    otherSwitch.setEnabled(true);
                    eventSwitch.setEnabled(true);
                    classSwitch.setEnabled(true);
                }
            }
        });// End of other Reminder


        String aTitle = data.getStringExtra("Title");
        String aDescription = data.getStringExtra("Description");
        String aDate = data.getStringExtra("Date");
        String aTime = data.getStringExtra("Time");

        txtTitle.setText(aTitle);
        txtDescription.setText(aDescription);
        showTime.setText(aTime);
        showDate.setText(aDate);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        updateDateTime(c);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        updateDateTime(calendar);
        startAlarm(calendar);
    }

    private void updateDateTime (Calendar c)
    {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        String formattedTime = sf.format(c.getTime());
        showTime.setText(formattedTime);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        showDate.setText(formattedDate);
    }

    private void startAlarm (Calendar c)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(EditTimetable.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditTimetable.this, 1, intent, 0);

        if (c.before(Calendar.getInstance()))
        {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private void cancelAlarm ()
    {
        AlarmManager alarmManager = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(EditTimetable.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditTimetable.this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

}
