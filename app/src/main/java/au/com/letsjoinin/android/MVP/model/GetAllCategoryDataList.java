package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 */
public class GetAllCategoryDataList {

    public String id;
    public String CategoryCode;
    public String Name;
    public String ImagePath;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
