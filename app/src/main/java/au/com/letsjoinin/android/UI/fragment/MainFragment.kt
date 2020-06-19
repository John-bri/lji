package au.com.letsjoinin.android.UI.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.UI.view.EndlessRecyclerViewScrollListener
import au.com.letsjoinin.android.sample.MasonryAdapter
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
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
 *
 * https://www.designyourway.net/blog/wp-content/uploads/2017/12/CeaG_rH64.jpg
 * https://d3nuqriibqh3vw.cloudfront.net/styles/aotw_card_ir/s3/media-vimeo/162638981.jpg?itok=K6whWlHp
 *https://blog.printsome.com/wp-content/uploads/coca-cola-marketing.jpg
 *https://www.adgully.com/img/400/201704/screen-shot-2017-04-04-at-12-51-15-pm.jpg
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainFragment : Fragment() {
    private lateinit var timelineAdapter: MasonryAdapter
    var timeline_mShimmerViewContainer: ShimmerFrameLayout? = null;
    var toAllowClickTab: Boolean = false
    private var layout_parent: LinearLayout? = null
    private var txt_no_content: TextView? = null
    var items: ArrayList<ProgramListData> = ArrayList()
    private lateinit var mLayoutManager: StaggeredGridLayoutManager
    private lateinit var timeline_recycler_view: RecyclerView
    fun tabNames(): IntArray {
        return intArrayOf(
            R.string.demo_tab_1,
            R.string.demo_tab_2,
            R.string.demo_tab_3,
            R.string.demo_tab_4,
            R.string.demo_tab_5

        )
    }

    var tabName = ArrayList<String>()
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var ContentDataList = ArrayList<SearchUserDocData>()
    var AllContentDataList = ArrayList<SearchUserDocData>()
    var mPrefMgr = PreferenceManager.getInstance();
    var tabPosition = 0
    private var listener: OnFragmentInteractionListener? = null
    //    var mShimmerViewContainer : ShimmerFrameLayout? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).menu_title != null) {
                (activity as NavigationActivity).menu_title.setText("Timeline")
            }
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.GONE
            }
            if (timeline_mShimmerViewContainer != null)
                timeline_mShimmerViewContainer!!.startShimmerAnimation()


        }


    }

    override fun onDestroy() {
        super.onDestroy()
        if(timelineAdapter != null) {
            timelineAdapter.onDestroyViews()
        }
        if (timeline_mShimmerViewContainer != null)
            timeline_mShimmerViewContainer!!.stopShimmerAnimation()
    }

    override fun onPause() {
        if (timeline_mShimmerViewContainer != null)
            timeline_mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_main, container, false)

        //highly-recommended in Firebase docs to initialize things early as possible
//test_admob_app_id is different with unit_id! you could get it in your Admob console
//        MobileAds.initialize(activity!!, getString(R.string.test_admob_app_id))

        try {
            if (activity != null) {
                layout_parent = v.findViewById(R.id.layout_parent)
                val pages = FragmentPagerItems(activity)
                timeline_mShimmerViewContainer =
                    v.findViewById(R.id.timeline_shimmer_view_container)


                for (titleResId in tabNames()) {
                    pages.add(
                        FragmentPagerItem.of(
                            getString(titleResId),
                            BlankFragment::class.java
                        )
                    )
                    tabName.add(getString(titleResId))
                }

                val adapter = FragmentPagerItemAdapter(
                    (activity as AppCompatActivity).supportFragmentManager, pages
                )
                val viewPager = v.findViewById(R.id.viewpager) as ViewPager
                viewPager.setAdapter(adapter)

                val viewPagerTab = v.findViewById(R.id.viewpagertab) as SmartTabLayout
                viewPagerTab.setViewPager(viewPager)
                val tabStrip = viewPagerTab.getChildAt(0) as LinearLayout
                for (i in 0 until tabStrip.childCount) {
                    tabStrip.getChildAt(i).setOnTouchListener(object : View.OnTouchListener {
                        override fun onTouch(v: View, event: MotionEvent): Boolean {
                            return !toAllowClickTab
                        }
                    })
                }
                viewPagerTab.setOnTabClickListener(object : SmartTabLayout.OnTabClickListener {
                    override fun onTabClicked(position: Int) {


//                        if (toAllowClickTab) {
//                            tabPosition = position
//                            var s = tabName[tabPosition]
//
//                            ContentDataList.clear()
//                            ContentDataList.addAll(AllContentDataList)
//
//                            if (!TextUtils.isEmpty(s)) {
//                                if (!s.equals("All")) {
//                                    for (data in AllContentDataList) {
//                                        if (!TextUtils.isEmpty(data.ContentType)) {
//                                            if (!data.ContentType!!.equals(s, true)) {
//                                                ContentDataList.remove(data)
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            if (ContentDataList.size > 0) {
//                                timeline_recycler_view.visibility = View.VISIBLE
//                                txt_no_content!!.visibility = View.GONE
//                                timelineAdapter!!.notifyDataSetChanged()
//                            } else {
//                                timeline_recycler_view.visibility = View.GONE
//                                txt_no_content!!.visibility = View.VISIBLE
//                            }
//
//                        }



                        if (toAllowClickTab) {
                            tabPosition = position
                            var s = tabName[tabPosition]
                            if (!TextUtils.isEmpty(s)) {
                                if (s.equals("All")) {
                                    AdsService()
                                    getTimlineListData()
                                } else {

                                    ContentDataList.clear()
                                    ContentDataList.addAll(AllContentDataList)
                                    for (data in AllContentDataList) {
                                        if (!TextUtils.isEmpty(data.ContentType)) {
                                            if (!data.ContentType!!.equals(s, true)) {
                                                ContentDataList.remove(data)
                                            }
                                        }
                                    }
                                    if (ContentDataList.size > 0) {
                                        timeline_recycler_view.visibility = View.VISIBLE
                                        txt_no_content!!.visibility = View.GONE
                                        timelineAdapter!!.notifyDataSetChanged()
                                    } else {
                                        timeline_recycler_view.visibility = View.GONE
                                        txt_no_content!!.visibility = View.VISIBLE
                                    }

                                }
                            }
                        }
                    }
                })
                try {
                    val localField = ViewPager::class.java.getDeclaredField("mScroller")
                    localField.isAccessible = true
                    val scroller = au.com.letsjoinin.android.utils.FixedSpeedScroller(
                        viewPager.getContext(),
                        DecelerateInterpolator(1.5f)
                    )
                    scroller.setDuration(700)
                    localField.set(viewPager, scroller)
                } catch (localIllegalAccessException: IllegalAccessException) {
                } catch (localIllegalArgumentException: IllegalArgumentException) {
                } catch (localNoSuchFieldException: NoSuchFieldException) {
                }

                timeline_recycler_view = v.findViewById(R.id.timeline_recycler_view) as RecyclerView
                mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                timeline_recycler_view.setLayoutManager(mLayoutManager)
                timeline_recycler_view.setHasFixedSize(true)
                txt_no_content = v.findViewById(R.id.txt_no_content)
                timeline_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_IDLE) {
                            timelineAdapter.onScrolled(recyclerView)
                        }
                    }
                })
                endlessRecyclerViewScrollListener =
                    object : EndlessRecyclerViewScrollListener(mLayoutManager) {

                        override fun onLoadMore(
                            page: Int,
                            totalItemsCount: Int,
                            view: RecyclerView
                        ) {
                            Log.e("", "onLoadMore: ---------------------------------------->$page")
//                listItem.addAll(localList)
//                itemAdapter.setListItem(listItem)
                        }

                        override fun lastVisibleItem(lastVisibleItem: Int) {
                            Log.e(
                                "",
                                "lastVisibleItem: -------------------------------------->$lastVisibleItem"
                            )
//                tvLastItem.setText("With in $lastVisibleItem")
                        }
                    }
//                timeline_recycler_view.addOnScrollListener(endlessRecyclerViewScrollListener as EndlessRecyclerViewScrollListener)


//                timelineAdapter = TimelineAdapter(ContentDataList, activity!!, layout_parent!!)
                timelineAdapter = MasonryAdapter(ContentDataList!!, activity!!, layout_parent!!)
//                timelineAdapter.setHasStableIds(true)
                //creating your adapter, it could be a custom adapter as well


                timeline_recycler_view.adapter = timelineAdapter
                mPrefMgr.setString(AppConstant.ADTitle, AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.AdMediaType, AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.AdInChatImagePath, AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.ADTimeLineImagePath,AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.AdVideoPath,AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.AdLocation, AppConstant.EMPTY)
                mPrefMgr.setString(AppConstant.ADChatBannerImagePath,AppConstant.EMPTY)
                AdsService()
                getTimlineListData()

            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return v;
    }
    private fun AdsService() {
        try {
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.GetEnterpriseAds("c9ff2fca-d3e7-4913-87f2-638e226d0105")
            call.enqueue(object : Callback<GetEnterpriseAdsData> {
                override fun onResponse(
                    call: Call<GetEnterpriseAdsData>,
                    response: Response<GetEnterpriseAdsData>
                ) {
                    var res = response.body();
                    if (res !=null && res.ServerMessage != null) {

                        if (res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
                           val content : ContentsAdData = res.ContentAds!!
                            if(content != null) {

                                mPrefMgr.setString(AppConstant.ADTitle, content.Title)
                                mPrefMgr.setString(AppConstant.AdMediaType, content.AdMediaType)
                                if( content.InChatImagePath != null) {
                                    mPrefMgr.setString(
                                        AppConstant.AdInChatImagePath,
                                        content.InChatImagePath
                                    )
                                }
                                if( content.TimeLineImagePath != null) {
                                    mPrefMgr.setString(
                                        AppConstant.ADTimeLineImagePath,
                                        content.TimeLineImagePath
                                    )
                                }
                                if( content.VideoPath != null) {
                                    mPrefMgr.setString(AppConstant.AdVideoPath, content.VideoPath)
                                }
                                    if( content.AdLocation != null) {
                                        mPrefMgr.setString(
                                            AppConstant.AdLocation,
                                            content.AdLocation
                                        )
                                    }
                                        if( content.ChatBannerImagePath != null) {
                                            mPrefMgr.setString(
                                                AppConstant.ADChatBannerImagePath,
                                                content.ChatBannerImagePath
                                            )
                                        }
                            }

                        }
                    } else {
                    }

                }

                override fun onFailure(
                    call: Call<GetEnterpriseAdsData>,
                    t: Throwable
                ) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun getTimlineListData() {
        try {
            if (timeline_mShimmerViewContainer != null) {
                timeline_recycler_view.visibility = View.GONE
                timeline_mShimmerViewContainer!!.visibility = View.VISIBLE
                timeline_mShimmerViewContainer!!.startShimmerAnimation()
                toAllowClickTab = false
            }
            txt_no_content!!.visibility = View.GONE
            val input: ResendOTPReq = ResendOTPReq()
            input.UserID = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY)
            input.UserCountry = mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY)
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.GetUserContents(input)
            call.enqueue(object : Callback<SearchUserData> {
                override fun onResponse(
                    call: Call<SearchUserData>,
                    response: Response<SearchUserData>
                ) {
                    if (activity != null) {
                        if (timeline_mShimmerViewContainer != null) {

                            timeline_mShimmerViewContainer!!.stopShimmerAnimation()
                            timeline_mShimmerViewContainer!!.visibility = View.GONE
                            timeline_recycler_view!!.visibility = View.VISIBLE
                        }
                        if (activity != null) {
                            if (response.body() != null) {
                                var res = response.body()
                                if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                                    timeline_recycler_view.visibility = View.VISIBLE
                                    if (res.ContentDataList != null && res.ContentDataList.size > 0) {
                                        //your test devices' ids

                                        toAllowClickTab = true
                                        ContentDataList.clear()
                                        AllContentDataList.clear()
                                        AllContentDataList.addAll(res.ContentDataList)
                                        ContentDataList.addAll(AllContentDataList)

                                        if(res.ContentDataList.size > 1) {
                                            val ad = SearchUserDocData()
                                            ad.ContentType = "AD"
                                            ad.MediaPath =
                                                "https://www.designyourway.net/blog/wp-content/uploads/2017/12/CeaG_rH64.jpg"
                                            ContentDataList.add(1, ad)
                                        }
                                        if(res.ContentDataList.size > 13) {
                                            val ad2 = SearchUserDocData()
                                            ad2.ContentType = "AD"
                                            ad2.MediaPath =
                                                "https://d3nuqriibqh3vw.cloudfront.net/styles/aotw_card_ir/s3/media-vimeo/162638981.jpg"
                                            ContentDataList.add(13, ad2)
                                        }
                                        if(res.ContentDataList.size > 25) {
                                            val ad3 = SearchUserDocData()
                                            ad3.ContentType = "AD"
                                            ad3.MediaPath =
                                                "https://blog.printsome.com/wp-content/uploads/coca-cola-marketing.jpg"
                                            ContentDataList.add(25, ad3)
                                        }
                                        if(res.ContentDataList.size > 37) {
                                            val ad4 = SearchUserDocData()
                                            ad4.ContentType = "AD"
                                            ad4.MediaPath =
                                                "https://www.adgully.com/img/400/201704/screen-shot-2017-04-04-at-12-51-15-pm.jpg"
                                            ContentDataList.add(37, ad4)
                                        }
                                        if (timeline_recycler_view != null && timeline_recycler_view.adapter != null) {
                                            timeline_recycler_view.adapter!!.notifyDataSetChanged()
                                        }
                                    } else {
                                        timeline_recycler_view.visibility = View.GONE
                                        txt_no_content!!.visibility = View.VISIBLE
                                    }
                                } else {
                                    timeline_recycler_view.visibility = View.GONE
                                    txt_no_content!!.visibility = View.VISIBLE
                                }
                            } else {
                                timeline_recycler_view.visibility = View.GONE
                                txt_no_content!!.visibility = View.VISIBLE
                            }

                        } else {
                            timeline_recycler_view.visibility = View.GONE
                            txt_no_content!!.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<SearchUserData>, t: Throwable) {


                    if (timeline_recycler_view != null) {
                        toAllowClickTab = false
                        timeline_recycler_view.visibility = View.GONE
                    }
                    txt_no_content!!.visibility = View.VISIBLE
                    if (timeline_mShimmerViewContainer != null) {
                        timeline_mShimmerViewContainer!!.stopShimmerAnimation()
                        timeline_mShimmerViewContainer!!.visibility = View.GONE
                    }
                    if (layout_parent != null) {
                        CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
