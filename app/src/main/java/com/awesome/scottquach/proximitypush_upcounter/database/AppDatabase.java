package com.awesome.scottquach.proximitypush_upcounter.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by Scott Quach on 11/21/2017.
 */
@Database(version = 1, entities = {SessionEntity.class}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    abstract public SessionDOA sessionDOA();

}
