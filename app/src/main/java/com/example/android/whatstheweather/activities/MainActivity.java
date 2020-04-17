package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;
import com.example.android.whatstheweather.utils.DataLayoutSetter;
import com.example.android.whatstheweather.utils.ExtractData;
import com.example.android.whatstheweather.utils.JSONFileReader;
import com.example.android.whatstheweather.utils.LocationDataProcessor;
import com.example.android.whatstheweather.utils.LocationServices;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Geocoder geocoder;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private List<String> locationSuggestionsList;
    private ArrayAdapter suggestedLocationsAdaptor;

    private List<String> favLocationsList;
    private ArrayAdapter favLocationsAdaptor;

    private ListView suggestedLocationsView;

    private ListView favLocationsView;

    private boolean addToFavorites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         geocoder = new Geocoder(this);

//        try {
//            if (JSONFileReader.locationMap.isEmpty()){
//                JSONFileReader.readFile(new Pair<Context, String>(this, "citylist.json"));
//            }
//        } catch (ExecutionException | InterruptedException | JSONException e) {
//            e.printStackTrace();
//        }

        drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addToFavorites = false;

        locationSuggestionsList = new ArrayList<>();
        suggestedLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationSuggestionsList);
        suggestedLocationsView = findViewById(R.id.locationsList);
        suggestedLocationsView.setAdapter(suggestedLocationsAdaptor);
        suggestedLocationsView.setVisibility(View.INVISIBLE);

        favLocationsList = new ArrayList<String>();
        favLocationsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favLocationsList);
        favLocationsView = findViewById(R.id.favLocationsList);
        favLocationsView.setAdapter(favLocationsAdaptor);

        final Context context = this;
        favLocationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, SearchedLocationActivity.class);
                try {
                    String rawData = getRawDataFromLocationName(favLocationsView.getItemAtPosition(position).toString());
                    intent.putExtra("rawData", rawData);
                    startActivity(intent);
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /* setup location services */
        LocationServices locationServices = new LocationServices((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));

        /* get user location */
        Location userLocation = locationServices.getLocation(this, this);


        /* if permission to access user's location is granted */
        if (userLocation != null)
        {
            try {
                String rawData = ExtractData.extractData(userLocation.getLatitude(), userLocation.getLongitude());

                CurrentData currentData = LocationDataProcessor.getCurrentData(rawData, this);
                HourlyData hourlyData = LocationDataProcessor.getHourlyData();
                DailyData dailyData = LocationDataProcessor.getDailyData();

                toolbar.setTitle(currentData.locationName);
                ((TextView) findViewById(R.id.hourlyHeading)).setText("Hourly");
                ((TextView) findViewById(R.id.dailyHeading)).setText("Daily");

                DataLayoutSetter.setDataLayout(this, this, currentData, hourlyData, dailyData);

            }
            catch ( ExecutionException | InterruptedException | JSONException | IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            toolbar.setTitle("Home");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchView)v).setIconified(false);
            }
        });

        findViewById(R.id.addLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                findViewById(R.id.app_bar_search).callOnClick();
                addToFavorites = true;
            }
        });

        setupLocationSearch(searchView, this);

        suggestedLocationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(suggestedLocationsView.getItemAtPosition(position).toString(), false);
            }
        });

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private void setupLocationSearch(final SearchView searchView, final Context context) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                try {
                    String rawData = getRawDataFromLocationName(query);
                    if (rawData == null) {
                        Toast.makeText(context, "Location not found. Please provide more information (eg. country name).", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    suggestedLocationsView.setVisibility(View.INVISIBLE);

                    if ( addToFavorites ) {
                        addToFavorites = false;
                        favLocationsList.add(query);
                        favLocationsAdaptor.notifyDataSetChanged();
                        Toast.makeText(context, "Location added to favorites.", Toast.LENGTH_SHORT).show();
                        drawer.openDrawer(GravityCompat.START);
                        return false;
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
    }

    private String getRawDataFromLocationName(String locationname) throws ExecutionException, InterruptedException, IOException {
        List<Address> addresses = geocoder.getFromLocationName(locationname, 1);
        if (addresses.size() > 0) {
            Address address = geocoder.getFromLocationName(locationname, 1).get(0);
            return ExtractData.extractData(address.getLatitude(), address.getLongitude());
        }
        else if (JSONFileReader.locationMap.containsKey(locationname)) {
            Coordinates coordinates = JSONFileReader.locationMap.get(locationname);
            return ExtractData.extractData(coordinates.latitude, coordinates.longitude);
        }
        return null;
    }
}
