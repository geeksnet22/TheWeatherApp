package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

        final Context context = this;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Intent userlocationIntent = getIntent();
        String rawData = userlocationIntent.getStringExtra("rawData");
        try {
            CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
            HourlyData hourlyData = LocationDataProcessor.getHourlyData();
            DailyData dailyData = LocationDataProcessor.getDailyData();

            DataLayoutSetter.setDataLayout(this, this, currentData, hourlyData, dailyData);
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

