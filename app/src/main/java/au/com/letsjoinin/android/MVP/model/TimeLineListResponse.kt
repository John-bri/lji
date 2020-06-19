package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class TimeLineListResponse {
    val TimelineContent = ArrayList<ProgramListData>()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}