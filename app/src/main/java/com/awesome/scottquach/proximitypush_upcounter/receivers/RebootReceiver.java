package com.awesome.scottquach.proximitypush_upcounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Scott Quach on 9/20/2017.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        SharedPreferences sharedPref = context.getSharedPreferences("settingsFile", MODE_PRIVATE);
//
//        if (sharedPref.getInt("reminderSetting", 0) == 1) {
//            int alarmHour = sharedPref.getInt("reminder_hour", 0);
//            int alarmMinute = sharedPref.getInt("reminder_minute", 0);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
//            calendar.set(Calendar.MINUTE, alarmMinute);
//
//            Intent alarmIntent = new Intent(context, NotificationReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY
//                    , pendingIntent);
//        }
    }
}
