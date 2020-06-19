package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 * Created by ZGDev1 on 02/02/18.
 */
public class SignUpServerMessage {

    public String UserID,OTP;
    public ServerMessageData ServerMessage;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
