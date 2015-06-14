package pl.warszawa.gdg.metrodatacollector;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.data.ParseInitOutside;
import pl.warszawa.gdg.metrodatacollector.data.ParseUpdateCallback;
import pl.warszawa.gdg.metrodatacollector.location.NetworkLocation;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;
import pl.warszawa.gdg.metrodatacollector.location.geofence.GeofenceConstants;
import pl.warszawa.gdg.metrodatacollector.location.geofence.GeofenceTransitionsIntentService;
import pl.warszawa.gdg.metrodatacollector.subway.MapElement;
import pl.warszawa.gdg.metrodatacollector.subway.Station;
import pl.warszawa.gdg.metrodatacollector.subway.SubwaySystem;

public class AppMetroDataCollector extends Application implements GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = AppMetroDataCollector.class.getSimpleName();
    public static boolean isRunning; //CellId Service status
    public static SubwaySystem subwaySystem;
    public static SharedPreferences sharedPreferences;

    private static ResultCallback resultCallback;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;

    //Used in CellMonitorReceiver
    public static MapElement mapElementPrev;//As only info about towerCurrent is not enough (can switch 2g->3g in same place)
    public static TowerInfo towerCurrent;
    public static TowerInfo towerPrev;

    @Override
    public void onCreate() {
        sharedPreferences = AppMetroDataCollector.this.getSharedPreferences("pl.warszawa.gdg.metrodatacollector", Context.MODE_PRIVATE);
        FlagsLocal.read();

        if (FlagsLocal.parseEnabled) {
            super.onCreate();
            if (FlagsLocal.fabricEnabled) {
                Fabric.with(this, new Crashlytics());
            }
            if (FlagsLocal.parseEnabled) {
                ParseInitOutside.initParseWithKey(this);
                if (FlagsLocal.parsePushes) {
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
            }
        }
        subwaySystem = new SubwaySystem();
        NetworkLocation.init(AppMetroDataCollector.this);
        ParseHelper.updateLocalStations();
        ParseHelper.updateLocalStations(new ParseUpdateCallback() {
            @Override
            public void success(List<ParseObject> list) {
                mGeofenceList = new ArrayList<Geofence>();
                for(ParseObject parseObject : list) {
                    Station station = ParseHelper.getStation(parseObject);
                    if(station.getLocation() != null) {
                        mGeofenceList.add(new Geofence.Builder()
                                .setRequestId(station.getName())
                                .setCircularRegion(
                                        station.getLocation().getLatitude(),
                                        station.getLocation().getLongitude(),
                                        GeofenceConstants.GEOFENCE_RADIUS_METERS)
                                .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_TIME)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build());
                    }
                }
                if (FlagsLocal.useGeofance) {
                    initGeofences();
                }
            }

            @Override
            public void failure(ParseException parseException) {
                Toast.makeText(AppMetroDataCollector.this, "Failed to fetch station list", Toast.LENGTH_LONG).show();
                Log.e(TAG, "failure :" + parseException.getLocalizedMessage());
            }
        });


    }

    private void initGeofences() {
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "Google Play services unavailable.");
            return;
        }

        resultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.e(TAG, "Registering success.");
                }else {
                    Log.e(TAG, "Registering failed: " + status.getStatusMessage());
                }
            }
        };

        //addMockLocation();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }

    private void addMockLocation() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("mock_location")
                .setCircularRegion(
                        52.262201,
                        20.971661,
                        GeofenceConstants.GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_TIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnected(Bundle bundle) {
        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        );
        result.setResultCallback(resultCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * Checks if Google Play services is available.
     * @return true if it is.
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(GeofenceConstants.GEOTAG, Log.DEBUG)) {
                Log.d(GeofenceConstants.GEOTAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(GeofenceConstants.GEOTAG, "Google Play services is unavailable.");
            return false;
        }
    }
}