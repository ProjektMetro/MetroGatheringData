package pl.warszawa.gdg.metrodatacollector.location;

import android.net.wifi.ScanResult;

import java.util.List;


public class WifiLocation {
    private List<ScanResult> scanResults;

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public WifiLocation(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public String toString() {
        return "WifiLocation{" +
                "scanResults=" + scanResults +
                '}';
    }
}
