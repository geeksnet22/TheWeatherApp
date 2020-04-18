package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.OverallData;
import com.example.android.whatstheweather.utils.DataLayoutSetter;
import com.example.android.whatstheweather.utils.LocationDataProcessor;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class SearchedLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_searched);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent userlocationIntent = getIntent();
        String rawData = userlocationIntent.getStringExtra("rawData");
        try {
            LocationDataProcessor locationDataProcessor = new LocationDataProcessor(new Pair<Context,
                    String>(this, rawData));

            OverallData data = locationDataProcessor.fetchRawWeatherData(new Pair<Context,
                    String>(this, rawData));

            ((TextView) findViewById(R.id.hourlyHeading)).setText("Hourly");
            ((TextView) findViewById(R.id.dailyHeading)).setText("Daily");
            ((TextView) findViewById(R.id.detailsHeading)).setText("Details");

            DataLayoutSetter.setDataLayout(this, this, data.currentData, data.hourlyData,
                    data.dailyData, data.detailsData);
        }
        catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

