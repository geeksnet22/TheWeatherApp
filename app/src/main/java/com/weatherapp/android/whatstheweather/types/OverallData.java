package com.weatherapp.android.whatstheweather.types;

public class OverallData {

    public CurrentData currentData;
    public HourlyData hourlyData;
    public DailyData dailyData;
    public DetailsData detailsData;

    public OverallData(CurrentData currentData, HourlyData hourlyData, DailyData dailyData, DetailsData detailsData) {
        this.currentData = currentData;
        this.hourlyData = hourlyData;
        this.dailyData = dailyData;
        this.detailsData = detailsData;
    }
}