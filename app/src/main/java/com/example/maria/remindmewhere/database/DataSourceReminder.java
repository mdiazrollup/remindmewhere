package com.example.maria.remindmewhere.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import com.example.maria.remindmewhere.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maria on 4/1/17.
 */

public class DataSourceReminder extends DataSource {

    public DataSourceReminder(Context context) {
        super(context);
    }

    public Reminder create(Reminder reminder) {
        ContentValues values = reminder.toValues();
        mDatabase.insert(ReminderTable.TABLE_REMINDER, null, values);
        return reminder;
    }

    public Reminder update(Reminder reminder) {
        ContentValues values = reminder.toValues();
        mDatabase.update(ReminderTable.TABLE_REMINDER,values, ReminderTable.COLUMN_ID + "='" + reminder.getId()+"'",null);
        return reminder;
    }

    public int delete(Reminder reminder) {
        return mDatabase.delete(ReminderTable.TABLE_REMINDER,ReminderTable.COLUMN_ID + "='" + reminder.getId()+"'",null);
    }

    public ArrayList<Reminder> getRemindersByLocation(String idLocation) {
        ArrayList<Reminder> dataItems = new ArrayList<>();


        Cursor cursor = mDatabase.rawQuery(ReminderTable.SQL_SELECT_LOCATION, new String[]{idLocation});
        while (cursor.moveToNext()) {
            Reminder item = new Reminder();
            item.setId(cursor.getString(
                    cursor.getColumnIndex(ReminderTable.COLUMN_ID)));
            item.setName(cursor.getString(
                    cursor.getColumnIndex(ReminderTable.COLUMN_NAME)));
            item.setDescription(cursor.getString(
                    cursor.getColumnIndex(ReminderTable.COLUMN_DESCRIPTION)));
            item.setIdLocation(cursor.getString(
                    cursor.getColumnIndex(ReminderTable.COLUMN_ID_LOCATION)));
            dataItems.add(item);
        }

        return dataItems;
    }
}
