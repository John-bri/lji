package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class LoginData {
    var LoginType: String? = null
    var LoginID: String? = null
    var Password: String? = null
    var DeviceDetails: DeviceData? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}