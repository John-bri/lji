package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ValidateEnterprise {
    var ServerMessage : ServerMessageData? = ServerMessageData()
    var EnterpriseInfo : EnterpriseInfoClass? = EnterpriseInfoClass()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}