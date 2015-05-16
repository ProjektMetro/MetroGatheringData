package pl.warszawa.gdg.metrodatacollector.location;

import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TowerInfo {
    private static final String TAG = "TowerInfo";
    private static final int UNKNOWN = -1;

    private int mcc;//Mobile Country Code
    private int mnc;//Mobile Network Code
    private int lac;//Location Area Code
    private int cid;//Cell Identifier for 2G
    private int psc;//Cell Identifier for 3G/LTE

    private int pci;//Physical Cell Id
    private int ci;//Cell Identity
    private int tac;//Tracking Area Code

    private int networkType;
    private int signalStrength;

    public TowerInfo(CellIdentityLte cellIdentityLte) {
        this.mcc = cellIdentityLte.getMcc();
        this.mnc = cellIdentityLte.getMnc();
        this.ci = cellIdentityLte.getCi();
        this.tac = cellIdentityLte.getTac();
        this.pci = cellIdentityLte.getPci();
        this.networkType = TelephonyManager.NETWORK_TYPE_LTE;
    }

    public TowerInfo(CellIdentityGsm cellIdentityGsm) {
        this.mcc = cellIdentityGsm.getMcc();
        this.mnc = cellIdentityGsm.getMnc();
        this.cid = cellIdentityGsm.getCid();
        this.lac = cellIdentityGsm.getLac();
    }

    public TowerInfo(CellIdentityCdma cellIdentityCdma) {
        //TODO not needed for Warsaw
    }

    public TowerInfo(CellIdentityWcdma cellIdentityWcdma) {
        //TODO only API > 18
        this.cid = cellIdentityWcdma.getCid();
        this.lac = cellIdentityWcdma.getLac();
        this.mcc = cellIdentityWcdma.getMcc();
        this.mnc = cellIdentityWcdma.getMnc();
        this.psc = cellIdentityWcdma.getPsc();
    }


    public TowerInfo(int mcc, int mnc, int lac, int cid, int psc) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.psc = psc;
    }

    public TowerInfo(int mcc, int mnc, int lac, int cid, int psc, int signalStrength) {
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.psc = psc;
        this.signalStrength = signalStrength;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        Log.d(TAG, "setNetworkType type: " + networkType);
        this.networkType = networkType;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public int getLac() {
        return lac;
    }

    public int getCid() {
        return cid;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    /**
     * Get Global Cell Id
     */
    public String getUniqueId() {
        if(this.networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            //LTE
            return String.valueOf(mcc) + String.valueOf(mnc) + String.valueOf(pci) + String.valueOf(ci) + String.valueOf(tac);
        }
        if(this.networkType == TelephonyManager.NETWORK_TYPE_HSPA || this.networkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
            //3G
            return  String.valueOf(mcc) + String.valueOf(mnc) +  String.valueOf(lac) + String.valueOf(cid) + String.valueOf(psc);
        }
        if(this.networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
            //2G
            return  String.valueOf(mcc) + String.valueOf(mnc) +  String.valueOf(lac) + String.valueOf(cid);
        }

        //Most likely you shouldn't be here.
        return String.valueOf(mcc) + String.valueOf(mnc) + String.valueOf(lac) + String.valueOf(cid) + String.valueOf(psc);
    }



    @Override
    public String toString() {
        return "TowerInfo{" +
                "id=" + getUniqueId() +
                ", networkType=" + networkType +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", lac=" + lac +
                ", cid=" + cid +
                ", psc=" + psc +
                ", pci=" + pci +
                ", ci=" + ci +
                ", tac=" + tac +
                ", signalStrength=" + signalStrength +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o instanceof TowerInfo) {
            if(this.getUniqueId() == null) {
                return false;
            }
            return this.getUniqueId().equals(((TowerInfo) o).getUniqueId());
        }
        return false;
    }
}