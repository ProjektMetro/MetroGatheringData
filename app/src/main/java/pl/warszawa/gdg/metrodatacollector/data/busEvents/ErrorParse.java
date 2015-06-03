package pl.warszawa.gdg.metrodatacollector.data.busEvents;


import com.parse.ParseException;

/**
 * Send if on refresh of Parse there is an error - used for handling for example as a Toast
 */
public class ErrorParse {
    public ParseException parseException;

    public ErrorParse(ParseException parseException) {
        this.parseException = parseException;
    }
}
