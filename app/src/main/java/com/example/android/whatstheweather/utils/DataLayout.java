package com.example.android.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DailyDataFormat;
import com.example.android.whatstheweather.types.HourlyData;
import com.example.android.whatstheweather.types.HourlyDataFormat;

import java.util.List;

public class DataLayout {

    public static void setDataLayout(Activity activity, Context context, CurrentData currentData, HourlyData hourlyData, DailyData dailyData) {
        System.out.println("GSB temperature: " + currentData.temperature);
        setupCurrentInfoLayout(activity, currentData);
        setupHourlyInfoLayout(activity, context, hourlyData);
        setupDailyInfoLayout(activity, context, dailyData);
    }

    private static void setupCurrentInfoLayout(Activity activity, CurrentData currentData) {
        ((TextView) activity.findViewById(R.id.userLocationName)).setText(currentData.locationName);
        ((TextView) activity.findViewById(R.id.currentTime)).setText(currentData.dateTime);
        ((ImageView) activity.findViewById(R.id.currentWeatherIcon)).setImageResource(currentData.icon);
        ((TextView) activity.findViewById(R.id.currentTemp)).setText(currentData.temperature);
        ((TextView) activity.findViewById(R.id.currentSummary)).setText(currentData.summary);
    }

    private static void setupHourlyInfoLayout(Activity activity, Context context, HourlyData hourlyData) {
        HorizontalScrollView hourlyDataView = activity.findViewById(R.id.hourlyScrollView);
        LinearLayout hourlyDataLayout = new LinearLayout(context);

        List<HourlyDataFormat> hourlyDataList = hourlyData.hourlyDataFormatList;

        try {
            for (int i = 0; i < hourlyDataList.size(); i++) {
                View view = activity.getLayoutInflater().inflate(R.layout.hourly_info_layout, null);
                ((TextView) view.findViewById(R.id.hourlyTime)).setText(hourlyDataList.get(i).time);
                ((ImageView) view.findViewById(R.id.hourlyWeatherIcon)).setImageResource(hourlyDataList.get(i).icon);
                ((TextView) view.findViewById(R.id.hourlyTemp)).setText(hourlyDataList.get(i).temperature);
                ((TextView) view.findViewById(R.id.hourlySummary)).setText(hourlyDataList.get(i).summary);
                hourlyDataLayout.addView(view);
            }
        }
        catch (Exception e) {
            System.out.println("GSB error!!!");
            e.printStackTrace();
        }
        hourlyDataView.addView(hourlyDataLayout);
    }

    private static void setupDailyInfoLayout(Activity activity, Context context, DailyData dailyData) {
        HorizontalScrollView dailyScrollView = activity.findViewById(R.id.dailyScrollView);
        LinearLayout dailyDataLayout = new LinearLayout(context);
        List<DailyDataFormat> dailyDataList = dailyData.dailyDataFormatList;
        for (int i = 0; i < dailyDataList.size(); i++) {
            View view = activity.getLayoutInflater().inflate(R.layout.daily_info_layout, null);
            ((TextView) view.findViewById(R.id.dailyTime)).setText(dailyDataList.get(i).day);
            ((ImageView) view.findViewById(R.id.dailyWeatherIcon)).setImageResource(dailyDataList.get(i).icon);
            ((TextView) view.findViewById(R.id.minMaxTemp)).setText(String.format(activity.getResources().getString
                    (R.string.min_max_temp), dailyDataList.get(i).minTemp, dailyDataList.get(i).maxTemp));
            dailyDataLayout.addView(view);
        }
        dailyScrollView.addView(dailyDataLayout);
    }
}
