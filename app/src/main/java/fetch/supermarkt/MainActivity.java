package fetch.supermarkt;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fetch.supermarkt.adapters.RequestListAdapter;
import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.database.IUpdatable;
import fetch.supermarkt.model.Request;


public class MainActivity extends BaseActivity implements IUpdatable, GoogleApiClient.OnConnectionFailedListener {

    private TextView tvWorth;
    private TextView tvEarnings;

    private Button fetchRequest;

    private ListView requestList;
    private List<Request> allRequests;
    private List<Request> checkedRequests;

    private double productsWorth = 0.00;
    private double earnings = 0.00;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "GeofenceActivity";
    public static final String GEOFENCE_ID = "myGeofenceId";
    private static final byte LOCATION_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestList = (ListView) findViewById(R.id.list_home);
        tvWorth = (TextView) findViewById(R.id.val_worth);
        tvEarnings = (TextView) findViewById(R.id.val_earnings);

        fetchRequest = (Button) findViewById(R.id.btn_fetch);

        allRequests = new ArrayList<>();
        checkedRequests = new ArrayList<>();

        updateValues();


        allRequests = FirebaseDB.instance.getRequestList();
        FirebaseDB.instance.registerUpdatable("main",this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddRequest();
            }
        });

        fetchRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRequests();
            }
        });


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

        update();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart called");
        super.onStart();
        mGoogleApiClient.reconnect();
    }

    @Override
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

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, result.getErrorMessage());
    }

    private void goToAddRequest(){
        Intent intent = new Intent(this, NewRequestActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getActivityID() {
        return 0;
    }

    public void addCheckedRequest(int index){
        checkedRequests.add(allRequests.get(index));
        updateValues();
    }

    public void removeCheckedRequest(Request toRemove){
        checkedRequests.remove(toRemove);
        updateValues();
    }

    private void updateValues(){
        productsWorth = 0.00;
        earnings = 0.00;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        for(Request r : checkedRequests){
            productsWorth += r.getWorth();
            earnings += r.getDeliveryFee();
        }

        String strWorth = formatter.format(productsWorth);
        String strEarnings = formatter.format(earnings);

        tvEarnings.setText(strEarnings);
        tvWorth.setText(strWorth);
    }

    @Override
    public void update(){
        allRequests = FirebaseDB.instance.getRequestList();
        ListAdapter listAdapter = new RequestListAdapter(this, R.layout.a_request_list_item, allRequests);
        requestList.setAdapter(listAdapter);

        if(FirebaseDB.instance.getYourJobsList().isEmpty())
            stopGeofenceMonitoring();
    }

    private void fetchRequests(){
        for(Request r : checkedRequests){
            removeCheckedRequest(r);
            r.setDelivererName(loginActivity.applicationUser);
            r.setEta();
            FirebaseDB.instance.pickupRequest(r);
        }

        if(!checkedRequests.isEmpty()){
            try{
                checkPermission();
                startGeofenceMonitoring();
            }catch (SecurityException e){
                Log.d(TAG, "SecurityExeption - " + e.getMessage());
            }
        }
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
                    //Link request id from activity_geofence to request id from supermarket request (?)
                    .setRequestId(GEOFENCE_ID)
                    .setCircularRegion(51.451184, 5.471967, 200)
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
                                    Log.d(TAG, "Succesfully added activity_geofence");
                                } else {
                                    Log.d(TAG, "Failed to add activity_geofence " + status.getStatus());
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "SecurityExeption -" + e.getMessage());
        }

    }

    private void stopGeofenceMonitoring() {
        if (mGoogleApiClient.hasConnectedApi(LocationServices.API)) {
            Log.d(TAG, "stopMonitoring called");
            ArrayList<String> geofenceIds = new ArrayList<String>();
            geofenceIds.add(GEOFENCE_ID);
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceIds);
        }
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

}
