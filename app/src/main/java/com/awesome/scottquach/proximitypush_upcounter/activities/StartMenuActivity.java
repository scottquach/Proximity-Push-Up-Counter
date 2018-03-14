package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.awesome.scottquach.proximitypush_upcounter.Constants;
import com.awesome.scottquach.proximitypush_upcounter.Instrumentation;
import com.awesome.scottquach.proximitypush_upcounter.LegacyDatabaseTransferer;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import timber.log.Timber;

public class StartMenuActivity extends Activity {

    private Button upButton;
    private Button downButton;
    private TextView goalTextView;

    private int goalValue;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);


        goalTextView = (TextView) findViewById(R.id.goalTextView);

        sharedPref = getSharedPreferences("goalFile", MODE_PRIVATE);
        editor = sharedPref.edit();

        goalValue = sharedPref.getInt("goalValue", 0);
        goalTextView.setText(String.valueOf(goalValue));

        upButton = (Button) findViewById(R.id.upButton);
        downButton = (Button) findViewById(R.id.downButton);

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

        //When button is held down, incrase faster using handler
        upButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
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
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    goalValue++;
                    editor.putInt("goalValue", goalValue);
                    editor.apply();
                    goalTextView.setText(String.valueOf(goalValue));
                    mHandler.postDelayed(this, 100);
                }
            };

        });

        //When button is held down, decrease faster using handler
        downButton.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    if(goalValue >=1) {
                        goalValue--;
                    }
                    editor.putInt("goalValue", goalValue);
                    editor.apply();
                    goalTextView.setText(String.valueOf(goalValue));
                    mHandler.postDelayed(this, 100);
                }
            };

        });
    }

    /*button
    clicks
     */


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
        goalValue++;
        editor.putInt("goalValue", goalValue);
        editor.apply();
        goalTextView.setText(String.valueOf(goalValue));
    }

    /**
     * Decreases the gaol value and updates the screen as well as updates the shared preference
     * @param view
     */
    public void downButtonClicked(View view) {
        if(goalValue >=1) {
            goalValue--;
        }
        editor.putInt("goalValue", goalValue);
        editor.apply();
        goalTextView.setText(String.valueOf(goalValue));
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
}
