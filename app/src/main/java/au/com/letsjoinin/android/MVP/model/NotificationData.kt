package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class NotificationData {
    var id: String? = null
    var Title: String? = null
    var ContentType : String? = null
    var MediaPath : String? = null
    var Log = ArrayList<LogClass>()


    var Categories = ArrayList<CategoryRef>()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}