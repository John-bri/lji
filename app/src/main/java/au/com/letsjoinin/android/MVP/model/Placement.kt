package au.com.letsjoinin.android.MVP.model

import com.google.gson.GsonBuilder

class Placement {
     val banner_id: Int = 0
     val redirect_url: String? = null
     val image_url: String? = null
     val width: Int = 0
     val height: Int = 0
     val alt_text: String? = null
     val target: String? = null
     val tracking_pixel: String? = null
     val accompanied_html: String? = null
     val refresh_url: String? = null
     val refresh_time: String? = null
     val body: String? = null

    override fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}
