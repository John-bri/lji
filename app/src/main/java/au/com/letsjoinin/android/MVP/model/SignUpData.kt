package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class SignUpData {
    var email: String? = null
    var TimeZone : String? = null
    var gender: String? = null
    var Country: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var FirstName: String? = null
    var LastName: String? = null
    var DOB: String? = null
    var Mobile: String? = null
    var SuburbCode: String? = null
    var SuburbName: String? = null
    var avatar: String? = null
    val Categories = ArrayList<GetAllSubCategoryDataList>()
    var categoryPosition: Int = 0
    var isClicked: Boolean = false
    var subCategory: Boolean = false

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}