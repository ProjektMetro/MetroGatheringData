package pl.warszawa.gdg.metrodatacollector.location;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class NetworkLocation {
    private static final String TAG = "NetworkLocation";

    public static WifiLocation getAllWifi(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(manager.isWifiEnabled()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> wifiAround = wifiManager.getScanResults();
            //Log.d(TAG, "getAllWifi " + wifiAround);
            return new WifiLocation(wifiAround);
        }
        return null;
    }

    public static TowerInfo getTowerCurrent(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        TowerInfo connectedTower = findConnectedTower(telephonyManager.getAllCellInfo());
        connectedTower.setNetworkType(telephonyManager.getNetworkType());
        return connectedTower;
    }

    /**
     * Return TowerInfo of all towers in range, but doesn't work for LTE.
     * @param context
     * @return
     * @throws RuntimeException
     */
    public static ArrayList<TowerInfo> getAllTowers(Context context) throws RuntimeException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String networkOperator = telephonyManager.getNetworkOperator();
        if (networkOperator == null || "".equals(networkOperator)) {
            //not connected - no GSM network module or it is OFF
            throw new RuntimeException("GSM module is OFF or unavailable.");
        }

        ArrayList<TowerInfo> allTowers = new ArrayList<>();
        List<CellInfo> cells = telephonyManager.getAllCellInfo();
        for (CellInfo info : cells) {
            Log.d(TAG, "getNetworkTowersIds cell: " + info.toString());
            TowerInfo tower = null;
            if (info instanceof CellInfoGsm) {
                final CellIdentityGsm gsm = ((CellInfoGsm) info).getCellIdentity();
                tower = new TowerInfo(gsm);
            } else if (info instanceof CellInfoCdma) {
                final CellIdentityCdma cdma = ((CellInfoCdma) info).getCellIdentity();
                tower = new TowerInfo(cdma);
            } else if (info instanceof CellInfoLte) {
                final CellIdentityLte lte = ((CellInfoLte) info).getCellIdentity();
                tower = new TowerInfo(lte);
            }
            /*else if (info instanceof CellInfoWcdma) {
                final CellIdentityWcdma gsm = ((CellInfoWcdma) info).getCellIdentity();//TODO API 18 support
            }*/
            if (tower != null) {
                allTowers.add(tower);
            }
        }
        return allTowers;
    }

    public static ArrayList<TowerInfo> getTowersNearby(Context context) throws RuntimeException {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = null;
        try {
            cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        } catch (SecurityException e) {
            //No ACCESS_COARSE_LOCATION permission
            Log.e(TAG, "getNetworkTowersIds No ACCESS_COARSE_LOCATION permission!");
        }

        String networkOperator = telephonyManager.getNetworkOperator();
        if(networkOperator == null || "".equals(networkOperator)) {
            //not connected - no GSM network module or it is OFF
            throw new RuntimeException("GSM module is OFF or unavailable.");
        }
        int mcc = Integer.parseInt(networkOperator.substring(0, 3));
        int mnc = Integer.parseInt(networkOperator.substring(3));
        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        int psc = cellLocation.getPsc();
        TowerInfo connectedTower = new TowerInfo(mcc, mnc, lac, cid, psc);
        connectedTower.setNetworkType(telephonyManager.getNetworkType());
        Log.d(TAG, "getNetworkTowersIds connected to: " +connectedTower);

        List<NeighboringCellInfo> neighboringCells = telephonyManager.getNeighboringCellInfo();
        ArrayList<TowerInfo> towersNearby = new ArrayList<>();
        //TODO if null chipset doesn't support this feature (looking at you, Samsung)
        for(NeighboringCellInfo cellNearby : neighboringCells) {
            TowerInfo tower = new TowerInfo(mcc, mnc, cellNearby.getLac(), cellNearby.getCid(), cellNearby.getPsc(), getDbmSignalStrength(cellNearby));//TODO check if first two parameters are correct?
            tower.setNetworkType(cellNearby.getNetworkType());
            towersNearby.add(tower);
        }

        Log.d(TAG, "getNetworkTowersIds towers nearby: " +towersNearby);
       return towersNearby;
    }

    private static int getDbmSignalStrength(NeighboringCellInfo cell) {
        int rssi = cell.getRssi();
        if(rssi == NeighboringCellInfo.UNKNOWN_RSSI) {
            return 0;
        } else {
            return (-113 + 2 * rssi);
        }
    }

    public static TowerInfo findConnectedTower(List<CellInfo> cells) {
        if(cells == null) {
            return null;
        }
        for(CellInfo cell: cells) {
            if(cell.isRegistered()) {
                TowerInfo tower = null;
                if (cell instanceof CellInfoGsm) {
                    final CellIdentityGsm gsm = ((CellInfoGsm) cell).getCellIdentity();
                    tower = new TowerInfo(gsm);
                } else if (cell instanceof CellInfoCdma) {
                    final CellIdentityCdma cdma = ((CellInfoCdma) cell).getCellIdentity();
                    tower = new TowerInfo(cdma);
                } else if (cell instanceof CellInfoLte) {
                    final CellIdentityLte lte = ((CellInfoLte) cell).getCellIdentity();
                    tower = new TowerInfo(lte);
                } else if (cell instanceof CellInfoWcdma) {
                    final CellIdentityWcdma lte = ((CellInfoWcdma) cell).getCellIdentity();
                    tower = new TowerInfo(lte);
                }
                return  tower;
            }
        }
        return null;
    }
}
