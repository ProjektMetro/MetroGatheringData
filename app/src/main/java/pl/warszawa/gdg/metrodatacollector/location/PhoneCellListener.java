package pl.warszawa.gdg.metrodatacollector.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.subway.MapElement;
import pl.warszawa.gdg.metrodatacollector.subway.Station;
import pl.warszawa.gdg.metrodatacollector.ui.NotificationHelper;

public class PhoneCellListener extends PhoneStateListener {
    private static final String TAG = "PhoneCellListener";
    private static final long TIME_BETWEENMEASURES = 4000;
    private static final int NOTIFICATION_KNOWN_ID = 2341;
    private static long prevMeasurement;
    private Context context;

    private static TowerInfo prevTower;
    /**
     * As only info about prevTower is not enough (can switch 2g->3g in same place)
     */
    private static MapElement prevMapElement;

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
        NotificationHelper.showRunningNotification(context);

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

    private void gsmTowerChanged(final TowerInfo tower, TowerInfo prevTower, TelephonyManager telephonyManager, List<CellInfo> cells){
        Log.d(TAG, "gsmTowerChanged from:" + prevTower + ", to: " + tower);
        if(prevTower != null && tower != null) {
            Log.d(TAG, "gsmTowerChanged from: " + prevTower.getUniqueId() + ", to: " + tower.getUniqueId());
        }
        if(FlagsLocal.useWifi) {
            try {
                List<ScanResult> wifiNetworks = NetworkLocation.getAllWifi(context).getScanResults();
            } catch (Exception e) {
                //TODO Handle it
            }
        }
        EventBus.getDefault().post(tower);

        if(FlagsLocal.showNotificationInfo) {
            //TODO first check offline then if not found check online - and update offline storage (?)
            //Check if we know towerId
            //If not ask to add place
            ParseHelper.getStation(tower.getUniqueId(), tower.getMnc(), new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list != null && list.size() > 0 && list.get(0) != null) {
                        Station station = ParseHelper.getStation(list.get(0));
                        if (station != null && station.getName() != null) {
                            Log.d(TAG, "gsmTowerChanged station known: " + station);
                            //check if place has actually changed - as can be 2g->3g in same place
                            if(!station.equals(prevMapElement)) {
                                //We checked if we are at same place but on different cellId
                                prevMapElement = station;
                                NotificationHelper.showNotification(NOTIFICATION_KNOWN_ID, station.getName(), "Your current location.", context);
                                NotificationHelper.hideNotificationNewPlace(context);
                            }
                        } else {
                            //Place not known, lets ask to add it
                            NotificationHelper.showNotificationNewPlace(tower.getUniqueId(), context);
                            NotificationHelper.hideNotification(context, NOTIFICATION_KNOWN_ID);
                        }
                    } else {
                        //Place not known, lets ask to add it
                        NotificationHelper.showNotificationNewPlace(tower.getUniqueId(), context);
                        NotificationHelper.hideNotification(context, NOTIFICATION_KNOWN_ID);
                    }
                }
            });
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

    /**
     * Used to deleted previous locations - TowerInfo and MapElement
     */
    public static void reset() {
        PhoneCellListener.prevMapElement = null;
        PhoneCellListener.prevTower = null;
        PhoneCellListener.prevMeasurement = 0;
    }
}
