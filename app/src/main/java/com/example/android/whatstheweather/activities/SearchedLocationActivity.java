package com.example.android.whatstheweather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.OverallData;
import com.example.android.whatstheweather.utils.CommonUtilFunctions;
import com.example.android.whatstheweather.utils.DataLayoutSetter;
import com.example.android.whatstheweather.utils.DatabaseHandler;
import com.example.android.whatstheweather.utils.LocationDataProcessor;
import com.example.android.whatstheweather.utils.LocationsStorage;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SearchedLocationActivity extends AppCompatActivity {

    private String location;
    private Toolbar toolbar;
    private Map<String, Coordinates> locationsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_searched);

        if (LocationsStorage.isSafeToRead) {
            locationsMap = LocationsStorage.locationsMap;
        }
        else {
            locationsMap = new HashMap<>();
        }

        toolbar = findViewById(R.id.toolbar);

        Intent userLocationIntent = getIntent();
        String rawData = userLocationIntent.getStringExtra("rawData");
        location = userLocationIntent.getStringExtra("location");
        try {
            fetchDataAndSetupLayout(rawData, false, this, this);
        }
        catch (JSONException | InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        setupRefreshListener(this, this);
    }

    private void fetchDataAndSetupLayout(String rawData, boolean fetchRawData, Activity activity, Context context) throws
            JSONException, ExecutionException, InterruptedException, IOException {

        if (fetchRawData) {
            rawData = CommonUtilFunctions.getRawDataFromLocationName(location, new Geocoder(this), locationsMap);
        }

        LocationDataProcessor locationDataProcessor = new LocationDataProcessor(new Pair<Context,
                String>(context, rawData));

        OverallData data = locationDataProcessor.fetchWeatherData(new Pair<Context,
                String>(context, rawData));

        toolbar.setTitle(data.currentData.locationName);
        DataLayoutSetter.setDataLayout(activity, context, data.currentData, data.hourlyData,
                data.dailyData, data.detailsData);
    }

    private void setupRefreshListener(final Activity activity, final Context context) {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    fetchDataAndSetupLayout("", true, activity, context);
                    pullToRefresh.setRefreshing(false);
                } catch (JSONException | ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

