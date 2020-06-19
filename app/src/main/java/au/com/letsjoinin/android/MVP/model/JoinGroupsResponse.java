package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class JoinGroupsResponse {
    public String JoinMode,GroupID ;
    public ServerMessageData ServerMessage = new ServerMessageData();
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
