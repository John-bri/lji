package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class SearchUserData {
    var ServerMessage: ServerMessageData = ServerMessageData()
    var ContentDataList = ArrayList<SearchUserDocData>()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}