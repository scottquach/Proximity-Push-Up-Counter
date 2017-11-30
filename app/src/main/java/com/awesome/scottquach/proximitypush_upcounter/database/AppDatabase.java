package com.awesome.scottquach.proximitypush_upcounter.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Scott Quach on 11/21/2017.
 */
@Database(version = 1, entities = {SessionEntity.class})
public abstract class AppDatabase extends RoomDatabase{

    abstract public SessionDOA sessionDOA();

}
