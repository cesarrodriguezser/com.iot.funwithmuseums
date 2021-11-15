package com.iot.funwithmuseums;

//import com.google.android.gms.maps.model.LatLng;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;


@Entity(tableName = "items")
public class Item {
    @PrimaryKey
    private  int id;

    private  String DisplayText;
    private  float StepAvg;
    private  double longitude;
    private  double latitude;

    private  boolean favorite = false;


    public void setDisplayText(String myDisplayText){
        this.DisplayText = myDisplayText;
    }
    public String getDisplayText() {
        return DisplayText;
    }

    public void setLocation(double latitude, double longitude){
        setLatitude(latitude);
        setLongitude(longitude);
    }
    public LatLng getLocation() {
        return new LatLng(latitude,longitude);
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return  longitude;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public double getLatitude(){
        return latitude;
    }

    public void setStepAvg(float StepAvg){
        this.StepAvg = StepAvg;
    }
    public float getStepAvg(){
        return StepAvg;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    public void setFavorite(boolean favorite){
        this.favorite = favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }
    /*
    boolean isLocationValid() {
        return (myLocation != null);
    }

     */


    public String getTopic() {
        return DisplayText.toLowerCase().replaceAll("\\s+", "");
    }

}
