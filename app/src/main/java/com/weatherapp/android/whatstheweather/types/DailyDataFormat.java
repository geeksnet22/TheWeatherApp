package com.weatherapp.android.whatstheweather.types;

public class DailyDataFormat {

    public String day;
    public int icon;
    public String precProbability;
    public String minTemp;
    public String maxTemp;

    public DailyDataFormat(String day, int icon, String precProbability, String minTemp, String maxTemp) {
        this.day = day;
        this.icon = icon;
        this.precProbability = precProbability;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}
