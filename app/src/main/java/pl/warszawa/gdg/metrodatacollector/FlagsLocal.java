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
    private static final String PREFS_FABRICENABLED = "PREFS_FABRICENABLED ";
    private static final String PREFS_PARSEENABLED = "PREFS_PARSEENABLED ";
    private static final String PREFS_PARSEPUSHES = "PREFS_PARSEPUSHES ";
    private static final String PREFS_PARSEUPLOADHARDCODEDDATA = "PREFS_PARSEUPLOADHARDCODEDDATA";
    private static final String PREFS_UPLOADONLINE = "PREFS_UPLOADONLINE";
    private static final String PREFS_WRITEFILE = "PREFS_WRITEFILE";
    private static final String PREFS_SHOWNOTIFICATIONPROGRESS = "PREFS_SHOWNOTIFICATIONPROGRESS";
    private static final String PREFS_USEGEOFANCE = "PREFS_USEGEOFANCE";
    private static final String PREFS_RUNBACKGROUND = "PREFS_RUNBACKGROUND";
    private static final String PREFS_USEWIFI = "PREFS_USEWIFI";
    private static final String PREFS_SHOWNOTIFICATIONINFO = "PREFS_SHOWNOTIFICATIONINFO";

    private static void save() {
        SharedPreferences.Editor editor = AppMetroDataCollector.sharedPreferences.edit();
        editor.putBoolean(PREFS_FABRICENABLED, fabricEnabled);
        editor.putBoolean(PREFS_PARSEENABLED, parseEnabled);
        editor.putBoolean(PREFS_PARSEPUSHES, parsePushes);
        editor.putBoolean(PREFS_PARSEUPLOADHARDCODEDDATA, parseUploadHardcodedData);
        editor.putBoolean(PREFS_UPLOADONLINE, uploadOnline);
        editor.putBoolean(PREFS_WRITEFILE, writeFile);
        editor.putBoolean(PREFS_SHOWNOTIFICATIONPROGRESS, showNotificationProgress);
        editor.putBoolean(PREFS_USEGEOFANCE, useGeofance);
        editor.putBoolean(PREFS_RUNBACKGROUND, runBackground);
        editor.putBoolean(PREFS_USEWIFI, useWifi);
        editor.putBoolean(PREFS_SHOWNOTIFICATIONINFO, showNotificationInfo);

        editor.apply();
    }

    public static void read() {
        fabricEnabled = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_FABRICENABLED, false);
        parseEnabled = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_PARSEENABLED, true);
        parsePushes = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_PARSEPUSHES, true);
        parseUploadHardcodedData = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_PARSEUPLOADHARDCODEDDATA, false);
        uploadOnline = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_UPLOADONLINE, false);
        writeFile = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_WRITEFILE, false);
        showNotificationProgress = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_SHOWNOTIFICATIONPROGRESS, false);
        useGeofance = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_USEGEOFANCE, true);
        runBackground = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_RUNBACKGROUND, false);
        useWifi = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_USEWIFI, false);
        showNotificationInfo = AppMetroDataCollector.sharedPreferences.getBoolean(PREFS_SHOWNOTIFICATIONINFO, true);
    }
}
