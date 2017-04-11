package fetch.supermarkt;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by stefg on 11/04/2017.
 */

public class GeofenceActivity extends FragmentActivity implements OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Button startLocationMonitoring;
    private Button startGeofenceMonitoring;
    private Button stopGeofenceMonitoring;

    private static final String TAG = "GeofenceActivity";
    public static final String GEOFENCE_ID = "myGeofenceId";
    private static final byte LOCATION_PERMISSION = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geofence);

        Log.d(TAG, "Entering onCreate method");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "Connected to GoogleApiClient");
                    }

                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Suspended connection to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "Failed to connect to GoogleApiClient" + result.getErrorMessage());
                    }
                })
                .build();


        startLocationMonitoring = (Button) findViewById(R.id.startLocationMonitoring);
        startLocationMonitoring.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermission();
            }
        });

        startGeofenceMonitoring = (Button) findViewById(R.id.startGeofenceMonitoring);
        startGeofenceMonitoring.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startGeofenceMonitoring();
            }
        });

        stopGeofenceMonitoring = (Button) findViewById(R.id.stopGeofenceMonitoring);
        stopGeofenceMonitoring.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopGeofenceMonitoring();
            }
        });
    }

    protected void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();
        mGoogleApiClient.reconnect();
    }

    /*protected void onStop(){
        Log.d(TAG, "onStop called");
        super.onStop();
        mGoogleApiClient.disconnect();
    }*/

    protected void onResume() {
        Log.d(TAG, "onResume called");
        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google play services is not available - Show dialog to ask user to download it");
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d(TAG, "Google play services available - No action is required");
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, result.getErrorMessage());
    }


    private void startLocationMonitoring() {
        Log.d(TAG, "startLocationMonitoring called");
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000);


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
                public void onLocationChanged(Location location) {
                    //Log doesn't work for some reason. Debug to see result. (It works)
                    Log.d(TAG, "Location update lat/long" + location.getLatitude() + " " + location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityExeption - " + e.getMessage());
        }
    }

    private void startGeofenceMonitoring() {
        Log.d(TAG, "StartMonitoring called");

        try {
            Geofence geofence = new Geofence.Builder()
                    //Link request id from geofence to request id from supermarket request (?)
                    .setRequestId(GEOFENCE_ID)
                    .setCircularRegion(51.1472505, 5.8918397, 400)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence).build();

            Intent intent = new Intent(this, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!mGoogleApiClient.isConnected()) {
                Log.d(TAG, "GoogleApiClient is not connected");
            } else {
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofenceRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {

                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Log.d(TAG, "Succesfully added geofence");
                                } else {
                                    Log.d(TAG, "Failed to add geofence " + status.getStatus());
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityExeption -" + e.getMessage());
        }

    }



    private void stopGeofenceMonitoring() {
        Log.d(TAG, "stopMonitoring called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceIds);
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            startLocationMonitoring();
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationMonitoring();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public Activity getActivity() {
        return this;
    }


}
