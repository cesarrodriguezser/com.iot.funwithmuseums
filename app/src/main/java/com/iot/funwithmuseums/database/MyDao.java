package com.iot.funwithmuseums.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.iot.funwithmuseums.Item;

import java.util.List;

@Dao
public interface MyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertItem(Item item);
    @Update
    public void UpdateItem(Item item);
    @Query("SELECT * FROM items")
    public LiveData<List<Item>> getAllitems();
    @Query("SELECT * FROM items WHERE DisplayText = :displaytext")
    public LiveData<Item> getbyName(String displaytext);
    @Query("SELECT * FROM items WHERE id = :id ")
    public LiveData<Item> getbyId(int id);
    @Query("SELECT * FROM items WHERE favorite = :favorite")
    public LiveData<List<Item>> getFavorites(boolean favorite);
}
