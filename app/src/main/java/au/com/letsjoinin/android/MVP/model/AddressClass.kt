package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class AddressClass {
    var Street: String? = null
    var City: String? = null
    var State : String? = null
    var Country : String? = null
    var PostalCode : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}