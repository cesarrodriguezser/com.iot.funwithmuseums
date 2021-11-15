package com.iot.funwithmuseums;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.iot.funwithmuseums.database.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewSubscription<observer> extends AppCompatActivity implements ItemViewHolder.ItemClickListener  {

    private static List<Item> listOfItems = new ArrayList<>();

    private String logTag; // to clearly identify logs
    private RecyclerView myRecycleView;
    private ItemAdapter myAdapter;

    ExecutorService es;
    TextView tvLoadContent;

    ViewModel myViewmodel;
    Observer<List<Item>> observer;
    private Object Item;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_subscription);


        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);

        listOfItems.clear();

        // Build the logTag with the Thread and Class names:

        myRecycleView = findViewById(R.id.recyclerView);

        tvLoadContent = findViewById(R.id.tvLoadContent);
        myAdapter = new ItemAdapter(listOfItems, this);
        myRecycleView.setAdapter(myAdapter);
        myRecycleView.setLayoutManager(new LinearLayoutManager(this));
;


        myViewmodel.getAllItems().observe(this, new Observer<List<Item>>() {
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


    public void onItemClick ( int position, View v){

        Item fav = listOfItems.get(position);
        fav.setFavorite(true);
        Toast t = Toast.makeText(getApplicationContext(), fav.getDisplayText() + " added to favorites",
                Toast.LENGTH_LONG);
        t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
        t.show();

        myViewmodel.UpdateItem(fav);
    }

}