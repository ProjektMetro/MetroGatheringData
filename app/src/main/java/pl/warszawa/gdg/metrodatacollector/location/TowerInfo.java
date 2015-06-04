package pl.warszawa.gdg.metrodatacollector.location;

import android.annotation.TargetApi;
import android.os.Build;
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
import android.util.Log;

public class TowerInfo {

    public enum Type {
        PLAY, PLUS, ORANGE, T_MOBILE, OTHER;
    }

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

    public static TowerInfo getTowerInfo(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoLte) {
            return new TowerInfo(((CellInfoLte) cellInfo).getCellIdentity());
        } else if (cellInfo instanceof CellInfoGsm) {
            return new TowerInfo(((CellInfoGsm) cellInfo).getCellIdentity());
        } else if (cellInfo instanceof CellInfoCdma) {
            return new TowerInfo(((CellInfoCdma) cellInfo).getCellIdentity());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cellInfo instanceof CellInfoWcdma) {
            return new TowerInfo(((CellInfoWcdma) cellInfo).getCellIdentity());
        }
        return null;
    }

    public static TowerInfo getTowerInfo(NeighboringCellInfo cellInfo) {
        //TODO: To implement
        return null;
    }

    public TowerInfo(CellIdentityLte cellIdentityLte) {
        this.mcc = cellIdentityLte.getMcc();
        this.mnc = cellIdentityLte.getMnc();
        this.ci = cellIdentityLte.getCi();
        this.tac = cellIdentityLte.getTac();
        this.pci = cellIdentityLte.getPci();
        this.networkType = TelephonyManager.NETWORK_TYPE_LTE;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public TowerInfo(CellIdentityGsm cellIdentityGsm) {
        this.mcc = cellIdentityGsm.getMcc();
        this.mnc = cellIdentityGsm.getMnc();
        this.cid = cellIdentityGsm.getCid();
        this.lac = cellIdentityGsm.getLac();
    }

    public TowerInfo(CellIdentityCdma cellIdentityCdma) {
        //TODO not needed for Warsaw
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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

    public Type getType() {
        if (mnc == 6 || mnc == 98) {
            return Type.PLAY;
        } else if (mnc == 1) {
            return Type.PLUS;
        } else if (mnc == 3) {
            return Type.ORANGE;
        } else if (mnc == 2) {
            return Type.T_MOBILE;
        } else {
            return Type.OTHER;
        }
    }

    /**
     * Get Global Cell Id
     */
    public String getUniqueId() {
        if (this.networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            //LTE
            return String.valueOf(mcc) + String.valueOf(mnc) + String.valueOf(pci) + String.valueOf(ci) + String.valueOf(tac);
        }
        if (this.networkType == TelephonyManager.NETWORK_TYPE_HSPA || this.networkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
            //3G
            return String.valueOf(mcc) + String.valueOf(mnc) + String.valueOf(lac) + String.valueOf(cid) + String.valueOf(psc);
        }
        if (this.networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
            //2G
            return String.valueOf(mcc) + String.valueOf(mnc) + String.valueOf(lac) + String.valueOf(cid);
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
