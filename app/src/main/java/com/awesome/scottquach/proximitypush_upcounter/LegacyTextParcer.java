package com.awesome.scottquach.proximitypush_upcounter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static String convertDateFormat(String oldDate) {
        final String oldFormat = "yyyy-MM-dd";
        final String newFormat = "MM-dd-yyyy";

        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date date = null;
        try {
            date = sdf.parse(oldDate);
        } catch (ParseException e) {
            Timber.e(e, "converting date formats");
        }
        sdf.applyPattern(newFormat);
        return sdf.format(date);
    }
}
