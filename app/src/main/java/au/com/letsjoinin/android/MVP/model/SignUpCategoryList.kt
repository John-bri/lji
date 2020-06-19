package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class SignUpCategoryList {
    var RoleCode: String? = null
    var Name: String? = null
    var ImagePath: String? = null

    var categoryPosition: Int = 0
    var isClicked: Boolean = false
    var subCategory: Boolean = false

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}