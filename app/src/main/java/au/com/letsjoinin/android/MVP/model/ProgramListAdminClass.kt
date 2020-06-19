package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ProgramListAdminClass {
    var UserID: String? = null
    var AvatarPath: String? = null
    var Name : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}