package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class SearchUserDocData {
    var id: String? = null
    var Title: String? = null
    var Description : String? = null
    var ContentType : String? = null
    var OccuranceType : String? = null
    var StartDateTime : String? = null
    var EndDateTime : String? = null
    var Frequency : String? = null
    var MediaPath : String? = null
    var CoverImagePath : String? = null
    var GroupType : String? = null
    var GroupSize : String? = null
    var AdInfo : String? = null
    var FavouritesBy =HashMap<String,ProgramListAdminClass> ()
    var StatusCode : String? = null
    var isFlipped : Boolean? = false
    var isFavourite : Boolean? = false
    var CreditPoints : String? = null
    var PKUserCountry : String? = null
    var CreatedBy: ProgramListAdminClass? = ProgramListAdminClass()
    var Admins = ArrayList<ProgramListAdminClass>()
    var Host: ProgramListAdminClass? = ProgramListAdminClass()
    var ChannelInfo: ChannelRefData? = ChannelRefData()
    var Celebrity: ProgramListAdminClass? = ProgramListAdminClass()
    var ModifiedBy: ProgramListAdminClass? = ProgramListAdminClass()
    var RepeatDays = ArrayList<String>()


    var Categories = ArrayList<CategoryRef>()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}