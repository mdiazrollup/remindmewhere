package com.example.maria.remindmewhere.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.example.maria.remindmewhere.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria on 4/1/17.
 */

public class DataSourceLocation extends DataSource {
    public DataSourceLocation(Context context) {
        super(context);
    }

    public Location create(Location location) {
        ContentValues values = location.toValues();
        mDatabase.insert(LocationTable.TABLE_LOCATION, null, values);
        return location;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> dataItems = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = mDatabase.query(LocationTable.TABLE_LOCATION, LocationTable.ALL_COLUMNS,
                    null, null, null, null, null);

            while (cursor.moveToNext()) {
                Location item = new Location();
                item.setId(cursor.getString(
                        cursor.getColumnIndex(LocationTable.COLUMN_ID)));
                item.setName(cursor.getString(
                        cursor.getColumnIndex(LocationTable.COLUMN_NAME)));
                item.setLatitude(cursor.getDouble(
                        cursor.getColumnIndex(LocationTable.COLUMN_LATITUDE)));
                item.setLongitud(cursor.getDouble(
                        cursor.getColumnIndex(LocationTable.COLUMN_LONGITUD)));
                dataItems.add(item);
            }
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        return dataItems;
    }

    public Location getById(String idLocation) {
        Location location = null;


        Cursor cursor = mDatabase.rawQuery(LocationTable.SQL_SELECT_BY_ID, new String[]{idLocation});
        while (cursor.moveToNext()) {
            location = new Location();
            location.setId(cursor.getString(
                    cursor.getColumnIndex(LocationTable.COLUMN_ID)));
            location.setName(cursor.getString(
                    cursor.getColumnIndex(LocationTable.COLUMN_NAME)));
            location.setLatitude(cursor.getDouble(
                    cursor.getColumnIndex(LocationTable.COLUMN_LATITUDE)));
            location.setLongitud(cursor.getDouble(
                    cursor.getColumnIndex(LocationTable.COLUMN_LONGITUD)));
        }

        return location;
    }
}
