package com.weatherapp.android.whatstheweather.types;

public class FavoriteLocationFormat {

    public boolean showCheckbox;
    public boolean deleteLocation;
    public String locationName;

    public FavoriteLocationFormat(boolean showCheckbox, boolean deleteLocation, String locationName) {
        this.showCheckbox = showCheckbox;
        this.deleteLocation = deleteLocation;
        this.locationName = locationName;
    }
}
