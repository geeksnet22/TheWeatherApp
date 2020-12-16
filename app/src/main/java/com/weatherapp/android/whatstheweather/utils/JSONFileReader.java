package com.weatherapp.android.whatstheweather.utils;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.weatherapp.android.whatstheweather.types.Coordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class JSONFileReader extends IntentService {

    public JSONFileReader() {
        super("fileReader");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String fileContent = null;
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            assert intent != null;
            InputStream is = getApplicationContext().getAssets()
                    .open(Objects.requireNonNull(intent.getStringExtra("fileName")));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContent = new String(buffer, "UTF-8");
        }
        catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
        }

        while (!LocationsStorage.isSafeToRead);
        LocationsStorage.isSafeToRead = false;
        try {
            JSONArray locationArray = new JSONArray(fileContent);
            for (int i = 0; i < locationArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(locationArray.get(i).toString());
                String locationName = jsonObject.getString("name") + ", " + jsonObject.getString("country");
                JSONObject locationCoordsObject = new JSONObject(jsonObject.getString("coord"));
                Coordinates coordinates = new Coordinates(locationCoordsObject.getDouble("lon"),
                        locationCoordsObject.getDouble("lat"));
                LocationsStorage.locationsMap.put(locationName, coordinates);
            }
            Map<String, Coordinates> dbMap = new DatabaseHandler(getApplicationContext()).getAllLocations();
            LocationsStorage.locationsMap.putAll(dbMap);
            LocationsStorage.isSafeToRead = true;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
