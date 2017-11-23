package com.awesome.scottquach.proximitypush_upcounter;

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

    @Delete()
    public void resetTable(SessionEntity[] entities);

}
