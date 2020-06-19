package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class SetFavReqClass {
    var ContentID: String? = null
    var PKContentType: String? = null
    var FavouriteBy : FavUserRef? = null
    var PKUserCountry : String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}