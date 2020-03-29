package com.example.android.whatstheweather.types;

public class DailyData {

    public String day;

    public String icon;

    public double minTemp;

    public double maxTemp;

    public DailyData(String day, String icon, double minTemp, double maxTemp) {
        this.day = day;
        this.icon = icon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}
