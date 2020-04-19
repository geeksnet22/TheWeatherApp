package com.example.android.whatstheweather.utils;

import android.location.Address;
import android.location.Geocoder;

import com.example.android.whatstheweather.types.Coordinates;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommonUtilFunctions {

    public static String getRawDataFromLocationName(String locationname, Geocoder geocoder) throws ExecutionException, InterruptedException, IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationname, 1);
        if (addresses.size() > 0) {
            Address address = geocoder.getFromLocationName(locationname, 1).get(0);
            return ExtractData.extractData(address.getLatitude(), address.getLongitude());
        }
        else if (JSONFileReader.locationMap.containsKey(locationname)) {
            Coordinates coordinates = JSONFileReader.locationMap.get(locationname);
            return ExtractData.extractData(coordinates.latitude, coordinates.longitude);
        }
        return null;
    }
}
