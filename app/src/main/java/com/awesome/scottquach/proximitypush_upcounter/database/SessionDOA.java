package com.awesome.scottquach.proximitypush_upcounter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

/**
 * Created by Scott Quach on 11/21/2017.
 */

@Dao
public interface SessionDOA {

    @Insert
    public void insertSession(SessionEntity entity);

    @Query("SELECT * FROM sessions")
    public SessionEntity[] querySessions();

    @Query("SELECT number_of_pushups FROM sessions")
    public int[] querySessionsPushups();

    @Query("SELECT * FROM sessions WHERE is_goal_reached = 1")
    public SessionEntity[] queryWhenGoalReached();

    @Query("SELECT * FROM sessions WHERE is_goal_reached = 0")
    public SessionEntity[] queryWhenGoalFailed();

    @Query("SELECT sum(number_of_pushups) FROM sessions GROUP BY date")
    public int[] queryDaySessionTotal();

    @Delete()
    public void resetTable(SessionEntity[] entities);

}
