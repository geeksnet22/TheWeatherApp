package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DataLayout;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.utils.LocationDataProcessor;

import org.json.JSONException;

import java.io.IOException;
public class SearchedLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_searched);

        Toolbar toolbar = findViewById(R.id.searchedLocationToolbar);
        setSupportActionBar(toolbar);

        final Context context = this;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(context, UserLocationActivity.class);
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

            ScrollView mainView = DataLayout.getDataLayout(this, this, currentData, hourlyData, dailyData, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 200, 0, 0);
            this.addContentView(mainView, layoutParams);
            mainView.draw(new Canvas());
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

