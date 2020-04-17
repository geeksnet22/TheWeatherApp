package com.example.android.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.CurrentData;
import com.example.android.whatstheweather.types.DailyData;
import com.example.android.whatstheweather.types.DailyDataFormat;
import com.example.android.whatstheweather.types.HourlyData;

import java.util.List;

public class DataLayoutSetter {

    public static void setDataLayout(Activity activity, Context context, CurrentData currentData, HourlyData hourlyData, DailyData dailyData) {
        setupCurrentInfoLayout(activity, currentData);

        RecyclerView hourlyView = activity.findViewById(R.id.hourlyView);
        HourlyViewAdapter hourlyViewAdapter = new HourlyViewAdapter(hourlyData.hourlyDataFormatList);
        hourlyView.setAdapter(hourlyViewAdapter);
        hourlyView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        RecyclerView dailyView = activity.findViewById(R.id.dailyView);
        DailyViewAdapter dailyViewAdapter = new DailyViewAdapter(dailyData.dailyDataFormatList);
        dailyView.setAdapter(dailyViewAdapter);
        dailyView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    private static void setupCurrentInfoLayout(Activity activity, CurrentData currentData) {
        ((TextView) activity.findViewById(R.id.userLocationName)).setText(currentData.locationName);
        ((TextView) activity.findViewById(R.id.currentTime)).setText(currentData.dateTime);
        ((ImageView) activity.findViewById(R.id.currentWeatherIcon)).setImageResource(currentData.icon);
        ((TextView) activity.findViewById(R.id.currentTemp)).setText(currentData.temperature);
        ((TextView) activity.findViewById(R.id.currentSummary)).setText(currentData.summary);
    }
}
