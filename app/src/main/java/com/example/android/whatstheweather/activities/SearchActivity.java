package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.DataLayout;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.JSONFileReader;
import com.example.android.whatstheweather.utils.LocationDataProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    private List<String> locationsList;
    private ArrayAdapter adapter;
    private SearchView locationSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupLocationSearch(this);

        locationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationsList);

        final ListView suggestedLocations = findViewById(R.id.locationsList);
        suggestedLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                locationSearch.setQuery(suggestedLocations.getItemAtPosition(position).toString(), true);
            }
        });
        suggestedLocations.setAdapter(adapter);
    }

    private void setupLocationSearch(final Context context) {
        locationSearch = findViewById(R.id.locationSearch);
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                locationsList.clear();
                adapter.notifyDataSetChanged();
                for (Map.Entry<String, Coordinates> e: JSONFileReader.locationMap.entrySet()) {
                    if (e.getKey().startsWith(newText)) {
                        locationsList.add(e.getKey());
                        adapter.notifyDataSetChanged();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Geocoder geocoder = new Geocoder(context);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    String rawData;
                    if (addresses.size() > 0) {
                        Address address = geocoder.getFromLocationName(query, 1).get(0);
                        rawData = ExtractData.extractData(address.getLatitude(), address.getLongitude());
                    }
                    else if (JSONFileReader.locationMap.containsKey(query)) {
                        Coordinates coordinates = JSONFileReader.locationMap.get(query);
                        rawData = ExtractData.extractData(coordinates.latitude, coordinates.longitude);
                    }
                    else {
                        Toast.makeText(context, "Location data not available", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Intent searchedLocationIntent = new Intent(context, SearchedLocationActivity.class);
                    searchedLocationIntent.putExtra("rawData", rawData);
                    startActivity(searchedLocationIntent);
                    return false;
                }
                catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return true;
                }
            }
        });
    }
}
