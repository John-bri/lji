package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

public class ChannelRefData {
    public String ChannelID,Name,LogoPath,PKCountry,CreatedOn;
    public ProgramListAdminClass CreatedBy;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
