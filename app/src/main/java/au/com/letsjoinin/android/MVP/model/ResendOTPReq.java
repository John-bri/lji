package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class ResendOTPReq {
    public String UserID,UserCountry;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
