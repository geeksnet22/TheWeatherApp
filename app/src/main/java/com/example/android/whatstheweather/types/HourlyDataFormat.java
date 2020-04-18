package com.example.android.whatstheweather.types;

public class HourlyDataFormat {

    public String time;
    public int icon;
    public String temperature;
    public String precProbability;
    public String summary;

    public HourlyDataFormat(String time, int icon, String temperature, String precProbability, String summary) {
        this.time = time;
        this.icon = icon;
        this.temperature = temperature;
        this.precProbability = precProbability;
        this.summary = summary;
    }
}
