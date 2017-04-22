package com.example.maria.remindmewhere.utils;

/**
 * Created by maria on 4/2/17.
 */

public class Constants {
    public static final String LOCATION_KEY = "location";
    public static final String REMINDER_KEY = "reminder";
    public static final String LOCATION_EXTRA_SAVED = "location_saved";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    public static final int GEOFENCE_DWELL_WAIT_TIMER = 60000; //1min in milliseconds

    public static final String PACKAGE_NAME = "com.example.maria.remindmewhere";

    public static final String ADDRESS_RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    //Error Messages
    public static final String NAME_ERROR_MSG = "Please enter a name";
    public static final String DESCRIPTION_ERROR_MSG = "Please enter a description";
    public static final String SAVE_LOCATION_ERROR_MSG = "Can not save the location";
    public static final String SAVE_REMINDER_ERROR_MSG = "Can not save the reminder";
    public static final String DELETE_REMINDER_ERROR_MSG = "Can not delete the reminder";
    public static final String GEOCODER_NOT_AVAILABLE = "Geocoder not available";
    public static final String ADDRESS_NOT_FOUND = "Address not found";
    public static final String INVALID_LATITUDE = "Invalid latitude";
    public static final String INVALID_LONGITUD = "Invalid longitud";
    public static final String NOT_LOCATION_PROVIDED = "Not location provided";
}
