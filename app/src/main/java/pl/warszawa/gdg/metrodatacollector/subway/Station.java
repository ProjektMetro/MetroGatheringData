package pl.warszawa.gdg.metrodatacollector.subway;

import android.location.Location;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michal on 2015-04-29.
 */
public class Station extends MapElement {
    private static final String TAG = Station.class.getSimpleName();
    private final String name;
    private ParseObject parseObject;
    private List<SubwayLine> lines = new ArrayList<>();
    private boolean separatedPlatforms;

    private String [] cellIdsOutsidePlay;
    private String [] cellIdsOutsidePlus;
    private String [] cellIdsOutsideTmobile;
    private String [] cellIdsOutsideOrange;
    private String [] cellIdsOutsideOther;

    public List<SubwayLine> getLines() {
        return lines;
    }

    public static class Builder {
        private Location location;
        private int distanceInMeters;
        private int distanceInSeconds;

        private String [] wifiBssids;

        private String [] cellIdsOutsidePlay;
        private String [] cellIdsOutsidePlus;
        private String [] cellIdsOutsideOrange;
        private String [] cellIdsOutsideTmobile;
        private String [] cellIdsOutsideOther;

        private String [] cellIdsPlay;
        private String [] cellIdsPlus;
        private String [] cellIdsOrange;
        private String [] cellIdsTmobile;
        private String [] cellIdsOther;

        private final String name;
        private List<SubwayLine> lines;
        private boolean separatedPlatforms;


        public Builder(String name) {
            this.name = name;
        }

        public Builder(String name, List<SubwayLine> lines) {
            this.name = name;
            this.lines = lines;
        }

        public Builder separatedPlatforms(boolean separatedPlatforms) {
            this.separatedPlatforms = separatedPlatforms;
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

        public Builder gsmOutsidePlay(String[] gsmCellsId) {
            this.cellIdsOutsidePlay = gsmCellsId;
            return this;
        }

        public Builder gsmOutsidePlus(String[] gsmCellsId) {
            this.cellIdsOutsidePlus = gsmCellsId;
            return this;
        }

        public Builder gsmOutsideOrange(String[] gsmCellsId) {
            this.cellIdsOutsideOrange = gsmCellsId;
            return this;
        }

        public Builder gsmOutsideTmobile(String[] gsmCellsId) {
            this.cellIdsOutsideTmobile = gsmCellsId;
            return this;
        }

        public Builder gsmOutsideOther(String[] gsmCellsId) {
            this.cellIdsOutsideOther = gsmCellsId;
            return this;
        }

        public Builder wifiBssids(String[] wifiBssids) {
            this.wifiBssids = wifiBssids;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder distanceInSeconds(int distanceInSeconds) {
            this.distanceInSeconds = distanceInSeconds;
            return this;
        }

        public Builder distanceInMeters(int distanceInMeters) {
            this.distanceInMeters = distanceInMeters;
            return this;
        }
    }

    public void setLines(List<SubwayLine> lines) {
        this.lines = lines;
    }

    public boolean getSeparatedPlatforms() {
        return separatedPlatforms;
    }

    public void setSeparatedPlatforms(boolean separatedPlatforms) {
        this.separatedPlatforms = separatedPlatforms;
    }

    public String[] getCellIdsOutsidePlay() {
        return cellIdsOutsidePlay;
    }

    public void setCellIdsOutsidePlay(String[] cellIdsOutsidePlay) {
        this.cellIdsOutsidePlay = cellIdsOutsidePlay;
    }

    public String[] getCellIdsOutsidePlus() {
        return cellIdsOutsidePlus;
    }

    public void setCellIdsOutsidePlus(String[] cellIdsOutsidePlus) {
        this.cellIdsOutsidePlus = cellIdsOutsidePlus;
    }

    public String[] getCellIdsOutsideTmobile() {
        return cellIdsOutsideTmobile;
    }

    public void setCellIdsOutsideTmobile(String[] cellIdsOutsideTmobile) {
        this.cellIdsOutsideTmobile = cellIdsOutsideTmobile;
    }

    public String[] getCellIdsOutsideOrange() {
        return cellIdsOutsideOrange;
    }

    public void setCellIdsOutsideOrange(String[] cellIdsOutsideOrange) {
        this.cellIdsOutsideOrange = cellIdsOutsideOrange;
    }

    public String[] getCellIdsOutsideOther() {
        return cellIdsOutsideOther;
    }

    public void setCellIdsOutsideOther(String[] cellIdsOutsideOther) {
        this.cellIdsOutsideOther = cellIdsOutsideOther;
    }

    public String getName() {
        return name;
    }


    public Station(Builder builder) {
        name = builder.name;
        lines = builder.lines;

        if(lines !=null) {
            for (SubwayLine line : lines) {
                line.addStation(this);
            }
        }

        setLocation(builder.location);
        setDistanceInMeters(builder.distanceInMeters);
        setDistanceInSeconds(builder.distanceInSeconds);

        setCellIdsPlay(builder.cellIdsPlay);
        setCellIdsOrange(builder.cellIdsOrange);
        setCellIdsPlus(builder.cellIdsPlus);
        setCellIdsTmobile(builder.cellIdsTmobile);
        setCellIdsOther(builder.cellIdsOther);

        setWifiBssids(builder.wifiBssids);

        this.cellIdsOutsideOrange = builder.cellIdsOutsideOrange;
        this.cellIdsOutsidePlay = builder.cellIdsOutsidePlay;
        this.cellIdsOutsidePlus = builder.cellIdsOutsidePlus;
        this.cellIdsOutsideTmobile = builder.cellIdsOutsideTmobile;
        this.cellIdsOutsideOther = builder.cellIdsOutsideOther;

        cellIdsOutsidePlay = builder.cellIdsOutsidePlay;
        separatedPlatforms = builder.separatedPlatforms;
    }
    //elevators, stairs, ied, police station etc.

    public static final String PARSE_CLASS_STATION = "SubwayStation";
    public static final String PARSE_NAME = "SubwayElementName";
    public static final String PARSE_ORANGE = "SubwayElementOrange";
    public static final String PARSE_OTHER = "SubwayElementOther";
    public static final String PARSE_PLAY = "SubwayElementPlay";
    public static final String PARSE_PLUS = "SubwayElementPlus";
    public static final String PARSE_TMOBILE = "SubwayElementTmobile";
    public static final String PARSE_ORANGE_OUTSIDE = "SubwayElementOrangeOutside";
    public static final String PARSE_OTHER_OUTSIDE = "SubwayElementOtherOutside";
    public static final String PARSE_PLAY_OUTSIDE = "SubwayElementPlayOutside";
    public static final String PARSE_PLUS_OUTSIDE = "SubwayElementPlusOutside";
    public static final String PARSE_TMOBILE_OUTSIDE = "SubwayElementTmobileOutside";
    public static final String PARSE_DISTANCE_METERS = "SubwayElementDistanceMeters";
    public static final String PARSE_DISTANCE_SECONDS = "SubwayElementDistanceSeconds";
    public static final String PARSE_LOCATION_LAT = "SubwayElementLocationLat";
    public static final String PARSE_LOCATION_LON = "SubwayElementLocatioNLon";
    public static final String PARSE_LINES = "SubwayElementLines";
    public static final String PARSE_PLATFORMS = "SubwayElementPlatforms";
    public static final String PARSE_BSSIDS = "SubwayElementBssids";

    /**
     * Used for updating whole object, use it carefully!
     */
    public void updateParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CLASS_STATION);
        query.whereEqualTo(PARSE_NAME, getName());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    parseObject = object;
                } else {
                    parseObject = new ParseObject(PARSE_CLASS_STATION);
                }
                parseObject.put(PARSE_NAME, getName());
                //Add cellids for inside tower
                if (getCellIdsOrange() != null) {
                    parseObject.addAllUnique(PARSE_ORANGE, Arrays.asList(getCellIdsOrange()));
                }
                if (getCellIdsOther() != null) {
                    parseObject.addAllUnique(PARSE_OTHER, Arrays.asList(getCellIdsOther()));
                }
                if (getCellIdsPlay() != null) {
                    parseObject.addAllUnique(PARSE_PLAY, Arrays.asList(getCellIdsPlay()));
                }
                if (getCellIdsPlus() != null) {
                    parseObject.addAllUnique(PARSE_PLUS, Arrays.asList(getCellIdsPlus()));
                }
                if (getCellIdsTmobile() != null) {
                    parseObject.addAllUnique(PARSE_TMOBILE, Arrays.asList(getCellIdsTmobile()));
                }

                //add cellids for outside tower
                if (cellIdsOutsideOrange != null) {
                    parseObject.addAllUnique(PARSE_ORANGE_OUTSIDE, Arrays.asList(cellIdsOutsideOrange));
                }
                if (cellIdsOutsideOther != null) {
                    parseObject.addAllUnique(PARSE_OTHER_OUTSIDE, Arrays.asList(cellIdsOutsideOther));
                }
                if (cellIdsOutsidePlay != null) {
                    parseObject.addAllUnique(PARSE_PLAY_OUTSIDE, Arrays.asList(cellIdsOutsidePlay));
                }
                if (cellIdsOutsidePlus != null) {
                    parseObject.addAllUnique(PARSE_PLUS_OUTSIDE, Arrays.asList(cellIdsOutsidePlus));
                }
                if (cellIdsOutsideTmobile != null) {
                    parseObject.addAllUnique(PARSE_TMOBILE_OUTSIDE, Arrays.asList(cellIdsOutsideTmobile));
                }

                parseObject.put(PARSE_PLATFORMS, getSeparatedPlatforms());
                parseObject.put(PARSE_DISTANCE_METERS, getDistanceInMeters());
                parseObject.put(PARSE_DISTANCE_SECONDS, getDistanceInSeconds());

                if (getLocation() != null) {
                    parseObject.put(PARSE_LOCATION_LAT, getLocation().getLatitude());
                    parseObject.put(PARSE_LOCATION_LON, getLocation().getLongitude());
                }
                if (getLines() != null) {
                    //parseStation.addAllUnique(PARSE_LINES, getLines());
                    //TODO
                }
                if (getWifiBssids() != null) {
                    parseObject.addAllUnique(PARSE_BSSIDS, Arrays.asList(getWifiBssids()));
                }
                try {
                    parseObject.save();
                } catch (ParseException e1) {
                    Log.d(TAG, "done error while saving: " + e1.getLocalizedMessage());
                }
            }
        });
    }
}
