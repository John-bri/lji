package au.com.letsjoinin.android.UI.fragment

import au.com.letsjoinin.android.MVP.model.UserRef
import com.google.gson.GsonBuilder

class ChannelFollowing {
    var ChannelID: String? = null
    var PKCountry: String? = null
    var Name: String? = null
    var LogoPath: String? = null
    var Description: String? = null
    var MediaPath: String? = null
    var CoverImagePath: String? = null
    var ProgramCount: String? = null
    var CreatedOn: String? = null
    var CreatedBy: UserRef = UserRef()


    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}