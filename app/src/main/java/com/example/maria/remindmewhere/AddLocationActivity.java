package com.example.maria.remindmewhere;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maria.remindmewhere.database.DataSourceLocation;
import com.example.maria.remindmewhere.service.FetchAddressIntentService;
import com.example.maria.remindmewhere.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AddLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String CLASS_KEY_LOG = "AddLocationActivity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private DataSourceLocation dsLocation;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocRequest;
    protected Location mCurLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    private ProgressDialog mProgressDialog;

    protected TextView mLocationAddressTextView;

    protected Button mFetchAddressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mCurLocation = null;
        dsLocation = new DataSourceLocation(this);
        dsLocation.open();

        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) findViewById(R.id.ala_current_loc_input);
        mFetchAddressButton = (Button) findViewById(R.id.ala_set_loc_btn);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();

        // build the Play Services client object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // create the LocationRequest we'll use for location updates
        mLocRequest = new LocationRequest();
        mLocRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {

        mLocationAddressTextView.setText(mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressDialog = ProgressDialog.show(this, "Searching..", "Location address...", true,false);
            mFetchAddressButton.setEnabled(false);
        } else {
            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mFetchAddressButton.setEnabled(true);
        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCurLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void setCurrentLocation (View view){
        if(mGoogleApiClient.isConnected() && mCurLocation != null){
            Log.d(CLASS_KEY_LOG, mCurLocation.getLatitude() + " " + mCurLocation.getLongitude());
            startIntentService();

            mAddressRequested = true;
            updateUIWidgets();
        }
    }

    public void saveLocation (View view){
        EditText nameEditText = (EditText) findViewById(R.id.ala_name_input);
        String name = nameEditText.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, Constants.NAME_ERROR_MSG,Toast.LENGTH_LONG).show();
        }else {
            com.example.maria.remindmewhere.model.Location newLocation = new com.example.maria.remindmewhere.model.Location(null,name,mCurLocation.getLatitude(),mCurLocation.getLongitude());
            newLocation = dsLocation.create(newLocation);
            Log.d(CLASS_KEY_LOG, newLocation.toString());
            Intent result = new Intent();
            result.putExtra(Constants.LOCATION_EXTRA_SAVED,true);
            setResult(RESULT_OK,result);
            finish();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurLocation = location;
        Log.d(CLASS_KEY_LOG, location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] perms, int[] results){
        if (reqCode == 1) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(CLASS_KEY_LOG, "Permissions granted");
                startLocationUpdates();
                if(mCurLocation != null){
                    // Determine whether a Geocoder is available.
                    if (!Geocoder.isPresent()) {
                        Toast.makeText(this, Constants.GEOCODER_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (mAddressRequested) {
                        startIntentService();
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        int permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.d(CLASS_KEY_LOG, "Permissions already granted");
            startLocationUpdates();
            if(mCurLocation != null){
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, Constants.GEOCODER_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
                    return;
                }

                if (mAddressRequested) {
                    startIntentService();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(CLASS_KEY_LOG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(CLASS_KEY_LOG, "Connection was suspended for some reason");
        mGoogleApiClient.connect();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.ADDRESS_RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Activity lifecycle events
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
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

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d(CLASS_KEY_LOG,"Address found: " + mAddressOutput);
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

}
