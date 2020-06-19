package com.developers.viewpager


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.UI.adapter.SearchListAdapter
import au.com.letsjoinin.android.UI.fragment.BlankFragment
import au.com.letsjoinin.android.UI.view.EndlessRecyclerViewScrollListener
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.facebook.shimmer.ShimmerFrameLayout
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.search_frag.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 */
//class SearchFrag : Fragment() {


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFrag : Fragment() {
    var mPrefMgr = PreferenceManager.getInstance();
    private var searchadapter: SearchListAdapter? = null
    private lateinit var mLayoutManager: StaggeredGridLayoutManager
    private lateinit var search_recycler_view: RecyclerView
    // TODO: Rename and change types of parameters
    private var layout_parent: RelativeLayout? = null
    private var txt_no_content: TextView? = null
    private var search_text_box: EditText? = null
    var search_shimmer_view_container: ShimmerFrameLayout? = null;
    private var param1: String? = null
    private var param2: String? = null
    var toAllowClickTab: Boolean = false
    var tabPosition = 0
    val arrayList = ArrayList<Int>()
    var ContentDataList = ArrayList<SearchUserDocData>()
    var AllContentDataList = ArrayList<SearchUserDocData>()
    var searchContentDataList = ArrayList<SearchUserDocData>()
    private var listener: OnFragmentInteractionListener? = null
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




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.search_frag, container, false)

        try {
            layout_parent = view.findViewById(R.id.search_parent)
            txt_no_content = view.findViewById(R.id.txt_no_content)
            search_shimmer_view_container = view.findViewById(R.id.search_shimmer_view_container)
            search_text_box = view.findViewById(R.id.search_text_box)
            val search_img_view: ImageView = view.findViewById(R.id.search_img_view)
            val layout_search_result: LinearLayout = view.findViewById(R.id.layout_search_result)
            val pages = FragmentPagerItems(activity)


            for (titleResId in tabNames()) {
                pages.add(FragmentPagerItem.of(getString(titleResId), BlankFragment::class.java))
                tabName.add(getString(titleResId))
            }


            val adapter = FragmentPagerItemAdapter(
                (activity as AppCompatActivity).supportFragmentManager, pages
            )
            val viewPager = view.findViewById(R.id.viewpager) as ViewPager
            viewPager.setAdapter(adapter)

            val viewPagerTab = view.findViewById(R.id.search_viewpagertab) as SmartTabLayout
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

                    if (toAllowClickTab) {
                        tabPosition = position
                        var s = tabName[tabPosition]
                        if (!TextUtils.isEmpty(s)) {
                            if (s.equals("All")) {
                                if (TextUtils.isEmpty(search_text_box!!.text.toString().trim())) {
                                    search_text_box!!.setText("")
                                    getListData()
                                }else{
                                    ContentDataList.clear()
                                    ContentDataList.addAll(searchContentDataList)
                                    if (ContentDataList.size > 0) {
                                        search_recycler_view.visibility = View.VISIBLE
                                        txt_no_content!!.visibility = View.GONE
                                        searchadapter!!.notifyDataSetChanged()
                                    } else {
                                        search_recycler_view.visibility = View.GONE
                                        txt_no_content!!.visibility = View.VISIBLE
                                    }
                                }
                            } else {

                                ContentDataList.clear()
                                ContentDataList.addAll(AllContentDataList)
                                for (data in AllContentDataList) {
                                    if(!TextUtils.isEmpty(data.ContentType)) {
                                        if (TextUtils.isEmpty(search_text_box!!.text.toString().trim())) {
                                            if (!data.ContentType!!.equals(s, true)) {
                                                ContentDataList.remove(data)
                                            }
                                        } else {
                                            if (!data.ContentType!!.equals(
                                                    s,
                                                    true
                                                ) || !data.Title!!.equals(
                                                    search_text_box!!.text.toString().trim(),
                                                    true
                                                )
                                            ) {
                                                ContentDataList.remove(data)
                                            }
                                        }
                                    }
                                }
                                if (ContentDataList.size > 0) {
                                    search_recycler_view.visibility = View.VISIBLE
                                    txt_no_content!!.visibility = View.GONE
                                    searchadapter!!.notifyDataSetChanged()
                                } else {
                                    search_recycler_view.visibility = View.GONE
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
            search_recycler_view = view.findViewById(R.id.search_recycler_view) as RecyclerView


            mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            search_recycler_view.setLayoutManager(mLayoutManager)



            endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(mLayoutManager) {

                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    Log.e("", "onLoadMore: ---------------------------------------->$page")
//                listItem.addAll(localList)
//                itemAdapter.setListItem(listItem)
                }

                override fun lastVisibleItem(lastVisibleItem: Int) {
                    Log.e("", "lastVisibleItem: -------------------------------------->$lastVisibleItem")
//                tvLastItem.setText("With in $lastVisibleItem")
                }
            }
            var categoryPosition = 0
//            search_recycler_view.addOnScrollListener(endlessRecyclerViewScrollListener as EndlessRecyclerViewScrollListener)
//            search_recycler_view.addOnItemClickListener(object : OnItemClickListener {
//                override fun onItemClicked(position: Int, view: View) {
//
//                    categoryPosition = position
//                    // Display the selected/clicked item text and position on TextView
//                    addChild(position)
//                }
//            })


            // Initialize a new instance of RecyclerView Adapter instance


            searchadapter =
                SearchListAdapter(ContentDataList, activity!!, layout_parent!!)
            search_recycler_view.adapter = searchadapter

            val yoyoString: YoYo.YoYoString = YoYo.with(Techniques.Pulse)
                .duration(2000)
                .repeat(5)
                .playOn(view.findViewById(R.id.search_img_view));
//        yoyoString.stop()
            layout_search_result.visibility = View.VISIBLE
            search_img_view!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                }

            })


            search_text_box!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(ch: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(ch: Editable) {

                    try {
                        if (!TextUtils.isEmpty(ch.toString())) {
                            var s = tabName[tabPosition]
                            getSearchListData(s.toUpperCase(), ch.toString())
                        }
                        var s = tabName[tabPosition]
                        if (TextUtils.isEmpty(ch)) {
                            if (s.equals("All")) {
                                val fetch_data_length = 5000
                                Handler().postDelayed({

                                    if (ContentDataList != null) {
                                        ContentDataList.clear()
                                        ContentDataList.addAll(AllContentDataList)
                                        if (searchadapter != null) {
                                            searchadapter!!.notifyDataSetChanged()
                                        }

                                    }
                                }, fetch_data_length.toLong())

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
            getListData()
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return view;
    }
    private fun getListData() {
        if (search_shimmer_view_container != null) {
            search_recycler_view.visibility = View.GONE
            search_shimmer_view_container!!.visibility = View.VISIBLE
            search_shimmer_view_container!!.startShimmerAnimation()
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
            override fun onResponse(call: Call<SearchUserData>, response: Response<SearchUserData>) {
                if (search_shimmer_view_container != null) {

                    search_shimmer_view_container!!.stopShimmerAnimation()
                    search_shimmer_view_container!!.visibility = View.GONE
                    search_recycler_view!!.visibility = View.VISIBLE
                }
                if (activity != null) {
                    if (response.body() != null) {
                        toAllowClickTab = true
                        var res = response.body()
                        if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {

                            if (res.ContentDataList != null && res.ContentDataList.size > 0) {
                                if (txt_no_content != null) {
                                    txt_no_content!!.visibility = View.GONE
                                }
                                if (ContentDataList != null) {
                                    ContentDataList.clear()
                                    searchContentDataList.clear()
                                    AllContentDataList.clear()
                                    AllContentDataList.addAll(res.ContentDataList)
                                    ContentDataList.addAll(AllContentDataList)
                                }
                                if (searchadapter != null && search_recycler_view != null) {
                                    search_recycler_view.visibility = View.VISIBLE
                                    searchadapter!!.notifyDataSetChanged()
                                }
                            } else {
                                if (txt_no_content != null && search_recycler_view != null) {
                                    search_recycler_view.visibility = View.GONE
                                    txt_no_content!!.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            if (txt_no_content != null && search_recycler_view != null) {
                                search_recycler_view.visibility = View.GONE
                                txt_no_content!!.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        if (txt_no_content != null && search_recycler_view != null) {
                            search_recycler_view.visibility = View.GONE
                            txt_no_content!!.visibility = View.VISIBLE
                        }
                    }

                }
            }

            override fun onFailure(call: Call<SearchUserData>, t: Throwable) {
                toAllowClickTab = true
                if (search_shimmer_view_container != null) {
                    search_shimmer_view_container!!.stopShimmerAnimation()
                    search_shimmer_view_container!!.visibility = View.GONE
                }
                if (txt_no_content != null && search_recycler_view != null) {
                    search_recycler_view.visibility = View.GONE
                    txt_no_content!!.visibility = View.VISIBLE
                }
                if (layout_parent != null) {
                    CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
                }
            }
        })
    }
    private fun getSearchListData(ContentType: String, SearchText: String) {
        txt_no_content!!.visibility = View.GONE
        if (search_shimmer_view_container != null) {
            search_recycler_view.visibility = View.GONE
            search_shimmer_view_container!!.visibility = View.VISIBLE
            search_shimmer_view_container!!.startShimmerAnimation()
        }
        val input: SearchReq = SearchReq()
        input.ContentType = ContentType
        input.SearchText = SearchText
        input.StatusCode = "ACTV"

        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.SearchTimeline(input)
        call.enqueue(object : Callback<SearchUserData> {
            override fun onResponse(call: Call<SearchUserData>, response: Response<SearchUserData>) {
                if (search_shimmer_view_container != null) {
                    search_shimmer_view_container!!.stopShimmerAnimation()
                    search_shimmer_view_container!!.visibility = View.GONE
                    search_recycler_view.visibility = View.VISIBLE
                }
                if (activity != null) {
                    if (response.body() != null) {
                        var res = response.body()
                        if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                            Log.d(
                                "Searchtext",
                                "Searchtext - " + SearchText + "  -   Res Size - " + res.ContentDataList.size
                            )
                            /*Searchtext - test m  -   Res Size - 3
                        Searchtext - test me  -   Res Size - 3
                        Searchtext - tes  -   Res Size - 17
                        Searchtext - te  -   Res Size - 18
                        Searchtext - t  -   Res Size - 29
                        Searchtext - test  -   Res Size - 17*/
                            if (search_text_box != null && SearchText.equals(search_text_box!!.text.toString().trim())) {
                                if (res.ContentDataList != null && res.ContentDataList.size > 0) {
                                    txt_no_content!!.visibility = View.GONE
                                    ContentDataList.clear()
                                    searchContentDataList.clear()
                                    ContentDataList.addAll(res.ContentDataList)
                                    searchContentDataList.addAll(res.ContentDataList)
                                    searchadapter!!.notifyDataSetChanged()
                                } else {
                                    if (txt_no_content != null && search_recycler_view != null) {
                                        search_recycler_view.visibility = View.GONE
                                        txt_no_content!!.visibility = View.VISIBLE
                                    }
                                }
                            }
                        } else {
                            if (txt_no_content != null && search_recycler_view != null) {
                                search_recycler_view.visibility = View.GONE
                                txt_no_content!!.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        if (txt_no_content != null && search_recycler_view != null) {
                            search_recycler_view.visibility = View.GONE
                            txt_no_content!!.visibility = View.VISIBLE
                        }
                    }

                } else {
                    if (txt_no_content != null && search_recycler_view != null) {
                        search_recycler_view.visibility = View.GONE
                        txt_no_content!!.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<SearchUserData>, t: Throwable) {
                if (txt_no_content != null && search_recycler_view != null) {
                    search_recycler_view.visibility = View.GONE
                    txt_no_content!!.visibility = View.VISIBLE
                }
                if (search_shimmer_view_container != null) {
                    search_shimmer_view_container!!.stopShimmerAnimation()
                    search_shimmer_view_container!!.visibility = View.GONE
                    search_recycler_view.visibility = View.VISIBLE
                }

            }
        })
    }

    val items: ArrayList<TimelineList> = ArrayList()
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view?.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view?.setOnClickListener({
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                })
            }


        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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


    fun addChild(position: Int) {
        arrayList.add(position)
//        adapter.notifyDataSetChanged()
        val child = layoutInflater.inflate(R.layout.category_list, null)
        val txt = child.findViewById<TextView>(R.id.txt_category)
        val close_view = child.findViewById<ImageView>(R.id.close_view)
        close_view.setTag(child)
        val jParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        jParams.setMargins(pxTodp(10f), 0, pxTodp(10f), 0)
        close_view.setOnClickListener(View.OnClickListener {
            arrayList.remove(position)
            val t = close_view.getTag() as View;
            layout_selected_category.removeView(t)
//            adapter.notifyDataSetChanged()
        })

        txt.setText(txt.text.toString() + " " + position)
        layout_selected_category.addView(child, jParams)
    }


    // TODO: Rename method, update argument and hook method into UI eventLoaderConstant
    //LoaderController
    //LoaderImageView
    //LoaderTextView
    //LoaderView
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


    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.GONE
            }
//            if((activity as NavigationActivity).relative_tool_bar != null) {
//                (activity as NavigationActivity).relative_tool_bar.visibility = View.VISIBLE
//            }
        }
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
            SearchFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}