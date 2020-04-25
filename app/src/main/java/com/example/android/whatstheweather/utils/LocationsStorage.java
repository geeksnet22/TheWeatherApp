package com.example.android.whatstheweather.utils;

import com.example.android.whatstheweather.types.Coordinates;

import java.util.HashMap;
import java.util.Map;

public class LocationsStorage {

    public static Map<String, Coordinates> locationsMap;
    public static boolean isSafeToRead;

    public synchronized static void initializeLocationMap() {
        locationsMap = new HashMap<>();
        isSafeToRead = false;
    }
}
