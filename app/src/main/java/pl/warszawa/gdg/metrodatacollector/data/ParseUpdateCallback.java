package pl.warszawa.gdg.metrodatacollector.data;


import com.parse.ParseException;

/**
 * Created by Michal Tajchert on 2015-05-26.
 */
public interface ParseUpdateCallback {
    void success();
    void failure(ParseException parseException);
}
