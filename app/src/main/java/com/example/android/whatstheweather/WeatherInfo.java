package com.example.android.whatstheweather;

public class WeatherInfo {

    private String weatherType;

    private String weatherInfo;

    public WeatherInfo(String weatherType, String weatherInfo)
    {
        this.weatherType = weatherType;
        this.weatherInfo = weatherInfo;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public String getWeatherType() {
        return weatherType;
    }
}
