package com.example.maria.remindmewhere.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.maria.remindmewhere.database.LocationTable;

import java.util.UUID;

/**
 * Created by maria on 4/1/17.
 */

public class Location implements Parcelable {
    private String id;
    private String name;
    private double latitude;
    private double longitud;

    public Location() {
    }

    public Location(String id, String name, double latitude, double longitud) {

        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitud = longitud;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues(4);

        values.put(LocationTable.COLUMN_ID, id);
        values.put(LocationTable.COLUMN_NAME, name);
        values.put(LocationTable.COLUMN_LATITUDE, latitude);
        values.put(LocationTable.COLUMN_LONGITUD, longitud);
        return values;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitud=" + longitud +
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
        dest.writeDouble(latitude);
        dest.writeDouble(longitud);
    }

    protected Location(Parcel in) {
        id = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitud = in.readDouble();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
