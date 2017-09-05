package com.awesome.scottquach.proximitypush_upcounter;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import timber.log.Timber;

/**
 * Created by Scott Quach on 9/4/2017.
 */

public class Instrumentation {

    public static final Instrumentation instance = new Instrumentation();
    public static FirebaseAnalytics firebaseAnalytics;


    public Instrumentation() {

    }

    public static Instrumentation getInstance() {
        return instance;
    }

    public void init(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void track(String event, String value) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, value);
        firebaseAnalytics.logEvent(event, bundle);
        Timber.i("Firebase event was: event - " + event + " value " + value);
    }

    public class TrackEvents {
        final static String RESET_SAVES = "reset_saves";
        final static String OPENED_SETTINGS = "opened_settings";
        final static String SET_NAME = "set_name";
        final static String TOGGLE_VOICE_FEEDBACK = "toggle_voice_feedback";
        final static String TOGGLE_SPEED_UP_ENG = "toggle_speed_up_eng";
        final static String TOGGLE_DAILY_REMINDER = "toggle_daily_reminder";
    }

    public class TrackValues {
        final static String SUCCESS = "success";
        final static String FAILURE = "failure";
    }

}
