package au.com.letsjoinin.android;

import com.google.gson.GsonBuilder;


public class GroupCommentsFireBase {

    public String RoleCode,FireBaseID,CommentType,CommentText,PostedOn,LJIID,UserName,AvatarPath;

    public long OffensiveMarkedBySK,OffensiveCount,HostSK,OffenseCount,CommentSK,UserSK,ParentCommentSK,MyRating,RatingGivenBy,NoteWorthyClicks,AvgRating,RatingSK;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
