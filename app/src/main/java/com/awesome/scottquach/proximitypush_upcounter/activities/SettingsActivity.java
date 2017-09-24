package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.awesome.scottquach.proximitypush_upcounter.Instrumentation;
import com.awesome.scottquach.proximitypush_upcounter.receivers.NotificationReceiver;
import com.awesome.scottquach.proximitypush_upcounter.R;

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
                    Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_VOICE_FEEDBACK, Instrumentation.TrackValues.SUCCESS);
                    Toast.makeText(SettingsActivity.this, "Voice Activated", Toast.LENGTH_SHORT).show();
                }else{
                    prefEditor.putInt("voiceSetting",0);
                    Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_VOICE_FEEDBACK, Instrumentation.TrackValues.FAILURE);
                }
                prefEditor.commit();
            }
        });

        encouragementSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefEditor.putInt("encouragementSetting",1);
                    Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_SPEED_UP_ENG, Instrumentation.TrackValues.SUCCESS);
                    Toast.makeText(SettingsActivity.this, "Encouragement Activated", Toast.LENGTH_SHORT).show();
                }else{
                    prefEditor.putInt("encouragementSetting",0);
                    Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_SPEED_UP_ENG, Instrumentation.TrackValues.FAILURE);
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
                    Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_DAILY_REMINDER, Instrumentation.TrackValues.FAILURE);
                }
                prefEditor.commit();
            }
        });
    }

    /**
     * Restores saved settings
     */
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

    /**Create time picker for times to be used for a
     * daily reminder
     */
    private void createTimePickerDialog(){
        TimePickerDialog timePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                settingsPref.edit().putInt("reminder_hour", selectedHour).apply();
                settingsPref.edit().putInt("reminder_minute", selectedMinute).apply();
                createReminderNotification();
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.TOGGLE_DAILY_REMINDER, Instrumentation.TrackValues.SUCCESS);
            }
        }, 12, 0, false);
        timePicker.setTitle("Select Time");
        timePicker.setCancelable(false);
        timePicker.show();

    }

    /**
     * Uses time gathered from time picker and will create a daily repeating notification
     * reminder
     */
    private void createReminderNotification(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);

        Intent intent = new Intent(SettingsActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
                , pendingIntent);
        Toast.makeText(this, "Reminder set for " + selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();
    }

    /**
     * Cancel the alarm that triggers daily notifications
     */
    private void cancelReminderNotification(){
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent sender =  PendingIntent.getBroadcast(SettingsActivity.this, 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(sender);


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Toast.makeText(SettingsActivity.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    /**
     * user can set name to be used in TTS
     * @param view
     */
    public void nameButtonClicked(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setTitle(R.string.change_name);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                prefEditor.putString("name",newName);
                prefEditor.commit();
                nameButton.setText(newName);
                Instrumentation.getInstance().track(Instrumentation.TrackEvents.SET_NAME, Instrumentation.TrackValues.SUCCESS);
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
