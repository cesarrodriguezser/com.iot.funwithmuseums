package com.iot.funwithmuseums.database;

import android.content.Context;

import androidx.room.*;

import com.iot.funwithmuseums.Item;

import java.util.Date;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract MyDao myDao();
    private static AppDataBase db;

    public static synchronized AppDataBase getInstance(Context context){
        if (db == null){
            db = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class, "database.db")
                    .fallbackToDestructiveMigration().build();
        }
        return db;
    }
}
