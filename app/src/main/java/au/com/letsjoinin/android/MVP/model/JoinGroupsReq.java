package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class JoinGroupsReq {
    public String ContentID,PKChannelID,GroupID ;
    public JoiningUserDataReq JoiningUserData = new JoiningUserDataReq();
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
