package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends Activity {

    Button nameButton;

    Switch voiceSwitch;
    Switch encouragementSwitch;
    Switch reminderSwitch;

    SharedPreferences settingsPref;
    SharedPreferences.Editor prefEditor;

    final static int DIALOG_ID = 80;
    int selectedHour = 12;
    int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameButton = (Button) findViewById(R.id.nameButton);

        voiceSwitch = (Switch)findViewById(R.id.voiceSwitch);
        encouragementSwitch = (Switch)findViewById(R.id.encouragementSwitch);
        reminderSwitch = (Switch)findViewById(R.id.reminderSwitch);

        settingsPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        prefEditor = settingsPref.edit();

        nameButton.setText(settingsPref.getString("name","set name"));

        retrieveSettings();

        //listeners
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefEditor.putInt("voiceSetting",1);
                    Toast.makeText(SettingsActivity.this, "Voice Activated", Toast.LENGTH_SHORT).show();
                }else{
                    prefEditor.putInt("voiceSetting",0);
                }
                prefEditor.commit();
            }
        });

        encouragementSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefEditor.putInt("encouragementSetting",1);
                    Toast.makeText(SettingsActivity.this, "Encouragement Activated", Toast.LENGTH_SHORT).show();
                }else{
                    prefEditor.putInt("encouragementSetting",0);
                }
                prefEditor.commit();
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    prefEditor.putInt("reminderSetting", 1);
                    createTimePickerDialog();

                }else{
                    prefEditor.putInt("reminderSetting", 0);
                    cancelReminderNotification();
                }
                prefEditor.commit();
            }
        });
    }

    //check  settings saved from the last change
    private void retrieveSettings() {
        if (settingsPref.getInt("voiceSetting", 1) == 1) {
            voiceSwitch.setChecked(true);
        } else {
            voiceSwitch.setChecked(false);
        }

        if (settingsPref.getInt("encouragementSetting", 1) == 1) {
            encouragementSwitch.setChecked(true);
        } else {
            encouragementSwitch.setChecked(false);
        }

        if (settingsPref.getInt("reminderSetting", 0) == 1) {
            reminderSwitch.setChecked(true);
        } else{
            reminderSwitch.setChecked(false);
        }
    }

    //Create and show timePicker
    private void createTimePickerDialog(){
        TimePickerDialog timePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                createReminderNotification();
            }
        }, 12, 0, false);
        timePicker.setTitle("Select Time");
        timePicker.show();

    }

    //create alarm for daily reminder notification and start
    private void createReminderNotification(){
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(SettingsActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
                , pendingIntent);
        Toast.makeText(this, "Reminder set for " + selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();
    }

    //cancel reminder notification and alarm
    private void cancelReminderNotification(){
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent sender =  PendingIntent.getBroadcast(SettingsActivity.this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(sender);


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Toast.makeText(SettingsActivity.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    /*button
    clicks
     */

    //Set the name that is said when tts reminds you to incrase speed
    public void nameButtonClicked(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setTitle("Change Name");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                prefEditor.putString("name",newName);
                prefEditor.commit();
                nameButton.setText(newName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

}
