package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.location.Geocoder;
import android.widget.ArrayAdapter;

import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocationDataProcessor {

    private static Geocoder geocoder;

    private static JSONObject jsonifiedData;

    public static CurrentData getCurrentData(String rawData, Context context) throws JSONException, IOException {

        geocoder = new Geocoder(context);

        jsonifiedData = new JSONObject(rawData);

        JSONObject currentData = new JSONObject(rawData).getJSONObject("currently");


        String locationName = geocoder.getFromLocation(jsonifiedData.getDouble("latitude"),
                jsonifiedData.getDouble("longitude"), 1).get(0).getLocality();

        Date datetime = convertTimeStringToDate(currentData.getLong("time"));

        return new CurrentData(locationName, currentData.getString("summary"),
                currentData.getInt("temperature"), datetime.toString(), currentData.getString("icon"));
    }


    public static HourlyData getHourlyData(String rawData, Context context) throws JSONException {

        JSONArray hourlyData = new JSONArray(jsonifiedData.getJSONObject("hourly").getString("data"));

        List<HourlyDataFormat> hourlyDataFormatList = new ArrayList<>();

        for (int i = 0; i < hourlyData.length(); i++) {
            JSONObject hourlyInfoObject = new JSONObject(hourlyData.getString(i));

            Date datetime = convertTimeStringToDate(hourlyInfoObject.getLong("time"));

            String time = ( datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime.getHours() )
                    + ":" + ( datetime.getMinutes() < 10 ? datetime.getMinutes() + "0" : datetime.getMinutes() );
            String icon = hourlyInfoObject.getString("icon");
            int temperature = (int) hourlyInfoObject.getLong("temperature");
            String summary = hourlyInfoObject.getString("summary");

            hourlyDataFormatList.add(new HourlyDataFormat(time, icon, temperature, summary));
        }
        return new HourlyData(hourlyDataFormatList);
    }

    public static DailyData getDailyData(String rawData, Context context) throws JSONException {

        System.out.println("GSB daily data: " + new JSONObject(rawData).getString("daily"));

        return null;
    }

    private static Date convertTimeStringToDate(long time) {
        return new Date(new Timestamp(time*1000).getTime());
    }
}
