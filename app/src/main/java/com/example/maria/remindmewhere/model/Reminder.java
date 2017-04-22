package com.example.maria.remindmewhere.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.maria.remindmewhere.database.ReminderTable;

import java.util.UUID;

/**
 * Created by maria on 4/1/17.
 */

public class Reminder implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String idLocation;

    public Reminder() {
    }

    public Reminder(String id, String description, String name, String idLocation) {

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        this.id = id;
        this.description = description;
        this.name = name;
        this.idLocation = idLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(String idLocation) {
        this.idLocation = idLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues(4);

        values.put(ReminderTable.COLUMN_ID, id);
        values.put(ReminderTable.COLUMN_NAME, name);
        values.put(ReminderTable.COLUMN_DESCRIPTION, description);
        values.put(ReminderTable.COLUMN_ID_LOCATION, idLocation);
        return values;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idLocation='" + idLocation + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(idLocation);
    }

    protected Reminder(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        idLocation = in.readString();
    }

    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel source) {
            return new Reminder(source);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
}
