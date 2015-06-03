package pl.warszawa.gdg.metrodatacollector.data.busEvents;

import com.parse.ParseObject;

import java.util.List;

/**
 * Send on success of refreshing data on each parse refresh - so we can update UI if there are new stations for example
 */
public class SuccessParse {
    public List<ParseObject> list;

    public SuccessParse(List<ParseObject> list) {
        this.list = list;
    }
}
