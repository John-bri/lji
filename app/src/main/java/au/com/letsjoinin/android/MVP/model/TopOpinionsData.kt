package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class TopOpinionsData {
    var GroupID: String? = null
    var GroupName: String? = null
    var ContentID : String? = null
    var ContentName : String? = null
    var ChatID : String? = null
    var CommentText : String? = null
    var CommentType : String? = null
    var CreatedOn : String? = null
    var PostedOn : String? = null


    var CreatedBy : UserRef = UserRef()
    var PostedBy : UserRef = UserRef()
    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

}