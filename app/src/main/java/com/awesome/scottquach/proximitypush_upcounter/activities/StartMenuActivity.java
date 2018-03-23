package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesome.scottquach.proximitypush_upcounter.Constants;
import com.awesome.scottquach.proximitypush_upcounter.GoalPreferenceUtil;
import com.awesome.scottquach.proximitypush_upcounter.Instrumentation;
import com.awesome.scottquach.proximitypush_upcounter.LegacyDatabaseTransferer;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.database.DatabaseManager;
import com.awesome.scottquach.proximitypush_upcounter.database.SessionEntity;
import com.awesome.scottquach.proximitypush_upcounter.features.Statistics.StatisticsDatabase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StartMenuActivity extends Activity implements DatabaseManager.DatabaseCallback, StatisticsDatabase.StatisticsDatabaseCallback {

    private Button dailyUpButton, dailyDownButton, monthlyUpButton, monthlyDownButton;
    private TextView dailyGoalView, monthlyGoalView;
    private ProgressBar monthProgress, dayProgress;

    private int dailyGoalValue, monthlyGoalValue;

    private DatabaseManager database = new DatabaseManager(this);
    private StatisticsDatabase statsDatabase = new StatisticsDatabase(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        dailyGoalView = (TextView) findViewById(R.id.dayGoalTextView);
        monthlyGoalView = (TextView) findViewById(R.id.monthlyGoalTextView);

        dayProgress = (ProgressBar) findViewById(R.id.progressDay);
        monthProgress = (ProgressBar) findViewById(R.id.progressMonth);

        dailyGoalValue = GoalPreferenceUtil.getDailyGoal(this);
        dailyGoalView.setText(String.valueOf(dailyGoalValue));

        monthlyGoalValue = GoalPreferenceUtil.getMonthlyGoal(this);
        monthlyGoalView.setText(String.valueOf(monthlyGoalValue));

        dailyUpButton = (Button) findViewById(R.id.dailyUpButton);
        dailyDownButton = (Button) findViewById(R.id.dailyDownButton);

        monthlyUpButton = (Button) findViewById(R.id.monthlyUpButton);
        monthlyDownButton = (Button) findViewById(R.id.monthlyDownButton);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1876787092384518~2446206781");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences settingPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        if (!settingPref.getBoolean(Constants.LEGACY_DATABASE_TRANSFER, false)) {
            new LegacyDatabaseTransferer(this).transferData();
            settingPref.edit().putBoolean(Constants.LEGACY_DATABASE_TRANSFER, true).apply();
            Timber.d("legacy database called");
        } else {
            Timber.d("legacy datbase not called");
        }

        requestDataUpdate();

        //When button is held down, incrase faster using handler
        dailyUpButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null){
                            return true;
                        }
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 150);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null){
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        requestDataUpdate();
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    dailyGoalValue++;
                    GoalPreferenceUtil.setDailyGoal(StartMenuActivity.this, dailyGoalValue);
                    dailyGoalView.setText(String.valueOf(dailyGoalValue));
                    mHandler.postDelayed(this, 150);
                }
            };

        });

        //When button is held down, decrease faster using handler
        dailyDownButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 150);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        requestDataUpdate();
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    if(dailyGoalValue >=1) {
                        dailyGoalValue--;
                    }
                    GoalPreferenceUtil.setDailyGoal(StartMenuActivity.this, dailyGoalValue);
                    dailyGoalView.setText(String.valueOf(dailyGoalValue));
                    mHandler.postDelayed(this, 150);
                }
            };

        });

        monthlyUpButton.setOnClickListener((listener)-> {
            monthlyGoalValue++;
            GoalPreferenceUtil.setMonthlyGoal(this, monthlyGoalValue);
            monthlyGoalView.setText(String.valueOf(monthlyGoalValue));
            requestDataUpdate();
        });

        monthlyDownButton.setOnClickListener((listener) -> {
            if(monthlyGoalValue >=1) {
                monthlyGoalValue--;
            }
            GoalPreferenceUtil.setMonthlyGoal(this, monthlyGoalValue);
            monthlyGoalView.setText(String.valueOf(monthlyGoalValue));
            requestDataUpdate();
        });

        monthlyUpButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null){
                            return true;
                        }
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null){
                            return true;
                        }
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        requestDataUpdate();
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    monthlyGoalValue++;
                    GoalPreferenceUtil.setMonthlyGoal(StartMenuActivity.this, monthlyGoalValue);
                    monthlyGoalView.setText(String.valueOf(monthlyGoalValue));
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        monthlyDownButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 150);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        requestDataUpdate();
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    if(monthlyGoalValue >=1) {
                        monthlyGoalValue--;
                    }
                    GoalPreferenceUtil.setMonthlyGoal(StartMenuActivity.this, monthlyGoalValue);
                    monthlyGoalView.setText(String.valueOf(monthlyGoalValue));
                    mHandler.postDelayed(this, 150);
                }
            };
        });
    }

    /**
     * Requests that the databases load the nessesary data retaining to calculating user goal progress
     */
    private void requestDataUpdate() {
        database.loadSessions();
        statsDatabase.requestTotalDayPushups();

    }

    /**
     * Navigates to the tracker activity
     * @param view
     */
    public void startTrackingClicked (View view) {
        Intent openMainActivity = new Intent(this,TrackerActivity.class);
        startActivity(openMainActivity);
    }

    /**
     * Increases the goal value and updates the screen as well as updates the shared preference
     * @param view
     */
    public void upButtonClicked(View view) {
        dailyGoalValue++;
        GoalPreferenceUtil.setDailyGoal(this, dailyGoalValue);
        dailyGoalView.setText(String.valueOf(dailyGoalValue));
        requestDataUpdate();
    }

    /**
     * Decreases the gaol value and updates the screen as well as updates the shared preference
     * @param view
     */
    public void downButtonClicked(View view) {
        if(dailyGoalValue >=1) {
            dailyGoalValue--;
        }
        GoalPreferenceUtil.setDailyGoal(this, dailyGoalValue);
        dailyGoalView.setText(String.valueOf(dailyGoalValue));
        requestDataUpdate();
    }

    /**
     * Navigates to the settings page
     * @param view
     */
    public void openSettingsPage(View view) {
        Instrumentation.getInstance().track(Instrumentation.TrackEvents.OPENED_SETTINGS, Instrumentation.TrackValues.SUCCESS);
        Intent openSettings = new Intent(StartMenuActivity.this,SettingsActivity.class);
        startActivity(openSettings);
    }

    /**
     * Navigates to the logs page
     * @param view
     */
    public void logButtonClicked(View view) {
        Intent openSaves = new Intent(StartMenuActivity.this,SavesActivity.class);
        startActivity(openSaves);
    }

    /**
     * Navigates to the statistics page
     * @param view
     */
    public void statiticsButtonClicked(View view) {
        startActivity(new Intent(StartMenuActivity.this, StatisticsActivity.class));
    }

    private void updateMonthlyGoalProgress(int pushupsDone) {
        monthProgress.setMax(monthlyGoalValue);
        monthProgress.setProgress(pushupsDone);
    }

    private void updateDailyGoalProgress(int pushupsDone) {
        dayProgress.setMax(dailyGoalValue);
        dayProgress.setProgress(pushupsDone);
    }


    /**
     * Callback for when session data is loaded, used to compile the total pushups done by the
     * user this month
     *
     * @param data array of stored sessions
     */
    @Override
    public void onSessionDataLoaded(@Nullable SessionEntity[] data) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        DateTime currentDate = new DateTime();
        int currentMonth = currentDate.getMonthOfYear();
        int currentYear = currentDate.getYear();

        List<SessionEntity> monthsEntities = new ArrayList<>();

        for (SessionEntity entity : data) {
            String stringDate = entity.date;
            DateTime entityDate = formatter.parseDateTime(stringDate);

            if (entityDate.getYear() == currentYear && entityDate.getMonthOfYear() == currentMonth) {
                monthsEntities.add(entity);
            }
        }

        int monthlyTotal = 0;
        for (SessionEntity entity : monthsEntities) {
            monthlyTotal += entity.numberOfPushups;
        }

        Timber.d("monthlyTotal is " + monthlyTotal);
        updateMonthlyGoalProgress(monthlyTotal);
    }


    /**
     * statsDatabase callback that is used to determine how many pushups the user has done today
     * @param total pushups done today
     */
    @Override
    public void totalDayPushupsLoaded(int total) {
        updateDailyGoalProgress(total);
    }

    /*
    The following are unused callbacks
     */
    @Override
    public void dayHighScoreLoaded(int highscore) {

    }
    @Override
    public void totalPushupsLoaded(int total) {

    }

    @Override
    public void highScoreLoaded(int highscore) {

    }
    @Override
    public void timesGoalReached(int num) {

    }
    @Override
    public void timesGoalFailed(int num) {

    }

    @Override
    public void graphDataLoaded(LineGraphSeries<DataPoint> series) {

    }



}
