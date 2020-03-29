package com.example.android.whatstheweather.types;

public class CurrentData {

    public String locationName;
    public String summary;
    public int temperature;
    public String dateTime;
    public String icon;

    public CurrentData(String locationName, String summary, int temperature,
                       String dateTime, String icon) {
        this.locationName = locationName;
        this.summary = summary;
        this.temperature = temperature;
        this.dateTime = dateTime;
        this.icon = icon;
    }

}
