package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class StartMenu extends Activity {

    private Button upButton;
    private Button downButton;
    private TextView goalTextView;

    private int goalValue;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        goalTextView = (TextView) findViewById(R.id.goalTextView);

        sharedPref = getSharedPreferences("goalFile",MODE_PRIVATE);
        editor = sharedPref.edit();

        goalValue = sharedPref.getInt("goalValue", 0);
        goalTextView.setText(String.valueOf(goalValue));
    }



    public void startPushUpStarted(View view) {
        Intent openMainActivity = new Intent(this,MainActivity.class);
        startActivity(openMainActivity);
    }

    public void saveButtonClicked(View view) {
        Intent openSaves = new Intent(this, Saves.class);
        startActivity(openSaves);
    }

    public void upButtonClicked(View view) {
        goalValue++;
        editor.putInt("goalValue", goalValue);
        editor.apply();
        goalTextView.setText(String.valueOf(goalValue));
    }

    public void downButtonClicked(View view) {
        if(goalValue >=1) {
            goalValue--;
        }
        editor.putInt("goalValue", goalValue);
        editor.apply();
        goalTextView.setText(String.valueOf(goalValue));
    }
}
