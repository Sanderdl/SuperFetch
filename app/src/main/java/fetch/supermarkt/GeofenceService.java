package fetch.supermarkt;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import fetch.supermarkt.database.FirebaseDB;
import fetch.supermarkt.model.Request;

/**
 * Created by stefg on 11/04/2017.
 */

public class GeofenceService extends IntentService{
    public static final String TAG = "GeofenceService";

    private List<Request> jobs;

    public GeofenceService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            // TODO: Handle error
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d(TAG, "Entering activity_geofence - " + requestId);
                jobs = FirebaseDB.instance.getYourJobsList();
                for(Request r: jobs){
                    FirebaseDB.instance.updateRequestStatus(r,"At the store");
                }

            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.d(TAG, "Exiting activity_geofence - " + requestId);
                jobs = FirebaseDB.instance.getYourJobsList();
                for(Request r: jobs){
                    FirebaseDB.instance.updateRequestStatus(r,"On the way back");
                }
            }
        }

    }

}
