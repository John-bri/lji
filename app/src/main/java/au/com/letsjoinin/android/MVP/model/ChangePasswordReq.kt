package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ChangePasswordReq {
    var UserID: String? = null
    var OldPassword : String? = null
    var UserCountry: String? = null
    var NewPassword: String? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}