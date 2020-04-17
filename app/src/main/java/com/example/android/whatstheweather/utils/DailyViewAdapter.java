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
import com.example.android.whatstheweather.types.DailyDataFormat;

import java.util.List;

public class DailyViewAdapter extends RecyclerView.Adapter<DailyViewAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView day;
        private ImageView weatherIcon;
        private TextView minMaxTemp;

        ViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.dailyTime);
            weatherIcon = itemView.findViewById(R.id.dailyWeatherIcon);
            minMaxTemp = itemView.findViewById(R.id.minMaxTemp);
        }
    }

    private List<DailyDataFormat> dailyViews;

    DailyViewAdapter(List<DailyDataFormat> dailyViews) {
        this.dailyViews = dailyViews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View dailyInfoView = inflater.inflate(R.layout.daily_info_layout, parent, false);
        return new ViewHolder(dailyInfoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyDataFormat dailyData = dailyViews.get(position);

        holder.day.setText(dailyData.day);
        holder.weatherIcon.setImageResource(dailyData.icon);
        holder.minMaxTemp.setText(dailyData.minTemp + " - " + dailyData.maxTemp);
    }

    @Override
    public int getItemCount() {
        return dailyViews.size();
    }
}
