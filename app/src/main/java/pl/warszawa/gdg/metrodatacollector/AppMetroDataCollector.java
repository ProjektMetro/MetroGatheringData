package pl.warszawa.gdg.metrodatacollector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseInstallation;

import io.fabric.sdk.android.Fabric;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.data.ParseInitOutside;
import pl.warszawa.gdg.metrodatacollector.location.PhoneCellListener;
import pl.warszawa.gdg.metrodatacollector.subway.SubwaySystem;

public class AppMetroDataCollector extends Application{
    public static boolean isRunning; //CellId Service status
    public static TelephonyManager telephonyManager;
    public static SubwaySystem subwaySystem;
    public static PhoneCellListener phoneCellListener;
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        sharedPreferences = AppMetroDataCollector.this.getSharedPreferences("pl.warszawa.gdg.metrodatacollector", Context.MODE_PRIVATE);
        FlagsLocal.read();

        if(FlagsLocal.parseEnabled) {
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
        telephonyManager = (TelephonyManager) AppMetroDataCollector.this.getSystemService(Context.TELEPHONY_SERVICE);
        ParseHelper.updateLocalStations();
    }

    /**
     * Register to LISTEN_SIGNAL_STRENGTHS to detect cellId changes in background
     */
    public static void registerToCellEvent() {
        AppMetroDataCollector.telephonyManager.listen(AppMetroDataCollector.phoneCellListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * Stop listening to LISTEN_SIGNAL_STRENGTHS
     */
    public static void unregisterToCellEvent() {
        AppMetroDataCollector.telephonyManager.listen(AppMetroDataCollector.phoneCellListener, PhoneStateListener.LISTEN_NONE);
    }
}