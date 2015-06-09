package pl.warszawa.gdg.metrodatacollector;


import android.content.SharedPreferences;

public class FlagsLocal {
    public static final boolean fabricEnabled = false;
    public static final boolean parseEnabled = true;
    public static final boolean parsePushes = true;

    /**
     * Used to upload data that is hardcoded at app start to Parse.com - use if Parse table is empty.
     */
    public static final boolean parseUploadHardcodedData = false;

    public static final boolean uploadOnline = false;
    public static final boolean writeFile = false;
    public static final boolean showNotificationProgress = false;
    public static final boolean useGeofance = false;

    public static boolean runBackground = false;
    public static boolean useWifi = false;
    public static boolean showNotificationInfo = true;


    //Keys for saving to SharedPreferences
    private static final String PREFS_RUN_BACKGROUND = "PREFS_RUN_BACKGROUND";

    public static void save() {
        SharedPreferences.Editor editor = AppMetroDataCollector.sharedPreferences.edit();
        editor.putBoolean(PREFS_RUN_BACKGROUND, runBackground);
        //TODO... do for all flags
        editor.apply();
    }

    public static void read() {
        //TODO read from SharedPreferences (as in save())
    }
}
