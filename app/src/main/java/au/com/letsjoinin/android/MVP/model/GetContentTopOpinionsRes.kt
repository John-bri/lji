package au.com.letsjoinin.android.MVP.model

import au.com.letsjoinin.android.UI.fragment.ChannelFollowing
import com.google.gson.GsonBuilder
import java.util.ArrayList

class GetContentTopOpinionsRes {
    var ServerMessage:ServerMessageData? = ServerMessageData()
    var TopOpinions =HashMap<String, TopOpinionsData> ()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}