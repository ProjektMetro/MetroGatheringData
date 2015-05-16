package pl.warszawa.gdg.metrodatacollector.subway;

import android.location.Location;

import com.parse.ParseObject;

public class MapElement {

    private int distanceInMeters;
    private long distanceInSeconds;
    //For storing map location
    private Location location;

    //GSM
    private String [] cellIdsPlay;
    private String [] cellIdsPlus;
    private String [] cellIdsTmobile;
    private String [] cellIdsOrange;
    private String [] cellIdsOther;

    //Wifi
    private String [] wifiBssids = new String[0];

    public String[] getCellIdsOther() {
        return cellIdsOther;
    }

    public void setCellIdsOther(String[] cellIdsOther) {
        this.cellIdsOther = cellIdsOther;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String[] getWifiBssids() {
        return wifiBssids;
    }

    public void setWifiBssids(String[] wifiBssids) {
        this.wifiBssids = wifiBssids;
    }

    public int getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(int distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public long getDistanceInSeconds() {
        return distanceInSeconds;
    }

    public void setDistanceInSeconds(long distanceInSeconds) {
        this.distanceInSeconds = distanceInSeconds;
    }

    public String[] getCellIdsPlay() {
        return cellIdsPlay;
    }

    public void setCellIdsPlay(String[] cellIdsPlay) {
        this.cellIdsPlay = cellIdsPlay;
    }

    public String[] getCellIdsPlus() {
        return cellIdsPlus;
    }

    public void setCellIdsPlus(String[] cellIdsPlus) {
        this.cellIdsPlus = cellIdsPlus;
    }

    public String[] getCellIdsTmobile() {
        return cellIdsTmobile;
    }

    public void setCellIdsTmobile(String[] cellIdsTmobile) {
        this.cellIdsTmobile = cellIdsTmobile;
    }

    public String[] getCellIdsOrange() {
        return cellIdsOrange;
    }

    public void setCellIdsOrange(String[] cellIdsOrange) {
        this.cellIdsOrange = cellIdsOrange;
    }

    public ParseObject getParseObject(){return null;}
}
