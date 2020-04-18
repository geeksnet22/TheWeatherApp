package com.example.android.whatstheweather.utils;

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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private ImageView weatherIcon;
        private TextView temperature;
        private TextView precProbability;
        private TextView summary;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.hourlyTime);
            weatherIcon = itemView.findViewById(R.id.hourlyWeatherIcon);
            temperature = itemView.findViewById(R.id.hourlyTemp);
            precProbability = itemView.findViewById(R.id.hourlyPrecProbability);
            summary = itemView.findViewById(R.id.hourlySummary);
        }
    }

    private List<HourlyDataFormat> hourlyViews;

    HourlyViewAdapter(List<HourlyDataFormat> hourlyViews) {
        this.hourlyViews = hourlyViews;
    }

    @NonNull
    @Override
    public HourlyViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View hourlyInfoView = inflater.inflate(R.layout.hourly_info_layout, parent, false);
        return new ViewHolder(hourlyInfoView);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewAdapter.ViewHolder holder, int position) {
        HourlyDataFormat hourlyData = hourlyViews.get(position);

        holder.time.setText(hourlyData.time);
        holder.weatherIcon.setImageResource(hourlyData.icon);
        holder.temperature.setText(hourlyData.temperature);
        holder.precProbability.setText(hourlyData.precProbability);
        holder.summary.setText(hourlyData.summary);
    }

    @Override
    public int getItemCount() {
        return hourlyViews.size();
    }
}
