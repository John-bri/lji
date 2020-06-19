package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class FavUserRef {
    public String UserID,Name,AvatarPath,SetFavouriteOn;

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
