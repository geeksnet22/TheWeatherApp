package com.example.android.whatstheweather.utils;

import com.example.android.whatstheweather.types.Coordinates;

import java.time.LocalDate;

public class Location {

    public String name;
    public String coordinates;

    public Location(String name, String coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
}
