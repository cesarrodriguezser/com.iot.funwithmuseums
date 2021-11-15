package com.iot.funwithmuseums;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.iot.funwithmuseums.database.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ItemViewHolder.ItemClickListener{

    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT"; // to clearly identify logs
    private String logTag; // to clearly identify logs
    private static final String URL_MUSEUMS = "https://datos.madrid.es/portal/site/egob/menuitem.ac61933d6ee3c31cae77ae7784f1a5a0/?vgnextoid=00149033f2201410VgnVCM100000171f5a0aRCRD&format=json&file=0&filename=201132-0-museos&mgmtid=118f2fdbecc63410VgnVCM1000000b205a0aRCRD&preview=full";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static List<Item> listOfItems = new ArrayList<>();

    private RecyclerView myRecycleView;
    private ItemAdapter myAdapter;
    private FusedLocationProviderClient locationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    Button bmyMuseums;
    Button bChart;
    Button bNearMuseum;

    TextView nMuseum;
    TextView tvLoadContent;

    String string_result;
    String museumName = "";

    ArrayList<String> museumNames_ArrayList = new ArrayList<String>();

    ExecutorService es;

    LatLng currentLocation;

    String nearestMuseum;

    ViewModel myViewmodel;

    double distance = 1000000.0; //In km
    double calculatedDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myViewmodel = ViewModelProviders.of(this).get(ViewModel.class);

        listOfItems.clear();
        museumNames_ArrayList.clear();

        // Build the logTag with the Thread and Class names:
        logTag = LOGSLOADWEBCONTENT + ", Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        bNearMuseum = findViewById(R.id.subtitle);
        bNearMuseum.setEnabled(false);
        bmyMuseums = findViewById(R.id.bMyMuseums);
        bChart = findViewById(R.id.bChart);
        tvLoadContent = findViewById(R.id.tvLoadContent);

        myRecycleView = findViewById(R.id.recyclerView);
        myAdapter = new ItemAdapter(listOfItems, this);
        myRecycleView.setAdapter(myAdapter);
        myRecycleView.setLayoutManager(new LinearLayoutManager(this));

        myViewmodel.getAllItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                listOfItems.clear();
                listOfItems.addAll(items);
                Collections.sort(listOfItems, Comparator.comparing(Item::getDisplayText));
                myAdapter.notifyDataSetChanged();
                myRecycleView.invalidate();
            }
        });

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        tvLoadContent.setText("Loading Museums..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_MUSEUMS);
        es.execute(loadURLContents);

        // Callback for location permission request result
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        startLocationService();
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        bChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chartgo= new Intent(MainActivity.this, ChartActivity.class);
                startActivity(chartgo);
            }
        });
        bmyMuseums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mymuseumsgo= new Intent(MainActivity.this, MQTTActivity.class);
                startActivity(mymuseumsgo);
            }
        });
        getCurrentLocation();
    }

    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            super.handleMessage(msg);
            Log.d(logTag, "message received from background thread");
            if((string_result = msg.getData().getString("text")) != null) {
                //tvLoadContent.setText(string_result);
                SharedPreferences sharedPreferences = getSharedPreferences("JSON",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("file",string_result);
                editor.commit();
                parsingJSON(string_result);
            }
        }

    };

    public void goToNearestMuseum(View view){

        int position = 0;

        //Calculate the position in the listofitems array where the nearest museum is

        for(int i=0; i < listOfItems.size(); i++){

            if(listOfItems.get(i).getDisplayText().equals(nearestMuseum)){
                position = i;
            }
        }

        // Creating Intent For Navigating to Maps Activity (Explicit Intent)
        Intent i = new Intent(MainActivity.this, MapsActivity.class);

        Item museum = listOfItems.get(position);

        double calculatedDistance = getDistance(museum.getLocation());

        // Adding values to the intent to pass them to Maps Activity
        i.putExtra("museumName", String.valueOf(museum.getDisplayText()));
        i.putExtra("location", String.valueOf(museum.getLocation()));
        i.putExtra("distance", String.valueOf(calculatedDistance));
        i.putExtra("currentLocation", String.valueOf(currentLocation));

        // Once the intent is parametrized, start the Maps Activity:
        startActivity(i);

    }

    @Override
    public void onItemClick(int position, View v) {

        // Creating Intent For Navigating to Maps Activity (Explicit Intent)
        Intent i = new Intent(MainActivity.this, MapsActivity.class);

        Item museum = listOfItems.get(position);

        double calculatedDistance = getDistance(museum.getLocation());

        // Adding values to the intent to pass them to Maps Activity
        i.putExtra("museumName", String.valueOf(museum.getDisplayText()));
        i.putExtra("location", String.valueOf(museum.getLocation()));
        i.putExtra("distance", String.valueOf(calculatedDistance));
        i.putExtra("currentLocation", String.valueOf(currentLocation));

        // Once the intent is parametrized, start the Maps Activity:
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void parsingJSON(String parsing) {

        try {
            // get JSONObject from JSON file
            JSONObject json_obj = new JSONObject(parsing);
            // fetch JSONObject
            JSONArray graph = json_obj.getJSONArray("@graph");

            int nMuseums = graph.length();

            for(int i = 0; i < nMuseums; i++){

                JSONObject eachMuseum = graph.getJSONObject(i);
                LatLng location = null;
                try{
                    // get name of the museum
                    museumName = eachMuseum.getString("title");
                    museumNames_ArrayList.add(museumName);

                    JSONObject locationNode = eachMuseum.getJSONObject("location");

                    Item item = new Item();
                    item.setDisplayText(museumName);
                    item.setId(Integer.parseInt(eachMuseum.getString("id")));
                    item.setLocation(locationNode.getDouble("latitude"),
                            locationNode.getDouble("longitude"));

                    myViewmodel.InsertItem(item);

                    double calculatedDistance = getDistance(item.getLocation());

                    if(calculatedDistance < distance){
                        distance = calculatedDistance;
                        nearestMuseum = museumName;
                    }

                }catch (JSONException ignored){

                }
            }

            Collections.sort(listOfItems, Comparator.comparing(Item::getDisplayText));
            bNearMuseum.setEnabled(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvLoadContent.setText(""); // Inform the user by means of the TextView
        bNearMuseum.setText(nearestMuseum);

    }

    public void getCurrentLocation(){

        //Now we get the current location
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        // Request parameters
        com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                locationClient.removeLocationUpdates(this);
            }
        };

        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private double getDistance(LatLng location){

        double lat1, lon1, lat0, lon0, difLat, difLon, a, c;

        lat1 = location.latitude;
        lon1 = location.longitude;

        lat0 = currentLocation.latitude;
        lon0 = currentLocation.longitude;

        //Harversine's Formula to calculate the distance of 2 terrestrial points
        difLat = lat1 - lat0;
        difLat = Math.toRadians(difLat);
        difLon = lon1 - lon0;
        difLon = Math.toRadians(difLon);

        a = Math.pow(Math.sin(difLat/2), 2) + Math.cos(lat0) * Math.cos(lat1) * Math.pow(Math.sin(difLon/2), 2);

        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double calculatedDistance = 6371000 * c; //In metres
        return calculatedDistance;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        listOfItems.clear();
        museumNames_ArrayList.clear();
    }
}