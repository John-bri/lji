package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class Myfavorites {
    var ContentID: String? = null
    var Title: String? = null
    var Description : String? = null
    var ContentType : String? = null
    var MediaPath : String? = null
    var CoverImagePath : String? = null
    var GroupsCount : String? = null
    var CreatedOn : String? = null
    var CreatedBy : UserRef = UserRef()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}