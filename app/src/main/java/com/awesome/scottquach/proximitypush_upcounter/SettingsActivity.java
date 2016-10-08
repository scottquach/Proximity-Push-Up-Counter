package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Button nameButton;

    Switch voiceSwitch;

    SharedPreferences settingsPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameButton = (Button) findViewById(R.id.nameButton);

        voiceSwitch = (Switch)findViewById(R.id.voiceSwitch);


        settingsPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
        prefEditor = settingsPref.edit();

        nameButton.setText(settingsPref.getString("name","set name"));

        retrieveSettings();

        //listener
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prefEditor.putInt("voiceSetting",1);
                    Toast.makeText(SettingsActivity.this, "Voice Activated", Toast.LENGTH_SHORT).show();
                }else{
                    prefEditor.putInt("voiceSetting",0);
                }
                prefEditor.commit();
            }
        });
    }

    private void retrieveSettings(){
        if (settingsPref.getInt("voiceSetting",1) == 1){
            voiceSwitch.setChecked(true);
        }else{
            voiceSwitch.setChecked(false);
        }
    }


    /*button
    clicks
     */

    public void nameButtonClicked(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setTitle("Change Name");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editText.getText().toString();
                prefEditor.putString("name",newName);
                prefEditor.commit();
                nameButton.setText(newName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

}
