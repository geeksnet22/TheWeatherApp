package com.example.android.whatstheweather.utils;

import android.content.Context;
import android.location.Geocoder;
import android.widget.ArrayAdapter;

import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DailyDataFormat;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class LocationDataProcessor {

    private static Geocoder geocoder;

    private static JSONObject jsonifiedData;

    public static CurrentData getCurrentData(String rawData, Context context) throws JSONException, IOException {

        geocoder = new Geocoder(context);

        jsonifiedData = new JSONObject(rawData);

        JSONObject currentData = new JSONObject(rawData).getJSONObject("currently");
//        System.out.println("GSB current: " + rawData);
        String locationName = geocoder.getFromLocation(jsonifiedData.getDouble("latitude"),
                jsonifiedData.getDouble("longitude"), 1).get(0).getLocality();

        String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                currentData.getLong("time"));

        return new CurrentData(locationName, currentData.getString("summary"),
                currentData.getInt("temperature"), datetime, currentData.getString("icon"));
    }


    public static HourlyData getHourlyData() throws JSONException {

        JSONArray hourlyData = new JSONArray(jsonifiedData.getJSONObject("hourly").getString("data"));

        List<HourlyDataFormat> hourlyDataFormatList = new ArrayList<>();

        for (int i = 0; i < hourlyData.length(); i++) {
            JSONObject hourlyInfoObject = new JSONObject(hourlyData.getString(i));

            String datetime = getCurrentDateTimeAtTimezone(jsonifiedData.getString("timezone"),
                    hourlyInfoObject.getLong("time"));
            String hours = datetime.substring(11, 13);
            String mins = datetime.substring(14,16);

            String time = "";
            while (time.length() < 5) {
                time = ( hours.length() < 2 ? "0" + hours : hours )
                        + ":" + ( mins.length() < 2 ? mins + "0" : mins );
            }
            String icon = hourlyInfoObject.getString("icon");
            int temperature = (int) hourlyInfoObject.getLong("temperature");
            String summary = hourlyInfoObject.getString("summary");
            hourlyDataFormatList.add(new HourlyDataFormat(time, icon, temperature, summary));
        }
        return new HourlyData(hourlyDataFormatList);
    }

    public static DailyData getDailyData() throws JSONException {

        System.out.println("GSB daily data: " + jsonifiedData.getJSONObject("daily").getString("data"));

        JSONArray dailyData = new JSONArray(jsonifiedData.getJSONObject("daily").getString("data"));

        List<DailyDataFormat> dailyDataFormatList = new ArrayList<>();

        for (int i = 0; i < dailyData.length(); i++) {
            JSONObject dailyInfoObject = new JSONObject(dailyData.getString(i));

            dailyDataFormatList.add(new DailyDataFormat(getDayFromTimestamp(jsonifiedData.getString("timezone"),
                    dailyInfoObject.getLong("time")), dailyInfoObject.getString("icon"),
                    dailyInfoObject.getInt("temperatureMin"), dailyInfoObject.getInt("temperatureMax")));
        }

        return new DailyData(dailyDataFormatList);
    }

    private static String getCurrentDateTimeAtTimezone(String timezone, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp*1000));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(calendar.getTime());
    }

    private static String getDayFromTimestamp(String timezone, long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(timestamp*1000);
    }
}
