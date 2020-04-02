package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DataLayout;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.JSONFileReader;
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

        try {
            JSONFileReader.readFile(new Pair<Context, String>(this, "citylist.json"));
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        /* setup location services */
        LocationServices locationServices = new LocationServices((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));

        /* get user location */
        Location userLocation = locationServices.getLocation(this, this);

        /* if permission to access user's location is granted */
        if (userLocation != null)
        {
            try {
                String rawData = ExtractData.extractData(userLocation.getLatitude(), userLocation.getLongitude());

                CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
                HourlyData hourlyData = LocationDataProcessor.getHourlyData();
                DailyData dailyData = LocationDataProcessor.getDailyData();

                LocationDataProcessor.getDailyData();

                ScrollView mainView = DataLayout.getDataLayout(this, this, currentData, hourlyData, dailyData, true);
                this.addContentView(mainView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mainView.draw(new Canvas());
            }
            catch ( ExecutionException | InterruptedException | JSONException | IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Location data not available", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Button searchButton = DataLayout.getSearchButton(this, this);
            this.addContentView(searchButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            searchButton.draw(new Canvas());
        }
    }
}