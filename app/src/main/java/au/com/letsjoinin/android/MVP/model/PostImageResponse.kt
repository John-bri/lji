package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class PostImageResponse {
    var MediaPath:  String? = null
    var ServerMessage:ServerMessageData? = ServerMessageData()


    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}