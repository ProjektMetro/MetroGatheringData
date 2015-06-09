package pl.warszawa.gdg.metrodatacollector.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.GeofenceTransitionsIntentService;
import pl.warszawa.gdg.metrodatacollector.R;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.location.TowerInfo;



public class MainActivity extends AppCompatActivity
        implements ResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @InjectView(R.id.switchBackgroundState)
    Switch switchBackgroundState;

    public static final String GEOTAG = "GEO";

    List<Geofence> mGeofenceList;
    PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Track events to Parse
        if(FlagsLocal.parseEnabled) {
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        if(savedInstanceState != null) {
            //TODO restore previous state
        }

        if(FlagsLocal.useGeofance) {        if (!isGooglePlayServicesAvailable()) {
            Log.e(GEOTAG, "Google Play services unavailable.");
            finish();
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Tower has changed, update UI?
     * @param towerInfo
     */
    public void onEvent(TowerInfo towerInfo) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        FlagsLocal.save();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.switchBackgroundState)
    public void changeBackgroundState() {
        PhoneCellListener.reset();
        FlagsLocal.runBackground = !switchBackgroundState.isChecked();
        if(!FlagsLocal.runBackground) {
            AppMetroDataCollector.registerToCellEvent();
        } else {
            AppMetroDataCollector.unregisterToCellEvent();
        }
    }

    @OnClick(R.id.switchNewPlace)
    public void changeNewPlaceState() {
        FlagsLocal.showNotificationInfo = !FlagsLocal.showNotificationInfo;
    }

    @OnClick(R.id.buttonAddPlace)
    public void addPlaceClicked() {
        startActivity(new Intent(this, ActivityAddNewPoint.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createGeofences(){

        //TODO take a list of metro stations
        //TODO export constants
        String requestId = "1";
        float GEOFENCE_RADIUS_METERS = 50.0f;
        double latitude = 0;
        double longitude = 0;
        long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(requestId)

                .setCircularRegion(
                        latitude,
                        longitude,
                        GEOFENCE_RADIUS_METERS
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_TIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        //TODO add to storage
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
        // Request code to attempt to resolve Google Play services connection failures.
        int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(GEOTAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(GEOTAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }
    /**
     * Checks if Google Play services is available.
     * @return true if it is.
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(GEOTAG, Log.DEBUG)) {
                Log.d(GEOTAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(GEOTAG, "Google Play services is unavailable.");
            return false;
        }
    }
}
