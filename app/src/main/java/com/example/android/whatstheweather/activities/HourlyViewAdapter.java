package com.example.android.whatstheweather.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.whatstheweather.R;
import com.example.android.whatstheweather.types.HourlyDataFormat;

import java.util.List;

public class HourlyViewAdapter extends RecyclerView.Adapter<HourlyViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private ImageView weatherIcon;
        private TextView temperature;
        private TextView summary;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.hourlyTime);
            weatherIcon = itemView.findViewById(R.id.hourlyWeatherIcon);
            temperature = itemView.findViewById(R.id.hourlyTemp);
            summary = itemView.findViewById(R.id.hourlySummary);
        }
    }

    private List<HourlyDataFormat> hourlyViews;

    public HourlyViewAdapter(List<HourlyDataFormat> hourlyViews) {
        this.hourlyViews = hourlyViews;
    }

    @NonNull
    @Override
    public HourlyViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View hourlyInfoView = inflater.inflate(R.layout.hourly_info_layout, parent, false);
        return new ViewHolder(hourlyInfoView);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewAdapter.ViewHolder holder, int position) {
        HourlyDataFormat hourlyData = hourlyViews.get(position);

        TextView timeView = holder.time;
        timeView.setText(hourlyData.time);

        ImageView weatherIcon = holder.weatherIcon;
        weatherIcon.setImageResource(hourlyData.icon);

        TextView temperatureView = holder.temperature;
        temperatureView.setText(hourlyData.temperature);

        TextView summaryView = holder.summary;
        summaryView.setText(hourlyData.summary);
    }

    @Override
    public int getItemCount() {
        return hourlyViews.size();
    }
}
