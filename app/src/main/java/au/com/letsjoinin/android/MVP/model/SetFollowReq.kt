package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class SetFollowReq {
    var ChannelID: String? = null
    var PKUserCountry : String? = null
    var PKChannelCountry : String? = null
    var FollowBy : ProgramListAdminClass = ProgramListAdminClass()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}