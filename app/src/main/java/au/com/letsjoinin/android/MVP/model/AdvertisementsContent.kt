package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class AdvertisementsContent {
    var AdvertisementID: String? = null
    var CreatedOn: String? = null
    var ModifiedOn : String? = null
    var CreatedBy : UserRef = UserRef()
    var ModifiedBy : UserRef = UserRef()
    var AdButlerInfo : AdButlerData = AdButlerData()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}