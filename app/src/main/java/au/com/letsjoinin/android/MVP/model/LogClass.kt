package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class LogClass {
    var UserID: String? = null
    var AvatarPath: String? = null
    var Name : String? = null
    var Action : String? = null
    var ActionOn : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}