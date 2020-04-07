package com.example.android.whatstheweather.types;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.activities.SearchActivity;
import com.example.android.whatstheweather.utils.WeatherIconSelector;

public class DataLayout {

    public static ScrollView getDataLayout(Context context, Activity activity, CurrentData currentData, HourlyData hourlyData, DailyData dailyData, boolean addSearchButton) {

        ScrollView overallScroll = new ScrollView(context);
        overallScroll.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout overallLayout = new LinearLayout(context);
        overallLayout.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        overallLayout.setGravity(Gravity.CENTER);
        overallLayout.setOrientation(LinearLayout.VERTICAL);

        if (addSearchButton) {
            overallLayout.addView(getSearchButton(context, activity));
        }

        overallLayout.addView(getCurrentInfoLayout(context, currentData));

        overallLayout.addView(getHeading(context, "Hourly"));
        overallLayout.addView(getHourlyInfoLayout(context, hourlyData));

        overallLayout.addView(getHeading(context, "Daily"));
        overallLayout.addView(getDailyInfoLayout(context, dailyData));

        overallScroll.addView(overallLayout);

        return overallScroll;
    }

    public static Button getSearchButton(final Context context, final Activity activity) {
        Button searchButton = new Button(context);
        searchButton.setGravity(Gravity.CENTER);
        searchButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        searchButton.setText("Search location");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(context, SearchActivity.class);
                activity.startActivity(searchActivity);
            }
        });
        return searchButton;
    }

    private static LinearLayout.LayoutParams getLayoutParams(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
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

        HorizontalScrollView dailyInfoScroll = new HorizontalScrollView(context);
        dailyInfoScroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout dailyInfoLayout = new LinearLayout(context);
        dailyInfoLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < dailyData.dailyDataFormatList.size(); i++) {
            LinearLayout dailyInfoFormatLayout = new LinearLayout(context);
            dailyInfoFormatLayout.setLayoutParams(getLayoutParams(0, 0, 100, 0));
            dailyInfoFormatLayout.setGravity(Gravity.CENTER);
            dailyInfoFormatLayout.setOrientation(LinearLayout.VERTICAL);

            TextView dayView = new TextView(context);
            dayView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            dayView.append(dailyData.dailyDataFormatList.get(i).day);
            dayView.setTextSize(25);
            dayView.setTextColor(Color.WHITE);
            dayView.setGravity(Gravity.CENTER);

            ImageView weatherIcon = new ImageView(context);
            weatherIcon.setImageResource(WeatherIconSelector.getWeatherIcon(dailyData.dailyDataFormatList.get(i).icon));

            TextView minMaxTemp = new TextView(context);
            minMaxTemp.setLayoutParams(new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)));
            minMaxTemp.setTextSize(25);
            minMaxTemp.setTextColor(Color.WHITE);
            minMaxTemp.setGravity(Gravity.CENTER);
            minMaxTemp.append(dailyData.dailyDataFormatList.get(i).minTemp + " C-"
                    + dailyData.dailyDataFormatList.get(i).maxTemp + " C");

            dailyInfoFormatLayout.addView(dayView);
            dailyInfoFormatLayout.addView(weatherIcon);
            dailyInfoFormatLayout.addView(minMaxTemp);

            dailyInfoLayout.addView(dailyInfoFormatLayout);
        }

        dailyInfoScroll.addView(dailyInfoLayout);
        return dailyInfoScroll;
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
            timeView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            timeView.append(hourlyData.hourlyDataFormatList.get(i).time);
            timeView.setTextSize(25);
            timeView.setTextColor(Color.WHITE);
            timeView.setGravity(Gravity.CENTER);

            LinearLayout temperatureLayout = new LinearLayout(context);
            temperatureLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            temperatureLayout.setLayoutParams(layoutParams);
            temperatureLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView temperatureIcon = new ImageView(context);
            temperatureIcon.setImageResource(WeatherIconSelector.getWeatherIcon(hourlyData.hourlyDataFormatList.get(i).icon));

            TextView temperature = new TextView(context);
            temperature.append(hourlyData.hourlyDataFormatList.get(i).temperature + " C");
            temperature.setTextColor(Color.WHITE);
            temperature.setTextSize(25);
            temperature.setGravity(Gravity.CENTER);

            temperatureLayout.addView(temperatureIcon);
            temperatureLayout.addView(temperature);

            TextView summaryView = new TextView(context);
            summaryView.setLayoutParams(new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
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
        locationIcon.setForegroundGravity(Gravity.CENTER);
        locationIcon.setImageResource(R.drawable.location_pin);

        TextView locationName = new TextView(context);
        locationName.append(currentData.locationName);
        locationName.setTextSize(25);
        locationName.setTextColor(Color.WHITE);
        locationName.setGravity(Gravity.CENTER);

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
        weatherIcon.setImageResource(WeatherIconSelector.getWeatherIcon(currentData.icon));

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
