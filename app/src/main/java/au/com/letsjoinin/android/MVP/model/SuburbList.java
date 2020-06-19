package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 */

public class SuburbList {
    public long SuburbSK;
    public String PostalCode,Locality,PostalCodeLocality;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
