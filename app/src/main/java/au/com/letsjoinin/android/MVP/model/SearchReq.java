package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class SearchReq {
    public String ContentType,SearchText,StatusCode;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
