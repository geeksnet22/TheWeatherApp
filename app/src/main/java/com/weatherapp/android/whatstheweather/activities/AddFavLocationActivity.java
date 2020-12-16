package com.weatherapp.android.whatstheweather.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.weatherapp.android.whatstheweather.R;
import com.weatherapp.android.whatstheweather.types.Coordinates;
import com.weatherapp.android.whatstheweather.utils.CommonUtilFunctions;
import com.weatherapp.android.whatstheweather.utils.DatabaseHandler;
import com.weatherapp.android.whatstheweather.utils.LocationsStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AddFavLocationActivity extends AppCompatActivity {

    private SearchView searchView;

    private ListView suggestedLocationsView;

    private List<String> locationSuggestionsList;

    private ArrayAdapter suggestedLocationsAdaptor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_fav_add);

        initializeContent();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeContent() {
        locationSuggestionsList = new ArrayList<>();

        suggestedLocationsAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationSuggestionsList);

        suggestedLocationsView = findViewById(R.id.suggestedLocationsList);
        suggestedLocationsView.setAdapter(suggestedLocationsAdaptor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupLocationSearch() {
        final Context context = this;
        final Map<String, Coordinates> locationsMap = LocationsStorage.isSafeToRead ?
                LocationsStorage.locationsMap : new HashMap<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    searchView.setIconified(true);
                    String rawData = CommonUtilFunctions.getRawDataFromLocationName(query, new Geocoder(context), locationsMap);
                    if (rawData == null) {
                        Toast.makeText(context, "Location not found. Please provide more information (eg. country name).", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    CommonUtilFunctions.addLocationToStorage(query, new Geocoder(context), new DatabaseHandler(context));

                    SharedPreferences.Editor editor = getSharedPreferences("FAV_LOCS", Context.MODE_PRIVATE).edit();
                    String address = CommonUtilFunctions.getAddressFromLocationName(query, new Geocoder(context), locationsMap);
                    editor.putString(address, address);
                    editor.apply();

                    Toast.makeText(context, address + " added to favorite locations.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestedLocationsView.setVisibility(View.VISIBLE);
                locationSuggestionsList.clear();
                suggestedLocationsAdaptor.notifyDataSetChanged();
                for (Map.Entry<String, Coordinates> e: locationsMap.entrySet()) {
                    if (e.getKey().toLowerCase().startsWith(newText.toLowerCase())) {
                        locationSuggestionsList.add(e.getKey());
                        suggestedLocationsAdaptor.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

        suggestedLocationsView.setOnItemClickListener((parent, view, position, id) -> searchView.setQuery(suggestedLocationsView.getItemAtPosition(position).toString(), false));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
