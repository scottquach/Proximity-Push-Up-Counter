package com.awesome.scottquach.proximitypush_upcounter.features.Statistics;

/**
 * Created by Scott Quach on 11/24/2017.
 */

interface StatisticsContract {

    interface View {
        void setSessionHighScore(int highscore);
        void setDayHighScore(int highscore);
        void setTotalPushups(int totalPushups);
        void setTimesGoalReached(int times);
        void setTimesGoalFailed(int times);
    }
}
