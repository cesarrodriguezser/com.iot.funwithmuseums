package com.iot.funwithmuseums.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.iot.funwithmuseums.Item;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;

    public ViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }
    public void InsertItem (Item item){
        repository.InsertItem(item);
    }
    public void UpdateItem (Item item){
        repository.UpdateItem(item);
    }
    public LiveData<List<Item>> getAllItems(){
        return repository.getAllItems();
    }
    public LiveData <List<Item>> getFavorites(){
        return repository.getFavorites();
    }
    public LiveData<Item> getByName(String name){
        return repository.getByName(name);
    }
    public LiveData<Item> getByID(int id){
        return repository.getByID(id);
    }
}
