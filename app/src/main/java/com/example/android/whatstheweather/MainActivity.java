package com.example.android.whatstheweather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener{

    public EditText cityName;
    private LocationManager locationManager;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 99;

    /**
     * check if the location permission in granted. Prompt user to allow access to location,
     * return false if user chooses to disallows the permission, true otherwise
     * @return true if permission to access location is granted, false otherwise
     */
    public boolean checkLocationPermission()
    {
        /* if the permission is not granted */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            /* should we show an explanation */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                /* show explanation to the user about requiring location permission */
                new AlertDialog.Builder(this).setTitle("Permission Needed")
                        .setMessage("Permission needed to access location to function properly")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /* prompt the user once explanation has been shown */
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                    }
                }).create().show();
            }
            else
            {
                /* no explanation needed, just request the permission */
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * This function is run when user's location changes
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class ExtractData extends AsyncTask<String, Void, String> {
        String result = "";
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return "Failed";
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "Failed";
            }
        }
    }

    public String formatWeatherInfo(JSONObject rawData)
    {
        String weatherInfo = "";
        try
        {
            /* location information */
            String coordInfo = rawData.getString("coord");
            /* JSON object representing location coordinates */
            JSONObject jsonObjectCoord = new JSONObject(coordInfo);
            int lon = jsonObjectCoord.getInt("lon");
            int lat = jsonObjectCoord.getInt("lat");

            /* get main weather information */
            weatherInfo = rawData.getString("weather");
            /* JSON array containing various aspects of weather */
            JSONArray jsonArrayWeather = new JSONArray(weatherInfo);
            JSONObject jsonPartWeather = jsonArrayWeather.getJSONObject(0);

            /* get information about other weather conditions */
            String mainInfo = rawData.getString("main");
            JSONObject jsonObjectMain = new JSONObject(mainInfo);
            int temp = jsonObjectMain.getInt("temp");
            int temp_min = jsonObjectMain.getInt("temp_min");
            int temp_max = jsonObjectMain.getInt("temp_max");
            int humidity = jsonObjectMain.getInt("humidity");

            /* build string containing all the information to be shown on screen */
            weatherInfo = jsonPartWeather.getString("main") + ": " + jsonPartWeather.getString("description")
                    + "\nCurr temp: " + String.valueOf(Math.round(temp - 273.15)) + "C" +  "\nMin temp: " +
                    String.valueOf(Math.round(temp_min - 273.15)) + "C"
                    + "\nMax temp: " + String.valueOf(Math.round(temp_max - 273.15)) + "C" + "\nHumidity: " + humidity + "%"
                    + "\nLongitude: " + lon + "\nLatitude: " + lat;
            return weatherInfo;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /* This function is run when the  app first starts */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        this.checkLocationPermission();
        /* get location of the user */
        Location location = this.getLocation();
        /* if permission to access user's location is granted */
        if (location != null)
        {
            /* if the location permission was granted */
            float latitude = (int) location.getLatitude();    /* latitude corresponding to user location */
            float longitude = (int) location.getLongitude();  /* longitude corresponding to user location */
            /* get string consisting of weather information corresponding to user's location */
            String userLocationInfo = getWeatherInfo(latitude, longitude);
            try {
                JSONObject jsonObject = new JSONObject(userLocationInfo);
                String userInfo = this.formatWeatherInfo(jsonObject);
                if (userInfo.isEmpty()) {
                    ((TextView) findViewById(R.id.userInfo)).setText("");
                } else {
                    ((TextView) findViewById(R.id.userInfo)).setText(this.formatWeatherInfo(jsonObject));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return string consisting of weather information
     * @param lati latitude corresponding to the location
     * @param longi longitude corresponding to the location
     * @return Return string consisting of weather information
     */
    public String getWeatherInfo(float lati, float longi) {
        ExtractData data = new ExtractData();
        String result = "";
        try {
            /* retrieve weather information as a string */
            return data.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + lati + "&lon=" + longi + "&APPID=e34ed2404aade8ba726f190aa26da0c8").get();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * Return user's last known location, return null is location access is not permitted
     * @return user's last known location
     */
    public Location getLocation()
    {
        /* setup the location manager */
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            /* if the permission to access location is given by the user */
            if (checkLocationPermission()) {
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            return null;
        }
        return null;
    }

    /**
     *  Process city name entered by the user and open new
     *  activity to show weather information accordingly */
    public void clicked(View view)
    {
        String name = cityName.getText().toString();
        ExtractData data = new ExtractData();
        String result = "";
        try {
            /* retrieve weather information as a string */
            result = data.execute("http://api.openweathermap.org/data/2.5/weather?q=" + name + "&APPID=e34ed2404aade8ba726f190aa26da0c8").get();
//            data.execute("https://api.darksky.net/forecast/7d7c4d51abd38384fd51a174d0771a5d/52.1332,-106.6700").get();

            /* create JSON object from given string */
            JSONObject jsonObject = new JSONObject(result);

            /* location information */
            String coordInfo = jsonObject.getString("coord");
            /* JSON object representing location coordinates */
            JSONObject jsonObjectCoord = new JSONObject(coordInfo);
            int lon = jsonObjectCoord.getInt("lon");
            int lat = jsonObjectCoord.getInt("lat");

            /* get main weather information */
            String weatherInfo = jsonObject.getString("weather");
            /* JSON array containing various aspects of weather */
            JSONArray jsonArrayWeather = new JSONArray(weatherInfo);
            JSONObject jsonPartWeather = jsonArrayWeather.getJSONObject(0);

            /* get information about other weather conditions */
            String mainInfo = jsonObject.getString("main");
            JSONObject jsonObjectMain = new JSONObject(mainInfo);
            int temp = jsonObjectMain.getInt("temp");
            int temp_min = jsonObjectMain.getInt("temp_min");
            int temp_max = jsonObjectMain.getInt("temp_max");
            int humidity = jsonObjectMain.getInt("humidity");

            /* build string containing all the information to be shown on screen */
            weatherInfo = jsonPartWeather.getString("main") + ": " + jsonPartWeather.getString("description")
                    + "\nCurr temp: " + String.valueOf(Math.round(temp - 273.15)) + "C" +  "\nMin temp: " +
                    String.valueOf(Math.round(temp_min - 273.15)) + "C"
                    + "\nMax temp: " + String.valueOf(Math.round(temp_max - 273.15)) + "C" + "\nHumidity: " + humidity + "%"
                    + "\nLongitude: " + lon + "\nLatitude: " + lat;

            /* create an intent class for switching to a new activity */
            Intent weatherDisplay = new Intent(this, DisplayMessageActivity.class);
            /* pass string containing weather information to the new activity */
            weatherDisplay.putExtra("weatherInfo", weatherInfo);
            /* pass string representing the type of weather to the new activity */
            weatherDisplay.putExtra("weatherType", jsonPartWeather.getString("main"));
            /* start new activity */
            startActivity(weatherDisplay);
        }
        /* in case an error occurs */
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            Toast.makeText(this, "Data not available", Toast.LENGTH_SHORT).show();
        }
    }
}