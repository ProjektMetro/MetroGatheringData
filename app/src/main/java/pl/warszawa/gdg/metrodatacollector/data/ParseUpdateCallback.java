package pl.warszawa.gdg.metrodatacollector.data;


import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Michal Tajchert on 2015-05-26.
 */
public interface ParseUpdateCallback {
    void success(List<ParseObject> list);
    void failure(ParseException parseException);
}
