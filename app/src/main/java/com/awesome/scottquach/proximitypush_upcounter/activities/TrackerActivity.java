package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.scottquach.proximitypush_upcounter.GoalPreferenceUtil;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.database.DatabaseManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;


public class TrackerActivity extends AppCompatActivity implements SensorEventListener {

    private TextView countDisplay;
    private Switch vibrateSwitch;
    private Switch soundSwitch;

    private int numberOfPushUps = 0;
    private int goalValue;

    private MediaPlayer player;

    private SensorManager sm;
    private Sensor proximitySensor;

    private SharedPreferences savePref, settingPref;
    private SharedPreferences.Editor saveEditor;

    TextToSpeech tts;

    private DateTime startTime;
    private DateTime endTime;
    private boolean isTrackingDuration = false;
    private int intervalAverage = 0;

    private DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        database = new DatabaseManager(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1876787092384518~2446206781");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        vibrateSwitch = (Switch) findViewById(R.id.vibrateSwitch);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);
        countDisplay = (TextView) findViewById(R.id.countDisplay);

        savePref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        saveEditor = savePref.edit();
        settingPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        goalValue = GoalPreferenceUtil.getDailyGoal(this);

        player = MediaPlayer.create(TrackerActivity.this, R.raw.beep);

        //configure proximity sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sm == null) {
            Toast.makeText(this, "Proximity sensor not available on device", Toast.LENGTH_LONG).show();
        }

        proximitySensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        //configure TTS
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //retrieve highscore
    private int getHighscore() {
        int highscore = savePref.getInt("highscore", 1);
        return highscore;
    }

    //track duration between each push-up. tts reminder when slowing down
    private void trackEncouragement() {
        if (isTrackingDuration) {
//            isTrackingDuration = false;
            endTime = new DateTime();
            Duration dur = new Duration(startTime, endTime);

            long milliseconds = dur.getMillis();
            calculateAverageInterval((int) milliseconds);
            Timber.d("interval was " + milliseconds);
            if (numberOfPushUps > 3 && milliseconds >= (intervalAverage + 600)) {
                tts.speak("Your Slowing down, keep it up", TextToSpeech.QUEUE_FLUSH, null);
            }

            isTrackingDuration = true;
            startTime = new DateTime();
        } else {
            isTrackingDuration = true;
            startTime = new DateTime();
        }
    }

    private void calculateAverageInterval(int interval) {
        if (numberOfPushUps == 1) {
            intervalAverage = interval;
        } else {
            intervalAverage = ((intervalAverage + interval) / 2);
            Timber.d("Average interval is " + intervalAverage);
        }

    }

    //Canel media player
    @Override
    protected void onPause() {
        super.onPause();

        if (this.isFinishing()) {
            if (player != null) {
                player.stop();
            }
            proximitySensor = null;
            sm = null;
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } else {
            if (player != null) {
                player.stop();

            }
            proximitySensor = null;
            sm = null;
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Proximity sensor to detect push-ups
    @Override
    public void onSensorChanged(SensorEvent event) {

        for (int i = 0; i < 1; i++) {
            if (event.values[0] <= 3.0f) {

                if (player != null && proximitySensor != null) {
                    numberOfPushUps++;
                    if (settingPref.getInt("encouragementSetting", 1) == 1) {
                        trackEncouragement();
                    }
                    //check highscore
                    if (numberOfPushUps > getHighscore()) {
                        saveEditor.putInt("highscore", numberOfPushUps);
                        saveEditor.commit();
                    }
                    //determine if goal has been reached
                    if (numberOfPushUps == goalValue) {
                        int check = settingPref.getInt("voiceSetting", 1);
                        if (check == 1) {
                            String name = settingPref.getString("name", "");

                            tts.speak("Goal Reached nice job " + name, TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            if (player != null) player.start();
                        }
                    } else {
                        if (soundSwitch.isChecked()) {
                            if (player != null) player.start();
                        }
                    }

                    if (vibrateSwitch.isChecked() && proximitySensor != null) {
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);
                    }
                    countDisplay.setText(String.valueOf(numberOfPushUps));
                } else {
                    player = MediaPlayer.create(TrackerActivity.this, R.raw.beep);
                }
            }
        }
    }


/*
Button Clicks
 */

    public void countUpButtonPressed(View view) {
        if (settingPref.getInt("encouragementSetting", 1) == 1) {
            trackEncouragement();
        }
        numberOfPushUps++;
        countDisplay.setText(String.valueOf(numberOfPushUps));

        //check highscore
        if (numberOfPushUps > getHighscore()) {
            saveEditor.putInt("highscore", numberOfPushUps);
            saveEditor.commit();
        }

        if (vibrateSwitch.isChecked() && proximitySensor != null) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(80);
        }

        if (player != null && proximitySensor != null && soundSwitch.isChecked()) {
            if (numberOfPushUps == goalValue) {
                int check = settingPref.getInt("voiceSetting", 1);
                if (check == 1) {
                    String name = settingPref.getString("name", "set name");
                    tts.speak("Goal Reached nice job " + name, TextToSpeech.QUEUE_FLUSH, null);

                } else {
                    player.start();
                }

            } else {
                player.start();
            }
        } else {
            player = MediaPlayer.create(TrackerActivity.this, R.raw.beep);
        }
    }

    public void savedButtonClicked(View view) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c.getTime());

        if (numberOfPushUps >= goalValue) {
//            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups, GOAL REACHED";
            database.insertSession(numberOfPushUps, true, formattedDate);
        } else {
//            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups";
            database.insertSession(numberOfPushUps, false, formattedDate);
        }

        if (player != null) player.stop();
        proximitySensor = null;
        player = null;
        sm = null;

        startActivity(new Intent(TrackerActivity.this, SavesActivity.class));
    }

    public void refreshButtonPressed(View view) {
        numberOfPushUps = 0;
        countDisplay.setText(String.valueOf(numberOfPushUps));
    }

    public void decreaseButtonClicked(View view) {
        if (numberOfPushUps > 0) {
            numberOfPushUps--;
            countDisplay.setText(String.valueOf(numberOfPushUps));
        } else Toast.makeText(this, "Cannot decrease anymore", Toast.LENGTH_SHORT).show();
    }
}
