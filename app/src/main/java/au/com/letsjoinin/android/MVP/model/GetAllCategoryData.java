package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */
public class GetAllCategoryData {

    public ServerMessageData ServerMessage = new ServerMessageData();
    public ArrayList<GetAllCategoryDataList> CategoryList = new ArrayList<>();
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
