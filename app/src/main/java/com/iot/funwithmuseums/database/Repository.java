package com.iot.funwithmuseums.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.iot.funwithmuseums.Item;

import java.util.List;
import java.util.concurrent.Executors;

public class Repository {
    private MyDao myDao;


    public Repository(Application app){
        AppDataBase db = AppDataBase.getInstance(app);
        myDao = db.myDao();
    }
    public void InsertItem (Item item){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                myDao.insertItem(item);
            }
        });
    }
    public void UpdateItem (Item item){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                myDao.UpdateItem(item);
            }
        });
    }
    public LiveData<List<Item>> getAllItems(){
        return myDao.getAllitems();
    }
    public LiveData <List<Item>> getFavorites(){
        return myDao.getFavorites(true);
    }
    public LiveData<Item> getByName(String name){
        return myDao.getbyName(name);
    }
    public LiveData<Item> getByID(int id){
        return myDao.getbyId(id);
    }
}
