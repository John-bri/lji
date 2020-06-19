package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class NotificationRes {
    var ServerMessage: ServerMessageData = ServerMessageData()
    var UserNotificationData = ArrayList<NotificationData>()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}