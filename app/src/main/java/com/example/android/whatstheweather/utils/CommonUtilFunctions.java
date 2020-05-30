package com.example.android.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.Location;
import com.example.android.whatstheweather.types.OverallData;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CommonUtilFunctions {

    public static String getRawDataFromLocationName(String locationName, Geocoder geocoder,
                                                    Map<String, Coordinates> allLocationsMap)
            throws ExecutionException, InterruptedException, IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        if (addresses.size() > 0) {
            Address address = geocoder.getFromLocationName(locationName, 1).get(0);
            return new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    address.getLatitude(), address.getLongitude()).get();
        }
        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        if (allLocationsMap.containsKey(locationName)) {
            Coordinates coordinates = allLocationsMap.get(locationName);
            LocationsStorage.isSafeToRead = true;
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
            return address.getLocality() + ", " + address.getCountryCode();
        }
        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        if (allLocationsMap.containsKey(locationName)) {
            Coordinates coordinates = allLocationsMap.get(locationName);
            LocationsStorage.isSafeToRead = true;
            addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            return addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        }
        return null;
    }

    public static void addLocationToStorage(String locationName, Geocoder geocoder, DatabaseHandler db)
            throws IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
        String address = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryCode();
        Coordinates coordinates = new Coordinates(addresses.get(0).getLongitude(), addresses.get(0).getLatitude());
        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        LocationsStorage.locationsMap.put(address, coordinates);
        LocationsStorage.isSafeToRead = true;
        db.addLocation(new Location(address, "{lon:" + coordinates.longitude + ",lat:" +
                coordinates.latitude + "}"));
    }

    public static void setupRefreshListener(final Toolbar toolbar, final String location, final Activity activity,
                                            final Context context) {
        final SwipeRefreshLayout pullToRefresh = activity.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    fetchDataAndSetupLayout(toolbar, "", location, true, activity,
                            context);
                } catch (JSONException | ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                    activity.findViewById(R.id.mainScroll).setVisibility(View.INVISIBLE);
                    Toast.makeText(activity, "Please check you internet connection.", Toast.LENGTH_SHORT).show();
                }
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public static void fetchDataAndSetupLayout(Toolbar toolbar, String rawData, String location,
                                               boolean fetchRawData, Activity activity, Context context)
            throws JSONException, ExecutionException, InterruptedException, IOException {

        if (activity.findViewById(R.id.mainScroll).getVisibility() != View.VISIBLE) {
            activity.findViewById(R.id.mainScroll).setVisibility(View.VISIBLE);
        }

        if (fetchRawData) {
            rawData = CommonUtilFunctions.getRawDataFromLocationName(location, new Geocoder(context),
                    LocationsStorage.locationsMap);
        }

        LocationDataProcessor locationDataProcessor = new LocationDataProcessor(new Pair<Context,
                String>(context, rawData));
        OverallData data = locationDataProcessor.fetchWeatherData(new Pair<Context,
                String>(context, rawData));

        toolbar.setTitle(data.currentData.locationName);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        DataLayoutSetter.setDataLayout(activity, context, data.currentData, data.hourlyData,
                data.dailyData, data.detailsData);
    }
}
