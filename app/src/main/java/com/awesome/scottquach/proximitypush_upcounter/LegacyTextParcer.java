package com.awesome.scottquach.proximitypush_upcounter;

import android.util.Log;

import timber.log.Timber;

/**
 * Temporary class to handle parsing data that was stored in the old String format
 */

public class LegacyTextParcer {

    public static String getDate(String data) {
        String[] words = data.split(" ");
        return words[2].trim();
    }

    public static String getNumberPushUps(String data) {
        String[] words = data.split(" ");
        Timber.d(data);
        Log.d("data", data);
        return words[7].trim();
    }

    public static boolean isGoalReached(String data) {
        String[] words = data.split(" ");
        return words[words.length-1].equals("REACHED");
    }
}
