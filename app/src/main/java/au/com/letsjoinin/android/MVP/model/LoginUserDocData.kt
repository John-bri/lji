package au.com.letsjoinin.android.MVP.model

import au.com.letsjoinin.android.UI.fragment.ChannelFollowing
import com.google.gson.GsonBuilder
import java.util.ArrayList

class LoginUserDocData {
    var UserType: String? = null
    var LJIID : String? = null
    var id : String? = null
    var EmailID : String? = null
    var TimeZone : String? = null
    var GenderCode: String? = null
    var Country: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var FirstName: String? = null
    var LastName: String? = null
    var YearOfBirth: String? = null
    var PhoneNo: String? = null
    var AvatarPath: String? = null
    var FacebookID: String? = null
    var GPlusID: String? = null
    var OTP: String? = null
    var OTPGeneratedOn: String? = null
    var StatusCode: String? = null
    var GDPR: String? = null
    var UserPoints: String? = null
    var LastActiveOn: String? = null
    var CreatedOn: String? = null
    var ModifiedOn: String? = null
    var ModifiedBy: Any? = null
    var DeviceDetails: DeviceData? = null
    var Categories = ArrayList<String>()
    //var ChannelsFollowing = ArrayList<String>()
    var Suburb = SignUpSuburb()
    var EnterpriseInfo = EnterpriseRefClass()
    //    var MyContents:ContentsClass? = ContentsClass()
    var MyFavourites =HashMap<String,Myfavorites> ()
    var MyContents =HashMap<String,ContentsClass> ()
    var ChannelsFollowing =HashMap<String,ChannelFollowing> ()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}