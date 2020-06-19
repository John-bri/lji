package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class EnterpriseInfoClass {
    var EnterpriseID: String? = null
    var Name : String? = null
    var LogoPath : String? = null
    var PKCountry : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}