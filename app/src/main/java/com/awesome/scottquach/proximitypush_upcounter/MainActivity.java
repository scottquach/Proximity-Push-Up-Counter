package com.awesome.scottquach.proximitypush_upcounter;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    SharedPreferences goalSharedPref;
    SharedPreferences.Editor goalEditor;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1876787092384518~2446206781");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        c = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());

        vibrateSwitch = (Switch) findViewById(R.id.vibrateSwitch);
        soundSwitch = (Switch) findViewById(R.id.soundSwitch);

        sharedPref = getSharedPreferences("formattingFile", MODE_PRIVATE);
        editor = sharedPref.edit();

        goalSharedPref = getSharedPreferences("goalFile", MODE_PRIVATE);
        goalValue = goalSharedPref.getInt("goalValue", 0);

        player=MediaPlayer.create(MainActivity.this,R.raw.beep);

        countDisplay = (TextView)findViewById(R.id.countDisplay);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        sentinel = sharedPref.getInt("formattingSentinel", 0);
        if(sentinel == 0 ){
            editor.putInt("formattingSentinel",1);
            editor.apply();
        }else{
            numberOfSaves = sharedPref.getInt("numberSaves", 1);
        }

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });



    }






    @Override
    protected void onPause() {
        super.onPause();

        if (this.isFinishing()){ //basically BACK was pressed from this activity
            if(player != null){
                player.stop();
            }
//            player = null;
            proximitySensor = null;
            sm = null;
            if(tts !=null){
                tts.stop();
                tts.shutdown();
            }
        }else{
            if(player != null){
                player.stop();

            }
//                player = null;
            proximitySensor = null;
            sm = null;
            if(tts !=null){
                tts.stop();
                tts.shutdown();
            }

        }





    }

    @Override
    public void onSensorChanged(SensorEvent event) {

            for (int i = 0; i<1;i++){
                if(event.values[0] <= 3.0f) {

                    if (player != null && proximitySensor != null) {
                        numberOfPushUps++;
                        if (numberOfPushUps == goalValue) {
                            tts.speak("Goal Reached", TextToSpeech.QUEUE_FLUSH, null);
                        }else {
                            if (soundSwitch.isChecked()) {
                                player.start();
                            }

                        }


                        if(vibrateSwitch.isChecked() && proximitySensor != null){
                            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(80);
                        }
                        countDisplay.setText(String.valueOf(numberOfPushUps));
                    }else{
                        player=MediaPlayer.create(MainActivity.this,R.raw.beep);
                    }
                }

            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void countUpButtonPressed(View view) {
        numberOfPushUps++;
        countDisplay.setText(String.valueOf(numberOfPushUps));

        if(vibrateSwitch.isChecked() && proximitySensor != null){
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(80);
        }

        if(player != null && proximitySensor != null && soundSwitch.isChecked()) {
            if (numberOfPushUps == goalValue) {
                tts.speak("Goal Reached", TextToSpeech.QUEUE_FLUSH, null);

            }else {
                player.start();
            }
        }else{
            player=MediaPlayer.create(MainActivity.this,R.raw.beep);
        }

//        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
    }

    public void savedButtonClicked(View view) {


        if (numberOfPushUps >= goalValue) {
            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups, GOAL REACHED";
        }else{
            savePushUpFile = numberOfSaves + "." + " " + "On " + formattedDate + " you did : " + numberOfPushUps + " Push-Ups";
        }


        editor.putInt(String.valueOf(numberOfSaves),numberOfPushUps);
        editor.apply();





        Toast.makeText(this, savePushUpFile, Toast.LENGTH_SHORT).show();

        numberOfSaves++;
        editor.putInt("numberSaves", numberOfSaves);
        editor.apply();

        player.stop();
        proximitySensor = null;
        player = null;
        sm = null;

        Intent openSaves = new Intent(this, Saves.class);
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
