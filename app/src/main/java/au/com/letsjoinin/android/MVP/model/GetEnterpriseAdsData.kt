package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class GetEnterpriseAdsData {
    var ServerMessage : ServerMessageData? = ServerMessageData()
    var ContentAds : ContentsAdData? = ContentsAdData()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}