package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class EnterpriseSecQData {


    var  RegSecurityQuestion:  RegSecurityQuestionData? = RegSecurityQuestionData()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}