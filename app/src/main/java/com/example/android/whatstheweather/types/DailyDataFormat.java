package com.example.android.whatstheweather.types;

public class DailyDataFormat {

    public String day;
    public int icon;
    public String minTemp;
    public String maxTemp;

    public DailyDataFormat(String day, int icon, String minTemp, String maxTemp) {
        this.day = day;
        this.icon = icon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}
