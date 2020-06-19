package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class ContentsAdData {
    var id: String? = null
    var Title:String? = null
    var EnterpriseID: String? = null
    var AdMediaType:String? = null
    var AdLocation:String? = null
    var VideoPath:String? = null
    var TimeLineImagePath:String? = null
    var InChatImagePath:String? = null
    var ChatBannerImagePath:String? = null
    var StatusCode:String? = null
    var ModifiedOn:String? = null
    var CreatedOn:String? = null
    var CreatedBy : UserRef? = null
    var ModifiedBy : UserRef? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}