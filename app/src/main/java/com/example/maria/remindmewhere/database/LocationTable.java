package com.example.maria.remindmewhere.database;

/**
 * Created by maria on 4/1/17.
 */

public class LocationTable {
    public static final String TABLE_LOCATION = "locations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUD = "longitud";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_NAME, COLUMN_LATITUDE,
                    COLUMN_LONGITUD};

    public static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_LOCATION + " WHERE " + COLUMN_ID + " = ?";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_LOCATION + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_LATITUDE + " REAL," +
                    COLUMN_LONGITUD + " REAL" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_LOCATION;
}
