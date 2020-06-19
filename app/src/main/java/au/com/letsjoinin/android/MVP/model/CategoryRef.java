package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class CategoryRef {
    public String Name,CategoryID;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
