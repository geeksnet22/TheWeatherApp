package com.example.android.whatstheweather.types;

import java.util.List;

public class DailyData {

    List<DailyDataFormat> dailyDataFormatList;

    public DailyData(List<DailyDataFormat> dailyDataFormatList) {
        this.dailyDataFormatList = dailyDataFormatList;
    }
}
