package com.example.maria.remindmewhere.database;

/**
 * Created by maria on 4/1/17.
 */

public class ReminderTable {
    public static final String TABLE_REMINDER = "reminders";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ID_LOCATION = "id_location";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION,
                    COLUMN_ID_LOCATION};

    public static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_REMINDER + " WHERE " + COLUMN_ID + " = ?";

    public static final String SQL_SELECT_LOCATION = "SELECT * FROM " + TABLE_REMINDER + " WHERE " + COLUMN_ID_LOCATION + " = ? ";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_REMINDER + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_ID_LOCATION + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_REMINDER;
}
