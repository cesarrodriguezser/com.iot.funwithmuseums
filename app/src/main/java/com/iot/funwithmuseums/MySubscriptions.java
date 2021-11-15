package com.iot.funwithmuseums;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iot.funwithmuseums.database.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MySubscriptions <observer> extends AppCompatActivity implements ItemViewHolder.ItemClickListener{


    private static List<Item> listOfItems = new ArrayList<>();

    private RecyclerView myRecycleView;
    private ItemAdapter myAdapter;

    ViewModel myViewmodel;
    Observer<List<Item>> observer;
    private Object Item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_subscriptions);


        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);
        myRecycleView = findViewById(R.id.recyclerView);
        myAdapter = new ItemAdapter(listOfItems,  this);
        myRecycleView.setAdapter(myAdapter);
        myRecycleView.setLayoutManager(new LinearLayoutManager(this));


        myViewmodel.getFavorites().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                listOfItems.clear();
                listOfItems.addAll(items);
                Collections.sort(listOfItems, Comparator.comparing(com.iot.funwithmuseums.Item::getDisplayText));
                myAdapter.notifyDataSetChanged();
                myRecycleView.invalidate();

            }

        });


    }


    @Override
    public void onItemClick(int position, View v) {
        Item nomorefav = listOfItems.get(position);
        nomorefav.setFavorite(false);
        Toast t = Toast.makeText(getApplicationContext(), nomorefav.getDisplayText() + " deleted from favorites",
                Toast.LENGTH_LONG);
        t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
        t.show();

        myViewmodel.UpdateItem(nomorefav);

    }
}
