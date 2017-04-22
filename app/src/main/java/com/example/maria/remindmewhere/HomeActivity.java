package com.example.maria.remindmewhere;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maria.remindmewhere.adapter.LocationAdapter;
import com.example.maria.remindmewhere.database.DBHelper;
import com.example.maria.remindmewhere.database.DataSourceLocation;
import com.example.maria.remindmewhere.model.Location;
import com.example.maria.remindmewhere.service.GeofenceErrorMessages;
import com.example.maria.remindmewhere.service.GeofenceTransitionsIntentService;
import com.example.maria.remindmewhere.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static final String ACTIVITY_NAME_TAG = "HomeActivity";
    public static final int REQUEST_CODE_ADD_LOCATION = 10;

    private DataSourceLocation dsLocation;
    private ListView locationsListView;
    private Button addLocationBtn;
    private ArrayList<Location> locations;

    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dsLocation = new DataSourceLocation(this);
        dsLocation.open();

        initLocationsList();

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        if(mGoogleApiClient.isConnected() && !mGeofencesAdded){
            addGeofences();
        }
    }

    private void initLocationsList() {
        locationsListView = (ListView)findViewById(R.id.ha_list_view_loc);
        // Array holding our data
        locations = dsLocation.getAllLocations();
        //adapter which will convert each data item into view item.
        LocationAdapter adapter = new LocationAdapter(this, locations);
        //place each view-item inside listview by setting adapter for our listview
        locationsListView.setAdapter(adapter);

        locationsListView.setOnItemClickListener(this);
    }

    public void goToAddLocation(View view){
        Intent intent = new Intent(HomeActivity.this, AddLocationActivity.class);
        startActivityForResult(intent,REQUEST_CODE_ADD_LOCATION);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Location location = locations.get(position);

        TextView listText = (TextView) view.findViewById(R.id.ha_locations_list_item);
        String text = listText.getText().toString();

        Intent intent = new Intent(HomeActivity.this, LocationListActivity.class);
        intent.putExtra(Constants.LOCATION_KEY,location);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dsLocation.open();
        if(requestCode == REQUEST_CODE_ADD_LOCATION && resultCode == RESULT_OK){
            initLocationsList();
            if(mGoogleApiClient.isConnected() && data.hasExtra(Constants.LOCATION_EXTRA_SAVED)){
                Log.d(ACTIVITY_NAME_TAG,"============After returning from activity Population geofences: "+locations.size());
                populateGeofenceList();
                removeGeofences();
                addGeofences();
            }else {
                Log.d(ACTIVITY_NAME_TAG,"============After returning from activity Population geofences: "+mGoogleApiClient.isConnected());
                Log.d(ACTIVITY_NAME_TAG,"============After returning from activity Population geofences: "+data.hasExtra(Constants.LOCATION_EXTRA_SAVED));


            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dsLocation.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dsLocation.open();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(ACTIVITY_NAME_TAG, "Connected to GoogleApiClient");
        if(!mGeofencesAdded){
            populateGeofenceList();
            addGeofences();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(ACTIVITY_NAME_TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(ACTIVITY_NAME_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_DWELL notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google api not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if(locations.size() > 0){
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        getGeofencingRequest(),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            }
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google api not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(ACTIVITY_NAME_TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            Log.d(ACTIVITY_NAME_TAG, mGeofencesAdded ? "Geofences Added" : "Geofences removed");

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(ACTIVITY_NAME_TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        Log.d(ACTIVITY_NAME_TAG,"============Population geofences: "+locations.size());
        //locations
        for (Location loc : locations){
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(loc.getId())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            loc.getLatitude(),
                            loc.getLongitud(),
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)

                    .setLoiteringDelay(Constants.GEOFENCE_DWELL_WAIT_TIMER)

                    // Create the geofence.
                    .build());
        }
    }
}
