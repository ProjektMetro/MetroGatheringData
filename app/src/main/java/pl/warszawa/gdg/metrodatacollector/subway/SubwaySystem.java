package pl.warszawa.gdg.metrodatacollector.subway;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SubwaySystem {
    public ArrayList<Station> stations = new ArrayList<>();
    public ArrayList<SubwayLine> lines = new ArrayList<>();

    public SubwaySystem() {
        setLines();
    }

    private void setLines() {
        SubwayLine subwayOne = new SubwayLine();
        SubwayLine subwayTwo = new SubwayLine();
        lines = new ArrayList<>();
        subwayOne.line = new ArrayList<>();
        lines.add(subwayOne);

        ArrayList<SubwayLine> m1 = new ArrayList<>();
        ArrayList<SubwayLine> m1m2 = new ArrayList<>();
        m1.add(subwayOne);
        m1m2.add(subwayOne);
        m1m2.add(subwayTwo);

        //GSM order LTE, 3G, 2G - network type 13, 15, 10
        addElement(subwayOne, new Station(new Station.Builder("MLOCINY", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("WAWRZYSZEW", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("STARE_BIELANY", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("SLODOWIEC", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("MARYMONT", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("PL_WILSONA", m1)
                .gsmInsidePlay(new String[]{"26063138141010", "26061079723952", "26061080598952"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260622138122010", "26061079723952"})));
        addElement(subwayOne, new Station(new Station.Builder("DWORZEC_GDANSKI", m1)
                .gsmInsidePlay(new String[]{"260622138122010", "26061079723944", "26061080598744"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260623138121010"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"26067138092010"})));
        addElement(subwayOne, new Station(new Station.Builder("RATUSZ_ARSENAL", m1)
                .gsmInsidePlay(new String[]{"26066138091010", "26061079723928", "26061080597920"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"26066138091010"})));
        addElement(subwayOne, new Station(new Station.Builder("SWIETOKRZYSKA", m1m2)
                .gsmInsidePlay(new String[]{"260624138061010", "2606107972394", "2606108059764"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260624138061010"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260623138042010"})));
        addElement(subwayOne, new Station(new Station.Builder("CENTRUM", m1)
                .gsmInsidePlay(new String[]{"260623138042010", "26061079723911", "2606108059733", "260621138041010"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260621138041010"})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"260628138012010"})));
        addElement(subwayOne, new Station(new Station.Builder("POLITECHNIKA", m1)
                .gsmInsidePlay(new String[]{"260628138012010", "26061079723927", "26061080596727"})));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("POLE_MOKOTOWSKIE", m1)
                .gsmInsidePlay(new String[]{"", "26061080596435", ""})));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("RACLAWICKA", m1)
                .gsmInsidePlay(new String[]{"", "26061079263699", ""})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"", "26061079263699", ""})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"", "260610792636115", ""})));
        addElement(subwayOne, new Station(new Station.Builder("WIERZBNO", m1)
                .gsmInsidePlay(new String[]{"", "260610792636115", ""})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"", "260610792636115", ""})));
        addElement(subwayOne, new Track(new Track.Builder()
                .gsmInsidePlay(new String[]{"", "260610792636131", ""})));
        addElement(subwayOne, new Station(new Station.Builder("WILANOWSKA", m1)
                .gsmInsidePlay(new String[]{"", "260610792636131", ""})));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("SLUZEW", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("URSYNOW", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("STOKLOSY", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("IMIELIN", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("NATOLIN", m1)));
        addElement(subwayOne, new Track());
        addElement(subwayOne, new Station(new Station.Builder("KABATY", m1)));

        //TODO add some id for order stations in particular lines - problem with more than one line
        for(Station station : stations) {
            subwayOne.addStation(station);
            subwayOne.addMapElemnent(new Track());
        }
        subwayOne.line.remove(subwayOne.line.size()-1);//To remove last additional track element
    }

    private void addElement(SubwayLine subwayLine, final MapElement element) {
        if(element instanceof Station) {
            stations.add((Station)element);
            subwayLine.addStation((Station)element);
        } else if (element instanceof Track) {
            subwayLine.addMapElemnent(element);
        }

        if(element.getParseObject() != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SubwayStation");
            query.whereEqualTo(Station.PARSE_NAME, ((Station) element).getName());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + scoreList.size() + " scores");
                        if(scoreList != null && scoreList.size() >= 1) {
                            //it
                        } else {
                            element.getParseObject().saveInBackground();
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    public Track getTrackBetween(Station subwayStation, Station subwayStationTwo) {
        if(lines == null || lines.size() == 0) {
            return null;
        }
        if(subwayStation == null || subwayStationTwo == null) {
            return null;
        }
        for(SubwayLine subwayLine : lines) {
            //check each line
            Station prevStation = null;
            Track prevTrack = null;
            for(MapElement mapElement : subwayLine.line) {
                //Check through all connected lines

                if(mapElement instanceof Station) {
                    if(((Station) mapElement).equals(subwayStation) || ((Station) mapElement).equals(subwayStationTwo)) {
                        if(prevStation != null && prevStation.equals(subwayStation) || prevStation.equals(subwayStationTwo)) {
                            //it was between prevStation and this station so prevTrack
                            return prevTrack;
                        }
                    }
                    prevStation = ((Station) mapElement);
                }
                if(mapElement instanceof Track) {
                    prevTrack = ((Track) mapElement);
                }
            }
        }
        return  null;
    }
}
