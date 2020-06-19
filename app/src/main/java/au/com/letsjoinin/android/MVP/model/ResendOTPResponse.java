package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class ResendOTPResponse {
    public String OTP;
    public ServerMessageData ServerMessage;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
