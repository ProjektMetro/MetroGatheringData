package pl.warszawa.gdg.metrodatacollector;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseInstallation;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.data.ParseInitOutside;
import pl.warszawa.gdg.metrodatacollector.location.NetworkLocation;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.subway.SubwaySystem;
import pl.warszawa.gdg.metrodatacollector.ui.MainActivity;

public class AppMetroDataCollector extends Application implements ResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static boolean isRunning; //CellId Service status
    public static SubwaySystem subwaySystem;
    public static PhoneCellListener phoneCellListener;
    public static SharedPreferences sharedPreferences;

    List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
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

        phoneCellListener = new PhoneCellListener(AppMetroDataCollector.this);
        subwaySystem = new SubwaySystem();
        NetworkLocation.init(AppMetroDataCollector.this);
        ParseHelper.updateLocalStations();

        if (FlagsLocal.useGeofance) {
            if (!isGooglePlayServicesAvailable()) {
                Log.e(GeofenceConstants.GEOTAG, "Google Play services unavailable.");
                return;
            }

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
            //TODO create storage
            mGeofenceList = new ArrayList<>();
            createGeofences();

        }
    }
    private void createGeofences(){

        //TODO take a list of metro stations
        String requestId = "1";
        double latitude = 0;
        double longitude = 0;

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(requestId)

                .setCircularRegion(
                        latitude,
                        longitude,
                        GeofenceConstants.GEOFENCE_RADIUS_METERS
                )
                .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_TIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        //TODO add to storage ??
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
    public void onResult(Result result) {
        //TODO for Geofencing
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            int errorCode = connectionResult.getErrorCode();
            Log.e(GeofenceConstants.GEOTAG, "Connection to Google Play services failed with error code " + errorCode);
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