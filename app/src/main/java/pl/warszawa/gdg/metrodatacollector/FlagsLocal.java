package pl.warszawa.gdg.metrodatacollector;


public class FlagsLocal {
    public static final boolean fabricEnabled = false;
    public static final boolean parseEnabled = false;
    public static final boolean parsePushes = true;
    public static final boolean uploadOnline = false;
    public static final boolean writeFile = false;
    public static final boolean showNotificationProgress = false;
    public static final boolean showNotificationInfo = true;

    //Dynamic ones - keep in SharedPrefs? //TODO?
    public static boolean runBackground = true;
    public static boolean useWifi = false;
}
