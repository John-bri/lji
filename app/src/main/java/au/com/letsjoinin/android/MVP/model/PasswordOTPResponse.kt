package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class PasswordOTPResponse {
    var UserID: String? = null
    var OTP : String? = null
    var UserCountry: String? = null
    var ServerMessage: ServerMessageData? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}