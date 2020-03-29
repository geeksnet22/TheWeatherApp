package com.example.android.whatstheweather.types;

public class HourlyDataFormat {

    public String time;
    public String icon;
    public int temperature;
    public String summary;

    public HourlyDataFormat(String time, String icon, int temperature, String summary) {
        this.time = time;
        this.icon = icon;
        this.temperature = temperature;
        this.summary = summary;
    }
}
