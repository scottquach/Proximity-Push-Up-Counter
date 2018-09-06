package com.awesome.scottquach.proximitypush_upcounter.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.scottquach.proximitypush_upcounter.Constants;
import com.awesome.scottquach.proximitypush_upcounter.database.DatabaseManager;
import com.awesome.scottquach.proximitypush_upcounter.Instrumentation;
import com.awesome.scottquach.proximitypush_upcounter.R;
import com.awesome.scottquach.proximitypush_upcounter.adapters.RecyclerSavesAdapter;
import com.awesome.scottquach.proximitypush_upcounter.database.SessionEntity;

import timber.log.Timber;

public class SavesActivity extends Activity implements DatabaseManager.DatabaseCallback{

    private TextView highscoreView;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private SessionEntity[] saveData;

    private RecyclerView recyclerView;
    private RecyclerSavesAdapter adapter;

    private DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);

        sharedPref = getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        editor = sharedPref.edit();

        highscoreView = (TextView) findViewById(R.id.highscoreView);
        setHighscoreView();

        recyclerView = (RecyclerView) findViewById(R.id.savedRecyclerView);

        database = new DatabaseManager(this);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Timber.d("An item was swiped");
                Toast.makeText(SavesActivity.this, "Session was deleted", Toast.LENGTH_SHORT).show();
                database.deleteSession(saveData[viewHolder.getAdapterPosition()]);
                loadData();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * Update the TextView with the users high score, defaults to 0
     */
    private void setHighscoreView() {
        int highscore = sharedPref.getInt("highscore", 0);
        highscoreView.setText("Highscore: " + String.valueOf(highscore));

    }

    /**
     * Calls to the database to load the sessions in order to be displayed, recycler will be updated
     * inside callback
     */
    private void loadData() {
        database.loadSessions();
    }

    /**
     * Takes in session data and updates the recycler
     * @param data
     */
    private void populateListView(SessionEntity[] data) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerSavesAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.scheduleLayoutAnimation();
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
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(DialogInterface dialog, int which) {
//                        sentinel = sharedPref.getInt("sentinel", 0);
                        editor.clear();
                        editor.apply();
                        database.resetSessionData(saveData);

                        adapter.resetData();
                        adapter.notifyDataSetChanged();

                        highscoreView.setText("Highscore: ");
                        SharedPreferences settingsPref = getSharedPreferences("settingsFile", MODE_PRIVATE);
                        settingsPref.edit().putBoolean(Constants.LEGACY_DATABASE_TRANSFER, false).apply();

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

    @Override
    public void onSessionDataLoaded(SessionEntity[] data) {
        Timber.d("Callback called");
        saveData = data;
        populateListView(data);
    }
}
