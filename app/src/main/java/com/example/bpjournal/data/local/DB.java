package com.example.bpjournal.data.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = RecordEntity.class, version = 1, exportSchema = false)
public abstract class DB extends RoomDatabase {
    private static DB database;
    private static String databaseName = "BloodPressure";
    public abstract BPJournalDAO mainDAO();


    public synchronized static DB getInstance(Context context){
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(),
                            DB.class, databaseName)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }


}
