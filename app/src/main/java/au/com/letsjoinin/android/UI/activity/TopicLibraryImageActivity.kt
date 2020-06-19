package au.com.letsjoinin.android.UI.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.GetImagesForTopicRes
import au.com.letsjoinin.android.MVP.model.ResendOTPReq
import au.com.letsjoinin.android.MVP.model.ResendOTPResponse
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_topic_library_image.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TopicLibraryImageActivity : AppCompatActivity() {
    private var mPrefMgr: PreferenceManager? = PreferenceManager.getInstance();
    private var data: Long = 0L
    internal var left: LinearLayout? = null
    internal var Right:LinearLayout? = null
    internal var txt_no_content:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_library_image)
        left = findViewById(R.id.channel_left) as LinearLayout
        Right = findViewById(R.id.channel_Right) as LinearLayout
        txt_no_content = findViewById(R.id.txt_no_content) as TextView
       val back = findViewById(R.id.back) as ImageView
        data = intent.getLongExtra("CategorySKImage",0L)
        data = 6L
        back.setOnClickListener(View.OnClickListener {
            mPrefMgr!!.setBoolean(AppConstant.TopicImageSelected, false)
            finish()
        })

        GetImagesForTopic()


    }
    private fun GetImagesForTopic() {
        try {
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.GetImagesForTopic("ACTV")
            call.enqueue(object : Callback<GetImagesForTopicRes> {
                override fun onResponse(call: Call<GetImagesForTopicRes>, response: Response<GetImagesForTopicRes>) {
                    val res = response.body()
                    if (res.ServerMessage.Status.equals(AppConstant.SUCCESS,true)) {
                        val channelsRes = res
                        if (channelsRes != null) {
                            val ListOfChannels = channelsRes!!.ImageLibraryList


                            if (ListOfChannels != null && ListOfChannels!!.size > 0) {

                                txt_no_content!!.visibility = View.GONE

                                for (i in ListOfChannels!!.indices) {
                                    val text_chnl_name: TextView
                                    val type_image: ImageView
                                    val child = layoutInflater.inflate(R.layout.topic_image_item, null)
                                    val channels = ListOfChannels!!.get(i)
                                    text_chnl_name = child.findViewById(R.id.txt_name)
                                    type_image = child.findViewById(R.id.img_topic)

                                    child.setOnClickListener(View.OnClickListener {
//                                        mPrefMgr.setLong(AppConstant.TopicImage_CategorySK, channels.CategorySK)
//                                        mPrefMgr.setLong(AppConstant.TopicImageSK, channels.TopicImageSK)
                                        mPrefMgr.setBoolean(AppConstant.TopicImageSelected, true)
                                        mPrefMgr.setString(AppConstant.TopicImageURL, channels.MediaPath)

                                        finish()
                                    })


                                    text_chnl_name.setText(channels.Title)
                                    if (!TextUtils.isEmpty(channels.MediaPath)) {
                                        Picasso.with(this@TopicLibraryImageActivity).load(channels.MediaPath)
                                            .into(type_image)
                                    }

                                    if (i == 0) {
                                        left!!.addView(child)
                                    } else if (i % 2 == 0) {
                                        left!!.addView(child)
                                    } else {
                                        Right!!.addView(child)
                                    }
                                }
                            }else{
                                txt_no_content!!.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetImagesForTopicRes>, t: Throwable) {
                    txt_no_content!!.visibility = View.VISIBLE
                }
            })
        } catch (e: Exception) {
            txt_no_content!!.visibility = View.VISIBLE
        }
    }
}
