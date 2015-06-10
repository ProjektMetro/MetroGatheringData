package pl.warszawa.gdg.metrodatacollector;


import android.content.SharedPreferences;

public class FlagsLocal {
    public static boolean fabricEnabled = false;
    public static boolean parseEnabled = true;
    public static boolean parsePushes = true;

    /**
     * Used to upload data that is hardcoded at app start to Parse.com - use if Parse table is empty.
     */
    public static boolean parseUploadHardcodedData = false;

    public static boolean uploadOnline = false;
    public static boolean writeFile = false;
    public static boolean showNotificationProgress = false;
    public static boolean useGeofance = true;

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
        runBackground = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_RUN_BACKGROUND, false);
    }
}
