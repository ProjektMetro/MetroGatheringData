package pl.warszawa.gdg.metrodatacollector.data;

import android.content.Context;

/**
 * Class used for storing Parse.com key outside of public repo.
 */
public class ParseInitOutside {
    public static void initParseWithKey(Context context) {
        throw new RuntimeException("There should be a key for Parse service, please contact Michal Tajchert for it.");
    }
}
