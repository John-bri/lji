package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList
import au.com.letsjoinin.android.MVP.model.ProgramListAdminClass



class AdvertisementsClass {
    var mAdMap: Map<String, AdvertisementsContent>? = null
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}