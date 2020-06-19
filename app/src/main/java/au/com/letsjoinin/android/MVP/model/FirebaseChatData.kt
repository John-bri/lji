package au.com.letsjoinin.android.MVP.model

import com.google.firebase.database.PropertyName
import com.google.gson.GsonBuilder
import java.util.ArrayList

open class FirebaseChatData (){
    @PropertyName("AvgRating")
    var AvgRating:  Any? = 0





    var OffensiveClicked:  Any? = false

    var replyViewSelected:  Boolean? = false

    @PropertyName("CommentText")
    var CommentText : String? = null

    @PropertyName("CommentType")
    var CommentType: String? = null

    @PropertyName("FireBaseID")
    var FireBaseID: String? = null

    @PropertyName("NoteWorthyClicks")
    var NoteWorthyClicks:  Any? = 0

    @PropertyName("OffenseCount")
    var OffenseCount:  Any? = 0


    var RatingCount:  Any? = 0

    @PropertyName("ParentCommentID")
    var ParentCommentID: String? = null

    @PropertyName("PostedOn")
    var PostedOn: String? = null

    @PropertyName("Rating")
    var Rating = ArrayList<RatingJavaData>()

    @PropertyName("PostedBy")
    var PostedBy: PostedByClass? = PostedByClass()

    @PropertyName("ParentComment")
    var ParentComment: FirebaseChatParentCommentData? = FirebaseChatParentCommentData()



}