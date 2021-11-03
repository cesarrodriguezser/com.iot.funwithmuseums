package com.iot.funwithmuseums;

//import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLng;

public class Item {
    private final String myDisplayText;
    private final LatLng myLocation;

    Item(String display_text , LatLng location ) {
        myDisplayText = display_text;
        myLocation = location;
        //myURL = URL;
    }

    String getDisplayText() {
        return myDisplayText;
    }


    LatLng getLocation() {
        return myLocation;
    }


    /*
    boolean isLocationValid() {
        return (myLocation != null);
    }
    */

}
