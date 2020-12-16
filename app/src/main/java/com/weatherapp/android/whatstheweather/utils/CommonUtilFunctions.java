package com.weatherapp.android.whatstheweather.utils;

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

import com.weatherapp.android.whatstheweather.R;
import com.weatherapp.android.whatstheweather.types.Coordinates;
import com.weatherapp.android.whatstheweather.types.Location;
import com.weatherapp.android.whatstheweather.types.OverallData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CommonUtilFunctions {

    static class GeocoderOperations extends AsyncTask<String, Void, List<Address>> {

        private final Geocoder geocoder;

        private String locationName;

        private double latitude;
        private double longitude;

        private final boolean getFromLocationName;

        private GeocoderOperations(Geocoder geocoder, String locationName) {
            this.geocoder = geocoder;
            this.locationName = locationName;

            this.getFromLocationName = true;
        }

        private GeocoderOperations(Geocoder geocoder, double latitude, double longitude) {
            this.geocoder = geocoder;
            this.latitude = latitude;
            this.longitude = longitude;

            this.getFromLocationName = false;
        }

        @Override
        protected List<Address> doInBackground(String... strings) {
            try {
                if (getFromLocationName) {
                    return this.geocoder.getFromLocationName(this.locationName, 1);
                }
                return this.geocoder.getFromLocation(this.latitude, this.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        List<Address> getAddressesFromLocationName() throws ExecutionException, InterruptedException {
            return new GeocoderOperations(this.geocoder, this.locationName)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        }

        List<Address> getAddressesFromLocationCoordinates() throws ExecutionException, InterruptedException {
            return new GeocoderOperations(this.geocoder, this.latitude, this.latitude)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        }
    }

    public static String getRawDataFromLocationName(String locationName, Geocoder geocoder,
                                                    Map<String, Coordinates> allLocationsMap)
            throws ExecutionException, InterruptedException {
        List<Address> addresses = new GeocoderOperations(geocoder, locationName)
                .getAddressesFromLocationName();
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
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
            throws IOException, ExecutionException, InterruptedException {
        List<Address> addresses = new GeocoderOperations(geocoder, locationName)
                .getAddressesFromLocationName();
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getLocality() + ", " + address.getCountryCode();
        }
        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        if (allLocationsMap.containsKey(locationName)) {
            Coordinates coordinates = allLocationsMap.get(locationName);
            LocationsStorage.isSafeToRead = true;
            addresses = new GeocoderOperations(geocoder, coordinates.latitude, coordinates.longitude)
                    .getAddressesFromLocationCoordinates();
            return addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
        }
        return null;
    }

    public static void addLocationToStorage(String locationName, Geocoder geocoder, DatabaseHandler db)
            throws ExecutionException, InterruptedException {
        List<Address> addresses = new GeocoderOperations(geocoder, locationName).getAddressesFromLocationName();
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
        pullToRefresh.setOnRefreshListener(() -> {
            try {
                fetchDataAndSetupLayout(toolbar, "", location, true, activity,
                        context);
            } catch (JSONException | ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
                activity.findViewById(R.id.mainScroll).setVisibility(View.INVISIBLE);
                Toast.makeText(activity, "Please check you internet connection.", Toast.LENGTH_SHORT).show();
            }
            pullToRefresh.setRefreshing(false);
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

        LocationDataProcessor locationDataProcessor = new LocationDataProcessor(new Pair<>(context, rawData));
        OverallData data = locationDataProcessor.fetchWeatherData(new Pair<>(context, rawData));

        toolbar.setTitle(data.currentData.locationName);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        DataLayoutSetter.setDataLayout(activity, context, data.currentData, data.hourlyData,
                data.dailyData, data.detailsData);
    }
}
