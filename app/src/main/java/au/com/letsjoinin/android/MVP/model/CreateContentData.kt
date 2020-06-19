package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.*

class CreateContentData {
    var id: String? = null
    var Title:String? = null
    var Description: String? = null
    var ContentType:String? = null
    var GroupSize:String? = null
    var GroupType:String? = null
    var MediaPath:String? = null
    var PKUserCountry:String? = null
    var Source:String? = null
    var CreatedBy : UserRef? = null
    var ModifiedBy : UserRef? = null
    var EnterpriseInfo = EnterpriseInfoClass()
    val Categories = ArrayList<CategoryRef>()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}