package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingScreen extends Activity {

    int i;
    Timer t;

    //test comment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);





//        waitToLoad();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(LoadingScreen.this, StartMenu.class);
                LoadingScreen.this.startActivity(mainIntent);
                LoadingScreen.this.finish();
            }
        }, 4000);
    }

    private void waitToLoad(){
       t = new Timer();


        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        i++;
                        if (i>3){

                            t.cancel();
                            Intent openMenu = new Intent(LoadingScreen.this, StartMenu.class);
                            startActivity(openMenu);

                        }
                    }
                });

            }
        }, 0, 1000);
    }


}
