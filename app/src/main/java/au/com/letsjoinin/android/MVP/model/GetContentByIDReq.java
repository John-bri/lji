package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class GetContentByIDReq {
    public String ContentID,PKContentType;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
