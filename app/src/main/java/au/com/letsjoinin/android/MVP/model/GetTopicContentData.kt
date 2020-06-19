package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class GetTopicContentData {
    var ContentID: String? = null
    var Title:String? = null
    var Description: String? = null
    var ContentType:String? = null
    var GroupSize:String? = null
    var GroupType:String? = null
    var MediaPath:String? = null
    var CoverImagePath:String? = null
    var StatusCode:String? = null
    var PKUserCountry:String? = null
    var CreatedOn:String? = null
    var Source:String? = null
    var ModifiedOn:String? = null
    var CreatedBy : UserRef = UserRef()
    var ModifiedBy : UserRef = UserRef()
    var Advertisements : HashMap<String, AdvertisementsContent>? = null
    val Categories = ArrayList<CategoryRef>()
    var ChannelInfo: ChannelRefData? = ChannelRefData()
    var FavouritesBy =HashMap<String,ProgramListAdminClass> ()
    var Log = ArrayList<LogClass>()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}