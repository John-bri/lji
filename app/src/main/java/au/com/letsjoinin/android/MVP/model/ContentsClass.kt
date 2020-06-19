package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ContentsClass {
    var DocType: String? = null
    var ContentID: String? = null
    var ContentType : String? = null
    var Title : String? = null
    var MediaPath : String? = null
    var CoverImagePath : String? = null
    var Description : String? = null
    var GroupsCount : String? = null
    var CreatedOn : String? = null
    var CreatedBy : UserRef = UserRef()


    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}