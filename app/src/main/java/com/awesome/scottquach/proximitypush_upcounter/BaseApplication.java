package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.crashlytics.android.Crashlytics;

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

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "sessions-database").build();
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
