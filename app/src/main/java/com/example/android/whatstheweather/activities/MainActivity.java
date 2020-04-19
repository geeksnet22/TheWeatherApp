package com.example.android.whatstheweather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.OverallData;
import com.example.android.whatstheweather.utils.CommonUtilFunctions;
import com.example.android.whatstheweather.utils.DataLayoutSetter;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.JSONFileReader;
import com.example.android.whatstheweather.utils.LocationDataProcessor;
import com.example.android.whatstheweather.utils.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Geocoder geocoder;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private List<String> locationSuggestionsList;
    private ArrayAdapter suggestedLocationsAdaptor;
    private ListView suggestedLocationsView;

    private List<String> favLocationsList;
    private ArrayAdapter favLocationsAdaptor;

    private ListView favLocationsView;

    private SearchView searchView;

    private Toolbar toolbar;

    private final String FAV_LOC_PREFS = "FavoriteLocationsPreferences";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeContent(this);

        setupRefreshListener(this);

        readJsonFile();

        setupFavLocationsNavigationDrawer(this);

        fetchDataAndSetupLayout(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Map<String, ?> favLocationsMap = getSharedPreferences("FAV_LOCS", MODE_PRIVATE).getAll();

        for (Map.Entry<String, ?> entry: favLocationsMap.entrySet()) {
            if (!favLocationsList.contains(entry.getValue().toString())) {
                favLocationsList.add(entry.getValue().toString());
            }
        }
        favLocationsAdaptor.notifyDataSetChanged();

        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchViewItem.getActionView();

        setupLocationSearch(searchView, this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void setupLocationSearch(final SearchView searchView, final Context context) {
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

                    Intent searchedLocationIntent = new Intent(context, SearchedLocationActivity.class);
                    searchedLocationIntent.putExtra("rawData", rawData);
                    searchedLocationIntent.putExtra("location", query);
                    startActivity(searchedLocationIntent);
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

    private void setupRefreshListener(final Context context) {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataAndSetupLayout(context);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void setupFavLocationsNavigationDrawer(final Context context) {
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        favLocationsList = new ArrayList<String>();
        favLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favLocationsList);
        favLocationsView = findViewById(R.id.favLocationsList);
        favLocationsView.setAdapter(favLocationsAdaptor);

        favLocationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, SearchedLocationActivity.class);
                try {
                    String rawData = CommonUtilFunctions.getRawDataFromLocationName(favLocationsView.getItemAtPosition(position).toString(), geocoder);
                    intent.putExtra("rawData", rawData);
                    intent.putExtra("location",favLocationsView.getItemAtPosition(position).toString());
                    startActivity(intent);
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.addLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddFavLocationActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initializeContent(final Context context) {
        geocoder = new Geocoder(this);
        toolbar = findViewById(R.id.toolbar);
        locationSuggestionsList = new ArrayList<>();
        suggestedLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationSuggestionsList);
        suggestedLocationsView = findViewById(R.id.locationsList);
        suggestedLocationsView.setAdapter(suggestedLocationsAdaptor);
        suggestedLocationsView.setVisibility(View.INVISIBLE);
    }

    private void readJsonFile() {
        try {
            if (JSONFileReader.locationMap.isEmpty()){
                JSONFileReader.readFile(new Pair<Context, String>(this, "citylist.json"));
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchDataAndSetupLayout(Context context) {
        /* setup location services */
        LocationServices locationServices = new LocationServices((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));

        /* get user location */
        Location userLocation = locationServices.getLocation(this, this);

        /* if permission to access user's location is granted */
        if (userLocation != null)
        {
            try {
                String rawData = ExtractData.extractData(userLocation.getLatitude(), userLocation.getLongitude());

                LocationDataProcessor locationDataProcessor = new LocationDataProcessor(new Pair<Context, String>(context, rawData));

                OverallData data = locationDataProcessor.fetchWeatherData(new Pair<Context, String>(this, rawData));

                toolbar.setTitle(data.currentData.locationName);

                DataLayoutSetter.setDataLayout(this, this, data.currentData, data.hourlyData,
                        data.dailyData, data.detailsData);
            }
            catch ( ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            toolbar.setTitle("Home");
            findViewById(R.id.mainScroll).setVisibility(View.GONE);

            Toast.makeText(context, "Not able to retrive weather information at this moment. Make " +
                    "sure location access is granted and try refreshing the page by pulling down.", Toast.LENGTH_LONG).show();

        }
    }


}
