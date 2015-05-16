package pl.warszawa.gdg.metrodatacollector.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import pl.warszawa.gdg.metrodatacollector.subway.Station;

/**
 * Created by Michal Tajchert on 2015-05-14.
 */
public class ParseHelper {
    private static final String TAG = "ParseHelper";
    //TODO local queries first and if no results online one.

    public static void getAllStations(FindCallback<ParseObject> findCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        query.findInBackground(findCallback);
    }

    public static void getStation(String name,FindCallback findCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        query.whereEqualTo(Station.PARSE_NAME, (name));
        query.findInBackground(findCallback);
    }

    public static void getStation(String cellId, int mnc, FindCallback findCallback) {
        List<String> valuesTemp = new ArrayList<>();
        valuesTemp.add(cellId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        if(mnc == 6 || mnc == 98) {
            query.whereContainedIn(Station.PARSE_PLAY, valuesTemp);
        } else if(mnc == 1) {
            query.whereContains(Station.PARSE_PLUS, cellId);
        } else if(mnc == 3) {
            query.whereContains(Station.PARSE_ORANGE, cellId);
        } else if(mnc == 2) {
            query.whereContains(Station.PARSE_TMOBILE, cellId);
        } else {
            query.whereContains(Station.PARSE_OTHER, cellId);
        }
        query.findInBackground(findCallback);
    }

    public static void addStationCellId(Station station, final String cellId, final int mnc) {
        if(cellId == null || cellId.length() == 0 || station == null) {
            return;
        }
        getStation(station.getName(), new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (list != null && list.size() > 0) {
                    if (list.get(0) != null) {
                        if (mnc == 6 || mnc == 98) {
                            addCellIdParseObject(list, Station.PARSE_PLAY, cellId);
                        } else if (mnc == 1) {
                            addCellIdParseObject(list, Station.PARSE_PLUS, cellId);
                        } else if (mnc == 3) {
                            addCellIdParseObject(list, Station.PARSE_ORANGE, cellId);
                        } else if (mnc == 2) {
                            addCellIdParseObject(list, Station.PARSE_TMOBILE, cellId);
                        } else {
                            addCellIdParseObject(list, Station.PARSE_OTHER, cellId);
                        }
                        list.get(0).saveInBackground();
                        Log.d(TAG, "done is first");
                    }
                }
            }
        });
    }

    private static void addCellIdParseObject(List<ParseObject> list, String arrayName, String cellId) {
        ArrayList<String> ids = (ArrayList) list.get(0).get(arrayName);
        if(ids == null) {
            ids = new ArrayList<String>();
        }
        ids.add(cellId);
        list.get(0).addAllUnique(arrayName, ids);
    }
}
