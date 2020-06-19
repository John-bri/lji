package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class GetContentGroupList {
    var Value: GetContentGroupValue? = GetContentGroupValue()
    var Key : String? = null
    var Color : String? = "GREEN"

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}