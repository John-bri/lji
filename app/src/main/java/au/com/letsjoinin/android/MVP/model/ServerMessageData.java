package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 * Created by ZGDev1 on 02/02/18.
 */
public class ServerMessageData {

    public String Status,ExMsg,DisplayMsg;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
