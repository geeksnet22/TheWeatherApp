package com.example.android.whatstheweather.types;

public class CurrentData {

    public String locationName;
    public String summary;
    public String temperature;
    public String dateTime;
    public int icon;

    public CurrentData(String locationName, String summary, String temperature,
                       String dateTime, int icon) {
        this.locationName = locationName;
        this.summary = summary;
        this.temperature = temperature;
        this.dateTime = dateTime;
        this.icon = icon;
    }

}
