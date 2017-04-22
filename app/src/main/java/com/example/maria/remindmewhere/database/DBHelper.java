package com.example.maria.remindmewhere.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maria on 4/1/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "remindmewhere.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocationTable.SQL_CREATE);
        db.execSQL(ReminderTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ReminderTable.SQL_DELETE);
        db.execSQL(LocationTable.SQL_DELETE);
        onCreate(db);
    }
}
