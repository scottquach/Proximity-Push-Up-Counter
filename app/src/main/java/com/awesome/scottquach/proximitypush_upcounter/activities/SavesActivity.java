package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.scottquach.proximitypush_upcounter.Instrumentation;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.RecyclerSavesAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class SavesActivity extends Activity {

    TextView highscoreView;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String newSaveString;
    List<String> saveData;

    RecyclerView recyclerView;
    RecyclerSavesAdapter adapter;

    private int sentinel = 0;
    private int numberOfSavedFiles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        Set<String> set = new HashSet<String>();

        sharedPref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        editor = sharedPref.edit();

        highscoreView = (TextView) findViewById(R.id.highscoreView);
        setHighscoreView();

        sentinel = sharedPref.getInt("sentinel", 0);

        if (getIntent() != null) {
            Intent intentExtras = getIntent();
            if (intentExtras != null) {
                retrieveDataToSave(intentExtras);
                if (sentinel == 0) {
                    sentinel = 1;
                    editor.putInt("sentinel", 1);
                    editor.putString("save" + numberOfSavedFiles, newSaveString);
                    numberOfSavedFiles++;
                    editor.putInt("numberSaves", numberOfSavedFiles);
                    editor.apply();
                } else {
                    numberOfSavedFiles = sharedPref.getInt("numberSaves", 0);
                    editor.putString("save" + numberOfSavedFiles, newSaveString);
                    numberOfSavedFiles++;
                    editor.putInt("numberSaves", numberOfSavedFiles);
                    sentinel++;
                    editor.putInt("sentinel", sentinel);
                    editor.apply();
                }
                iterateThroughSharedPref();
                populateListView();
            } else {
                iterateThroughSharedPref();
                populateListView();
            }
        } else {
            iterateThroughSharedPref();
            populateListView();
        }
    }

    //set highscoreView with current highscore
    private void setHighscoreView() {
        int highscore = sharedPref.getInt("highscore", 1);
        highscoreView.setText("Highscore: " + String.valueOf(highscore));

    }

    //Retrieve data through sharedPref
    private void iterateThroughSharedPref() {
        saveData = new ArrayList<>();
        for (int i = 0; i < sentinel; i++) {
            String temp = sharedPref.getString("save" + i, "No data available");
            Timber.d(temp);
            if (!temp.equals("No data available")) {
                saveData.add(temp);
            }
        }
    }

    //Retrieve data from current session
    private void retrieveDataToSave(Intent intent) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (!bundle.isEmpty()) {
                newSaveString = bundle.getString("newPushUpSave");
//            if(newSaveString == null);
//                newSaveString = "No Saved Data Currently Available";
            }
        }
    }

    private void populateListView() {
        recyclerView = (RecyclerView) findViewById(R.id.savedRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerSavesAdapter(this, saveData);
        recyclerView.setAdapter(adapter);
    }

/*On Button
Cicks
 */


    public void homeButtonClicked(View view) {
        Intent openHome = new Intent(this, StartMenuActivity.class);
        startActivity(openHome);
    }

    public void resetButtonClicked(View view) {
        new AlertDialog.Builder(SavesActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        sentinel = sharedPref.getInt("sentinel", 0);
                        editor.apply();

                        SharedPreferences mainActivitySharedPreference = getSharedPreferences("formattingFile", MODE_PRIVATE);
                        SharedPreferences.Editor mainActivityEditor = mainActivitySharedPreference.edit();
                        mainActivityEditor.clear();
                        mainActivityEditor.apply();
                        adapter.resetData();
                        adapter.notifyDataSetChanged();
                        highscoreView.setText("Highscore: ");
                        Instrumentation.getInstance().track(Instrumentation.TrackEvents.RESET_SAVES, Instrumentation.TrackValues.SUCCESS);
                        Toast.makeText(SavesActivity.this, "Data has been reset", Toast.LENGTH_SHORT).show();
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

//    public void openGraph(View view) {
//        Intent openGraph = new Intent(SavesActivity.this,Graph.class);
//        startActivity(openGraph);
//    }
//
//    public void graphButtonClicked(View view) {
//        Intent openGraph = new Intent(SavesActivity.this,Graph.class);
//        startActivity(openGraph);
//    }
}
