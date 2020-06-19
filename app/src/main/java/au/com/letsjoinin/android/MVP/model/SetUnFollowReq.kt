package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class SetUnFollowReq {
    var ChannelID: String? = null
    var PKUserCountry : String? = null
    var PKChannelCountry : String? = null
    var UserID : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}