package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ProgramListData {
    var _id: String? = null
    var DocType : String? = null
    var ContentType: String? = null
    var Title: String? = null
    var Description: String? = null
    var MediaPath: String? = null
    var CoverImagePath: String? = null
    var FromDateTime: String? = null
    var ToDateTime: String? = null
    var Frequency: String? = null
    var Status: String? = null
    var CreditPoints: String? = null
    val Categories = ArrayList<String>()
    val Host = ArrayList<ProgramListAdminClass>()
    var ModifiedBy: String? = null
    var ModifiedOn: String? = null
    var CreatedOn: String? = null
    var Channel: ProgramListChannelClass? = ProgramListChannelClass()
    var Admins: ProgramListAdminClass? = ProgramListAdminClass()
    var Celebrity: ProgramListAdminClass? = ProgramListAdminClass()
    var CreatedBy: ProgramListAdminClass? = ProgramListAdminClass()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}