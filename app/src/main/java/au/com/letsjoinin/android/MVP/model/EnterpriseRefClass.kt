package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class EnterpriseRefClass {
    var EnterpriseID: String? = null
    var Name : String? = null
    var CreatedOn : String? = null
    var LogoPath : String? = null
    var PKCountry : String? = null
    var CreatedBy : ProgramListAdminClass? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}