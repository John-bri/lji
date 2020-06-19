package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */
public class GetCategoryRes {


    public ArrayList<CategoryData> CategoryList;
    public ServerMessageData ServerStatus;


    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
