package au.com.letsjoinin.android.MVP.model

import com.google.firebase.database.PropertyName
import com.google.gson.GsonBuilder
import java.util.ArrayList

class PostedByClass {
    @PropertyName("UserID")
    var UserID: String? = ""

    @PropertyName("MyRating")
    var MyRating: Int? = 0

    @PropertyName("AvatharPath")
    var AvatharPath : String? = ""

    @PropertyName("Name")
    var Name : String? = ""


    
}