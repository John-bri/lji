package au.com.letsjoinin.android.UI.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChannelFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChannelFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ChannelFragment : Fragment() {
    private var ChannelID: String? = null
    private var FollowingCount  = 0
    private var ProgramPKCountry: String? = null
    private var PKChannelCountry: String? = null
    var channel_mShimmerViewContainer: ShimmerFrameLayout? = null;
    var mPrefMgr = PreferenceManager.getInstance();
    internal var txt_channel_user_name: TextView? = null
    internal var txt_channel_address: TextView? = null
    internal var txt_channel_fav_count: TextView? = null
    internal var txt_channel_follow_count: TextView? = null
    internal var txt_channel_follow_unfollow: TextView? = null
    internal var txt_channel_desc: TextView? = null
    internal var channel_img_avatar: CircleImageView? = null
    var channel_recyclerView: RecyclerView? = null
    var channel_parent: LinearLayout? = null
    var channel_share_view: LinearLayout? = null
    var channel_follow_view: LinearLayout? = null
    var progress_lyt: RelativeLayout? = null
    var RecyclerViewLayoutManager: RecyclerView.LayoutManager? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    //sean tim jamesmccollum porter adair getkate rankin obrie stirling
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.activity_channel, container, false)



        try {
            val args = arguments
            ChannelID = args!!.getString("ChannelID")
            ProgramPKCountry = args!!.getString("ProgramPKCountry")
            channel_recyclerView = v.findViewById(R.id.channel_recyclerview);
            channel_parent = v.findViewById(R.id.channel_parent);
            channel_share_view = v.findViewById(R.id.channel_share_view);
            channel_follow_view = v.findViewById(R.id.channel_follow_view);
            progress_lyt = v.findViewById(R.id.progress_lyt);
            channel_recyclerView!!.setNestedScrollingEnabled(false);
            RecyclerViewLayoutManager =
                GridLayoutManager((activity as AppCompatActivity), 2) as RecyclerView.LayoutManager?;
            txt_channel_user_name = v.findViewById(R.id.txt_channel_user_name)
            txt_channel_address = v.findViewById(R.id.txt_channel_address)
            txt_channel_fav_count = v.findViewById(R.id.txt_channel_fav_count)
            txt_channel_follow_count = v.findViewById(R.id.txt_channel_follow_count)
            txt_channel_follow_unfollow = v.findViewById(R.id.txt_channel_follow_unfollow)
            txt_channel_desc = v.findViewById(R.id.txt_channel_desc)
            channel_img_avatar = v.findViewById(R.id.channel_img_avatar)
            channel_recyclerView!!.setLayoutManager(RecyclerViewLayoutManager);

            progress_lyt!!.bringToFront()
            channel_mShimmerViewContainer = v.findViewById(R.id.channel_shimmer_view_container)

            channel_follow_view!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    progress_lyt!!.visibility = View.VISIBLE
                    if(txt_channel_follow_unfollow!!.text.toString().equals("Follow",true)) {
                        SetFollow()
                    }else  if(txt_channel_follow_unfollow!!.text.toString().equals("Unfollow",true)){
                        SetUnFollow()
                    }
                }

            })
            channel_share_view!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    GetShareDetails(ChannelID!!,"Live")
                }

            })



        } catch (e: Exception) {
            if (channel_parent != null) {
                CommonMethods.SnackBar(channel_parent, getString(R.string.error), false)
            }
        }
        return v;
    }

    fun pxTodp(dip: Float): Int {
        val resources = resources
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        ).toInt()
                )
    }

    private fun GetChannel() {


        val input: ChannelDataReq = ChannelDataReq()
        input.ChannelID = ChannelID
        input.PKCountry = ProgramPKCountry
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.GetChannelByID(input)
        call.enqueue(object : Callback<ChannelDataRes> {
            override fun onResponse(call: Call<ChannelDataRes>, response: Response<ChannelDataRes>) {
                if (activity != null && channel_parent != null) {
                    if (response.body() != null) {
                        var res = response.body()
                        if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
                            if (channel_mShimmerViewContainer != null) {
                                channel_mShimmerViewContainer!!.stopShimmerAnimation()
                                channel_mShimmerViewContainer!!.visibility = View.GONE
                            }
                            if (res != null && res.ChannelData != null) {
                                txt_channel_user_name!!.text = res.ChannelData!!.Name

//                                txt_channel_fav_count!!.text = res.FavouritesCount
                                if (res.ChannelData!!.Address != null) {
                                    PKChannelCountry = res.ChannelData!!.Address!!.Country
                                    txt_channel_address!!.text =
                                        res.ChannelData!!.Address!!.Street + ",\n" +res.ChannelData!!.Address!!.City +  "-" + res.ChannelData!!.Address!!.PostalCode+", " +  res.ChannelData!!.Address!!.State +
                                                ",\n" + res.ChannelData!!.Address!!.Country
                                }
                                txt_channel_desc!!.text = res.ChannelData!!.Description
                                if (!TextUtils.isEmpty(res.ChannelData!!.LogoPath)) {
                                    Picasso.with(context)
                                        .load(res.ChannelData!!.LogoPath)
                                        .error(R.drawable.image_placeholder_1)
                                        .into(channel_img_avatar)
                                }

                                if (res.ChannelData!!.Contents != null) {


                                    val items: ArrayList<ContentsClass> =  ArrayList()

                                    for ((key, value) in res.ChannelData!!.Contents) {
                                        items.add(value );
                                    }
                                    channel_recyclerView!!.visibility = View.VISIBLE
                                    channel_recyclerView!!.adapter =
                                        au.com.letsjoinin.android.UI.adapter.ChannelListadapter(
                                            items,
                                            activity!!
                                                    ,res.ChannelData!!.Name!!, res.ChannelData!!.Description!!
                                       ,channel_parent!! )
                                }
                                txt_channel_follow_unfollow!!.setText("Follow")
                                if (res.ChannelData!!.FollowingBy != null) {
                                    FollowingCount  = res.ChannelData!!.FollowingBy.size
                                    txt_channel_follow_count!!.text = FollowingCount.toString()
                                    if(res.ChannelData!!.FollowingBy!!.containsKey(mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)))
                                    {
                                        txt_channel_follow_unfollow!!.setText("Unfollow")
                                    }
                                }
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ChannelDataRes>, t: Throwable) {
                if (channel_mShimmerViewContainer != null) {
                    channel_mShimmerViewContainer!!.stopShimmerAnimation()
                    channel_mShimmerViewContainer!!.visibility = View.GONE
                }
                if (channel_parent != null) {
                    CommonMethods.SnackBar(channel_parent, getString(R.string.error), false)
                }
            }
        })
    }
    private fun GetShareDetails(ContentId : String,ContentType : String) {
        val querry = "ContentId=$ContentId~ContentType=$ContentType"
        try {
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.EncryptSharingLink(querry)
            call.enqueue(object : retrofit2.Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val res = response.body()
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "http://letsjoinin.azurewebsites.net/HomePage/MobileApp?$res"
//                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    if(context != null) {
                        context!!.startActivity(Intent.createChooser(sharingIntent, "Share via"))
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }
    private fun SetFollow() {
        val input: SetFollowReq = SetFollowReq()
        val followBy: ProgramListAdminClass = ProgramListAdminClass()
        input.ChannelID = ChannelID
        input.PKChannelCountry = PKChannelCountry
        input.PKUserCountry =  mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
        followBy.AvatarPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
        followBy.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
        followBy.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)


        input.FollowBy = followBy


        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.SetFollowChannel(input)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                if (activity != null) {
                    if(progress_lyt != null) {
                        progress_lyt!!.visibility = View.GONE
                    }
                    if (response.body() != null) {
                        var res = response.body()
                        if (res != null && res.Status.equals(AppConstant.SUCCESS)) {
//                            GetChannel()
                            FollowingCount  = FollowingCount + 1
                            if(txt_channel_follow_count != null) {
                                txt_channel_follow_count!!.text = FollowingCount.toString()
                                txt_channel_follow_unfollow!!.setText("Unfollow")
                            }
                            if(channel_parent != null) {
                                CommonMethods.SnackBar(channel_parent, res.DisplayMsg, false)
                            }

                            if (channel_mShimmerViewContainer != null) {
                                channel_mShimmerViewContainer!!.stopShimmerAnimation()
                                channel_mShimmerViewContainer!!.visibility = View.GONE
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                if (channel_mShimmerViewContainer != null) {
                    channel_mShimmerViewContainer!!.stopShimmerAnimation()
                    channel_mShimmerViewContainer!!.visibility = View.GONE
                }
                if (channel_parent != null) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(channel_parent, getString(R.string.error), false)
                }
            }
        })
    }
    private fun SetUnFollow() {
        val input: SetUnFollowReq = SetUnFollowReq()
        val followBy: ProgramListAdminClass = ProgramListAdminClass()
        input.ChannelID = ChannelID
        input.PKChannelCountry = PKChannelCountry
        input.PKUserCountry =  mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
        input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)


        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.SetUnFollowChannel(input)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                if (activity != null) {
                    progress_lyt!!.visibility = View.GONE
                    if (response.body() != null) {
                        var res = response.body()
                        if (res != null && res.Status.equals(AppConstant.SUCCESS)) {
//                            GetChannel()
                            FollowingCount  = FollowingCount - 1
                            if(channel_parent != null) {
                                txt_channel_follow_count!!.text = FollowingCount.toString()
                                CommonMethods.SnackBar(channel_parent, res.DisplayMsg, false)
                                txt_channel_follow_unfollow!!.setText("Follow")
                            }

                            if (channel_mShimmerViewContainer != null) {
                                channel_mShimmerViewContainer!!.stopShimmerAnimation()
                                channel_mShimmerViewContainer!!.visibility = View.GONE
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                if (channel_mShimmerViewContainer != null) {
                    channel_mShimmerViewContainer!!.stopShimmerAnimation()
                    channel_mShimmerViewContainer!!.visibility = View.GONE
                }
                if (channel_parent != null) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(channel_parent, getString(R.string.error), false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).menu_title != null) {
                (activity as NavigationActivity).menu_title.setText("Channel Name")
            }
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.VISIBLE
            }
        }
        if (!TextUtils.isEmpty(ChannelID)) {
            GetChannel()
        }
        if (channel_mShimmerViewContainer != null)
            channel_mShimmerViewContainer!!.startShimmerAnimation()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (channel_mShimmerViewContainer != null)
            channel_mShimmerViewContainer!!.stopShimmerAnimation()
    }

    override fun onPause() {

        if (channel_mShimmerViewContainer != null)
            channel_mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
