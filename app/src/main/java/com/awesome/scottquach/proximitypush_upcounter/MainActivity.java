package com.awesome.scottquach.proximitypush_upcounter;

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
import android.app.Activity;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends Activity implements SensorEventListener {


    private TextView countDisplay;
    private Switch vibrateSwitch;
    private Switch soundSwitch;

    private int numberOfPushUps = 0;
    private int numberOfSaves = 1;
    private int sentinel;
    private int goalValue;

    private Vibrator V;

    Calendar c;

    MediaPlayer player;

    SimpleDateFormat df;

    String formattedDate;
    String savePushUpFile;

    SensorManager sm;
    Sensor proximitySensor;

    SharedPreferences savePref;
    SharedPreferences.Editor saveEditor;

    SharedPreferences settingPref;
    SharedPreferences.Editor prefEditor;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    SharedPreferences goalSharedPref;
    SharedPreferences.Editor goalEditor;

    TextToSpeech tts;

    private DateTime startTime;
    private DateTime endTime;
    private boolean isTrackingDuration = false;
    private int maxDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JodaTimeAndroid.init(this);


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1876787092384518~2446206781");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Initialization
        c = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());

        vibrateSwitch = (Switch) findViewById(R.id.vibrateSwitch);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);

        savePref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        saveEditor = savePref.edit();

        sharedPref = getSharedPreferences("formattingFile", MODE_PRIVATE);
        editor = sharedPref.edit();

        settingPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        prefEditor = settingPref.edit();

        goalSharedPref = getSharedPreferences("goalFile", MODE_PRIVATE);
        goalValue = goalSharedPref.getInt("goalValue", 0);

        player = MediaPlayer.create(MainActivity.this, R.raw.beep);

        countDisplay = (TextView) findViewById(R.id.countDisplay);

        maxDuration = settingPref.getInt("maxDuration", 3100);

        //configure proximity sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        //configure TTS
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }


                //initial launch file
                sentinel = sharedPref.getInt("formattingSentinel", 0);
                if (sentinel == 0) {
                    editor.putInt("formattingSentinel", 1);
                    editor.apply();
                } else {
                    numberOfSaves = sharedPref.getInt("numberSaves", 1);
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
            isTrackingDuration = false;
            endTime = new DateTime();
            Duration dur = new Duration(startTime, endTime);

            long milliseconds = dur.getMillis();

            if (milliseconds >= maxDuration) {
                tts.speak("Your Slowing down, keep it up", TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            isTrackingDuration = true;
            startTime = new DateTime();
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
                            String name = settingPref.getString("name", "set name");

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
                    player = MediaPlayer.create(MainActivity.this, R.raw.beep);
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
            player = MediaPlayer.create(MainActivity.this, R.raw.beep);
        }
    }

    public void savedButtonClicked(View view) {

        //Format text to be saved
        if (numberOfPushUps >= goalValue) {
            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups, GOAL REACHED";
        } else {
            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups";
        }

        //Save number of push-ups done that session
        editor.putInt(String.valueOf(numberOfSaves), numberOfPushUps);
        editor.apply();

        for (int i = 1; i < numberOfSaves; i++) {
            int j = sharedPref.getInt(String.valueOf(i), 0);
            if (j > getHighscore()) {
                int highscore = sharedPref.getInt(String.valueOf(i), 0);
                saveEditor.putInt("highscore", highscore);
                saveEditor.commit();
            }
        }


        Toast.makeText(this, savePushUpFile, Toast.LENGTH_SHORT).show();

        numberOfSaves++;
        editor.putInt("numberSaves", numberOfSaves);
        editor.apply();

        if (player != null) player.stop();
        proximitySensor = null;
        player = null;
        sm = null;

        Intent openSaves = new Intent(this, SavesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("newPushUpSave", savePushUpFile);
        openSaves.putExtras(bundle);
        startActivity(openSaves);
    }

    public void refreshButtonPressed(View view) {
        numberOfPushUps = 0;
        countDisplay.setText(String.valueOf(numberOfPushUps));
    }
}
