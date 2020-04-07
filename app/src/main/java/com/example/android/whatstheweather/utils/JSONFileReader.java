package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.android.whatstheweather.types.Coordinates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JSONFileReader extends AsyncTask<Pair<Context, String>, Void, String> {

    public static Map<String, Coordinates> locationMap = new HashMap<>();

    @Override
    protected String doInBackground(Pair<Context, String>... pairs) {
        StringBuilder fileContent = new StringBuilder();
        try {
            InputStream inputStream = pairs[0].first.getAssets().open(pairs[0].second);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }

    public static void readFile(Pair<Context, String> pair) throws ExecutionException, InterruptedException, JSONException {
        JSONArray locationArray = new JSONArray(new JSONFileReader().execute(pair).get());
        for (int i = 0; i < locationArray.length(); i++) {
            JSONObject locationObject = new JSONObject(locationArray.getString(i));
            JSONObject coord = new JSONObject(locationObject.getString("coord"));
            locationMap.put((locationObject.getString("name") + ", " +
                            locationObject.getString("country")).toLowerCase(),
                    new Coordinates(Double.parseDouble(coord.getString("lon")),
                            Double.parseDouble(coord.getString("lat"))));
        }
    }
}
