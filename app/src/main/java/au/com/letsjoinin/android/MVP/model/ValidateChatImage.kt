package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
class ValidateChatImage {
    var MediaContent: String? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}