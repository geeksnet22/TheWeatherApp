package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.location.Geocoder;

import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DailyDataFormat;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LocationDataProcessor {

    private static Geocoder geocoder;

    private static JSONObject jsonifiedData;

    public static CurrentData getCurrentData(String rawData, Context context) throws JSONException, IOException {
        geocoder = new Geocoder(context);
        jsonifiedData = new JSONObject(rawData);

        JSONObject currentData = new JSONObject(rawData).getJSONObject("currently");

        String locationName = geocoder.getFromLocation(jsonifiedData.getDouble("latitude"),
                jsonifiedData.getDouble("longitude"), 1).get(0).getLocality();

        String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                currentData.getLong("time"), "yyyy-MM-dd hh:mm:ss aa");

        return new CurrentData(locationName, currentData.getString("summary"),
                currentData.getInt("temperature") + " C", datetime,
                WeatherIconSelector.getWeatherIcon(currentData.getString("icon")));
    }


    public static HourlyData getHourlyData() throws JSONException {
        JSONArray hourlyData = new JSONArray(jsonifiedData.getJSONObject("hourly").getString("data"));
        List<HourlyDataFormat> hourlyDataFormatList = new ArrayList<>();

        for (int i = 0; i < hourlyData.length(); i++) {
            JSONObject hourlyInfoObject = new JSONObject(hourlyData.getString(i));

            String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                    hourlyInfoObject.getLong("time"), "hh:mm aa");
            int icon = WeatherIconSelector.getWeatherIcon(hourlyInfoObject.getString("icon"));
            String temperature = hourlyInfoObject.getInt("temperature") + " C";
            String summary = hourlyInfoObject.getString("summary");
            hourlyDataFormatList.add(new HourlyDataFormat(datetime, icon, temperature, summary));
        }
        return new HourlyData(hourlyDataFormatList);
    }

    public static DailyData getDailyData() throws JSONException {
        JSONArray dailyData = new JSONArray(jsonifiedData.getJSONObject("daily").getString("data"));
        List<DailyDataFormat> dailyDataFormatList = new ArrayList<>();

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject dailyInfoObject = new JSONObject(dailyData.getString(i));
            dailyDataFormatList.add(new DailyDataFormat(getDayFromTimestamp(jsonifiedData.getString("timezone"),
                    dailyInfoObject.getLong("time")), WeatherIconSelector.getWeatherIcon(dailyInfoObject.getString("icon")),
                    dailyInfoObject.getInt("temperatureMin") + " C", dailyInfoObject.getInt("temperatureMax") + " C"));
        }

        return new DailyData(dailyDataFormatList);
    }

    private static String getCurrentDateTimeAtTimezone(String timezone, long timestamp, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp*1000));
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(calendar.getTime());
    }

    private static String getDayFromTimestamp(String timezone, long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(timestamp*1000);
    }
}
