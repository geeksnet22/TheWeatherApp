package com.example.android.whatstheweather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.whatstheweather.types.Coordinates;
import com.example.android.whatstheweather.types.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "locationsManager";
    private static String TABLE_LOCATIONS = "allLocations";
    private static String LOC_ID = "id";
    private static String LOC_NAME = "locationName";
    private static String LOC_COORDS= "locationCoord";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + LOC_ID + " INTEGER PRIMARY KEY," + LOC_NAME + " TEXT," + LOC_COORDS + " TEXT" + ")";
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void addLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOC_NAME, location.name); // Contact Name
        values.put(LOC_COORDS, location.coordinates);

        db.insert(TABLE_LOCATIONS, null, values);
    }

    public Map<String, Coordinates> getAllLocations() throws JSONException {
        SQLiteDatabase db = getReadableDatabase();
        Map<String, Coordinates> locationMap = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject coordsJsonObject = new JSONObject(cursor.getString(2));
                locationMap.put(cursor.getString(1), new Coordinates(coordsJsonObject.getDouble("lon"),
                        coordsJsonObject.getDouble("lat")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return locationMap;
    }

    public int getLocationsCount() {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
