package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 */
public class GetAllSubCategoryDataList {

    public String CategoryID;
    public String CategoryCode;
    public String Name;
    public String SubCategoryCode;
    public String ImagePath;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
