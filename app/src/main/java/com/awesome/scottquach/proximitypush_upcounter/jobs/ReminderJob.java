package com.awesome.scottquach.proximitypush_upcounter.jobs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.awesome.scottquach.proximitypush_upcounter.Constants;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.activities.TrackerActivity;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Scott Quach on 11/23/2017.
 */

public class ReminderJob extends Job {

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("daily_reminder", "Daily Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().build());
            notificationManager.createNotificationChannel(channel);

            Intent openApp = new Intent(getContext(), TrackerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 10, openApp, PendingIntent.FLAG_CANCEL_CURRENT);
            //Retreive default notification sound
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //Create notification
            Notification mBuilder = new Notification.Builder(getContext(), "daily_reminder")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getContext().getString(R.string.notification_title))
                    .setContentText(getContext().getString(R.string.notification_description))
                    .setContentIntent(pendingIntent)
                    .setSound(sound)
                    .setAutoCancel(true)
                    .build();

            //Notify
            notificationManager.notify(101, mBuilder);

            SharedPreferences settingsPref = getContext().getSharedPreferences("settingsFile", MODE_PRIVATE);
            if (settingsPref.getInt("reminderSetting", 1) == 1) {
                ReminderJob.cancelJob();
                ReminderJob.scheduleJob(settingsPref.getInt("reminder_hour", 7), settingsPref.getInt("reminder_minute", 0));
            }

            return Result.SUCCESS;
        } else {


            //PendingIntent to open app when notification is clicked
            Intent openApp = new Intent(getContext(), TrackerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 10, openApp, PendingIntent.FLAG_CANCEL_CURRENT);
            //Retreive default notification sound
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //Create notification
            Notification mBuilder = new Notification.Builder(getContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getContext().getString(R.string.notification_title))
                    .setContentText(getContext().getString(R.string.notification_description))
                    .setContentIntent(pendingIntent)
                    .setSound(sound)
                    .setAutoCancel(true)
                    .build();

            //Notify
            notificationManager.notify(101, mBuilder);

            SharedPreferences settingsPref = getContext().getSharedPreferences("settingsFile", MODE_PRIVATE);
            if (settingsPref.getInt("reminderSetting", 1) == 1) {
                ReminderJob.cancelJob();
                ReminderJob.scheduleJob(settingsPref.getInt("reminder_hour", 7), settingsPref.getInt("reminder_minute", 0));
            }

            return Result.SUCCESS;
        }
    }

    public static int scheduleJob(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());
        currentTime.set(Calendar.MINUTE, 5);

        if (calendar.before(currentTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int startIn = (int) (calendar.getTimeInMillis() - currentTime.getTimeInMillis());

        if (startIn > 0) {
            int jobId = new JobRequest.Builder(Constants.REMINDER_JOB)
                    .setExact(startIn)
                    .build()
                    .schedule();
            return jobId;
        } else return -1;
    }

    public static void cancelJob() {
        JobManager.instance().cancelAllForTag(Constants.REMINDER_JOB);
    }
}
