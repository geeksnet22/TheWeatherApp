package com.weatherapp.android.whatstheweather.types;

import java.util.List;

public class DailyData {

    public List<DailyDataFormat> dailyDataFormatList;

    public DailyData(List<DailyDataFormat> dailyDataFormatList) {
        this.dailyDataFormatList = dailyDataFormatList;
    }
}
