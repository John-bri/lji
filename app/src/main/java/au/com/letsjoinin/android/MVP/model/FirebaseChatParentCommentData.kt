package au.com.letsjoinin.android.MVP.model

import com.google.firebase.database.PropertyName
import com.google.gson.GsonBuilder
import java.util.ArrayList

class FirebaseChatParentCommentData {
    @PropertyName("AvgRating")
    var AvgRating: Any? = 0

    @PropertyName("CommentText")
    var CommentText : String? = ""

    @PropertyName("CommentType")
    var CommentType: String? = ""

    @PropertyName("FireBaseID")
    var FireBaseID: String? = ""

    @PropertyName("NoteWorthyClicks")
    var NoteWorthyClicks: Any? = 0

    @PropertyName("OffenseCount")
    var OffenseCount: Long? = 0

    @PropertyName("ParentCommentID")
    var ParentCommentID: String? = ""

    @PropertyName("PostedOn")
    var PostedOn: String? = ""

    @PropertyName("Rating")
    var Ratings =""

    @PropertyName("PostedBy")
    var PostedBy: PostedByClass? = PostedByClass()



}