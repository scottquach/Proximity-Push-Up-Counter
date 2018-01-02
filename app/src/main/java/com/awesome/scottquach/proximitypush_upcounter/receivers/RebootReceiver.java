package com.awesome.scottquach.proximitypush_upcounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.awesome.scottquach.proximitypush_upcounter.jobs.ReminderJob;
import com.evernote.android.job.JobManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Scott Quach on 9/20/2017.
 */

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settingsPref = context.getSharedPreferences("settingsFile", MODE_PRIVATE);
        if (settingsPref.getInt("reminderSetting", 1) == 1) {
//            ReminderJob.cancelJob();
            if (JobManager.instance().getAllJobRequests().isEmpty()) {
                ReminderJob.scheduleJob(settingsPref.getInt("reminder_hour", 7), settingsPref.getInt("reminder_minute", 0));
            }
        }
    }
}
