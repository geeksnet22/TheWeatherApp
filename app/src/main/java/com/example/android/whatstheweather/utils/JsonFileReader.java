package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class JsonFileReader extends AsyncTask<Pair<Context, String>, Void, String> {

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

    public static JSONArray readFile(Pair<Context, String> pair) throws ExecutionException, InterruptedException, JSONException {


        return new JSONArray(new JsonFileReader().execute(pair).get());
    }
}
