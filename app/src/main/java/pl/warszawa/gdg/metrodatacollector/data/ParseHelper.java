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
    private static final String TAG = ParseHelper.class.getSimpleName();
    //TODO local queries first and if no results online one.

    /**
     * Delete all local objects, then retreive new ones from Parse. Used only when you really need latest objects - to limit number of requests.
     */
    public static void updateLocalStations() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e != null) {
                    Log.d(TAG, "done local with error: " + e.getLocalizedMessage());
                    return;
                }
                for(ParseObject parseObject : list) {
                    try {
                        parseObject.unpin();
                    } catch (ParseException e1) {
                        Log.d(TAG, "updateLocalStations error while unpinning station: " + e1.getLocalizedMessage());
                    }
                }
                ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
                innerQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e != null) {
                            Log.d(TAG, "done with error: " + e.getLocalizedMessage());
                            return;
                        }
                        for(ParseObject parseObject : list) {
                            parseObject.pinInBackground();
                        }
                    }
                });
            }
        });
    }

    public static void getAllStations(FindCallback<ParseObject> findCallback) {
        getAllStations(findCallback, true);
    }

    public static void getAllStations(FindCallback<ParseObject> findCallback, boolean local) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        if(local) {
            query.fromLocalDatastore();
        }
        query.findInBackground(findCallback);
    }

    public static void getStation(final String name, final FindCallback findCallback) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
        query.whereEqualTo(Station.PARSE_NAME, (name));
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null || list == null || list.size() == 0) {
                    //Not found in local storage
                    //Refresh local storage and try again
                    updateLocalStations();
                    ParseQuery<ParseObject> queryRefreshed = ParseQuery.getQuery(Station.PARSE_CLASS_STATION);
                    queryRefreshed.whereEqualTo(Station.PARSE_NAME, (name));
                    queryRefreshed.fromLocalDatastore();
                    queryRefreshed.findInBackground(findCallback);
                } else {
                    query.findInBackground(findCallback);
                }
            }
        });
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
        ParseObject parseObject = list.get(0);
        parseObject.addAllUnique(arrayName, ids);
        parseObject.saveInBackground();
    }

    public static Station getStation(ParseObject parseObject) {
        if(parseObject == null) {
            return null;
        }

        Station station = new Station(new Station.Builder((parseObject.getString(Station.PARSE_NAME))));

        //Inside cellids
        if(parseObject.getList(Station.PARSE_PLAY) != null) {
            station.setCellIdsPlay(parseObject.getList(Station.PARSE_PLAY).toArray(new String[parseObject.getList(Station.PARSE_PLAY).size()]));
        }
        if(parseObject.getList(Station.PARSE_PLUS) != null) {
            station.setCellIdsPlus(parseObject.getList(Station.PARSE_PLUS).toArray(new String[parseObject.getList(Station.PARSE_PLUS).size()]));
        }
        if(parseObject.getList(Station.PARSE_OTHER) != null) {
            station.setCellIdsOther(parseObject.getList(Station.PARSE_OTHER).toArray(new String[parseObject.getList(Station.PARSE_OTHER).size()]));
        }
        if(parseObject.getList(Station.PARSE_TMOBILE) != null) {
            station.setCellIdsTmobile(parseObject.getList(Station.PARSE_TMOBILE).toArray(new String[parseObject.getList(Station.PARSE_TMOBILE).size()]));
        }
        if(parseObject.getList(Station.PARSE_ORANGE) != null) {
            station.setCellIdsOrange(parseObject.getList(Station.PARSE_ORANGE).toArray(new String[parseObject.getList(Station.PARSE_ORANGE).size()]));
        }
        //Outside cellids
        if(parseObject.getList(Station.PARSE_ORANGE_OUTSIDE) != null) {
            station.setCellIdsOutsideOrange(parseObject.getList(Station.PARSE_ORANGE_OUTSIDE).toArray(new String[parseObject.getList(Station.PARSE_ORANGE_OUTSIDE).size()]));
        }
        if(parseObject.getList(Station.PARSE_PLUS_OUTSIDE) != null) {
            station.setCellIdsOutsidePlus(parseObject.getList(Station.PARSE_PLUS_OUTSIDE).toArray(new String[parseObject.getList(Station.PARSE_PLUS_OUTSIDE).size()]));
        }
        if(parseObject.getList(Station.PARSE_OTHER_OUTSIDE) != null) {
            station.setCellIdsOutsideOther(parseObject.getList(Station.PARSE_OTHER_OUTSIDE).toArray(new String[parseObject.getList(Station.PARSE_OTHER_OUTSIDE).size()]));
        }
        if(parseObject.getList(Station.PARSE_TMOBILE_OUTSIDE) != null) {
            station.setCellIdsOutsideTmobile(parseObject.getList(Station.PARSE_TMOBILE_OUTSIDE).toArray(new String[parseObject.getList(Station.PARSE_TMOBILE_OUTSIDE).size()]));
        }
        if(parseObject.getList(Station.PARSE_PLAY_OUTSIDE) != null) {
            station.setCellIdsOutsidePlay(parseObject.getList(Station.PARSE_PLAY_OUTSIDE).toArray(new String[parseObject.getList(Station.PARSE_PLAY_OUTSIDE).size()]));
        }

        station.setSeparatedPlatforms(parseObject.getBoolean(Station.PARSE_PLATFORMS));
        station.setDistanceInMeters(parseObject.getInt(Station.PARSE_DISTANCE_METERS));
        station.setDistanceInSeconds(parseObject.getInt(Station.PARSE_DISTANCE_SECONDS));

        return station;
    }
}
