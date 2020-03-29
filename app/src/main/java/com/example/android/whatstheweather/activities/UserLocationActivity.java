package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.DataLayout;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.JsonFileReader;
import com.example.android.whatstheweather.utils.LocationDataProcessor;
import com.example.android.whatstheweather.utils.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserLocationActivity extends AppCompatActivity {

    /* This function is run when the app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_user);

        /* setup location services */
        LocationServices locationServices = new LocationServices((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));

        /* get user location */
        Location userLocation = locationServices.getLocation(this, this);

//        try {
//            JSONArray fileContent = JsonFileReader.readFile(new Pair<Context, String>(this, "citylist.json"));
//            System.out.println("GSB array: " + fileContent.get(0));
//        } catch (InterruptedException | ExecutionException | JSONException e) {
//            e.printStackTrace();
//        }

        /* if permission to access user's location is granted */
        if (userLocation != null)
        {
            try {
                String rawData = ExtractData.extractData(userLocation.getLatitude(), userLocation.getLongitude());

                CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
                HourlyData hourlyData = LocationDataProcessor.getHourlyData(rawData ,this);

                LocationDataProcessor.getDailyData(rawData, this);

                ScrollView mainView = DataLayout.getDataLayout(this, currentData, hourlyData, true);
                setupLocationSearch(this);
                this.addContentView(mainView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mainView.draw(new Canvas());


            }
            catch ( ExecutionException | InterruptedException | JSONException | IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Location data not available", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            SearchView locationSearch = DataLayout.getLocationSearchView(this);
            setupLocationSearch(this);
            this.addContentView(locationSearch, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            locationSearch.draw(new Canvas());
        }
    }

    private void setupLocationSearch(final Context context) {
        SearchView locationSearch = DataLayout.searchLocation;
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchedLocationIntent = new Intent(context, SearchedLocationActivity.class);
                searchedLocationIntent.putExtra("locationName", query);
                startActivity(searchedLocationIntent);
                return false;
            }
        });
    }
}