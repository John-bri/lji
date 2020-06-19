package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class BlocksClass {
    var _id: String? = null
    var DocType: String? = null
    var Name : String? = null
    var ParticipantCount : String? = null
    var Color : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}