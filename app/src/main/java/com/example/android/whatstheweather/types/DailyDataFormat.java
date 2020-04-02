package com.example.android.whatstheweather.types;

public class DailyDataFormat {

    public String day;
    public String icon;
    public int minTemp;
    public int maxTemp;

    public DailyDataFormat(String day, String icon, int minTemp, int maxTemp) {
        this.day = day;
        this.icon = icon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}
