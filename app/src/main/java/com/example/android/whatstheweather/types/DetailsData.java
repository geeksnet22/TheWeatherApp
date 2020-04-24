package com.example.android.whatstheweather.types;

public class DetailsData {

    public String uvIndex;
    public String sunrise;
    public String sunset;
    public String humidity;
    public String windspeed;
    public String visibility;

    public DetailsData(String uvIndex, String sunrise, String sunset, String humidity,
                       String windspeed, String visibility) {
        this.uvIndex = uvIndex;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.visibility = visibility;
    }
}
