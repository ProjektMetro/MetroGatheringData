package pl.warszawa.gdg.metrodatacollector.location;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.warszawa.gdg.metrodatacollector.AppMetroDataCollector;
import pl.warszawa.gdg.metrodatacollector.FlagsLocal;
import pl.warszawa.gdg.metrodatacollector.data.ParseHelper;
import pl.warszawa.gdg.metrodatacollector.subway.Station;
import pl.warszawa.gdg.metrodatacollector.ui.NotificationHelper;

public class CellMonitorService extends Service {
    private static final String TAG = CellMonitorService.class.getSimpleName();
    public CellMonitorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppMetroDataCollector.isRunning = true;
        NotificationHelper.showRunningNotification(CellMonitorService.this);
        TowerInfo tower = NetworkLocation.getCurrentTower(CellMonitorService.this);

        if (tower != null) {
            if (!tower.equals(AppMetroDataCollector.towerCurrent)) {
                //Towers has changed!
                tower.setNetworkType(NetworkLocation.telephonyManager.getNetworkType());
                gsmTowerChanged(tower, AppMetroDataCollector.towerCurrent);
                AppMetroDataCollector.towerPrev = AppMetroDataCollector.towerCurrent;
                AppMetroDataCollector.towerCurrent = tower;
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void gsmTowerChanged(final TowerInfo tower, TowerInfo prevTower){
        Log.d(TAG, "gsmTowerChanged from:" + prevTower + ", to: " + tower);
        if(prevTower != null) {
            Log.d(TAG, "gsmTowerChanged from: " + prevTower.getUniqueId() + ", to: " + tower.getUniqueId());
        }
        EventBus.getDefault().post(tower);
        if(FlagsLocal.useWifi) {
            try {
                List<ScanResult> wifiNetworks = NetworkLocation.getAllWifi(CellMonitorService.this).getScanResults();
            } catch (Exception e) {
                //TODO Handle it
            }
        }
        if(AppMetroDataCollector.timeLastKnownLocation != 0
                && System.currentTimeMillis() - AppMetroDataCollector.timeLastKnownLocation > CellMonitorReceiver.TIMEOUT_WITHOUT_KNOWN_TOWER) {
            CellMonitorReceiver.stopGsmMonitor(CellMonitorService.this);
            return;
        }
        ParseHelper.getStation(tower.getUniqueId(), tower.getMnc(), new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0 && list.get(0) != null) {
                    Station station = ParseHelper.getStation(list.get(0));
                    if (station != null && station.getName() != null) {
                        Log.d(TAG, "gsmTowerChanged station known: " + station);
                        AppMetroDataCollector.timeLastKnownLocation = System.currentTimeMillis();
                        //check if place has actually changed - as can be 2g->3g in same place
                        if (!station.equals(AppMetroDataCollector.mapElementPrev)) {
                            //We checked if we are at same place but on different cellId
                            AppMetroDataCollector.mapElementPrev = station;
                            showNotificationKnown(station);
                        }
                    } else {
                        //Place not known, lets ask to add it
                        showNotificationNewPlace(tower);
                    }
                } else {
                    //Place not known, lets ask to add it
                    showNotificationNewPlace(tower);
                }
                //TODO stopSelf() here also?
            }
        });

    }

    private void showNotificationKnown(Station station) {
        if(FlagsLocal.showNotificationInfo) {
            //Check if we know towerId
            //If not ask to add place
            NotificationHelper.showStation(station, CellMonitorService.this);
            NotificationHelper.hideNotificationNewPlace(CellMonitorService.this);
        }
    }

    private void showNotificationNewPlace(TowerInfo tower) {
        if(FlagsLocal.showNotificationInfo) {
            //Check if we know towerId
            //If not ask to add place
            NotificationHelper.showNotificationNewPlace(tower.getUniqueId(), CellMonitorService.this);
            NotificationHelper.hideStationNotification(CellMonitorService.this);
        }
    }

    public static void reset() {
        AppMetroDataCollector.mapElementPrev = null;
        AppMetroDataCollector.towerCurrent = null;
        AppMetroDataCollector.towerPrev = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
