package pl.warszawa.gdg.metrodatacollector.subway;

import android.location.Location;

/**
 * Created by Michal Tajchert on 2015-04-30.
 */
public class Track extends MapElement {
    private Location location;
    private int curve;//TODO int?

    public static class Builder {
        private Location location;
        private int curve;
        private int distanceMeters;
        private int distanceSeconds;

        //TODO create separate builder class in @MapElement to use across with Station object?
        private String [] cellIdsPlay;
        private String [] cellIdsPlus;
        private String [] cellIdsTmobile;
        private String [] cellIdsOrange;
        private String [] cellIdsOther;
        //Wifi
        private String [] wifiBssids;

        public Builder() {

        }

        public Builder setCurver(int curve) {
            this.curve = curve;
            return this;
        }

        //TODO code duplication with @Station.Builder
        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder distanceMeters(int distanceMeters) {
            this.distanceMeters = distanceMeters;
            return this;
        }

        public Builder distanceSeconds(int distanceSeconds) {
            this.distanceSeconds = distanceSeconds;
            return this;
        }

        public Builder gsmInsidePlay(String[] gsmCellsId) {
            this.cellIdsPlay = gsmCellsId;
            return this;
        }

        public Builder gsmInsidePlus(String[] gsmCellsId) {
            this.cellIdsPlus = gsmCellsId;
            return this;
        }

        public Builder gsmInsideTmobile(String[] gsmCellsId) {
            this.cellIdsTmobile = gsmCellsId;
            return this;
        }

        public Builder gsmInsideOrange(String[] gsmCellsId) {
            this.cellIdsOrange = gsmCellsId;
            return this;
        }

        public Builder gsmInsideOther(String[] gsmCellsId) {
            this.cellIdsOther = gsmCellsId;
            return this;
        }

        public Builder wifiBssids(String[] wifiBssids) {
            this.wifiBssids = wifiBssids;
            return this;
        }
    }

    public Track (Builder builder) {
        setDistanceInSeconds(builder.distanceSeconds);
        setDistanceInMeters(builder.distanceMeters);
        setCellIdsPlay(builder.cellIdsPlay);
        setCellIdsOrange(builder.cellIdsOrange);
        setCellIdsPlus(builder.cellIdsPlus);
        setCellIdsTmobile(builder.cellIdsTmobile);
        setCellIdsOther(builder.cellIdsOther);

        setWifiBssids(builder.wifiBssids);
    }

    public Track () {

    }
}
