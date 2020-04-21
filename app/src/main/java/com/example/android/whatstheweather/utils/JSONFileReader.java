package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.android.whatstheweather.types.Coordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JSONFileReader extends AsyncTask<Pair<Context, String>, Void, Map<String, Coordinates>> {

    @Override
    protected Map<String, Coordinates> doInBackground(Pair<Context, String>... pairs) {
        String fileContent = null;
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            InputStream is = pairs[0].first.getAssets().open(pairs[0].second);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContent = new String(buffer, "UTF-8");
        }
        catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
        }

        LocationsStorage.isSafeToRead = false;
        try {
            JSONArray locationArray = new JSONArray(fileContent);
            for (int i = 0; i < locationArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(locationArray.get(i).toString());
                String locationName = (jsonObject.getString("name") + ", " + jsonObject.getString("country")).toLowerCase();
                JSONObject locationCoordsObject = new JSONObject(jsonObject.getString("coord"));
                Coordinates coordinates = new Coordinates(locationCoordsObject.getDouble("lon"),
                        locationCoordsObject.getDouble("lat"));
                LocationsStorage.locationsMap.put(locationName, coordinates);
            }
            Map<String, Coordinates> dbMap = new DatabaseHandler(pairs[0].first).getAllLocations();
            LocationsStorage.locationsMap.putAll(dbMap);
            LocationsStorage.isSafeToRead = true;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
