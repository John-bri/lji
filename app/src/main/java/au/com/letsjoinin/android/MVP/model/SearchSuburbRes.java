package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */

public class SearchSuburbRes {
    public ArrayList<SuburbList> ListOfSuburb;
    public ServerMessageData ServerStatus;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
