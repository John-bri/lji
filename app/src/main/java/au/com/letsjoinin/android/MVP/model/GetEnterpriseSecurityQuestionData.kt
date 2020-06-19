package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class GetEnterpriseSecurityQuestionData {
    var ServerMessage : ServerMessageData? = ServerMessageData()
    var  EnterpriseSecQ:  EnterpriseSecQData? = EnterpriseSecQData()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}