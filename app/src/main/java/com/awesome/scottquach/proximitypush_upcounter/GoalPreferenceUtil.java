package com.awesome.scottquach.proximitypush_upcounter;

import android.content.Context;

/**
 * Created by Scott Quach on 3/21/2018.
 *
 * Childe of PreferenceUtil that manages storing and editing preferences that retain to the users
 * set goals (daily and monthly)
 */

public class GoalPreferenceUtil extends PreferenceUtil {

    public static final String FILE_KEY_GOAL = "goalFile";
    public static final String FILE_KEY_SETTINGS = "settingsFile";

    public static final String KEY_DAILY_GOAL = "goalValue";
    public static final String KEY_MONTHLY_GOAL = "key_monthly_goal";

    public static void setDailyGoal(Context context, int value) {
        getEditor(context, FILE_KEY_GOAL).putInt(KEY_DAILY_GOAL, value).apply();
    }

    public static void setMonthlyGoal(Context context, int value) {
        getEditor(context, FILE_KEY_GOAL).putInt(KEY_MONTHLY_GOAL, value).apply();
    }

    public static int getDailyGoal(Context context) {
        return getSharedPref(context, FILE_KEY_GOAL).getInt(KEY_DAILY_GOAL, 0);
    }

    public static int getMonthlyGoal(Context context) {
        return getSharedPref(context, FILE_KEY_GOAL).getInt(KEY_MONTHLY_GOAL, 0);
    }
}
