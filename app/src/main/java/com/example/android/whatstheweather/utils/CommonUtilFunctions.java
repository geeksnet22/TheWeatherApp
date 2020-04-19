package com.example.android.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.android.whatstheweather.activities.SearchedLocationActivity;
import com.example.android.whatstheweather.types.Coordinates;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CommonUtilFunctions {

    public static String getRawDataFromLocationName(String locationName, Geocoder geocoder) throws ExecutionException, InterruptedException, IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses.size() > 0) {
            Address address = geocoder.getFromLocationName(locationName, 1).get(0);
            return ExtractData.extractData(address.getLatitude(), address.getLongitude());
        }
        else if (JSONFileReader.locationMap.containsKey(locationName)) {
            Coordinates coordinates = JSONFileReader.locationMap.get(locationName);
            return ExtractData.extractData(coordinates.latitude, coordinates.longitude);
        }
        return null;
    }
}
