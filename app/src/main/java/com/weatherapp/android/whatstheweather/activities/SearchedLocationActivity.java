package com.weatherapp.android.whatstheweather.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.weatherapp.android.whatstheweather.R;
import com.weatherapp.android.whatstheweather.utils.CommonUtilFunctions;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SearchedLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_searched);

        Toolbar toolbar = findViewById(R.id.toolbar);

        String location = getIntent().getStringExtra("location");
        String rawData = getIntent().getStringExtra("rawData");
        try {
            CommonUtilFunctions.setupRefreshListener(toolbar, location,this, this);
            CommonUtilFunctions.fetchDataAndSetupLayout(toolbar, rawData, location,false,
                    this, this);
        }
        catch (JSONException | InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            setSupportActionBar(toolbar);
            findViewById(R.id.mainScroll).setVisibility(View.INVISIBLE);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

