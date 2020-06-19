package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class RegSecurityQuestionData {
    var Question: String? = null
    var DataType: String? = null
    var Enabled: String? = null
    var Length: String? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}