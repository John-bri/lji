package au.com.letsjoinin.android.UI.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_top_options.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TopOpinionsActivity : AppCompatActivity() {
    var top_opinion_shimmer_view_container: ShimmerFrameLayout? = null;
    var firebaseChatData: ArrayList<FirebaseChatDataWithGroupName>? = ArrayList()
    var activity_root_view: LinearLayout? = null;
    var lay_group_name: LinearLayout? = null;
    var TopOpinions = ArrayList<TopOpinionsData>()
    var TopOpinionsList = ArrayList<TopOpinionsData>()
    var CategoryList: ArrayList<GetContentGroupList> = ArrayList()
    private var FirebaseId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_options)
        try {
            top_opinion_shimmer_view_container = findViewById(R.id.top_opinion_shimmer_view_container)
            activity_root_view = findViewById(R.id.activity_root_view)
            lay_group_name = findViewById(R.id.lay_group_name)
            top_opinion_list_view.layoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL, false
            )
            top_opinion_img_arrow.setOnClickListener(View.OnClickListener { finish() })
//            var input: GetContentTopOpinionsReq = GetContentTopOpinionsReq()
//            input.ContentID = "c1380af9-bb41-466e-ac5c-844dd9907b3d"
//            input.PKContentType = "TOPIC"
//            GetContentTopOpinions(input)

            val manager =
                PreferenceManager.getInstance()
            val gson = Gson()
            val json = manager.getString("ContentGroupsList", null)
            FirebaseId = manager.getString("FirebaseIdChat", null)
            CategoryList =
                gson.fromJson<java.util.ArrayList<GetContentGroupList>>(
                    json,
                    object :
                        TypeToken<List<GetContentGroupList?>?>() {}.type
                )

            for (i in 0..CategoryList.size - 1) {
                val data : GetContentGroupList = CategoryList.get(i)
                firebase(data.Value!!.Name)
            }
            val handler = Handler()
            handler.postDelayed({
                if (top_opinion_list_view != null) {
                    if (top_opinion_shimmer_view_container != null) {
                        top_opinion_shimmer_view_container!!.stopShimmerAnimation()
                        top_opinion_shimmer_view_container!!.visibility = View.GONE
                        top_opinion_list_view!!.visibility = View.VISIBLE
                    }
                    top_opinion_list_view.adapter =
                        au.com.letsjoinin.android.UI.adapter.TopOpinionAdapter(firebaseChatData, applicationContext)
                }
            }, 6000)



        } catch (e: Exception) {
            if (activity_root_view != null) {
                CommonMethods.SnackBar(activity_root_view, "Something went wrong", false)
            }
        }
    }

    private fun firebase(GroupName: String)
    {
        var fbDb: DatabaseReference? = null
        if (fbDb == null) {
            fbDb = FirebaseDatabase.getInstance().reference.child(FirebaseId!!).child(GroupName)
        }


        fbDb
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { // get total available quest
                    var size = dataSnapshot.childrenCount.toInt()
                    val data: TopOpinionsData = TopOpinionsData()

                    if(size != 0)
                    {
                        if (size>2)
                        {
                            size = size - 2                        }
                    }
                    data.ChatID = size.toString();
                    data.GroupName = GroupName;
                    TopOpinionsList.add(data)

                    val valueTV =
                        TextView(this@TopOpinionsActivity)
                    valueTV.setText(GroupName + "-" + size)
                    valueTV.gravity = View.TEXT_ALIGNMENT_CENTER
                    valueTV.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    val scale = resources.displayMetrics.density
                    val dpAsPixels = (10 * scale + 0.5f).toInt()
                    val sdk = Build.VERSION.SDK_INT
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        valueTV.setBackgroundDrawable(resources.getDrawable(R.drawable.round_corner))
                    } else {
                        valueTV.setBackground(resources.getDrawable(R.drawable.round_corner))
                    }
                    valueTV.setPadding(dpAsPixels, 0, dpAsPixels, 0)
                    lay_group_name!!.addView(valueTV)
                    var mPrefMgr = PreferenceManager.getInstance();
                    for (ds in dataSnapshot.children) {
                    if (ds.exists()) {

                        if (!TextUtils.isEmpty(ds.key) && !ds.key.equals("ContentID") && !ds.key.equals(
                                "GroupName"
                            )
                        ) {
                            val message: FirebaseChatDataWithGroupName =
                                FirebaseChatDataWithGroupName()
                            val UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                            val CommentText = ds.child("CommentText").value as String?
                            val PostedOn = ds.child("PostedOn").value as String?
                            var FireBaseID = ds.child("FireBaseID").value as String?
                            if (FireBaseID != null) {
                                val ParentCommentID = ds.child("ParentCommentID").value as String?
                                val CommentType = ds.child("CommentType").value as String?
                                val AvgRating = ds.child("RatingSum").value as Any?
                                var NoteWorthyClicks: Any = 0
                                if (ds.child("NoteWorthyClicks").value != null) {
                                    NoteWorthyClicks = ds.child("NoteWorthyClicks").value as Any
                                }
                                var OffenseCount: Any = 0
                                if (ds.child("OffenseCount").value != null) {
                                    OffenseCount = ds.child("OffenseCount").value as Any
                                }
                                val PostedBy_UserID = ds.child("/PostedBy/UserID").value as String?
                                val PostedBy_Name = ds.child("/PostedBy/Name").value as String?
                                val PostedBy_AvatharPath =
                                    ds.child("/PostedBy/AvatarPath").value as String?


                                val ParentComment_CommentText =
                                    ds.child("/ParentComment/CommentText").value as String?
                                val ParentComment_PostedOn =
                                    ds.child("/ParentComment/PostedOn").value as String?
                                var ParentComment_FireBaseID =
                                    ds.child("/ParentComment/FireBaseID").value as String?
                                if (TextUtils.isEmpty(FireBaseID)) {
                                    ParentComment_FireBaseID =
                                        ds.child("/ParentComment/FirebaseID").value as String?
                                }
                                val ParentComment_ParentCommentID =
                                    ds.child("/ParentComment/ParentCommentID").value as String?
                                val ParentComment_CommentType =
                                    ds.child("/ParentComment/CommentType").value as String?
                                val ParentComment_AvgRating =
                                    ds.child("/ParentComment/RatingSum").value as Any?
                                var ParentComment_NoteWorthyClicks: Any = 0
                                if (ds.child("/ParentComment/NoteWorthyClicks").value != null) {
//                                    ParentComment_NoteWorthyClicks = ds.child("/ParentComment/NoteWorthyClicks").value as Long
                                }
                                var ParentComment_OffenseCount: Long = 0
                                if (ds.child("/ParentComment/OffenseCount").value != null && ds.child(
                                        "/ParentComment/OffenseCount"
                                    ).value is Long
                                ) {
                                    ParentComment_OffenseCount =
                                        ds.child("/ParentComment/OffenseCount").value as Long
                                }
                                val ParentComment_PostedBy_UserID =
                                    ds.child("/ParentComment/PostedBy/UserID").value as String?
                                val ParentComment_PostedBy_Name =
                                    ds.child("/ParentComment/PostedBy/Name").value as String?
                                val ParentComment_PostedBy_AvatharPath =
                                    ds.child("/ParentComment/PostedBy/AvatarPath").value as String?

                                message.RatingCount = 0L

                                if (ds.child("RatingBy").value != null) {
                                    var Rating = ds.child("RatingBy").value as HashMap<String, Any>
                                    if (Rating != null) {
                                        val ratingDataList =
                                            Rating.get(UserID)
                                        if (ratingDataList != null) {
                                            ratingDataList as HashMap<String, Any>
                                            message.RatingCount = ratingDataList.get("Rating")
                                        }

                                    }
                                }

                                if (ds.child("OffenseMarkedBy").value != null) {
                                    var Rating =
                                        ds.child("OffenseMarkedBy").value as HashMap<String, Any>
                                    if (Rating != null) {
                                        val ratingDataList =
                                            Rating.get(UserID)
                                        if (ratingDataList != null) {
                                            ratingDataList as HashMap<String, Any>
                                            message.OffensiveClicked = true

                                        } else {
                                            message.OffensiveClicked = false
                                        }
                                    }
                                }

                                var PostedBy: PostedByClass? = PostedByClass()
                                PostedBy!!.Name = PostedBy_Name
                                PostedBy!!.AvatharPath = PostedBy_AvatharPath
                                PostedBy!!.UserID = PostedBy_UserID


                                var ParentComment_PostedBy: PostedByClass? = PostedByClass()
                                ParentComment_PostedBy!!.Name = ParentComment_PostedBy_Name
                                ParentComment_PostedBy!!.AvatharPath =
                                    ParentComment_PostedBy_AvatharPath
                                ParentComment_PostedBy!!.UserID = ParentComment_PostedBy_UserID
//                                PostedBy!!.MyRating = PostedBy_Name
                                var ParentComment: FirebaseChatParentCommentData? =
                                    FirebaseChatParentCommentData()

                                ParentComment!!.CommentText = ParentComment_CommentText
                                ParentComment!!.AvgRating = ParentComment_AvgRating
                                ParentComment!!.CommentType = ParentComment_CommentType
                                ParentComment!!.FireBaseID = ParentComment_FireBaseID
                                ParentComment!!.NoteWorthyClicks = ParentComment_NoteWorthyClicks
                                ParentComment!!.OffenseCount = ParentComment_OffenseCount
                                ParentComment!!.CommentText = ParentComment_CommentText
                                ParentComment!!.PostedOn = ParentComment_PostedOn
                                ParentComment!!.PostedBy = ParentComment_PostedBy
//                                ParentComment!!.Rating = ParentComment_R


                                message.PostedBy = PostedBy
                                message.ParentComment = ParentComment
                                message.CommentText = CommentText
                                message.AvgRating = AvgRating
                                message.CommentType = CommentType
                                message.FireBaseID = FireBaseID
                                message.NoteWorthyClicks = NoteWorthyClicks
                                message.OffenseCount = OffenseCount
                                message.PostedOn = PostedOn
                                message.GroupName = GroupName
                                firebaseChatData!!.add(message)

                            }
                        }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }
    private fun GetContentTopOpinions(input: GetContentTopOpinionsReq) {
        try {
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.GetContentTopOpinions(input!!)
            call.enqueue(object : Callback<GetContentTopOpinionsRes> {
                override fun onResponse(call: Call<GetContentTopOpinionsRes>, response: Response<GetContentTopOpinionsRes>) {
                    if (top_opinion_shimmer_view_container != null) {
                        top_opinion_shimmer_view_container!!.stopShimmerAnimation()
                        top_opinion_shimmer_view_container!!.visibility = View.GONE
                        top_opinion_list_view!!.visibility = View.VISIBLE
                    }
                    val res = response.body()
                    if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {

                        val top:ArrayList<TopOpinionsData> = ArrayList<TopOpinionsData>()
                        for ((key, value) in res.TopOpinions) {
                            top.add(value );
                        }



                    }
                }

                override fun onFailure(call: Call<GetContentTopOpinionsRes>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (top_opinion_shimmer_view_container != null)
            top_opinion_shimmer_view_container!!.stopShimmerAnimation()
    }

    override fun onPause() {

        if (top_opinion_shimmer_view_container != null)
            top_opinion_shimmer_view_container!!.stopShimmerAnimation()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (top_opinion_shimmer_view_container != null) {
            top_opinion_shimmer_view_container!!.visibility = View.VISIBLE
            top_opinion_shimmer_view_container!!.startShimmerAnimation()
        }

    }
}
