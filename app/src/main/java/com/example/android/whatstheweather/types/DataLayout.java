package com.example.android.whatstheweather.types;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.android.whatstheweather.R;

public class DataLayout {

    public static SearchView searchLocation;

    public static ScrollView getDataLayout(Context context, CurrentData currentData, HourlyData hourlyData, boolean addSearchView) {

        ScrollView overallScroll = new ScrollView(context);
        overallScroll.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout overallLayout = new LinearLayout(context);
        overallLayout.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        overallLayout.setGravity(Gravity.CENTER);
        overallLayout.setOrientation(LinearLayout.VERTICAL);

        if (addSearchView) {
            overallLayout.addView(getLocationSearchView(context));
        }
        overallLayout.addView(getCurrentInfoLayout(context, currentData));

        overallLayout.addView(getHeading(context, "Hourly"));
        overallLayout.addView(getHourlyInfoLayout(context, hourlyData));

        overallScroll.addView(overallLayout);

        return overallScroll;
    }

    public static SearchView getLocationSearchView(Context context) {
        searchLocation = new SearchView(context);
        searchLocation.setLayoutParams(getLayoutParams(5,5,5,5));
        searchLocation.setQueryHint("Location name...");
        searchLocation.setIconifiedByDefault(false);
        return searchLocation;
    }

    private static LinearLayout.LayoutParams getLayoutParams(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    private static TextView getHeading(Context context, String content) {
        TextView headingText = new TextView(context);
        headingText.append(content);
        headingText.setGravity(Gravity.LEFT);
        headingText.setTextColor(Color.WHITE);
        headingText.setTextSize(30);
        return headingText;
    }

    private static HorizontalScrollView getDailyInfoLayout(Context context, DailyData dailyData) {



        return null;
    }

    private static HorizontalScrollView getHourlyInfoLayout(Context context, HourlyData hourlyData) {

        HorizontalScrollView hourlyInfoScroll = new HorizontalScrollView(context);
        hourlyInfoScroll.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout hourlyInfoLayout = new LinearLayout(context);
        hourlyInfoLayout.setOrientation(LinearLayout.HORIZONTAL);



        for (int i = 0; i < hourlyData.hourlyDataFormatList.size(); i++) {
            LinearLayout hourlyInfoFormatLayout = new LinearLayout(context);
            hourlyInfoFormatLayout.setLayoutParams(getLayoutParams(0, 0, 100, 0));
            hourlyInfoFormatLayout.setGravity(Gravity.CENTER);
            hourlyInfoFormatLayout.setOrientation(LinearLayout.VERTICAL);

            TextView timeView = new TextView(context);
            timeView.append(hourlyData.hourlyDataFormatList.get(i).time);
            timeView.setTextSize(25);
            timeView.setTextColor(Color.WHITE);
            timeView.setGravity(Gravity.CENTER);

            LinearLayout temperatureLayout = new LinearLayout(context);
            temperatureLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            temperatureLayout.setLayoutParams(layoutParams);
            temperatureLayout.setOrientation(LinearLayout.HORIZONTAL);
            ImageView temperatureIcon = new ImageView(context);
            temperatureIcon.setForegroundGravity(Gravity.CENTER);
            temperatureIcon.setImageResource(R.drawable.cloud_snowing_cloud_climate);
            TextView temperature = new TextView(context);
            temperature.append(String.valueOf(hourlyData.hourlyDataFormatList.get(i).temperature));
            temperature.setTextColor(Color.WHITE);
            temperature.setTextSize(25);
            temperature.setGravity(Gravity.CENTER);
            temperatureLayout.addView(temperatureIcon);
            temperatureLayout.addView(temperature);


            TextView summaryView = new TextView(context);
            summaryView.setLayoutParams(new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)));
            summaryView.append(hourlyData.hourlyDataFormatList.get(i).summary);
            summaryView.setTextColor(Color.WHITE);
            summaryView.setTextSize(25);
            summaryView.setGravity(Gravity.CENTER);

            hourlyInfoFormatLayout.addView(timeView);
            hourlyInfoFormatLayout.addView(temperatureLayout);
            hourlyInfoFormatLayout.addView(summaryView);

            hourlyInfoLayout.addView(hourlyInfoFormatLayout);
        }

        hourlyInfoScroll.addView(hourlyInfoLayout);
        return hourlyInfoScroll;
    }

    private static LinearLayout getCurrentInfoLayout(Context context, CurrentData currentData) {
        LinearLayout currentInfoLayout = new LinearLayout(context);
        currentInfoLayout.setGravity(Gravity.CENTER);
        currentInfoLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout locationLayout = new LinearLayout(context);
        locationLayout.setGravity(Gravity.CENTER);
        locationLayout.setOrientation(LinearLayout.HORIZONTAL);


        ImageView locationIcon = new ImageView(context);
        locationIcon.setLayoutParams(getLayoutParams(5,5,5,5));
        locationIcon.setImageResource(R.drawable.location_pin);

        TextView locationName = new TextView(context);
        locationName.append(currentData.locationName);
        locationName.setTextSize(25);
        locationName.setTextColor(Color.WHITE);
        locationName.setLayoutParams(getLayoutParams(5,5,5,5));

        locationLayout.addView(locationIcon);
        locationLayout.addView(locationName);

        TextView datetime = new TextView(context);
        datetime.setGravity(Gravity.CENTER);
        datetime.append(currentData.dateTime);
        datetime.setTextSize(20);
        datetime.setTextColor(Color.WHITE);

        LinearLayout temperatureLayout = new LinearLayout(context);
        temperatureLayout.setGravity(Gravity.CENTER);
        temperatureLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView weatherIcon = new ImageView(context);
        weatherIcon.setLayoutParams(getLayoutParams(5,5,5,5));
        weatherIcon.setImageResource(R.drawable.hot_sun_day);

        TextView temperature = new TextView(context);
        temperature.setLayoutParams(getLayoutParams(5,5,5,5));
        temperature.append(currentData.temperature + " C");
        temperature.setTextColor(Color.WHITE);
        temperature.setTextSize(25);

        temperatureLayout.addView(weatherIcon);
        temperatureLayout.addView(temperature);

        TextView summary = new TextView(context);
        summary.setGravity(Gravity.CENTER);
        summary.append(currentData.summary);
        summary.setTextColor(Color.WHITE);
        summary.setTextSize(25);

        /* Add all views to final layout */
        currentInfoLayout.addView(locationLayout);
        currentInfoLayout.addView(datetime);
        currentInfoLayout.addView(temperatureLayout);
        currentInfoLayout.addView(summary);

        return currentInfoLayout;
    }
}
