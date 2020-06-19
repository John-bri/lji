package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class GetContentGroupValue {
    public String Name ;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
