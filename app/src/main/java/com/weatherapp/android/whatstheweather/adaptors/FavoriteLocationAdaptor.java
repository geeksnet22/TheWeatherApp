package com.weatherapp.android.whatstheweather.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherapp.android.whatstheweather.R;
import com.weatherapp.android.whatstheweather.types.FavoriteLocationFormat;
import com.weatherapp.android.whatstheweather.utils.RecyclerViewClickListener;

import java.util.List;

public class FavoriteLocationAdaptor extends RecyclerView.Adapter<FavoriteLocationAdaptor.ViewHolder>  {

    private final List<FavoriteLocationFormat> favoriteLocationsViews;
    private final RecyclerViewClickListener recyclerViewClickListener;
    public FavoriteLocationAdaptor(List<FavoriteLocationFormat> favoriteLocationsViews,
                                   RecyclerViewClickListener recyclerViewClickListener) {
        this.favoriteLocationsViews = favoriteLocationsViews;
        this.recyclerViewClickListener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View favLocationView = inflater.inflate(R.layout.favorite_locations_layout, parent, false);
        return new ViewHolder(favLocationView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteLocationFormat favLocationData = favoriteLocationsViews.get(position);
        holder.deleteLocation.setChecked(favLocationData.deleteLocation);
        holder.deleteLocation.setVisibility(favLocationData.showCheckbox ? View.VISIBLE : View.GONE);
        holder.locationName.setText(favLocationData.locationName);
    }

    @Override
    public int getItemCount() {
        return favoriteLocationsViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final CheckBox deleteLocation;
        private final TextView locationName;

        ViewHolder(View itemView) {
            super(itemView);
            deleteLocation = itemView.findViewById(R.id.favLocationCheckBox);
            locationName = itemView.findViewById(R.id.favLocationName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.recycleViewListClicked(v, this.getLayoutPosition());
        }
    }
}
