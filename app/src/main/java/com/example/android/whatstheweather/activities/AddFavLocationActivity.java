package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.utils.CommonUtilFunctions;
import com.example.android.whatstheweather.utils.JSONFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddFavLocationActivity extends AppCompatActivity {

    private SearchView searchView;

    private ListView suggestedLocationsView;

    private List<String> locationSuggestionsList;

    private ArrayAdapter suggestedLocationsAdaptor;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_fav_add);

        initializeContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconified(false);

        setupLocationSearch();

        return super.onCreateOptionsMenu(menu);
    }

    private void initializeContent() {
        locationSuggestionsList = new ArrayList<>();

        suggestedLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationSuggestionsList);

        suggestedLocationsView = findViewById(R.id.suggestedLocationsList);
        suggestedLocationsView.setAdapter(suggestedLocationsAdaptor);
        suggestedLocationsView.setVisibility(View.INVISIBLE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupLocationSearch() {
        final Context context = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    String rawData = CommonUtilFunctions.getRawDataFromLocationName(query, new Geocoder(context));
                    if (rawData == null) {
                        Toast.makeText(context, "Location not found. Please provide more information (eg. country name).", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    suggestedLocationsView.setVisibility(View.INVISIBLE);

                    SharedPreferences.Editor editor = getSharedPreferences("FAV_LOCS", Context.MODE_PRIVATE).edit();
                    editor.putString(query, query.toUpperCase());
                    editor.apply();

                    Toast.makeText(context, query.toUpperCase() + " added to favorite locations.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    suggestedLocationsView.setVisibility(View.INVISIBLE);
                }
                else {
                    suggestedLocationsView.setVisibility(View.VISIBLE);
                }
                locationSuggestionsList.clear();
                suggestedLocationsAdaptor.notifyDataSetChanged();
                for (Map.Entry<String, Coordinates> e: JSONFileReader.locationMap.entrySet()) {
                    if (e.getKey().startsWith(newText)) {
                        locationSuggestionsList.add(e.getKey());
                        suggestedLocationsAdaptor.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

        suggestedLocationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(suggestedLocationsView.getItemAtPosition(position).toString(), false);
            }
        });
    }

}
