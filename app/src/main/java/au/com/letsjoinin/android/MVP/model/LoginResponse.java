package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

/**
 */
public class LoginResponse {

    public LoginUserDocData UserDoc;
    public String UserID,OTP;
    public ServerMessageData ServerMessage;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
