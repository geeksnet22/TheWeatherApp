package com.example.android.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.view.ActionProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DetailsData;
import com.example.android.whatstheweather.types.HourlyData;


public class DataLayoutSetter {

    public static void setDataLayout(Activity activity, Context context, CurrentData currentData,
                                     HourlyData hourlyData, DailyData dailyData, DetailsData detailsData) {
        setupCurrentInfoLayout(activity, currentData);

        RecyclerView hourlyView = activity.findViewById(R.id.hourlyView);
        hourlyView.setHasFixedSize(true);
        HourlyViewAdapter hourlyViewAdapter = new HourlyViewAdapter(hourlyData.hourlyDataFormatList);
        hourlyView.setAdapter(hourlyViewAdapter);
        hourlyView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        RecyclerView dailyView = activity.findViewById(R.id.dailyView);
        dailyView.setHasFixedSize(true);
        DailyViewAdapter dailyViewAdapter = new DailyViewAdapter(dailyData.dailyDataFormatList);
        dailyView.setAdapter(dailyViewAdapter);
        dailyView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        setupDetailsInfoLayout(activity, detailsData);
    }

    private static void setupCurrentInfoLayout(Activity activity, CurrentData currentData) {
        ((TextView) activity.findViewById(R.id.userLocationName)).setText(currentData.locationName);
        ((TextView) activity.findViewById(R.id.currentTime)).setText(currentData.dateTime);
        ((ImageView) activity.findViewById(R.id.currentWeatherIcon)).setImageResource(currentData.icon);
        ((TextView) activity.findViewById(R.id.currentTemp)).setText(currentData.temperature);
        ((TextView) activity.findViewById(R.id.currentSummary)).setText(currentData.summary);
    }

    private static void setupDetailsInfoLayout(Activity activity, DetailsData detailsData) {

        ((TextView) activity.findViewById(R.id.uvIndex)).setText(detailsData.uvIndex);
        ((TextView) activity.findViewById(R.id.sunrise)).setText(detailsData.sunrise);
        ((TextView) activity.findViewById(R.id.sunset)).setText(detailsData.sunset);
        ((TextView) activity.findViewById(R.id.humidity)).setText(detailsData.humidity);
    }
}
