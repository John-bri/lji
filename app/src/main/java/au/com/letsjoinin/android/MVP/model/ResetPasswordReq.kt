package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ResetPasswordReq {
    var UserID: String? = null
    var OTP : String? = null
    var UserCountry: String? = null
    var NewPassword: String? = null
    var  DeviceDetails: DeviceData? =null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}