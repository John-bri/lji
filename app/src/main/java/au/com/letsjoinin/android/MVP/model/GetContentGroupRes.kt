package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class GetContentGroupRes {
    var ContentGroups: ArrayList<GetContentGroupList>? = ArrayList()
    var ServerMessage : ServerMessageData? = ServerMessageData()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}