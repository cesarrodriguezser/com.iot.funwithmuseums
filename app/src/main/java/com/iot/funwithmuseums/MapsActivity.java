package com.iot.funwithmuseums;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.iot.funwithmuseums.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RadioGroup.OnCheckedChangeListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Intent inputIntent;
    String name, lat, lon, distance, currentLocation, cLat, cLon;

    Marker museumMarker;
    Marker currentLocMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RadioGroup radioGroup = findViewById(R.id.radioGroupMapType);
        radioGroup.setOnCheckedChangeListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Getting the Intent
        inputIntent = getIntent();

        //Getting the Values coming from Main Activity Activity extracting them from the Intent received
        name = inputIntent.getStringExtra("museumName");
        String location = inputIntent.getStringExtra("location");
        distance = inputIntent.getStringExtra("distance");
        currentLocation = inputIntent.getStringExtra("currentLocation");

        location = location.substring(10, location.length()-1);

        lat = location.substring(0, location.indexOf(","));
        lon = location.substring(location.indexOf(",")+1);

        try {
            currentLocation = currentLocation.substring(10, currentLocation.length() - 1);

            cLat = currentLocation.substring(0, currentLocation.indexOf(","));
            cLon = currentLocation.substring(currentLocation.indexOf(",") + 1);
        }catch (NullPointerException e){
            currentLocation = new String("Couldn't get location");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in the museum selected and move the camera
        LatLng museum = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
        LatLng cLocation;
        try {
            cLocation = new LatLng(Double.valueOf(cLat), Double.valueOf(cLon));
        }catch (NullPointerException e){
            cLocation = new LatLng(0,0);
        }
        museumMarker = mMap.addMarker(new MarkerOptions().position(museum).title(name).snippet(distance.substring(0, 7) + "m ->Click here to start the visit<-"));
        currentLocMarker = mMap.addMarker(new MarkerOptions().position(cLocation).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(museum));
        mMap.setMinZoomPreference(11);

        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.selMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.selSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.selHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.selTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                break;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if(!marker.getTitle().equals("Current Location")){

            // Creating Intent For Navigating to VisitActivity (Explicit Intent)
            Intent i = new Intent(MapsActivity.this, VisitActivity.class);

            String name = marker.getTitle();

            // Adding values to the intent to pass them to VisitActivity
            i.putExtra("museumName", name);

            // Once the intent is parametrized, start the VisitActivity:
            startActivity(i);
        }
    }
}