package com.example.android.whatstheweather.utils;

import com.example.android.whatstheweather.R;

public class WeatherIconSelector {

    public static int getWeatherIcon(String iconDesc) {
        int weatherIcon;
        switch (iconDesc) {
            case "clear-day":
                weatherIcon = R.drawable.hot_sun_day;
                break;
            case "clear-night":
                weatherIcon = R.drawable.moon_night_sky;
                break;
            case "partly-cloudy-day":
                weatherIcon = R.drawable.sunny_sun_cloudy;
                break;
            case "partly-cloudy-night":
                weatherIcon = R.drawable.moon_night_cloud;
                break;
            case "cloudy":
                weatherIcon = R.drawable.cloudy_season_cloud;
                break;
            case "rain":
                weatherIcon = R.drawable.rain_cloud_climate;
                break;
            case "fog":
                weatherIcon = R.drawable.cloudy_season_cloud;
                break;
            case "snow":
                weatherIcon = R.drawable.cloud_snowing_cloud_climate;
                break;
            default:
                weatherIcon = R.drawable.rain_cloud_climate;
        }
        return weatherIcon;
    }

}
