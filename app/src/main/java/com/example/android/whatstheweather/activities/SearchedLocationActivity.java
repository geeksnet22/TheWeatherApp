package com.example.android.whatstheweather.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DataLayout;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.LocationDataProcessor;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SearchedLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_searched);

        Intent userlocationIntent = getIntent();
        String locationName = userlocationIntent.getStringExtra("locationName");

        Geocoder geocoder = new Geocoder(this);
        try {
            System.out.println("GSB size: " + geocoder.getFromLocationName(locationName, 1).size());
            Address address = geocoder.getFromLocationName(locationName, 1).get(0);
            String rawData = ExtractData.extractData(address.getLatitude(), address.getLongitude());

            CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
            HourlyData hourlyData = LocationDataProcessor.getHourlyData(rawData, this);

            ScrollView mainView = DataLayout.getDataLayout(this, currentData, hourlyData, false);
            this.addContentView(mainView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mainView.draw(new Canvas());
        }
        catch (IOException | InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
}

