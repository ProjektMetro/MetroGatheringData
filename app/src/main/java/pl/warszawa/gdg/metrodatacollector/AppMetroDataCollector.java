package pl.warszawa.gdg.metrodatacollector;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import com.parse.ParseInstallation;

import io.fabric.sdk.android.Fabric;
import pl.warszawa.gdg.metrodatacollector.data.ParseInitOutside;
import pl.warszawa.gdg.metrodatacollector.subway.SubwaySystem;

public class AppMetroDataCollector extends Application{
    public static boolean isRunning; //CellId Service status
    public static TelephonyManager telephonyManager;
    public static SubwaySystem subwaySystem;

    @Override
    public void onCreate() {
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
        subwaySystem = new SubwaySystem();
        telephonyManager = (TelephonyManager) AppMetroDataCollector.this.getSystemService(Context.TELEPHONY_SERVICE);

    }
}