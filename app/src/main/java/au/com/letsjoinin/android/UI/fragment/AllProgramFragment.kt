package au.com.letsjoinin.android.UI.fragment

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveVideoView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.sackcentury.shinebuttonlib.ShineButton
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tyrantgit.explosionfield.ExplosionField
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

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
class AllProgramFragment : Fragment() {
    private var mPrefMgr: PreferenceManager? = null
    private lateinit var toolbar: Toolbar
    private var jsonStr: String? = null
    private var ContentType: String? = null
    var isFavourite = false
    private var jsonStrBlocks: String? = null
    private var programListData_ID: String? = null
    private val itemList: ArrayList<SignUpCategoryList> = ArrayList()
    val timer = Timer("schedule", true);
    internal var layoutBottomSheet: LinearLayout? = null
    internal var all_program_parent: CoordinatorLayout? = null
    internal var layout_txt_channel_name: LinearLayout? = null
    internal var tv_channel_name: TextView? = null
    internal var tv_program_desc: TextView? = null
    lateinit var layout_up_down: LinearLayout
    lateinit var program_banner_imageView: ImageView
    lateinit var program_banner_brightcove_video_view: BrightcoveVideoView
    lateinit var block_gridView: GridView
    private var mExplosionField: ExplosionField? = null
    var layout_group: LinearLayout? = null;
    fun tab5(): IntArray {
        return intArrayOf(
            R.string.demo_tab_1,
            R.string.demo_tab_2,
            R.string.demo_tab_3,
            R.string.demo_tab_4,
            R.string.demo_tab_5

        )
    }

    private var param1: String? = null
    var height: Int = 0
    var width: Int = 0
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var mShimmerViewContainer1: ShimmerFrameLayout? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer.cancel()
        }
//        if (mShimmerViewContainer != null)
//            mShimmerViewContainer!!.stopShimmerAnimation()

        if (mShimmerViewContainer1 != null)
            mShimmerViewContainer1!!.stopShimmerAnimation()
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.VISIBLE
            }
//            if ((activity as NavigationActivity).relative_tool_bar != null) {
//                (activity as NavigationActivity).relative_tool_bar.visibility = View.VISIBLE
//            }
        }
        if (mShimmerViewContainer1 != null)
            mShimmerViewContainer1!!.startShimmerAnimation()
        if (!TextUtils.isEmpty(jsonStr)) {
            Thread({
                if (!TextUtils.isEmpty(ContentType)) {
                    if (ContentType.equals("video")) {
                        programListData_ID = "5d2d93a6d7f2ac176031819b"

                    } else {
                        programListData_ID = "5d2d91b7d7f2ac176031819a"
                    }
                }
                val result =
                    URL("https://letsjoinin.blob.core.windows.net/lji/LJI20Contents/B-" + programListData_ID + ".json").readText()

//video 5d2d93a6d7f2ac176031819b
                //image 5d2d91b7d7f2ac176031819a
                activity!!.runOnUiThread({

                    val g = Gson()
                    var blockListResponse: BlockListResponse? =
                        g.fromJson(result.toString(), BlockListResponse::class.java)
                    jsonStrBlocks = result.toString()
                    block_gridView.visibility = View.VISIBLE
                    if (mShimmerViewContainer1 != null) {
                        mShimmerViewContainer1!!.stopShimmerAnimation()
                    }
                    val adapter =
                        BlockAdapter((activity as AppCompatActivity), blockListResponse!!.Blocks, height, this)
                    block_gridView.adapter = adapter

                    if (blockListResponse != null) {
                        program_banner_brightcove_video_view.visibility = View.GONE
                        program_banner_imageView.visibility = View.VISIBLE
                        if (!TextUtils.isEmpty(ContentType)) {
                            if (blockListResponse.ContentData!!.ContentType.equals("VIDEO")) {
                                program_banner_brightcove_video_view.visibility = View.VISIBLE
                                program_banner_imageView.visibility = View.GONE
                                val eventEmitter = program_banner_brightcove_video_view.eventEmitter
                                val catalog = Catalog(
                                    eventEmitter,
                                    "5854923465001",
                                    "BCpkADawqM32a1trLarQBq5VBf4IAV3MxzyPdh0arBbwudK1xspJpHvVSM6Uxkqgprq55mHZCGm6-4tN_sdgIbd3Pf_aDYN7LXjpNNhw-o95zFs0bXRH-pgUrv2w2lb7TLCuzT9oAfo_yRaw"
                                )
                                catalog.findVideoByID(
                                    blockListResponse.ContentData!!.MediaPath.toString(),
                                    object : VideoListener() {

                                        override fun onVideo(video: Video) {
                                            program_banner_brightcove_video_view.add(video)
//                            program_banner_brightcove_video_view.start()
                                        }
                                    })

                            } else {
                                if (!TextUtils.isEmpty(blockListResponse.ContentData!!.MediaPath)) {
                                    Picasso.with(context)
                                        .load(blockListResponse.ContentData!!.MediaPath)
                                        .error(R.drawable.image_placeholder_1)
                                        .into(program_banner_imageView)
                                }
                            }
                        }
                    }
                })
            }).start()
        }

    }

    override fun onPause() {
        super.onPause()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_all_program, container, false)

        try {
            mPrefMgr = PreferenceManager.getInstance();
            val args = arguments
            jsonStr = args!!.getString("ProgramDataID")
            ContentType = args!!.getString("ContentType")
            val g = Gson()
            programListData_ID = jsonStr
            toolbar = v.findViewById(R.id.toolbar) as Toolbar
            var layout_top_content_desc: LinearLayout = v.findViewById(R.id.layout_top_content_desc)

            all_program_parent = v.findViewById(R.id.all_program_parent)
            val lay_item_share_imageview: ImageView = v.findViewById(R.id.imageViewshare)
            val item_favourite: ShineButton = v.findViewById(R.id.item_favourite)
            val layout_share_program: RelativeLayout = v.findViewById(R.id.layout_share_program)
            val layout_fav_program: RelativeLayout = v.findViewById(R.id.layout_fav_program)
            layout_group = v.findViewById(R.id.layout_group)
            tv_program_desc = v.findViewById(R.id.tv_program_desc)
            tv_channel_name = v.findViewById(R.id.tv_channel_name)
            layout_txt_channel_name = v.findViewById(R.id.layout_txt_channel_name)

            layout_up_down = v.findViewById(R.id.layout_up_down)
            program_banner_imageView = v.findViewById(R.id.program_banner_imageView)
            program_banner_brightcove_video_view = v.findViewById(R.id.program_banner_brightcove_video_view)
            mExplosionField = ExplosionField.attach2Window(activity)
            mShimmerViewContainer1 = v.findViewById(R.id.shimmer_view_container1)
            val img_arrow: ImageView = v.findViewById(R.id.img_arrow)
            layoutBottomSheet = v.findViewById(R.id.bottom_sheet)
            var bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            block_gridView = layoutBottomSheet!!.findViewById(R.id.block_gridView) as GridView
            block_gridView!!.visibility = View.VISIBLE

            var bsheet = 0;
            val dp = pxTodp(10f)
            img_arrow.setPadding(dp, dp, dp, dp)
            img_arrow.setImageDrawable(
                ContextCompat.getDrawable(
                    (activity as AppCompatActivity),
                    R.drawable.shape_down
                )
            )
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    Log.d("StateChanged", "" + newState)
                    when (newState) {

                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {

                            val dp = pxTodp(10f)
                            img_arrow.setPadding(dp, dp, dp, dp)
                            img_arrow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    (activity as AppCompatActivity),
                                    R.drawable.shape_down
                                )
                            )
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            val dp = pxTodp(10f)
                            img_arrow.setPadding(dp, dp, dp, dp)
                            img_arrow.setImageDrawable(
                                ContextCompat.getDrawable(
                                    (activity as AppCompatActivity),
                                    R.drawable.shape_up
                                )
                            )
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            if (bsheet == 0) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                            } else {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
            val vto: ViewTreeObserver = layout_group!!.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        layout_group!!.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        layout_group!!.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    width = layout_group!!.width - pxTodp(60f)
                    height = width / 3

                    var bSheetHeight = all_program_parent!!.height - layout_top_content_desc.height - pxTodp(230f)
                    Log.d("bSheetHeight", "layout_top_content_desc - " + layout_top_content_desc.height)
                    Log.d("bSheetHeight", "all_program_parent - " + all_program_parent!!.height)
                    Log.d("bSheetHeight", "bSheetHeight - " + bSheetHeight)
                    Log.d("bSheetHeight", "==========================")
                    var pHeight = bSheetHeight - pxTodp(50f)
                    var dp = pxTodp(60f)
                    if (pHeight > dp) {
                        bottomSheetBehavior.peekHeight = pHeight
                    } else {
                        bottomSheetBehavior.peekHeight = dp
                    }
                    val adapter = LoadingAdapter((activity as AppCompatActivity), height)
                    block_gridView.adapter = adapter
                }

            });
            var toScrollAppBar: Boolean = false
            var appBarLayout: AppBarLayout = v.findViewById(R.id.appBarLayout)
            val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = AppBarLayout.Behavior()
            behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return toScrollAppBar
                }
            })
            params.behavior = behavior
            this.layout_up_down.setOnClickListener(View.OnClickListener {


                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    toScrollAppBar = true
                    bsheet = 1
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    val layoutParams = block_gridView.getLayoutParams()
                    layoutParams.height = pxTodp(200f) //this is in pixels
                    block_gridView.setLayoutParams(layoutParams)
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    toScrollAppBar = false
                    val layoutParams = block_gridView.getLayoutParams()
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    block_gridView.setLayoutParams(layoutParams)
                    bsheet = 0
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            })
            item_favourite.setOnClickListener(View.OnClickListener {


                if (isFavourite) {
                    var input: SetFavReqClass = SetFavReqClass()
                    var FavUserRefData: FavUserRef = FavUserRef()
                    FavUserRefData.AvatarPath = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                    FavUserRefData.Name =
                        mPrefMgr!!.getString(AppConstant.FirstName, AppConstant.EMPTY) + " " + mPrefMgr!!.getString(
                            AppConstant.LastName,
                            AppConstant.EMPTY
                        )
                    FavUserRefData.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                    FavUserRefData.SetFavouriteOn = null
                    input.ContentID = "9251616b-c2bf-42b5-b929-d74ca20b04d0"
                    input.PKContentType = "IMAGE"
                    input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                    input.FavouriteBy = FavUserRefData
                    SetFavourite(input)
                } else {
                    var input: RemoveFavReqClass = RemoveFavReqClass()
                    input.ContentID = "9251616b-c2bf-42b5-b929-d74ca20b04d0"
                    input.PKContentType = "IMAGE"
                    input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                    input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                    RemoveFavourite(input)
                }
//                item_favourite.setChecked(true,true)

            })

            val ProgramDescription = args!!.getString("ProgramDescription")
            val ProgramContentType = args!!.getString("ProgramContentType")
            var ProgramTitle = args!!.getString("ProgramTitle")
            var ProgramChannelName = ""
            var ProgramChannelId = ""
            var ProgramPKCountry = ""
            tv_channel_name!!.text = ProgramTitle
            toolbar.title = ProgramTitle
            toolbar.setTitleTextColor(Color.parseColor("#ef5823"))
            tv_program_desc!!.text = ProgramDescription


            if (!ProgramContentType.equals("TOPIC")) {
                ProgramChannelName = args!!.getString("ProgramChannelName")!!
                ProgramChannelId = args!!.getString("ProgramChannelId")!!
                ProgramPKCountry = args!!.getString("ProgramPKCountry")!!
                tv_channel_name!!.text = ProgramChannelName + " >"
                tv_channel_name!!.setOnClickListener(View.OnClickListener {
                    val fragment = ChannelFragment()
                    if (fragmentManager != null) {
                        val transaction = fragmentManager!!.beginTransaction()
                        transaction.replace(R.id.container_fragment, fragment)
                        transaction.addToBackStack(null)
                        val args = Bundle()
                        args.putString(
                            "ChannelID",
                            ProgramChannelId
                        )
                        args.putString(
                            "ProgramPKCountry",
                            ProgramPKCountry
                        )
                        fragment.setArguments(args)
                        transaction.commit()
                    }
                })
            }












            layout_share_program.setOnClickListener(View.OnClickListener {
                YoYo.with(Techniques.Wave)
                    .duration(1300)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(AccelerateDecelerateInterpolator())
                    .withListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }

                    })
                    .playOn(lay_item_share_imageview)

            })


        } catch (e: Exception) {
            e.printStackTrace()
            if (all_program_parent != null) {
                CommonMethods.SnackBar(all_program_parent, activity!!.getString(R.string.error), false)
            }
        }




        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    private fun SetFavourite(input: SetFavReqClass) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.SetFavourite(input)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                val res = response.body()
                if (activity != null) {
                    if (res != null) {
                        if (res.Status.equals(AppConstant.SUCCESS)) {
                            isFavourite = true
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {

            }
        })
    }

    private fun RemoveFavourite(input: RemoveFavReqClass) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.RemoveFavourite(input)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                if (activity != null) {
                    isFavourite = false
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {

            }
        })
    }

    private fun postLoadData(count: Int) {
        val total = count / 9
        val blocks = total * 9
        var remaining = count - blocks
        for (i in 0..total) {

            val signList: SignUpCategoryList =
                SignUpCategoryList()
            if (i < total) {
                signList.categoryPosition = 9
            } else {
                signList.categoryPosition = remaining
            }
            itemList.add(signList)
        }
        block_gridView.visibility = View.VISIBLE
//        val adapter = GroupSingleBlockAdapter((activity as AppCompatActivity), itemList, height, this)
//        block_gridView.adapter = adapter
        var rowWidthLayout: LinearLayout = LinearLayout(activity);

        if (mShimmerViewContainer1 != null) {
            mShimmerViewContainer1!!.stopShimmerAnimation()
        }
    }

    private fun addListener(root: View, position: Int) {
        root.isClickable = true
        root.setOnClickListener { v ->

            mExplosionField!!.explode(v)
            v.setOnClickListener(null)
            timer.schedule(600) {

                Handler(Looper.getMainLooper()).post(Runnable {
                    mExplosionField!!.clear()
                    val intent = Intent(activity, au.com.letsjoinin.android.UI.activity.ChatActivity::class.java)
                    if (!TextUtils.isEmpty(jsonStrBlocks)) {
                        intent.putExtra("ProgramData", jsonStrBlocks)
                        intent.putExtra("BlockPosition", position)
                    }
                    startActivity(intent)
                })
            }

        }
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

    class BlockAdapter(
        context: Context,
        arrayList: ArrayList<ArrayList<BlocksClass>>,
        h: Int,
        frag: AllProgramFragment
    ) :
        BaseAdapter() {
        var context = context
        var arrayList = arrayList
        var height = h
        var fragment = frag


        public var categoryPosition = 0
        //Auto Generated Method
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myView = convertView
            var holder: ViewHolder



            if (myView == null) {

                //If our View is Null than we Inflater view using Layout Inflater

                val mInflater = (context as Activity).layoutInflater

                //Inflating our grid_item.
                myView = mInflater.inflate(R.layout.block_grid_item, parent, false)

                //Create Object of ViewHolder Class and set our View to it

                holder = ViewHolder()


                //Find view By Id For all our Widget taken in grid_item.

                /*Here !! are use for non-null asserted two prevent From null.
                 you can also use Only Safe (?.)

                */


                holder.parent = myView!!.findViewById<LinearLayout>(R.id.parent) as LinearLayout
                holder.G1 = myView!!.findViewById<TextView>(R.id.g_1) as TextView
                holder.G2 = myView!!.findViewById<TextView>(R.id.g_2) as TextView
                holder.G3 = myView!!.findViewById<TextView>(R.id.g_3) as TextView
                holder.G4 = myView!!.findViewById<TextView>(R.id.g_4) as TextView
                holder.G5 = myView!!.findViewById<TextView>(R.id.g_5) as TextView
                holder.G6 = myView!!.findViewById<TextView>(R.id.g_6) as TextView
                holder.G7 = myView!!.findViewById<TextView>(R.id.g_7) as TextView
                holder.G8 = myView!!.findViewById<TextView>(R.id.g_8) as TextView
                holder.G9 = myView!!.findViewById<TextView>(R.id.g_9) as TextView


                //Set A Tag to Identify our view.
                myView.setTag(holder)
                fragment.addListener(holder.parent!!, position)

            } else {

                //If Our View in not Null than Just get View using Tag and pass to holder Object.

                holder = myView.getTag() as ViewHolder

            }


//            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
//            );
            holder.parent!!.layoutParams.height = height


            val block: ArrayList<BlocksClass> = arrayList!!.get(position)

            val categoryPosition = block.size;
            if (categoryPosition == 9) {
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
                blockColor(block.get(4).Color!!, holder.G5!!)
                blockColor(block.get(5).Color!!, holder.G6!!)
                blockColor(block.get(7).Color!!, holder.G8!!)
                blockColor(block.get(8).Color!!, holder.G9!!)
            }
            if (categoryPosition == 1) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G6!!.visibility = View.INVISIBLE
                holder.G5!!.visibility = View.INVISIBLE
                holder.G4!!.visibility = View.INVISIBLE
                holder.G3!!.visibility = View.INVISIBLE
                holder.G2!!.visibility = View.INVISIBLE

                blockColor(block.get(0).Color!!, holder.G1!!)
            }
            if (categoryPosition == 2) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G7!!.visibility = View.INVISIBLE
                holder.G6!!.visibility = View.INVISIBLE
                holder.G5!!.visibility = View.INVISIBLE
                holder.G4!!.visibility = View.INVISIBLE
                holder.G3!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
            }

            if (categoryPosition == 3) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G7!!.visibility = View.INVISIBLE
                holder.G6!!.visibility = View.INVISIBLE
                holder.G5!!.visibility = View.INVISIBLE
                holder.G4!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
            }

            if (categoryPosition == 4) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G7!!.visibility = View.INVISIBLE
                holder.G6!!.visibility = View.INVISIBLE
                holder.G5!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
            }
            if (categoryPosition == 5) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G7!!.visibility = View.INVISIBLE
                holder.G6!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
                blockColor(block.get(4).Color!!, holder.G5!!)
            }

            if (categoryPosition == 6) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                holder.G7!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
                blockColor(block.get(4).Color!!, holder.G5!!)
                blockColor(block.get(5).Color!!, holder.G6!!)
            }


            if (categoryPosition == 7) {
                holder.G9!!.visibility = View.INVISIBLE
                holder.G8!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
                blockColor(block.get(4).Color!!, holder.G5!!)
                blockColor(block.get(5).Color!!, holder.G6!!)
                blockColor(block.get(6).Color!!, holder.G7!!)
            }
            if (categoryPosition == 8) {
                holder.G9!!.visibility = View.INVISIBLE
                blockColor(block.get(0).Color!!, holder.G1!!)
                blockColor(block.get(1).Color!!, holder.G2!!)
                blockColor(block.get(2).Color!!, holder.G3!!)
                blockColor(block.get(3).Color!!, holder.G4!!)
                blockColor(block.get(4).Color!!, holder.G5!!)
                blockColor(block.get(5).Color!!, holder.G6!!)
                blockColor(block.get(7).Color!!, holder.G8!!)
            }

            return myView

        }

        //Auto Generated Method
        override fun getItem(p0: Int): Any {
            return arrayList.get(p0)
        }

        fun blockColor(color: String, v: TextView) {
            val sdk = android.os.Build.VERSION.SDK_INT;

            if (color.equals("AMBER")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_yellow
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_yellow
                        )
                    );
                }

            } else if (color.equals("RED")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_red
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_red
                        )
                    );
                }

            } else if (color.equals("GREEN")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_green
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.block_green
                        )
                    );
                }

            }
        }

        //Auto Generated Method
        override fun getItemId(p0: Int): Long {
            return 0
        }

        //Auto Generated Method
        override fun getCount(): Int {

            return arrayList.size
//            return block
        }


        //Create A class To hold over View like we taken in grid_item.xml

        class ViewHolder {

            var parent: LinearLayout? = null
            var G1: TextView? = null
            var G2: TextView? = null
            var G3: TextView? = null
            var G4: TextView? = null
            var G5: TextView? = null
            var G6: TextView? = null
            var G7: TextView? = null
            var G8: TextView? = null
            var G9: TextView? = null

        }

    }


    class LoadingAdapter(context: Context, h: Int) : BaseAdapter() {
        var context = context
        var height = h


        public var categoryPosition = 0
        //Auto Generated Method
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myView = convertView
            var holder: ViewHolder


            if (myView == null) {
                val mInflater = (context as Activity).layoutInflater
                myView = mInflater.inflate(R.layout.loading_grid_item, parent, false)
                holder = ViewHolder()
                holder.parent = myView!!.findViewById<LinearLayout>(R.id.parent) as LinearLayout
                holder.G1 = myView!!.findViewById<TextView>(R.id.g_1) as TextView
                holder.G2 = myView!!.findViewById<TextView>(R.id.g_2) as TextView
                holder.G3 = myView!!.findViewById<TextView>(R.id.g_3) as TextView
                holder.G4 = myView!!.findViewById<TextView>(R.id.g_4) as TextView
                holder.G5 = myView!!.findViewById<TextView>(R.id.g_5) as TextView
                holder.G6 = myView!!.findViewById<TextView>(R.id.g_6) as TextView
                holder.G7 = myView!!.findViewById<TextView>(R.id.g_7) as TextView
                holder.G8 = myView!!.findViewById<TextView>(R.id.g_8) as TextView
                holder.G9 = myView!!.findViewById<TextView>(R.id.g_9) as TextView

                myView.setTag(holder)

            } else {

                holder = myView.getTag() as ViewHolder
            }
            val random = Random().nextInt(3)
            holder.parent!!.layoutParams.height = height
            return myView

        }

        //Auto Generated Method
        override fun getItem(p0: Int): Any {
            return 0
        }

        //Auto Generated Method
        override fun getItemId(p0: Int): Long {
            return 0
        }

        //Auto Generated Method
        override fun getCount(): Int {

            return 12
//            return block
        }


        //Create A class To hold over View like we taken in grid_item.xml

        class ViewHolder {

            var parent: LinearLayout? = null
            var G1: TextView? = null
            var G2: TextView? = null
            var G3: TextView? = null
            var G4: TextView? = null
            var G5: TextView? = null
            var G6: TextView? = null
            var G7: TextView? = null
            var G8: TextView? = null
            var G9: TextView? = null
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
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


//        if (layout_group != null) {
//            layout_group!!.removeAllViews()
//            var viewID = 0;
//            for (i in 0..count) {
//                var rowLinear: LinearLayout = LinearLayout(activity);
//                var iParams: LinearLayout.LayoutParams =
//                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
//                for (j in 0..2) {
//                    var blockLinear: LinearLayout = LinearLayout(activity);
//                    blockLinear.background =
//                        ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.edittext_round_border)
//                    blockLinear.orientation = LinearLayout.VERTICAL
//                    val jParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
//                    jParams.weight = 1f
//                    jParams.setMargins(pxTodp(2f), pxTodp(2f), pxTodp(2f), pxTodp(2f))
//
//                    for (k in 0..2) {
//                        var groupofRelative: LinearLayout = LinearLayout(activity);
//                        val kParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
//                        kParams.weight = 1f
//                        groupofRelative.orientation = LinearLayout.HORIZONTAL
//                        if( k == 2)
//                        {
//                        }
//                        for (l in 0..2) {
//                            var group: LinearLayout = LinearLayout(activity);
//                            val lParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
//                            lParams.weight = 1f
//                            lParams.setMargins(pxTodp(10f), pxTodp(10f), pxTodp(10f), pxTodp(10f))
//                            group.orientation = LinearLayout.HORIZONTAL
//                            val random = Random().nextInt(3)

//                            val sdk = android.os.Build.VERSION.SDK_INT;
//                            if (random == 0) {
//                                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                                    group.setBackgroundDrawable(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_red
//                                        )
//                                    );
//                                } else {
//                                    group.setBackground(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_red
//                                        )
//                                    );
//                                }
//
//                            }
//                            if (random == 1) {
//                                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                                    group.setBackgroundDrawable(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_yellow
//                                        )
//                                    );
//                                } else {
//                                    group.setBackground(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_yellow
//                                        )
//                                    );
//                                }
//                            }
//                            if (random == 2) {
//                                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                                    group.setBackgroundDrawable(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_green
//                                        )
//                                    );
//                                } else {
//                                    group.setBackground(
//                                        ContextCompat.getDrawable(
//                                            activity as AppCompatActivity,
//                                            R.drawable.block_green
//                                        )
//                                    );
//                                }
//                            }
//
//
//
//                            groupofRelative.addView(group, lParams)
//                        }
//
//
//                        //Explosion
//                        addListener(blockLinear)
//
//                        blockLinear.addView(groupofRelative, kParams)
//                    }
//                    rowLinear.addView(blockLinear, jParams)
////                    if (j == 2) {
////                        rowWidthLayout = blockLinear
////                    }
//                }
