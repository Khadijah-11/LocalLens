package com.example.locallens.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.locallens.models.Signalement;

@Database(entities = {Signalement.class}, version = 1)
public abstract class MyDB extends RoomDatabase {

    private static MyDB instance;

    public abstract SignalementDao signalementDao();

    public static synchronized MyDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MyDB.class,
                            "locallens_db"
                    ).fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
