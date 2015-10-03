package com.awesome.scottquach.proximitypush_upcounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Saves extends Activity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String newSaveString;
    ArrayList<String> saveData;

    ListView listView;

    private int sentinel = 0;
    private int numberOfSavedFiles = 0;

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        saveData = new ArrayList<String>();
        Set<String> set = new HashSet<String>();

        sharedPref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        editor = sharedPref.edit();

        sentinel = sharedPref.getInt("sentinel", 0);

        if(getIntent() != null){
            Intent intentExtras = getIntent();
            if(intentExtras != null) {
                retrieveDataToSave(intentExtras);

                if(sentinel == 0){

                    sentinel = 1;
                    editor.putInt("sentinel", 1);
                    editor.putString("save" + numberOfSavedFiles, newSaveString);
                    numberOfSavedFiles++;
                    editor.putInt("numberSaves", numberOfSavedFiles);
                    editor.apply();

                }else {


                    numberOfSavedFiles = sharedPref.getInt("numberSaves", 0);
                    editor.putString("save" + numberOfSavedFiles, newSaveString);
                    numberOfSavedFiles++;
                    editor.putInt("numberSaves",numberOfSavedFiles);
                    sentinel++;
                    editor.putInt("sentinel", sentinel);
                    editor.apply();

                }

                iterateThroughSharedPref();
                populateListView();
            }else{
                iterateThroughSharedPref();
                populateListView();
            }

        }else{
            iterateThroughSharedPref();
            populateListView();
        }




    }


    private void iterateThroughSharedPref(){
        for (int i = 0; i < sentinel; i++){
            String temp = sharedPref.getString("save"+i, "No data available");
            saveData.add(temp);
        }
    }




    private void retrieveDataToSave(Intent intent) {
        Bundle bundle = intent.getExtras();
        if(!bundle.isEmpty()){
            newSaveString = bundle.getString("newPushUpSave");
//            if(newSaveString == null);
//                newSaveString = "No Saved Data Currently Available";
        }
    }

    private void populateListView() {


        //adapter
        adapter = new ArrayAdapter<String>(this, R.layout.saved_items, saveData);

        listView = (ListView)findViewById(R.id.savedListview);
        listView.setAdapter(adapter);


    }




    public void homeButtonClicked(View view) {
        Intent openHome = new Intent(this, StartMenu.class);
        startActivity(openHome);
    }

    public void resetButtonClicked(View view) {
        new AlertDialog.Builder(Saves.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        sentinel = sharedPref.getInt("sentinel", 0);
                        editor.apply();

                        SharedPreferences mainActivitySharedPreference  = getSharedPreferences("formattingFile", MODE_PRIVATE);
                        SharedPreferences.Editor mainActivityEditor = mainActivitySharedPreference.edit();
                        mainActivityEditor.clear();
                        mainActivityEditor.apply();
                        adapter.clear();
                        Toast.makeText(Saves.this, "Data has been reset", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void openGraph(View view) {
        Intent openGraph = new Intent(Saves.this,Graph.class);
        startActivity(openGraph);
    }
}
