package com.awesome.scottquach.proximitypush_upcounter;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Scott Quach on 11/21/2017.
 */

public class LegacyDatabaseTransferer {

    private final Context context;
    private final SharedPreferences sharedPref;
    private final int sentinel;

    public LegacyDatabaseTransferer(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences("savedPushUpsFile1", MODE_PRIVATE);
        sentinel = sharedPref.getInt("sentinel", 0);
    }

    public void transferData() {
        Timber.d("transfer data called");
        Timber.d("sentinel is " + sentinel);
        ArrayList<String> saveData = new ArrayList<String>();
        for (int i = 0; i < sentinel; i++) {
            String temp = sharedPref.getString("save" + i, "No data available");
            Timber.d("here is a data item" +  temp);
            if (!temp.equals("No data available")) {
                saveData.add(temp);
            }
        }
        Timber.d(saveData.toString());

        for (String value : saveData) {
            SessionEntity entity = new SessionEntity();
            entity.numberOfPushups = Integer.valueOf(LegacyTextParcer.getNumberPushUps(value));
            entity.isGoalReached = LegacyTextParcer.isGoalReached(value);
            entity.date = LegacyTextParcer.getDate(value);
            Timber.d(entity.toString());
            new InsertData().execute(entity);
        }
    }

    private class InsertData extends AsyncTask<SessionEntity, Void, Void> {

        @Override
        protected Void doInBackground(SessionEntity... entities) {
            BaseApplication.getInstance().database.sessionDOA().insertSession(entities[0]);
            return null;
        }
    }
}
