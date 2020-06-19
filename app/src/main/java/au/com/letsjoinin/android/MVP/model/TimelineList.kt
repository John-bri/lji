package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class TimelineList {
    var RoleCode: String? = null
    var jsonStr: String? = null
    var rID: Int? =0

    var OffensiveMarkedBySK: Long = 0
    var isClicked: Boolean = false
    var subCategory: Boolean = false

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}