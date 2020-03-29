package com.example.android.whatstheweather.utils;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.DarkSkyClient;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.model.Latitude;
import tk.plogitech.darksky.forecast.model.Longitude;

public class ExtractData extends AsyncTask<Double, Void, String> {
    @Override
    protected String doInBackground(Double... params) {

        final ForecastRequest request = new ForecastRequestBuilder().key(new APIKey("7d7c4d51abd38384fd51a174d0771a5d"))
                .location(new GeoCoordinates(new Longitude(params[1]), new Latitude(params[0])))
                .language(ForecastRequestBuilder.Language.en).units(ForecastRequestBuilder.Units.si)
                .build();

        DarkSkyClient client = new DarkSkyClient();
        try {
            return client.forecastJsonString(request);
        }
        catch (ForecastException e) {
            e.printStackTrace();
            return "failed";
        }
    }

    /**
     * Return string consisting of weather information
     * @param latitude latitude corresponding to the location
     * @param longitude longitude corresponding to the location
     * @return Return string consisting of weather information
     */
    public static String extractData(double latitude, double longitude) throws ExecutionException, InterruptedException {
        return new ExtractData().execute(latitude, longitude).get();
    }
}
