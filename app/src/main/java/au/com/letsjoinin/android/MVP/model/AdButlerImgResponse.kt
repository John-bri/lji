package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList
import java.util.HashMap

class AdButlerImgResponse {
    var status: String? = null
    var placements: HashMap<String, Placement>? = null



    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}