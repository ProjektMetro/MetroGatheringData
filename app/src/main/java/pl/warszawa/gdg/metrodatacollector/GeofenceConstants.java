package pl.warszawa.gdg.metrodatacollector;

import com.google.android.gms.location.Geofence;

/**
 * Created by gbielanski on 2015-06-10.
 */
public class GeofenceConstants {
    public static final float GEOFENCE_RADIUS_METERS = 100.0f;
    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    // Request code to attempt to resolve Google Play services connection failures.
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String GEOTAG = "GEO";
}
