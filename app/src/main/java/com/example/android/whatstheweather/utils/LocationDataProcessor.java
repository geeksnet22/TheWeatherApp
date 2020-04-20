package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Pair;

import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DailyDataFormat;
import com.example.android.whatstheweather.types.DetailsData;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;
import com.example.android.whatstheweather.types.OverallData;

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
import java.util.concurrent.ExecutionException;

public class LocationDataProcessor extends AsyncTask<Pair<Context, String>, Void, OverallData> {

    private Geocoder geocoder;
    private JSONObject jsonifiedData;

    private Pair<Context, String> pair;

    public LocationDataProcessor(Pair<Context, String> pair) throws JSONException {
        this.pair = pair;
        this.geocoder = new Geocoder(pair.first);
        this.jsonifiedData = new JSONObject(pair.second);
    }

    private CurrentData getCurrentData() throws JSONException, IOException {
        JSONObject currentData = jsonifiedData.getJSONObject("currently");

        String locationName = geocoder.getFromLocation(jsonifiedData.getDouble("latitude"),
                jsonifiedData.getDouble("longitude"), 1).get(0).getLocality();

        String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                currentData.getLong("time"), "yyyy-MM-dd hh:mm:ss aa");

        return new CurrentData(locationName, currentData.getString("summary"),
                currentData.getInt("temperature") + " C", datetime,
                WeatherIconSelector.getWeatherIcon(currentData.getString("icon")));
    }


    private HourlyData getHourlyData() throws JSONException {
        JSONArray hourlyData = new JSONArray(jsonifiedData.getJSONObject("hourly").getString("data"));
        List<HourlyDataFormat> hourlyDataFormatList = new ArrayList<>();

        for (int i = 1; i < hourlyData.length(); i++) {
            JSONObject hourlyInfoObject = new JSONObject(hourlyData.getString(i));

            String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                    hourlyInfoObject.getLong("time"), "hh:mm aa");
            int icon = WeatherIconSelector.getWeatherIcon(hourlyInfoObject.getString("icon"));
            String temperature = hourlyInfoObject.getInt("temperature") + " C";
            String rawSummary = hourlyInfoObject.getString("summary");

            String[] summaryArray= rawSummary.split(" ");
            StringBuilder summary = new StringBuilder();
            for (int j = 0; j < summaryArray.length; j++) {
                summary.append(summaryArray[j]);
                summary.append(" ");
                if (j % 2 == 1 && j < summaryArray.length-1) {
                    summary.append("\n");
                }
            }
            String precProbability = (int) (hourlyInfoObject.getDouble("precipProbability") * 100) + "%";
            hourlyDataFormatList.add(new HourlyDataFormat(datetime, icon, temperature, precProbability, summary.toString()));
        }
        return new HourlyData(hourlyDataFormatList);
    }

    private DailyData getDailyData() throws JSONException {
        JSONArray dailyData = new JSONArray(jsonifiedData.getJSONObject("daily").getString("data"));
        List<DailyDataFormat> dailyDataFormatList = new ArrayList<>();

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject dailyInfoObject = new JSONObject(dailyData.getString(i));
            dailyDataFormatList.add(new DailyDataFormat((i == 0) ? "Today" : getDayFromTimestamp(jsonifiedData.getString("timezone"),
                    dailyInfoObject.getLong("time")), WeatherIconSelector.getWeatherIcon(dailyInfoObject.getString("icon")),
                    (int) (dailyInfoObject.getDouble("precipProbability") * 100) + "%",
                    dailyInfoObject.getInt("temperatureMin") + " C", dailyInfoObject.getInt("temperatureMax") + " C"));
        }

        return new DailyData(dailyDataFormatList);
    }

    private DetailsData getDetailsData() throws JSONException {
        JSONObject currentData = jsonifiedData.getJSONObject("currently");
        JSONArray dailyData = new JSONArray(jsonifiedData.getJSONObject("daily").getString("data"));

        String sunriseTime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                new JSONObject(dailyData.getString(0)).getLong("sunriseTime"), "hh:mm aa");
        String sunsetTime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                new JSONObject(dailyData.getString(0)).getLong("sunsetTime"), "hh:mm aa");

        return new DetailsData(getUvIndexLevel(currentData.getInt("uvIndex")), sunriseTime,
                sunsetTime, (int)(currentData.getDouble("humidity") * 100) + "%");
    }

    private String getUvIndexLevel(int uvIndex) {
        String uvIndexLevel;
        if (uvIndex <= 2) {
            uvIndexLevel = "Low";
        }
        else if (uvIndex <=5) {
            uvIndexLevel = "Moderate";
        }
        else if (uvIndex <= 7) {
            uvIndexLevel = "High";
        }
        else {
            uvIndexLevel = "Very High";
        }
        return uvIndexLevel;
    }

    private String getCurrentDateTimeAtTimezone(String timezone, long timestamp, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp*1000));
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(calendar.getTime());
    }

    private String getDayFromTimestamp(String timezone, long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(timestamp*1000);
    }

    @Override
    protected OverallData doInBackground(Pair<Context, String>... pairs) {
        try {
            return new OverallData(getCurrentData(), getHourlyData(), getDailyData(), getDetailsData());
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public OverallData fetchWeatherData(Pair<Context, String> pair) throws JSONException, ExecutionException, InterruptedException {
        return new LocationDataProcessor(pair).execute().get();
    }
}
