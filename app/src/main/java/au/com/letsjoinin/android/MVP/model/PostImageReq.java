package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class PostImageReq {
    public String MediaContent;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
