package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.whatstheweather.R;
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
import java.util.concurrent.ExecutionException;

public class UserLocationActivity extends AppCompatActivity {

    /* This function is run when the app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_user);

        Toolbar toolbar = findViewById(R.id.userLocationToolbar);
        setSupportActionBar(toolbar);

        try {
            if (JSONFileReader.locationMap.isEmpty()){
                JSONFileReader.readFile(new Pair<Context, String>(this, "citylist.json"));
            }
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
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 200, 0, 0);
                this.addContentView(mainView, layoutParams);
                mainView.draw(new Canvas());
            }
            catch ( ExecutionException | InterruptedException | JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setGravity(Gravity.CENTER);
            Button searchButton = DataLayout.getSearchButton(this, this);
            linearLayout.addView(searchButton);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 200, 0, 0);
            this.addContentView(linearLayout, layoutParams);

            searchButton.draw(new Canvas());
        }
    }
}