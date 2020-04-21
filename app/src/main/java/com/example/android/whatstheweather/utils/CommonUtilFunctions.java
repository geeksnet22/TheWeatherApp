package com.example.android.whatstheweather.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.example.android.whatstheweather.types.Coordinates;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CommonUtilFunctions {

    public static String getRawDataFromLocationName(String locationName, Geocoder geocoder, Map<String, Coordinates> allLocationsMap)
            throws ExecutionException, InterruptedException, IOException, JSONException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses.size() > 0) {
            Address address = geocoder.getFromLocationName(locationName, 1).get(0);
            return new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    address.getLatitude(), address.getLongitude()).get();
        }
        else if (allLocationsMap.containsKey(locationName)) {
            Coordinates coordinates = allLocationsMap.get(locationName);
            return new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    coordinates.latitude, coordinates.longitude).get();
        }
        return null;
    }

    public static String getAddressFromLocationName(String locationName, Geocoder geocoder, Map<String,
            Coordinates> allLocationsMap)
            throws IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getLocality() + ", " + address.getCountryName();
        }
        else if (allLocationsMap.containsKey(locationName)) {
            Coordinates coordinates = allLocationsMap.get(locationName);
            addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            return addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        }
        return null;
    }

    public static void addLocationToStorage(String locationName, Geocoder geocoder, DatabaseHandler db)
            throws IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        String address = (addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName()).toLowerCase();
        Coordinates coordinates = new Coordinates(addresses.get(0).getLongitude(), addresses.get(0).getLatitude());
        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        LocationsStorage.locationsMap.put(address, coordinates);
        LocationsStorage.isSafeToRead = true;
        db.addLocation(new Location(address, "{lon:" + coordinates.longitude + ",lat:" + coordinates.latitude + "}"));
    }
}
