package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class RatingJavaData {
    public String UserID,Name,AvatharPath;
    public Long Rating;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
