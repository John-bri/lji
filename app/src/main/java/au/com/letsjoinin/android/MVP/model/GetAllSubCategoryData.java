package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */
public class GetAllSubCategoryData {

    public ServerMessageData ServerMessage = new ServerMessageData();
    public ArrayList<GetAllSubCategoryDataList> CategoryList = new ArrayList<>();
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
