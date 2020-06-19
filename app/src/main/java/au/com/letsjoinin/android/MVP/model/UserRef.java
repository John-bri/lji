package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class UserRef {
//    PKCountry
    public String UserID,Name,AvatarPath;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
