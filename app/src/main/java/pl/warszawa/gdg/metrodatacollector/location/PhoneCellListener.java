package pl.warszawa.gdg.metrodatacollector.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.ui.NotificationHelper;

public class PhoneCellListener extends PhoneStateListener {
    private static final String TAG = "PhoneCellListener";
    private static final long TIME_BETWEENMEASURES = 4000;
    private static long prevMeasurement;
    private Context context;
    private TowerInfo prevTower;

    public PhoneCellListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        AppMetroDataCollector.isRunning = true;
        if(System.currentTimeMillis() - prevMeasurement < TIME_BETWEENMEASURES) {
            //Too fast, wait
            return;
        }
        prevMeasurement = System.currentTimeMillis();

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cells = telephonyManager.getAllCellInfo();
        TowerInfo tower = NetworkLocation.findConnectedTower(cells);

        if(tower == null) {
            //TODO Eventbus with problem to determine location
            return;
        }

        if(!tower.equals(prevTower)) {
            //Towers has changed!
            tower.setNetworkType(telephonyManager.getNetworkType());
            gsmTowerChanged(tower, prevTower, telephonyManager, cells);
            prevTower = tower;
        }
    }

    private void gsmTowerChanged(TowerInfo tower, TowerInfo prevTower, TelephonyManager telephonyManager, List<CellInfo> cells){
        if(prevTower != null && tower != null) {
            Log.d(TAG, "gsmTowerChanged from: " + prevTower.getUniqueId() + ", to: " + tower.getUniqueId());
        }
        if(FlagsLocal.useWifi) {
            List<ScanResult> wifiNetworks = NetworkLocation.getAllWifi(context).getScanResults();
        }
        EventBus.getDefault().post(tower);

        //Check if we know towerId
        //If not ask to add place
        if(FlagsLocal.showNotificationInfo) {
            NotificationHelper.showNotification(3333, "Metro Data Collector", "Cell id: " + tower.getUniqueId(), context);
        }
    }



    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        //FIXME this doesn't work... :/ https://code.google.com/p/android/issues/detail?id=43467
    }

    /**
     * For manual scanning and adding a place (from UI)
     * @param name
     * @param context
     */
    public static void addNewPlace(String name, Context context) {
        if(name == null) {
            name = "Station_" + System.currentTimeMillis()/1000;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cells = telephonyManager.getAllCellInfo();
        TowerInfo tower = NetworkLocation.findConnectedTower(cells);
        tower.setNetworkType(telephonyManager.getNetworkType());

    }
}
