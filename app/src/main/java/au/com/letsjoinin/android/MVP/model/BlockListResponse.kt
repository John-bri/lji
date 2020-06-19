package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder
import java.util.ArrayList

class BlockListResponse {
    val ContentData : ProgramListData = ProgramListData()
    var Blocks = ArrayList<ArrayList<BlocksClass>>()

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
    
}