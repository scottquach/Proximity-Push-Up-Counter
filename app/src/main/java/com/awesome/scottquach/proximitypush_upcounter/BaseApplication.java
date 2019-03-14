package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Application;
import androidx.room.Room;
import android.content.SharedPreferences;

import com.awesome.scottquach.proximitypush_upcounter.database.AppDatabase;
import com.awesome.scottquach.proximitypush_upcounter.jobs.JobCreatorUtil;
import com.awesome.scottquach.proximitypush_upcounter.jobs.ReminderJob;
import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Scott Quach on 9/4/2017.
 */

public class BaseApplication extends Application {

    private static BaseApplication instance = null;
    public AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Instrumentation.getInstance().init(this);
        Timber.plant(new MyDebugTree());

        if (instance == null) {
            instance = this;
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1876787092384518~2446206781");

        JobManager.create(this).addJobCreator(new JobCreatorUtil());
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "sessions-database").build();

        SharedPreferences settingsPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        if (settingsPref.getInt("reminderSetting", 1) == 1) {
//            ReminderJob.cancelJob();
            if (JobManager.instance().getAllJobRequests().isEmpty()) {
                ReminderJob.scheduleJob(settingsPref.getInt("reminder_hour", 7), settingsPref.getInt("reminder_minute", 0));
            }
        }
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    /**
     * https://stackoverflow.com/questions/38689399/log-method-name-and-line-number-in-timber
     */
    public class MyDebugTree extends Timber.DebugTree {
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return String.format("[L:%s] [M:%s] [C:%s]",
                    element.getLineNumber(),
                    element.getMethodName(),
                    super.createStackElementTag(element));
        }
    }
}
