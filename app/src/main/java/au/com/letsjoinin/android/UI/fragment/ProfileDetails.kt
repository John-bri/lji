package au.com.letsjoinin.android.UI.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.LoginActivity
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
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
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var layout_parent: RelativeLayout? = null
    open var progress_lyt: RelativeLayout? = null
    private var listener: OnFragmentInteractionListener? = null
    var txt_sign_out: TextView? = null
    var txt_profile_user_name: TextView? = null
    var txt_no_profile: TextView? = null
    var txt_no_favourites: TextView? = null
    var txt_no_content: TextView? = null
    var txt_no_following: TextView? = null
    var txt_profile_token_count: TextView? = null
    var txt_profile_address: TextView? = null
    var profile_list_programs: RecyclerView? = null
    var profile_img_avatar: CircleImageView? = null
    var profile_list_favourites: RecyclerView? = null
    var profile_list_content: RecyclerView? = null
    var profile_list_programs_following: RecyclerView? = null
    var RecyclerViewLayoutManager1: RecyclerView.LayoutManager? = null
    var RecyclerViewLayoutManager2: RecyclerView.LayoutManager? = null
    var RecyclerViewLayoutManager3: RecyclerView.LayoutManager? = null
    var RecyclerViewLayoutManager4: RecyclerView.LayoutManager? = null
    var HorizontalLayout1: LinearLayoutManager? = null
    var HorizontalLayout2: LinearLayoutManager? = null
    var HorizontalLayout3: LinearLayoutManager? = null
    var HorizontalLayout4: LinearLayoutManager? = null
    var view_all_content: TextView? = null
    var view_all_fav: TextView? = null
    var view_all_engaged: TextView? = null
    var view_all_following: TextView? = null
    var ChildView: View? = null
    var RecyclerViewItemPosition: Int = 0
    val favListStr = java.util.ArrayList<String>()
    var profile_shimmer_view_container: ShimmerFrameLayout? = null;
    var profile_content_layout: LinearLayout? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)
        try {
            val edit_profile: ImageView = view.findViewById(R.id.edit_profile)

            layout_parent = view.findViewById(R.id.profile_parent)
            progress_lyt = view.findViewById(R.id.progress_lyt)
            progress_lyt!!.bringToFront()
            txt_no_following = view.findViewById(R.id.txt_no_following)
            txt_no_content = view.findViewById(R.id.txt_no_content)
            txt_no_favourites = view.findViewById(R.id.txt_no_favourites)
            txt_no_profile = view.findViewById(R.id.txt_no_profile)
            txt_profile_user_name = view.findViewById(R.id.txt_profile_user_name)
            txt_profile_token_count = view.findViewById(R.id.txt_profile_token_count)
            txt_profile_address = view.findViewById(R.id.txt_profile_address)
            profile_img_avatar = view.findViewById(R.id.profile_img_avatar)
            profile_shimmer_view_container = view.findViewById(R.id.profile_shimmer_view_container)
            txt_sign_out = view.findViewById(R.id.txt_sign_out)
            profile_content_layout = view.findViewById(R.id.profile_content_layout)
            profile_list_programs = view.findViewById(R.id.profile_list_programs);
            profile_list_favourites = view.findViewById(R.id.profile_list_favourites);
            profile_shimmer_view_container!!.visibility = View.VISIBLE


            view_all_engaged = view.findViewById(R.id.profile_view_all_program);
            view_all_fav = view.findViewById(R.id.profile_view_all_favourites);
            view_all_content = view.findViewById(R.id.profile_view_all_mycontents);
            view_all_following = view.findViewById(R.id.profile_view_all_);

            profile_list_content = view.findViewById(R.id.profile_list_content);


            profile_list_programs_following = view.findViewById(R.id.profile_list_programs_following);

            RecyclerViewLayoutManager1 = LinearLayoutManager(activity);

            profile_list_programs!!.setLayoutManager(RecyclerViewLayoutManager1);
            HorizontalLayout1 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            profile_list_programs!!.setLayoutManager(HorizontalLayout1)

            profile_list_programs!!.adapter =
                au.com.letsjoinin.android.UI.adapter.UserProfileListadapter(null, activity!!, layout_parent!!, this)


            //list2
            RecyclerViewLayoutManager2 = LinearLayoutManager(activity);

            profile_list_favourites!!.setLayoutManager(RecyclerViewLayoutManager2);
            HorizontalLayout2 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            profile_list_favourites!!.setLayoutManager(HorizontalLayout2)

            profile_list_favourites!!.adapter =
                au.com.letsjoinin.android.UI.adapter.UserProfileListadapter(null, activity!!, layout_parent!!, this)

            //list4
            RecyclerViewLayoutManager4 = LinearLayoutManager(activity);

            profile_list_content!!.setLayoutManager(RecyclerViewLayoutManager4);
            HorizontalLayout4 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            profile_list_content!!.setLayoutManager(HorizontalLayout4)

            profile_list_content!!.adapter =
                au.com.letsjoinin.android.UI.adapter.UserMyContentListAdapter(
                    null,
                    null,
                    activity!!,
                    layout_parent!!,
                    this
                )


            ///list3


            RecyclerViewLayoutManager3 = LinearLayoutManager(activity);

            profile_list_programs_following!!.setLayoutManager(RecyclerViewLayoutManager3);
            HorizontalLayout3 = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            profile_list_programs_following!!.setLayoutManager(HorizontalLayout3)

            profile_list_programs_following!!.adapter =
                au.com.letsjoinin.android.UI.adapter.UserFollowingListAdapter(null, activity!!, layout_parent!!, this)











            edit_profile.setOnClickListener(View.OnClickListener {
                val fragment = EditProfileDetails()
                if (fragmentManager != null) {
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            })
            txt_sign_out!!.setOnClickListener(View.OnClickListener {
                val remember = mPrefMgr!!.getBoolean(AppConstant.RememberMe, false)
                val emilId = mPrefMgr!!.getString(AppConstant.EmailID, AppConstant.EMPTY)
                val prefere = activity!!.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

                val editor = prefere.edit()
                editor.clear();
                editor.commit();
                mPrefMgr!!.setBoolean(AppConstant.RememberMe, remember)
                if (!mPrefMgr!!.getString(AppConstant.LOGINTYPE, AppConstant.EMPTY).equals(AppConstant.LJI)) {
                    mPrefMgr!!.setBoolean(AppConstant.RememberMe, false)
                }

                mPrefMgr!!.setBoolean(AppConstant.IsLogin, false)
                mPrefMgr!!.setString(AppConstant.EmailID, emilId)
                mPrefMgr!!.setString(AppConstant.USERID, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.SMEmailID, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.PASSWORD, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.LJIID, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.TimeZone, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.GenderCode, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.Country, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.FirstName, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.LastName, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.SignUPFirstName, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.SignUPLastName, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.YearOfBirth, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.PhoneNo, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.AvatarPath, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.FacebookID, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.GPlusID, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.StatusCode, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.UserPoints, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.Categories, AppConstant.EMPTY)
                mPrefMgr!!.setString(AppConstant.Suburb, AppConstant.EMPTY)
                mPrefMgr!!.setBoolean(AppConstant.TopicImageSelected, false)
                mPrefMgr!!.setString(AppConstant.TopicImageURL, AppConstant.EMPTY)


                val intent = Intent(activity!!, LoginActivity::class.java)

                startActivity(intent)
                activity!!.finish()
            })
            if (profile_shimmer_view_container != null) {
                profile_shimmer_view_container!!.startShimmerAnimation()
                profile_shimmer_view_container!!.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return view
    }

    fun SetUnFollow(ChannelID: String, PKChannelCountry: String) {
        progress_lyt!!.visibility = View.VISIBLE
        progress_lyt!!.bringToFront()
        val input: SetUnFollowReq = SetUnFollowReq()
        val followBy: ProgramListAdminClass = ProgramListAdminClass()
        input.ChannelID = ChannelID
        input.PKChannelCountry = PKChannelCountry
        input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
        input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)


        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.SetUnFollowChannel(input)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                progress_lyt!!.visibility = View.GONE
                if (response.body() != null) {
                    var res = response.body()
                    if (res.Status.equals(AppConstant.SUCCESS)) {
                        getprofileData()
                    }

                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                progress_lyt!!.visibility = View.GONE
//                if (channel_parent != null) {
//                    progress_lyt!!.visibility = View.GONE
//                    CommonMethods.SnackBar(channel_parent, getString(R.string.error), false)
//                }
            }
        })
    }

    open fun getprofileData() {
        favListStr.clear()
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val UserID = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY)
//        view_all_engaged!!.visibility = View.GONE
//        profile_content_layout!!.visibility = View.GONE
        val call = service.getProfileData(UserID)
        call.enqueue(object : Callback<ProfileDataResponse> {
            override fun onResponse(call: Call<ProfileDataResponse>, response: Response<ProfileDataResponse>) {
                if (activity != null) {
                    if (progress_lyt != null) {
                        progress_lyt!!.visibility = View.GONE
                    }
                    if (response.body() != null) {
                        if (profile_shimmer_view_container != null) {
                            profile_shimmer_view_container!!.stopShimmerAnimation()
                            profile_shimmer_view_container!!.visibility = View.GONE
                        }
                        if (profile_content_layout != null) {
                            profile_content_layout!!.visibility = View.VISIBLE
                        }
                        val profileDataResponse: ProfileDataResponse = response.body();

                        if (profileDataResponse != null) {

                            if (favListStr != null) {
                                favListStr.clear()
                            }
                            if (profileDataResponse != null && profileDataResponse.UserData != null) {
                                val g = Gson()
                                mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, false)
                                mPrefMgr!!.setString(AppConstant.LJIID, profileDataResponse.UserData!!.LJIID)
                                mPrefMgr!!.setString(AppConstant.TimeZone, profileDataResponse.UserData!!.TimeZone)
                                mPrefMgr!!.setString(AppConstant.GenderCode, profileDataResponse.UserData!!.GenderCode)
                                mPrefMgr!!.setString(AppConstant.Country, profileDataResponse.UserData!!.Country)
                                mPrefMgr!!.setString(AppConstant.FirstName, profileDataResponse.UserData!!.FirstName)
                                mPrefMgr!!.setString(AppConstant.LastName, profileDataResponse.UserData!!.LastName)
                                mPrefMgr!!.setString(
                                    AppConstant.YearOfBirth,
                                    profileDataResponse.UserData!!.YearOfBirth
                                )
                                mPrefMgr!!.setString(AppConstant.PhoneNo, profileDataResponse.UserData!!.PhoneNo)
                                mPrefMgr!!.setString(AppConstant.AvatarPath, profileDataResponse.UserData!!.AvatarPath)
                                mPrefMgr!!.setString(AppConstant.FacebookID, profileDataResponse.UserData!!.FacebookID)
                                mPrefMgr!!.setString(AppConstant.GPlusID, profileDataResponse.UserData!!.GPlusID)
                                mPrefMgr!!.setString(AppConstant.StatusCode, profileDataResponse.UserData!!.StatusCode)
                                mPrefMgr!!.setString(AppConstant.GDPR, profileDataResponse.UserData!!.GDPR)
                                mPrefMgr!!.setString(AppConstant.UserPoints, profileDataResponse.UserData!!.UserPoints)
                                mPrefMgr!!.setString(AppConstant.SMEmailID, profileDataResponse.UserData!!.EmailID)
                                mPrefMgr!!.setString(
                                    AppConstant.UserName,
                                    profileDataResponse.UserData!!.FirstName + " " + profileDataResponse.UserData!!.LastName
                                )
                                if (profileDataResponse.UserData!!.Categories != null && profileDataResponse.UserData!!.Categories.size > 0) {
                                    mPrefMgr!!.setString(
                                        AppConstant.Categories,
                                        profileDataResponse.UserData!!.Categories.toString()
                                    )
                                }
                                if (profileDataResponse.UserData!!.Suburb != null) {
                                    mPrefMgr!!.setString(
                                        AppConstant.Suburb,
                                        g.toJson(profileDataResponse.UserData!!.Suburb)
                                    )
                                }
                                if (profileDataResponse.UserData!!.ChannelsFollowing != null && profileDataResponse.UserData!!.ChannelsFollowing.size > 0) {
                                    mPrefMgr!!.setString(
                                        AppConstant.ChannelsFollowing,
                                        profileDataResponse.UserData!!.ChannelsFollowing.toString()
                                    )
                                }
                                if (profile_img_avatar != null && !TextUtils.isEmpty(profileDataResponse.UserData!!.AvatarPath)) {
                                    Picasso.with(context)
                                        .load(profileDataResponse.UserData!!.AvatarPath)
                                        .error(R.drawable.image_placeholder_1)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .into(profile_img_avatar)
                                }


                                if (profileDataResponse.UserData != null && profileDataResponse.UserData!!.MyFavourites != null && profileDataResponse.UserData!!.MyFavourites.size > 0) {
                                    val favList: ArrayList<Myfavorites> = ArrayList<Myfavorites>()
                                    for ((key, value) in profileDataResponse.UserData!!.MyFavourites) {
                                        favList.add(value);
                                        favListStr.add(value.ContentID!!)
                                    }
                                    if (profile_list_favourites != null) {
                                        profile_list_favourites!!.adapter =
                                            au.com.letsjoinin.android.UI.adapter.UserProfileListadapter(
                                                favList,
                                                activity!!
                                                , layout_parent!!, this@ProfileDetails
                                            )
//                                profile_list_programs!!.adapter =
//                                    au.com.letsjoinin.android.UI.adapter.UserProfileListadapter(
//                                        favList,
//                                        activity!!
//                                        ,layout_parent!!,this@ProfileDetails)

                                        profile_list_favourites!!.visibility = View.VISIBLE
//                                profile_list_programs!!.visibility = View.VISIBLE
                                        view_all_fav!!.visibility = View.VISIBLE
//                                view_all_engaged!!.visibility = View.VISIBLE
                                        txt_no_favourites!!.visibility = View.GONE
                                    }
//                                txt_no_profile!!.visibility = View.GONE
                                } else {
                                    if (profile_list_favourites != null) {
                                        profile_list_favourites!!.visibility = View.GONE
                                        profile_list_programs!!.visibility = View.GONE
                                        view_all_fav!!.visibility = View.GONE
                                        view_all_engaged!!.visibility = View.GONE
                                        txt_no_favourites!!.visibility = View.VISIBLE
                                        txt_no_profile!!.visibility = View.VISIBLE
                                    }
                                }
                                if (profileDataResponse.UserData!!.MyContents != null && profileDataResponse.UserData!!.MyContents.size > 0) {
                                    if (profile_list_content != null) {
                                        profile_list_content!!.visibility = View.VISIBLE
                                        view_all_content!!.visibility = View.VISIBLE
                                        txt_no_content!!.visibility = View.GONE
                                        val contentList: ArrayList<ContentsClass> = ArrayList<ContentsClass>()
                                        for ((key, value) in profileDataResponse.UserData!!.MyContents) {
                                            contentList.add(value);
                                        }
                                        profile_list_content!!.adapter =
                                            au.com.letsjoinin.android.UI.adapter.UserMyContentListAdapter(
                                                favListStr,
                                                contentList,
                                                activity!!
                                                , layout_parent!!, this@ProfileDetails
                                            )
                                    }
                                } else {
                                    if (profile_list_content != null) {
                                        profile_list_content!!.visibility = View.GONE
                                        view_all_content!!.visibility = View.GONE
                                        txt_no_content!!.visibility = View.VISIBLE
                                    }
                                }
                                if (profileDataResponse.UserData!!.ChannelsFollowing != null && profileDataResponse.UserData!!.ChannelsFollowing.size > 0) {
                                    if (profile_list_programs_following != null) {
                                        val followList: ArrayList<ChannelFollowing> = ArrayList<ChannelFollowing>()
                                        for ((key, value) in profileDataResponse.UserData!!.ChannelsFollowing) {
                                            followList.add(value);
                                        }
                                        profile_list_programs_following!!.adapter =
                                            au.com.letsjoinin.android.UI.adapter.UserFollowingListAdapter(
                                                followList,
                                                activity!!
                                                , layout_parent!!, this@ProfileDetails
                                            )

                                        txt_no_following!!.visibility = View.GONE
                                        profile_list_programs_following!!.visibility = View.VISIBLE
                                        view_all_following!!.visibility = View.VISIBLE
                                    }
                                } else {
                                    if (profile_list_programs_following != null) {
                                        txt_no_following!!.visibility = View.VISIBLE
                                        profile_list_programs_following!!.visibility = View.GONE
                                        view_all_following!!.visibility = View.GONE
                                    }
                                }
                            }
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ProfileDataResponse>, t: Throwable) {
                progress_lyt!!.visibility = View.GONE
                if (profile_shimmer_view_container != null) {
                    profile_shimmer_view_container!!.stopShimmerAnimation()
                    profile_shimmer_view_container!!.visibility = View.GONE
                    profile_content_layout!!.visibility = View.VISIBLE
                }
            }
        })
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    var mPrefMgr = PreferenceManager.getInstance();
    override fun onResume() {
        super.onResume()
        if (activity != null) {

            val username = mPrefMgr!!.getString(
                AppConstant.FirstName,
                AppConstant.EMPTY
            ) + " " + mPrefMgr!!.getString(AppConstant.LastName, AppConstant.EMPTY)
            val UserPoints = mPrefMgr!!.getString(AppConstant.UserPoints, AppConstant.EMPTY)
            txt_profile_user_name!!.setText(username)
            txt_profile_token_count!!.setText(UserPoints + " tokens")

            if ((activity as NavigationActivity).menu_title != null) {
                (activity as NavigationActivity).menu_title.setText("My Profile")
            }
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.GONE
            }
//            if((activity as NavigationActivity).relative_tool_bar != null) {
//                (activity as NavigationActivity).relative_tool_bar.visibility = View.VISIBLE
//            }
            val Suburb = mPrefMgr!!.getString(AppConstant.Suburb, AppConstant.EMPTY)
            val g1 = Gson()
            var suburb: SignUpSuburb? =
                g1.fromJson(Suburb, SignUpSuburb::class.java)
            if (suburb != null) {
                txt_profile_address!!.setText(suburb!!.Code + " - " + suburb!!.Name)
            }
            val AvatarPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY).toString().trim()
            val g = AvatarPath.length
            if (!TextUtils.isEmpty(AvatarPath)) {
                Picasso.with(context)
                    .load(AvatarPath)
                    .error(R.drawable.image_placeholder_1)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_img_avatar)
            }
//            progress_lyt!!.visibility = View.VISIBLE
            getprofileData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if(timer != null) {
//            timer.cancel()
//        }
    }

    override fun onPause() {
//        if(timer != null) {
//            timer.cancel()
//        }
        if (profile_shimmer_view_container != null)
            profile_shimmer_view_container!!.stopShimmerAnimation()
        super.onPause()
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
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
