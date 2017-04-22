package com.example.maria.remindmewhere.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by maria on 4/1/17.
 */

public class DataSource {
    protected Context mContext;
    protected SQLiteDatabase mDatabase;
    protected SQLiteOpenHelper mDbHelper;

    public DataSource(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(mContext);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void open() {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }
}
