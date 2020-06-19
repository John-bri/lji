package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class GetContentGroupsReq {
    public String ContentID,PKChannelID ;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
