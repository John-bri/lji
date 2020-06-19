package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
class DeviceData {
    var DeviceType: String? = null
    var DeviceID: String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}