package com.example.android.whatstheweather.activities;

import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.utils.CommonUtilFunctions;
import com.example.android.whatstheweather.utils.LocationsStorage;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FavLocationActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_fav);

        toolbar = findViewById(R.id.toolbar);

        String location = getIntent().getStringExtra("location");
        try {

            CommonUtilFunctions.setupRefreshListener(toolbar, location, this, this);
            String rawData = CommonUtilFunctions.getRawDataFromLocationName(location,
                    new Geocoder(this), LocationsStorage.locationsMap);
            CommonUtilFunctions.fetchDataAndSetupLayout(toolbar, rawData, location,false,
                    this, this);
        } catch (ExecutionException | InterruptedException | IOException | JSONException e) {
            e.printStackTrace();
            findViewById(R.id.mainScroll).setVisibility(View.INVISIBLE);
            toolbar.setTitle(location);
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
