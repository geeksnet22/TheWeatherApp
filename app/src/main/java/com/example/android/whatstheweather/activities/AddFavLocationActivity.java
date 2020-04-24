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
import com.example.android.whatstheweather.utils.DatabaseHandler;
import com.example.android.whatstheweather.utils.JSONFileReader;
import com.example.android.whatstheweather.utils.LocationsStorage;
import com.google.android.gms.common.internal.service.Common;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

        try {
            setupLocationSearch();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void initializeContent() {
        locationSuggestionsList = new ArrayList<>();

        suggestedLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationSuggestionsList);

        suggestedLocationsView = findViewById(R.id.suggestedLocationsList);
        suggestedLocationsView.setAdapter(suggestedLocationsAdaptor);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupLocationSearch() throws JSONException {
        final Context context = this;
        DatabaseHandler db = new DatabaseHandler(context);
        final Map<String, Coordinates> locationsMap = LocationsStorage.isSafeToRead ?
                LocationsStorage.locationsMap : new HashMap<String, Coordinates>();
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
                catch (IOException | InterruptedException | ExecutionException | JSONException e) {
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
