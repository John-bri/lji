package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */

public class GetImagesForTopicRes {

    public ArrayList<TopicImages> ImageLibraryList;
    public ServerMessageData ServerMessage;

    public  class  TopicImages {
        public String  MediaPath,Title,Category,StatusCode,CreatedOn,ModifiedOn;
        public ProgramListAdminClass CreatedBy;
    }
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
