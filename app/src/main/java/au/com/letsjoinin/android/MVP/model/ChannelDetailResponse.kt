package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ChannelDetailResponse {
    var id: String? = null
    var Name : String? = null
    var Description: String? = null
    var EmailID: String? = null
    var LogoPath: String? = null
    var CreditPoints: String? = null
    var StatusCode: String? = null
    var CreatedOn: String? = null
    var ModifiedOn: String? = null
    var FollowingBy = HashMap<String,ProgramListAdminClass> ()

    var Contents =HashMap<String,ContentsClass> ()
    val Categories = ArrayList<CategoryRef>()
    var Admins= ArrayList<ProgramListAdminClass>()
    var CreatedBy: ProgramListAdminClass? = ProgramListAdminClass()
    var EnterpriseInfo: EnterpriseRefClass? = EnterpriseRefClass()
    var Address: AddressClass? = AddressClass()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}