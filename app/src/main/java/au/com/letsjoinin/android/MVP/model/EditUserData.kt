package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class EditUserData {
    var LastName: String? = null
    var FirstName:String? = null
    var UserID: String? = null
    var PhoneNo:String? = null
    var GenderCode:String? = null
    var YearOfBirth:String? = null
    var Country:String? = null
    var TimeZone:String? = null
    var AvatarPath:String? = null
    var GDPR:String? = null
    var Suburb = SignUpSuburb()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}