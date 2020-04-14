package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.utils.DataLayoutSetter;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.utils.LocationDataProcessor;

import org.json.JSONException;

import java.io.IOException;
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
            CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
            HourlyData hourlyData = LocationDataProcessor.getHourlyData();
            DailyData dailyData = LocationDataProcessor.getDailyData();

            ((TextView) findViewById(R.id.hourlyHeading)).setText("Hourly");
            ((TextView) findViewById(R.id.dailyHeading)).setText("Daily");

            DataLayoutSetter.setDataLayout(this, this, currentData, hourlyData, dailyData);
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

