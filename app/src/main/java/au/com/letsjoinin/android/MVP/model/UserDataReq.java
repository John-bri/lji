package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 */

public class UserDataReq implements Serializable {
    public ArrayList<CatClass> Categories = new ArrayList<>();
    public SignUpSuburb Suburb = new SignUpSuburb();
    public DeviceData DeviceDetails = new DeviceData();
    public EnterpriseInfoClass EnterpriseInfo = new EnterpriseInfoClass();
    public String  LastName, FirstName,
            EmailID, Password, PhoneNo, GenderCode, YearOfBirth, Country,TimeZone, AvatarPath, GDPR,
            FacebookID, GPlusID,   CreatedOn;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }


}
