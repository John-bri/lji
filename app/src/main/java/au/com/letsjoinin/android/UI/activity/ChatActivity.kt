package au.com.letsjoinin.android.UI.activity

import android.Manifest
import android.animation.Animator
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.adapter.ChatAdapter
import au.com.letsjoinin.android.UI.adapter.PageTransformer
import au.com.letsjoinin.android.UI.view.DynamicHeightImageView
import au.com.letsjoinin.android.UI.view.TopSnappedStickyLayoutManager
import au.com.letsjoinin.android.exoplayer.MainActivity
import au.com.letsjoinin.android.utils.*
import com.brandongogetap.stickyheaders.exposed.StickyHeader
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.google.gson.Gson
import com.sackcentury.shinebuttonlib.ShineButton
import com.squareup.picasso.*
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiPopup
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_chat.*
import net.alexandroid.utils.exoplayerhelper.ExoAdListener
import net.alexandroid.utils.exoplayerhelper.ExoPlayerHelper
import net.alexandroid.utils.exoplayerhelper.ExoPlayerListener
import net.alexandroid.utils.exoplayerhelper.ExoThumbListener
import okhttp3.OkHttpClient
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener,
    ExoPlayerListener, ExoAdListener, ExoThumbListener {
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var UserID: String? = null
    val SAMPLE_1 = "http://cdn-fms.rbs.com.br/vod/hls_sample1_manifest.m3u8"
    val SAMPLE_2 = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
    val SAMPLE_3 =
        " http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8"
    val SAMPLE_4 = "http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8"
    val SAMPLE_5 = "http://www.streambox.fr/playlists/test_001/stream.m3u8"
    val SAMPLE_6 = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8"
    val SAMPLE_7 = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8"
    val SAMPLE_8 = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"

    val SUBTITLE = "http://www.storiesinflight.com/js_videosub/jellies.srt"

    //    public static final String TEST_TAG_URL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    val TEST_TAG_URL =
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator="

    val THUMB_IMG_URL =
        "https://i0.wp.com/androidlibs.net/alexandroid/wp-content/uploads/2013/11/ava.jpg"


    var mPrefMgr = PreferenceManager.getInstance();
    private var blockListResponse: BlockListResponse? = null
    //    private var jsonStr: String? = null
    private var FirebaseGroupId: String? = null
    private var FirebaseId: String? = null
    private var base64String: String? = null
    //    private var blockPosition: String? = "0"
    public lateinit var img_back_arrow: ImageView
    public lateinit var play_exo_player_ad: ImageView
    public lateinit var img_back_arrow_to_chat: ImageView
    public lateinit var chat_banner_imageView: DynamicHeightImageView
    public lateinit var badge_star: ImageView
    public lateinit var img_info: ImageView
    public lateinit var reply_img: ImageView
    private var areaOfInterestRatio = 0.65f
    var bsheet = 1;
    private var layout: LinearLayout? = null
    private var landscape: Boolean = false
    private var isReceivedAll: Boolean = false
    private var isReplyClicked: Boolean = false
    private var screen_ratio = 0.5
    val GroupIds = ArrayList<BlocksClass>()
    var ContentGroups: ArrayList<GetContentGroupList>? = java.util.ArrayList()
    var BlockGroups: ArrayList<ArrayList<GetContentGroupList>>? = java.util.ArrayList()
    private val MAX_AREA_OF_INTEREST_RATIO = 0.6
    private val MIN_AREA_OF_INTEREST_RATIO = 0.1
    private var resultsLayoutParams: LinearLayout.LayoutParams? = null
    private var moveButtonTouchListener: MoveButtonTouchListener? = null
    private var emptyLayout: LinearLayout? = null
    private var emptyLayoutParams: LinearLayout.LayoutParams? = null
    private var resultsLayout: LinearLayout? = null
    private var layout_up_down: LinearLayout? = null
    private var chat_layout_info: LinearLayout? = null
    private var layout_chat_text_box: LinearLayout? = null
    private var layout_drag: RelativeLayout? = null
    private var attach_layout: RelativeLayout? = null
    private var progress_lyt: RelativeLayout? = null
    private var lay_back_button: RelativeLayout? = null
    private var rel_click_to_participate: RelativeLayout? = null
    private var layout_main_reply_view: RelativeLayout? = null
    private var textview_main_user_name: TextView? = null
    private var textview_main_reply_message: TextView? = null
    private var close_reply_view_button: Button? = null
    private var layout_no_comments: RelativeLayout? = null
    internal var emojiPopup: EmojiPopup? = null
    internal var exoPlayerView: PlayerView? = null
    internal var exoPlayerViewAD: PlayerView? = null
    var height: Int = 0
    var width: Int = 0
    var firebaseChatData: ArrayList<FirebaseChatData>? = ArrayList()
    var replyChatData: FirebaseChatData = FirebaseChatData()
    var firebaseChatDataID: ArrayList<String>? = ArrayList()
    var firebaseIDToPost: ArrayList<String>? = ArrayList() // to post data if chat group changed
    val firebaseChatDataMap: HashMap<String, FirebaseChatData?> =
        HashMap<String, FirebaseChatData?>()
    internal lateinit var editText: EmojiEditText
    internal lateinit var emojiButton: ImageView
    internal lateinit var img_play_video: ImageView
    internal lateinit var img_previous_chat: ImageView
    internal lateinit var img_next_chat: ImageView
    internal lateinit var return_to_block: TextView
    internal lateinit var chat_tv_topic_name: TextView
    internal lateinit var chat_txt_group_id: TextView
    internal lateinit var chat_tv_topic_desc: TextView
    internal lateinit var txt_channel_name: TextView
    internal lateinit var txt_edit_topic: LinearLayout
    internal lateinit var layout_share: LinearLayout
    internal lateinit var layout_fav: LinearLayout
    internal lateinit var item_favourite: ShineButton
    internal lateinit var layout_follow: LinearLayout
    internal lateinit var rootView: ViewGroup
    internal var layoutBottomSheet: LinearLayout? = null
    //    internal lateinit var emojiCompat: EmojiCompat? = null
    var mShimmerViewContainer: ShimmerFrameLayout? = null;
    var chat_list_view: RecyclerView? = null;
    var chatAdapter: ChatAdapter? = null;
    var blockShimmerViewContainer: ShimmerFrameLayout? = null;
    lateinit var chat_block_gridView: GridView
    var chatPosition = 1
    public lateinit var tv_check_connection: TextView
    public lateinit var network_change_img: ImageView
    public lateinit var check_connection: RelativeLayout
    private var database: FirebaseDatabase? = null
    private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null
    private var mMessageListener: ChildEventListener? = null
    internal var pastVisiblesItems: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    internal var isLoading = false
    internal var allowSwipe = false
    internal var isDataFetchDone = false
    private var programListData_ID: String? = null
    var PKChannelId: String? = null
    var GroupName: String? = "G1"
    var FromDeepLink = false
    private var ContentType: String? = null
    private var NotificationItemFireBaseID: String? = null
    var isFavourite = false
    private var networkStateReceiver: NetworkStateReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        try {
            networkStateReceiver = NetworkStateReceiver()
            registerNetworkBroadcastForNougat()
            checkPermissions()
            Cache.NONE.clear()
            myCountDownTimer = MyCountDownTimer(this, this@ChatActivity, 1000 * 60 * 10, 1000)

            tv_check_connection = findViewById(R.id.tv_check_connection)
            network_change_img = findViewById(R.id.network_change_img)
            img_play_video = findViewById(R.id.img_play_video)
            play_exo_player_ad = findViewById(R.id.play_exo_player_ad)
            play_exo_player_ad.bringToFront()
            check_connection = findViewById(R.id.check_connection)
            check_connection.bringToFront()
            Clear.clearCache(Picasso.with(this))
            UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            val FromPushNotification =
                mPrefMgr!!.getBoolean(AppConstant.FromPushNotification, false)
            mPrefMgr.setBoolean(AppConstant.FromPushNotification, false)
            mShimmerViewContainer = findViewById(R.id.chat_shimmer_view_container)
            chat_list_view = findViewById(R.id.chat_list_view)
            blockShimmerViewContainer = findViewById(R.id.chat_block_shimmer_view_container)
            attach_layout = findViewById(R.id.attach_layout)
            attach_layout!!.bringToFront()
            chat_list_view!!.layoutManager = LinearLayoutManager(
                this,
                RecyclerView.VERTICAL, false
            )
            chatAdapter = ChatAdapter(
                firebaseChatData,
                this@ChatActivity
            )
//            chat_list_view!!.adapter = adapter
            val mLayoutManager = TopSnappedStickyLayoutManager(this, chatAdapter!!)
            mLayoutManager.elevateHeaders(true)
//            val mLayoutManager = LinearLayoutManager(this)
//            mLayoutManager.reverseLayout = true

            chat_list_view!!.setLayoutManager(mLayoutManager)
            chat_list_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    visibleItemCount = mLayoutManager.getChildCount()
                    totalItemCount = mLayoutManager.getItemCount()
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                    if (!recyclerView.canScrollVertically(-1)) {
                        if (!isLoading) {
                            isLoading = true
//                            progressRelativeLayout.setVisibility(View.VISIBLE)
//                            refreshItems()
                        }
                        Log.v("check for scroll", "onScrolledToTop ")
                    } else if (!recyclerView.canScrollVertically(1)) {
                        Log.v("check for scroll", "onScrolledToBottom")
                    } else if (dy < 0) {
                        Log.v("check for scroll", "onScrolledUp")
                    } else if (dy > 0) {
                        Log.v("check for scroll", "onScrolledDown")
                    }
                    Log.v(
                        "checkTopItem",
                        "FirstCompletely " + mLayoutManager.findFirstCompletelyVisibleItemPosition()
                    )
                    Log.v(
                        "checkTopItem",
                        "FirstVisible " + mLayoutManager.findFirstVisibleItemPosition()
                    )

                    Log.v("checkTopItem", "========= ")
                    //                    visibleItemCount = mLayoutManager.getChildCount();
                    //                    totalItemCount = mLayoutManager.getItemCount();
                    //                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                }
            })
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true) SUCCESS
            database = FirebaseDatabase.getInstance();
            layoutBottomSheet = findViewById(R.id.bottom_sheet)
            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED);

            img_back_arrow_to_chat =
                layoutBottomSheet!!.findViewById(R.id.img_back_arrow_to_chat) as ImageView
            return_to_block = layoutBottomSheet!!.findViewById(R.id.return_to_block) as TextView
            lay_back_button =
                layoutBottomSheet!!.findViewById(R.id.lay_back_button) as RelativeLayout
            layout_up_down = layoutBottomSheet!!.findViewById(R.id.layout_up_down) as LinearLayout
            chat_block_gridView =
                layoutBottomSheet!!.findViewById(R.id.chat_block_gridView) as GridView
            chat_block_gridView!!.visibility = View.VISIBLE
//        recycler_view_name.setHasFixedSize(true)

            this.layout_up_down!!.setOnClickListener(View.OnClickListener {

                bsheet = 1
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            })
            FromDeepLink = intent?.getBooleanExtra("FromDLink", false)!!

            GroupName = intent?.getStringExtra("IGroupName").toString()
            programListData_ID = intent?.getStringExtra("ProgramDataID").toString()
            PKChannelId = intent?.getStringExtra("PKChannelId").toString()
            FirebaseId = programListData_ID
            val EditTopicContentID = intent?.getStringExtra("ProgramDataID").toString()
            ContentType = intent?.getStringExtra("ContentType").toString()
            NotificationItemFireBaseID =
                intent?.getStringExtra("NotificationItemFireBaseID").toString()




            img_back_arrow = findViewById<ImageView>(R.id.profile_img_arrow)
            img_previous_chat = findViewById<ImageView>(R.id.img_previous_chat)
            img_next_chat = findViewById<ImageView>(R.id.img_next_chat)
            chat_banner_imageView = findViewById<DynamicHeightImageView>(R.id.chat_banner_imageView)
            badge_star = findViewById<ImageView>(R.id.img_badge)
            chat_tv_topic_desc = findViewById<TextView>(R.id.chat_tv_topic_desc)
            txt_edit_topic = findViewById<LinearLayout>(R.id.txt_edit_topic)
            layout_share = findViewById<LinearLayout>(R.id.layout_share)
            layout_fav = findViewById<LinearLayout>(R.id.layout_fav)
            item_favourite = findViewById<ShineButton>(R.id.item_favourite)
            layout_follow = findViewById<LinearLayout>(R.id.layout_follow)
            chat_tv_topic_name = findViewById<TextView>(R.id.chat_tv_topic_name)
            txt_channel_name = findViewById<TextView>(R.id.txt_channel_name)
            chat_txt_group_id = findViewById<TextView>(R.id.chat_txt_group_id)
            textview_main_user_name = findViewById<TextView>(R.id.textview_main_user_name)
            textview_main_reply_message = findViewById<TextView>(R.id.textview_main_reply_message)
            close_reply_view_button = findViewById(R.id.close_reply_view_button)
            img_info = findViewById<ImageView>(R.id.img_info)
            reply_img = findViewById<ImageView>(R.id.reply_img)
            rootView = findViewById(R.id.main_activity_root_view)
            img_back_arrow.bringToFront()
            img_info.bringToFront()
            this.layout_chat_text_box = findViewById(R.id.layout_chat_text_box) as LinearLayout
            this.emptyLayout = findViewById(R.id.empty_layout) as LinearLayout
            this.chat_layout_info = findViewById(R.id.chat_layout_info) as LinearLayout
            this.layout = findViewById(R.id.sizable_panel) as LinearLayout

            resultsLayout = findViewById(R.id.results_layout) as LinearLayout
            val chat_group_name: RelativeLayout = findViewById(R.id.chat_group_name)
            layout_drag = findViewById(R.id.layout_drag) as RelativeLayout
            progress_lyt = findViewById(R.id.progress_lyt) as RelativeLayout
            rel_click_to_participate = findViewById(R.id.rel_click_to_participate) as RelativeLayout
            layout_main_reply_view = findViewById(R.id.layout_main_reply_view) as RelativeLayout
            layout_no_comments = findViewById(R.id.layout_no_comments) as RelativeLayout
            exoPlayerView = findViewById<PlayerView>(R.id.exoPlayerView)
            exoPlayerViewAD = findViewById<PlayerView>(R.id.exoPlayerView_ad)
            editText = findViewById(R.id.main_activity_chat_bottom_message_edittext)
            emojiButton = findViewById(R.id.main_activity_emoji)
            emojiButton.setOnClickListener { ignore ->
                val handler = Handler()
                handler.postDelayed({
                    emojiPopup!!.toggle()
                }, 100)
//
            }

            layout_follow.visibility = View.GONE
            layout_fav.visibility = View.VISIBLE
            item_favourite.setOnClickListener(View.OnClickListener {
                if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
                    if (!isFavourite) {
                        isFavourite = true
                        var input: SetFavReqClass = SetFavReqClass()
                        var FavUserRefData: FavUserRef = FavUserRef()
                        FavUserRefData.AvatarPath =
                            mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
                        FavUserRefData.Name =
                            mPrefMgr!!.getString(
                                AppConstant.FirstName,
                                AppConstant.EMPTY
                            ) + " " + mPrefMgr!!.getString(
                                AppConstant.LastName,
                                AppConstant.EMPTY
                            )
                        FavUserRefData.UserID =
                            mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                        FavUserRefData.SetFavouriteOn = null
                        input.ContentID = EditTopicContentID
                        input.PKContentType = ContentType
                        input.PKUserCountry =
                            mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                        input.FavouriteBy = FavUserRefData
                        SetFavourite(input)
                    } else {
                        isFavourite = false
                        var input: RemoveFavReqClass = RemoveFavReqClass()
                        input.ContentID = EditTopicContentID
                        input.PKContentType = ContentType
                        input.PKUserCountry =
                            mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                        input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                        RemoveFavourite(input)
                    }
                } else {
                    if (layout != null) {
                        CommonMethods.SnackBar(layout, getString(R.string.network_error), false)
                    }
                }


            })

            var input: GetContentByIDReq = GetContentByIDReq()
            input.ContentID = programListData_ID
            input.PKContentType = ContentType
            GetContentByID(input)



            chat_group_name.setOnClickListener(View.OnClickListener {
                //                bsheet = 0
                if (isDataFetchDone) {
                    bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    layout_up_down!!.visibility = View.GONE
                    lay_back_button!!.visibility = View.VISIBLE
                    val adapter =
                        GroupSingleBlockAdapter(
                            this@ChatActivity,
                            ContentGroups!!,
                            height,
                            this@ChatActivity
                        )
                    chat_block_gridView.adapter = adapter
                }
            })
            play_exo_player_ad.setOnClickListener(View.OnClickListener {
                mExoPlayerHelperAD!!.playerPlay()
                play_exo_player_ad!!.visibility = View.GONE
                adPlaying = true
                exoPlayerView!!.visibility = View.GONE
            })
            mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, false)
            txt_edit_topic.setOnClickListener(View.OnClickListener {
                if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
                    mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, true)
                    mPrefMgr!!.setString(AppConstant.EditTopicPKContentType, ContentType)
                    mPrefMgr!!.setString(AppConstant.EditTopicContentID, EditTopicContentID)
                    finish()
                } else {
                    if (layout != null) {
                        CommonMethods.SnackBar(layout, getString(R.string.network_error), false)
                    }
                }


            })

            layout_share.setOnClickListener(View.OnClickListener {
                if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
                    GetShareDetails("test")
                } else {
                    if (layout != null) {
                        CommonMethods.SnackBar(layout, getString(R.string.network_error), false)
                    }
                }

            })

            txt_time.visibility = View.GONE
            lay_waiting_content.visibility = View.VISIBLE
            img_tick.visibility = View.GONE
            progress_1.visibility = View.GONE
            rel_click_to_participate!!.setOnClickListener(View.OnClickListener {
                allowSwipe = true
                rel_click_to_participate!!.visibility = View.GONE
                layout_chat_text_box!!.visibility = View.VISIBLE
                emojiButton!!.visibility = View.VISIBLE
                if (myCountDownTimer != null) {
                    myCountDownTimer!!.cancel()
                }
                if (myCountDownTimer != null) {
                    Log.w("GroupChatActivity", "MyCountDownTimer onTick")
                    myCountDownTimer!!.start()
                }

            })

            lay_waiting_content.setOnClickListener(View.OnClickListener {

                progress_1.visibility = View.VISIBLE
                val SPLASH_DISPLAY_LENGTH = 7000
                Handler().postDelayed({
                    lay_waiting_content.visibility = View.GONE
                    img_tick.visibility = View.VISIBLE
                    progress_1.visibility = View.GONE

                }, SPLASH_DISPLAY_LENGTH.toLong())


            })

            close_reply_view_button!!.setOnClickListener(View.OnClickListener {
                layout_main_reply_view!!.visibility = View.GONE
                textview_main_user_name!!.text = ""
                textview_main_reply_message!!.text = ""
                isReplyClicked = false;
            })
            return_to_block.setOnClickListener(View.OnClickListener {

                bsheet = 1
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED


                BlockGroups!!.add(ContentGroups!!)

                for (i in 0..ContentGroups!!.size - 1) {

                }
                val SPLASH_DISPLAY_LENGTH = 600
                Handler().postDelayed({

                    //                    bsheet = 0
                    bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    layout_up_down!!.visibility = View.VISIBLE
                    lay_back_button!!.visibility = View.GONE



                    val groupadapter =
                        BlockAdapter(
                            this@ChatActivity,
                            BlockGroups!!,

                            height,
                            this@ChatActivity
                        )
                    chat_block_gridView.adapter = groupadapter

                }, SPLASH_DISPLAY_LENGTH.toLong())

            })
            img_back_arrow_to_chat.setOnClickListener(View.OnClickListener {
                bsheet = 1
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            })

            progress_lyt!!.bringToFront()
//            layout_drag!!.visibility = View.GONE
//            empty_layout!!.visibility = View.GONE
//            img_info!!.visibility = View.GONE

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                    if (charSequence.toString().trim { it <= ' ' }.length > 0) { //                    mSendButton.setEnabled(true);
                    } else { //                    mSendButton.setEnabled(false);
                    }
                }

                override fun afterTextChanged(editable: Editable) {
//                    if (myCountDownTimer != null) {
//                        myCountDownTimer!!.cancel()
//                    }
//                    if (myCountDownTimer != null) {
//                        Log.w("GroupChatActivity", "MyCountDownTimer onTick")
//                        myCountDownTimer!!.start()
//                    }
                }
            })


            img_back_arrow.setOnClickListener(View.OnClickListener {
                if (myCountDownTimer != null) {
                    myCountDownTimer!!.cancel()
                }
                finish()
            })
            img_send.setOnClickListener(View.OnClickListener {
                if (myCountDownTimer != null) {
                    myCountDownTimer!!.cancel()
                }
                if (myCountDownTimer != null) {
                    Log.w("GroupChatActivity", "MyCountDownTimer onTick")
                    myCountDownTimer!!.start()
                }
                if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {

                    if (!TextUtils.isEmpty(editText.text.toString().trim())) {
                        firebaseIDToPost!!.add(GroupName!!)
                        val msg = editText.text.toString().trim()
                        editText.setText("")
                        ValidateChatContent(msg)
                    } else {
                        if (layout != null) {
//                            CommonMethods.SnackBar(layout, "Enter your thoughts", false)
                            CommonMethods.SnackBar(layout, "Type your message...", false)
                        }
                    }
                }


            })
            (findViewById(R.id.gallery) as LinearLayout).setOnClickListener {
                attach_layout!!.setVisibility(View.GONE)
                try {
                    galleryImageIntent()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            attach_layout!!.setOnClickListener {
                attach_layout!!.setVisibility(View.GONE)
            }
            (findViewById(R.id.camera) as LinearLayout).setOnClickListener {
                attach_layout!!.setVisibility(View.GONE)
                try {
                    TakePictureIntent()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            img_attach.setOnClickListener(View.OnClickListener {

                if (attach_layout!!.visibility == View.VISIBLE) {
                    attach_layout!!.setVisibility(View.GONE)
                } else {
                    attach_layout!!.setVisibility(View.VISIBLE)
                }
            })
            badge_star.setOnClickListener(View.OnClickListener {
                val gson = Gson()
                val json = gson.toJson(ContentGroups)
                val manager =
                    PreferenceManager.getInstance()
                manager.setString("ContentGroupsList", json)
                manager.setString("FirebaseIdChat", FirebaseId)
                val intent = Intent(this, TopOpinionsActivity::class.java)
                startActivity(intent)
            })

            val metrics = resources.displayMetrics
            chat_layout_info!!.visibility = View.VISIBLE
            chat_layout_info!!.setTag(0)
            YoYo.with(Techniques.SlideOutDown)
                .duration(300)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(AccelerateDecelerateInterpolator())
                .withListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        chat_layout_info!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
                .playOn(chat_layout_info!!)
            img_info.setOnClickListener {
                if (chat_layout_info!!.getTag() == 0) {
                    YoYo.with(Techniques.SlideInUp)
                        .duration(300)
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
                                chat_layout_info!!.visibility = View.VISIBLE
                            }

                        })
                        .playOn(chat_layout_info!!)
                    img_info.setColorFilter(ContextCompat.getColor(this, R.color.md_yellow_800))
                    chat_layout_info!!.setTag(1)

                } else {
                    YoYo.with(Techniques.SlideOutDown)
                        .duration(300)
                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                        .interpolate(AccelerateDecelerateInterpolator())
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                chat_layout_info!!.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {

                            }

                        })
                        .playOn(chat_layout_info!!)
                    img_info.setColorFilter(ContextCompat.getColor(this, R.color.md_blue_900))
                    chat_layout_info!!.setTag(0)
                }
            }
            img_info.setColorFilter(ContextCompat.getColor(this, R.color.md_blue_900))

            try {

                GetContentGroups()
                Thread({
//                    if (!TextUtils.isEmpty(ContentType)) {
//                        if (ContentType.equals("video")) {
//                            programListData_ID = "5d2d93a6d7f2ac176031819b"
//
//                        } else {
//                            programListData_ID = "5d2d91b7d7f2ac176031819a"
//                        }
//                    }
//                    val result =
//                        URL("https://letsjoinin.blob.core.windows.net/lji/LJI20Contents/B-" + programListData_ID + ".json").readText()

//video 5d2d93a6d7f2ac176031819b
                    //image 5d2d91b7d7f2ac176031819a
                    runOnUiThread({

//                        val g = Gson()
//                        blockListResponse =
//                            g.fromJson(result.toString(), BlockListResponse::class.java)
//                        if (mShimmerViewContainer != null) {
//                            mShimmerViewContainer!!.stopShimmerAnimation()
//                        }


//                        if (blockListResponse != null && blockListResponse!!.ContentData != null) {
//
//                            if (blockListResponse!!.Blocks != null) {
//                                GroupIds.clear()
//                                GroupIds.addAll(blockListResponse!!.Blocks.get(0))
//                            }
//
//                            img_previous_chat.setColorFilter(
//                                ContextCompat.getColor(this@ChatActivity, R.color.darkgry),
//                                android.graphics.PorterDuff.Mode.MULTIPLY
//                            );

//
//
//                        }

                        this.emptyLayoutParams =
                            emptyLayout!!.getLayoutParams() as LinearLayout.LayoutParams
                        this.resultsLayoutParams =
                            resultsLayout!!.getLayoutParams() as LinearLayout.LayoutParams
                        bottomSheetBehavior!!.setBottomSheetCallback(object :
                            BottomSheetBehavior.BottomSheetCallback() {
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
                                                this@ChatActivity,
                                                R.drawable.shape_down
                                            )
                                        )
                                    }
                                    BottomSheetBehavior.STATE_COLLAPSED -> {
                                        val dp = pxTodp(10f)
                                        img_arrow.setPadding(dp, dp, dp, dp)
                                        img_arrow.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                this@ChatActivity,
                                                R.drawable.shape_up
                                            )
                                        )
                                    }
                                    BottomSheetBehavior.STATE_DRAGGING -> {
                                        if (bsheet == 0) {
                                            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);

                                        } else {
                                            bottomSheetBehavior!!.state =
                                                BottomSheetBehavior.STATE_COLLAPSED
                                        }
                                    }
                                    BottomSheetBehavior.STATE_SETTLING -> {
                                    }
                                }
                            }

                            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                            }
                        })
                        val vto: ViewTreeObserver = rootView!!.getViewTreeObserver();
                        vto.addOnGlobalLayoutListener(object :
                            ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {

                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    rootView!!.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                                } else {
                                    rootView!!.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                                }
                                width = rootView!!.width - pxTodp(50f)
                                height = width / 3
//                            var bSheetHeight = all_program_parent!!.height  - pxTodp(230f)
//
//                            var pHeight = bSheetHeight - pxTodp(50f)
//                            var dp = pxTodp(60f)
//                            if (pHeight > dp) {
//                                bottomSheetBehavior!!.peekHeight = pHeight
//                            } else {
//                                bottomSheetBehavior!!.peekHeight = dp
//                            }

                            }

                        });

                        setUpEmojiPopup()
                        setAreaOfInterestRatio(0.37)
                    })
                }).start()
                this.moveButtonTouchListener = MoveButtonTouchListener()
                if (layout_drag != null)
                    this.layout_drag!!.setOnTouchListener(this.moveButtonTouchListener)
            } catch (e: Exception) {
                if (layout != null) {
                    CommonMethods.SnackBar(layout, getString(R.string.error), false)
                }
            }
        } catch (e: Exception) {
            if (layout != null) {
                CommonMethods.SnackBar(layout, getString(R.string.error), false)
            }
        }


    }

    var myCountDownTimer: MyCountDownTimer? = null

    class MyCountDownTimer(
        activ: ChatActivity,
        con: Context?,
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {
        val chatAct: ChatActivity = activ!!
        val act: Context = con!!
        var GroupName: String? = null
        var Chat_Group_ID: String? = null
        var ProgramDataID: String? = null
        var ContentType: String? = null
        var ChatID: String? = null
        override fun onTick(millisUntilFinished: Long) {
            val progress = (millisUntilFinished / 1000).toInt()
            Log.w(
                "MyCountDownTimer",
                "MyCountDownTimer - $progress"
            )
            val minute = millisUntilFinished / 60000.toFloat()
            if (progress <= 380) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / (1000 * 60) % 60)
                chatAct.txt_time.text = minutes.toString() + ":" + seconds.toString()
            }
            if (progress == 380) {


                chatAct.txt_time.text = minute.toString()
                chatAct.txt_time.visibility = View.VISIBLE
                chatAct.lay_waiting_content.visibility = View.GONE
                chatAct.img_tick.visibility = View.GONE
                chatAct.progress_1.visibility = View.GONE
                chatAct.rel_click_to_participate!!.visibility = View.VISIBLE
                chatAct.layout_chat_text_box!!.visibility = View.GONE
                chatAct.emojiButton!!.visibility = View.GONE


                val icon = R.mipmap.lji_launcher
                val `when` = System.currentTimeMillis()
                val notificationManager =
                    act.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //                    Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notification_mp3);
                    val attributes =
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
                    val mChannel = NotificationChannel(
                        "default",
                        "my_channel_01",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    //                    mChannel.setSound(sound, attributes); // This is IMPORTANT
                    mChannel.enableLights(true)
                    mChannel.lightColor = Color.RED
                    mChannel.enableVibration(true)
                    mChannel.importance = NotificationManager.IMPORTANCE_HIGH
                    mChannel.vibrationPattern = longArrayOf(
                        100,
                        200,
                        300,
                        400,
                        500,
                        400,
                        300,
                        200,
                        400
                    )
                    notificationManager.createNotificationChannel(mChannel)
                }
                val notificationIntent = Intent(
                    act,
                    SplashActivity::class.java
                )
                notificationIntent.putExtra("ProgramDataID", ProgramDataID)
                notificationIntent.putExtra("ContentType", ContentType)
                notificationIntent.putExtra("GroupName", Chat_Group_ID)
                notificationIntent.putExtra("FromPNIdle", true)
//                notificationIntent.putExtra(PNIdleTime, true)
//                notificationIntent.putExtra(PNGroupID, GroupChatFragment.Chat_Group_ID)
//                notificationIntent.putExtra(FromPN, true)
                //                mPrefsMgr.setLong(PNUserGroupForSK,UserGroupForSK);
//                mPrefsMgr.setString(PNUserGroupFor,UserGroupFor);
//                mPrefsMgr.setString(PNGroupName,GroupName);
//                mPrefsMgr.setString(PNimagePath, imagePath);
//                mPrefsMgr.setString(PNIdleTime, "IdleTime");
//                mPrefsMgr.setString(PNGroupID, GroupChatFragment.Chat_Group_ID);
                val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
                val pendingIntent =
                    PendingIntent.getActivity(
                        act,
                        uniqueInt,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                val notification: NotificationCompat.Builder =
                    NotificationCompat.Builder(act, "default")
                        .setContentTitle("Time Out")
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText("Since, you have been idle from Participating in " + GroupName!! + " - " + Chat_Group_ID!! + " group, you will be made an Observer automatically after 10 minutes.")
                        )
                        .setContentText("Since, you have been idle from Participating in " + GroupName!! + " - " + Chat_Group_ID!! + " group, you will be made an Observer automatically after 10 minutes.")
                        .setSmallIcon(icon)
                        .setWhen(`when`)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setContentIntent(pendingIntent)
                        .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                notificationManager.notify(1, notification.build())
            }
        }

        override fun onFinish() {


//            try {
//
//                progress_lyt!!.visibility = View.VISIBLE
//                var retrofit: Retrofit = Retrofit.Builder()
//                    .baseUrl(AppConstant.BaseURL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//                var service: ApiService = retrofit.create(ApiService::class.java)
//                var mPrefMgr = PreferenceManager.getInstance();
//                val call = service.GetContentByID(input!!)
//                Log.d("ChatContentID", "ContentID - " + input!!.ContentID)
//                call.enqueue(object : Callback<GetContentByIdRes> {
//                    override fun onResponse(
//                        call: Call<GetContentByIdRes>,
//                        response: Response<GetContentByIdRes>
//                    ) {
//
//                    }
//
//                    override fun onFailure(call: Call<GetContentByIdRes>, t: Throwable) {
//                        progress_lyt!!.visibility = View.GONE
//                    }
//                })
//            } catch (e: Exception) {
//                e.printStackTrace()
//                progress_lyt!!.visibility = View.GONE
//            }
        }
    }


    override fun networkAvailable() {
        if (tv_check_connection != null) {

            tv_check_connection.setText("Network connectivity resumed!")
            network_change_img.setImageResource(R.drawable.ic_success)
            val handler = Handler()
            val delayrunnable = Runnable {
                check_connection.setVisibility(View.GONE)
            }
            check_connection!!.getBackground()
                .setColorFilter(Color.parseColor("#63D532"), PorterDuff.Mode.SRC_ATOP);
            handler.postDelayed(delayrunnable, 2000)
        }
    }

    override fun networkUnavailable() {
        if (tv_check_connection != null) {

            check_connection.setVisibility(View.VISIBLE)
            network_change_img.setImageResource(R.drawable.ic_warning)
            tv_check_connection.setText("Network connectivity lost!")
            check_connection!!.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(
            networkStateReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    protected fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(networkStateReceiver)
        } catch (e: IllegalArgumentException) {
            // e.printStackTrace();
        }

    }

    private fun SetFavourite(input: SetFavReqClass) {
        progress_lyt!!.visibility = View.VISIBLE
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.SetFavourite(input)
        call.enqueue(object : retrofit2.Callback<ServerMessageData> {
            override fun onResponse(
                call: Call<ServerMessageData>,
                response: Response<ServerMessageData>
            ) {
                val res = response.body()
                progress_lyt!!.visibility = View.GONE
                if (res != null) {
                    CommonMethods.SnackBar(layout, res.DisplayMsg, false)
                    if (res.Status.equals(AppConstant.SUCCESS)) {


                    }
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                progress_lyt!!.visibility = View.GONE
            }
        })
    }

    private fun RemoveFavourite(input: RemoveFavReqClass) {
        progress_lyt!!.visibility = View.VISIBLE
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.RemoveFavourite(input)
        call.enqueue(object : retrofit2.Callback<ServerMessageData> {
            override fun onResponse(
                call: Call<ServerMessageData>,
                response: Response<ServerMessageData>
            ) {
                val res = response.body()
                progress_lyt!!.visibility = View.GONE
                CommonMethods.SnackBar(layout, res.DisplayMsg, false)
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                progress_lyt!!.visibility = View.GONE

            }
        })
    }

    private var mExoPlayerHelper: ExoPlayerHelper? = null
    private var mExoPlayerHelperAD: ExoPlayerHelper? = null
    private var adPlaying: Boolean? = false
    private fun GetContentByID(input: GetContentByIDReq) {
        try {

            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.GetContentByID(input!!)
            Log.d("ChatContentID", "ContentID - " + input!!.ContentID)
            call.enqueue(object : Callback<GetContentByIdRes> {
                override fun onResponse(
                    call: Call<GetContentByIdRes>,
                    response: Response<GetContentByIdRes>
                ) {
                    val res = response.body()
                    progress_lyt!!.visibility = View.GONE
                    if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(
                            AppConstant.SUCCESS
                        )
                    ) {
                        val Title = res.ContentData!!.Title
                        val Description = res.ContentData!!.Description
                        val MediaPath = res.ContentData!!.MediaPath
                        val CreatedById = res.ContentData!!.CreatedBy.UserID
                        isFavourite = false

                        if (res.ContentData!!.FavouritesBy != null) {
                            if (res.ContentData!!.FavouritesBy.containsKey(
                                    mPrefMgr!!.getString(
                                        AppConstant.USERID,
                                        AppConstant.EMPTY
                                    )
                                )
                            ) {
                                item_favourite.isChecked = true
                                isFavourite = true

                            } else {
                                item_favourite.isChecked = false

                            }
                        } else {
                            item_favourite.isChecked = false
                        }

                        myCountDownTimer!!.GroupName = Title
                        myCountDownTimer!!.ProgramDataID = FirebaseId
                        myCountDownTimer!!.ContentType = res.ContentData!!.ContentType
                        chat_tv_topic_name.text = Title
                        chat_tv_topic_desc.text = Description
                        txt_edit_topic.visibility = View.INVISIBLE
                        if (!TextUtils.isEmpty(CreatedById)) {
                            if (CreatedById.equals(UserID)) {
                                txt_edit_topic.visibility = View.VISIBLE
                            }
                        } else {
                            txt_edit_topic.visibility = View.INVISIBLE
                        }
                        img_play_video.visibility = View.GONE
                        if (ContentType.equals("VIDEO", true) || ContentType.equals(
                                "NEWS",
                                true
                            ) || ContentType.equals("LIVE", true)
                        ) {


                            img_play_video.visibility = View.GONE

                            var videoPath: String =
                                mPrefMgr.getString(AppConstant.AdVideoPath, null)
                            if (videoPath == null || TextUtils.isEmpty(videoPath) || videoPath.equals(
                                    AppConstant.EMPTY
                                )
                            ) {
                                videoPath = SAMPLE_8
                            }

//                            emptyLayoutParams - 0.6945981
//                            resultsLayoutParams - 0.30540195
//                                .setTagUrl(videoPath)
                            mExoPlayerHelperAD =
                                ExoPlayerHelper.Builder(this@ChatActivity, exoPlayerViewAD)
                                    .setVideoUrls(videoPath)
                                    .setSubTitlesUrls(
                                        java.util.ArrayList(
                                            Arrays.asList(
                                                MainActivity.SUBTITLE
                                            )
                                        )
                                    )
                                    .setRepeatModeOn(false)
                                    .setAutoPlayOn(false)
                                    .setUiControllersVisibility(false)
                                    .addProgressBarWithColor(resources.getColor(R.color.colorAccent))
                                    .setExoPlayerEventsListener(this@ChatActivity)
                                    .setExoAdEventsListener(this@ChatActivity)
                                    .setThumbImageViewEnabled(this@ChatActivity)
                                    .createAndPrepare()
                            exoPlayerView!!.visibility = View.GONE
                            play_exo_player_ad!!.visibility = View.VISIBLE
                            mExoPlayerHelper =
                                ExoPlayerHelper.Builder(this@ChatActivity, exoPlayerView)
                                    .setVideoUrls(SAMPLE_8)
                                    .setSubTitlesUrls(
                                        java.util.ArrayList(
                                            Arrays.asList(
                                                MainActivity.SUBTITLE
                                            )
                                        )
                                    )
                                    .setRepeatModeOn(false)
                                    .setAutoPlayOn(false)
                                    .enableLiveStreamSupport()
                                    .setUiControllersVisibility(true)
                                    .addProgressBarWithColor(resources.getColor(R.color.colorAccent))
                                    .setFullScreenBtnVisible()
                                    .addMuteButton(false, false)
                                    .setMuteBtnVisible()
                                    .setExoPlayerEventsListener(this@ChatActivity)
                                    .setExoAdEventsListener(this@ChatActivity)
                                    .setThumbImageViewEnabled(this@ChatActivity)
                                    .createAndPrepare()

                        } else {
                            play_exo_player_ad!!.visibility = View.GONE
                            exoPlayerView!!.visibility = View.GONE
                            exoPlayerViewAD!!.visibility = View.GONE
                            chat_banner_imageView.visibility = View.VISIBLE
                            Picasso.with(this@ChatActivity)
                                .load(MediaPath)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.image_placeholder_1)
                                .into(chat_banner_imageView)
                        }
                    }
                }

                override fun onFailure(call: Call<GetContentByIdRes>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress_lyt!!.visibility = View.GONE
        }
    }
    private fun JoinGroup(input: GetContentGroupList,pos : Int) {
        try {
//            fetchIndvidualGroupData(pos)
            val req : JoinGroupsReq = JoinGroupsReq()
            req.ContentID = programListData_ID
            req.GroupID = input.Key
            req.PKChannelID = PKChannelId

//            req.ContentID ="16ded8a7-5de8-4791-9240-3263c00ca826"
//            req.GroupID =  "64cae7fa-1e30-487b-a9dc-9ce54f4b6747"
//            req.PKChannelID =  "c8b5cace-c7a9-4462-a76a-02cd9bfba0c2"
            val joiningUser : JoiningUserDataReq = JoiningUserDataReq()

            joiningUser.AvatarPath = mPrefMgr.getString(AppConstant.AvatarPath,AppConstant.EMPTY)
            joiningUser.Name = mPrefMgr.getString(AppConstant.UserName,AppConstant.EMPTY)
            joiningUser.PKCountry = mPrefMgr.getString(AppConstant.Country,AppConstant.EMPTY)
            joiningUser.UserID = mPrefMgr.getString(AppConstant.USERID,AppConstant.EMPTY)
//            joiningUser.AvatarPath =  "https://letsjoinin.blob.core.windows.net/lji/Users/3130bb32-a85f-4e1b-b9e2-6e6b0d831f24.png"
//            joiningUser.Name ="Jack 50"
//            joiningUser.PKCountry = "AUS"
//            joiningUser.UserID = "64cae7fa-1e30-487b-a9dc-9ce54f4b6751"
            req.JoiningUserData = joiningUser

            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.JoinGroup(req!!)
            call.enqueue(object : Callback<JoinGroupsResponse> {
                override fun onResponse(
                    call: Call<JoinGroupsResponse>,
                    response: Response<JoinGroupsResponse>
                ) {
                    val res = response.body()
                    fetchIndvidualGroupData(pos)
                    progress_lyt!!.visibility = View.GONE
                    if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(
                            AppConstant.SUCCESS
                        )
                    ) {

                    }
                }

                override fun onFailure(call: Call<JoinGroupsResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress_lyt!!.visibility = View.GONE
        }
    }

    private fun GetContentGroups() {
        try {

            val input: GetContentGroupsReq = GetContentGroupsReq()
            input.ContentID = programListData_ID
            input.PKChannelID = PKChannelId


//            input.ContentID = "16ded8a7-5de8-4791-9240-3263c00ca826"
//            input.PKChannelID = "64cae7fa-1e30-487b-a9dc-9ce54f4b6747"
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.GetContentGroups(input!!)
            Log.d("ChatContentID", "ContentID - " + input!!.ContentID)
            call.enqueue(object : Callback<GetContentGroupRes> {
                override fun onResponse(
                    call: Call<GetContentGroupRes>,
                    response: Response<GetContentGroupRes>
                ) {
                    val res = response.body()
                    progress_lyt!!.visibility = View.GONE
                    if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(
                            AppConstant.SUCCESS
                        )
                    ) {

                        if (res != null) {
                            if(res.ContentGroups != null) {
                                if(res.ContentGroups!!.size > 0) {
                                    ContentGroups!!.clear()
                                    ContentGroups = res.ContentGroups!!
                                        isDataFetchDone = true
                                    if (ContentGroups!!.size > 0) {
                                        img_previous_chat.setOnLongClickListener(View.OnLongClickListener {

                                            val popup =
                                                PopupMenu(this@ChatActivity, img_previous_chat)
                                            popup.setOnMenuItemClickListener { ite ->
                                                when (ite.itemId) {
                                                    R.id.next_item ->  // do your code
                                                        true
                                                    R.id.active_item -> {
                                                        // do your code
                                                        true
                                                    }
                                                    R.id.most_active_item -> {
                                                        // do your code
                                                        true
                                                    }
                                                    else -> false
                                                }
                                            }
                                            popup.inflate(R.menu.chat_group_menu)
                                            popup.show()

                                            false })
                                        img_next_chat.setOnLongClickListener(View.OnLongClickListener {

                                            val popup =
                                                PopupMenu(this@ChatActivity, img_next_chat)
                                            popup.setOnMenuItemClickListener { ite ->
                                                when (ite.itemId) {
                                                    R.id.next_item ->  // do your code
                                                        true
                                                    R.id.active_item -> {
                                                        // do your code
                                                        true
                                                    }
                                                    R.id.most_active_item -> {
                                                        // do your code
                                                        true
                                                    }
                                                    else -> false
                                                }
                                            }
                                            popup.inflate(R.menu.chat_group_menu)
                                            popup.show()

                                            false })

                                        img_previous_chat.setOnClickListener(View.OnClickListener {
                                            if (isDataFetchDone) {
                                                isReplyClicked = false;
                                                if (chatPosition > 1) {
                                                    img_previous_chat.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@ChatActivity,
                                                            R.color.white
                                                        ),
                                                        android.graphics.PorterDuff.Mode.MULTIPLY
                                                    );
                                                    firebaseChatData!!.clear()
                                                    firebaseChatDataID!!.clear()
                                                    firebaseChatDataMap!!.clear()
                                                    chatPosition = chatPosition - 1
                                                    if (chatPosition <= ContentGroups!!.size - 1) {
                                                        img_next_chat.setColorFilter(
                                                            ContextCompat.getColor(
                                                                this@ChatActivity,
                                                                R.color.white
                                                            ),
                                                            android.graphics.PorterDuff.Mode.MULTIPLY
                                                        );
                                                    }
                                                    var block: GetContentGroupList = ContentGroups!!.get(chatPosition - 1)
                                                    GroupName = block.Value!!.Name
                                                    chat_txt_group_id.text = block.Value!!.Name
//                                                    if (block.Color.equals("RED")) {
//                                                        allowSwipe = false
//                                                        rel_click_to_participate!!.visibility = View.VISIBLE
//                                                        layout_chat_text_box!!.visibility = View.GONE
//                                                        emojiButton!!.visibility = View.GONE
//                                                    } else {
                                                        allowSwipe = true
                                                        rel_click_to_participate!!.visibility = View.GONE
                                                        layout_chat_text_box!!.visibility = View.VISIBLE
                                                        emojiButton!!.visibility = View.VISIBLE
//                                                    }

                                                    JoinGroup(ContentGroups!!.get(chatPosition),chatPosition)
                                                }

                                                if (chatPosition == 1) {
                                                    img_previous_chat.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@ChatActivity,
                                                            R.color.darkgry
                                                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                                                    );
                                                }

                                            }
                                        })


                                        //============




                                        img_next_chat.setOnClickListener(View.OnClickListener {
                                            if (isDataFetchDone) {
                                                isReplyClicked = false;
                                                if (ContentGroups!!.size > chatPosition) {
                                                    img_next_chat.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@ChatActivity,
                                                            R.color.white
                                                        ),
                                                        android.graphics.PorterDuff.Mode.MULTIPLY
                                                    );
                                                    if (chatPosition == ContentGroups!!.size - 1) {
                                                        img_next_chat.setColorFilter(
                                                            ContextCompat.getColor(
                                                                this@ChatActivity,
                                                                R.color.darkgry
                                                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                                                        );
                                                    }
                                                    chatPosition = chatPosition + 1
                                                    var block: GetContentGroupList = ContentGroups!!.get(chatPosition - 1)
                                                    GroupName = block.Value!!.Name
                                                    chat_txt_group_id.text = block.Value!!.Name
//                                                    if (block.Color.equals("RED")) {
//                                                        allowSwipe = false
//                                                        rel_click_to_participate!!.visibility = View.VISIBLE
//                                                        layout_chat_text_box!!.visibility = View.GONE
//                                                        emojiButton!!.visibility = View.GONE
//                                                    } else {
                                                        allowSwipe = true
                                                        rel_click_to_participate!!.visibility = View.GONE
                                                        emojiButton!!.visibility = View.VISIBLE
                                                        layout_chat_text_box!!.visibility = View.VISIBLE
//                                                    }
                                                    firebaseChatData!!.clear()
                                                    firebaseChatDataID!!.clear()
                                                    firebaseChatDataMap!!.clear()
                                                    JoinGroup(ContentGroups!!.get(chatPosition),chatPosition)
                                                }

                                                //============

                                                if (chatPosition == 1) {
                                                    img_previous_chat.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@ChatActivity,
                                                            R.color.darkgry
                                                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                                                    );
                                                }
                                                if (chatPosition > 1) {
                                                    img_previous_chat.setColorFilter(
                                                        ContextCompat.getColor(
                                                            this@ChatActivity,
                                                            R.color.white
                                                        ),
                                                        android.graphics.PorterDuff.Mode.MULTIPLY
                                                    );
                                                }
                                            }
                                        })

                                        JoinGroup(ContentGroups!!.get(0),0)

//
//
//                                        if (FromDeepLink) {
//                                            FromDeepLink = false
//                                            chat_txt_group_id.text = GroupName
//                                            val s: String = GroupName!!.get(1).toString()
//                                            var pos: Int = (s).toInt()
//                                            if (pos > 0) {
//                                                pos = pos - 1
//                                            }
//                                            var block: BlocksClass = GroupIds.get(pos)
//                                            GroupName = block.Name
//                                            chat_txt_group_id.text = GroupName
//                                            if (block.Color.equals("RED")) {
//                                                allowSwipe = false
//                                                rel_click_to_participate!!.visibility = View.VISIBLE
//                                                layout_chat_text_box!!.visibility = View.GONE
//                                                emojiButton!!.visibility = View.GONE
//                                            } else {
//                                                allowSwipe = true
//                                                rel_click_to_participate!!.visibility = View.GONE
//                                                layout_chat_text_box!!.visibility = View.VISIBLE
//                                                emojiButton!!.visibility = View.VISIBLE
//                                            }
//                                        } else {
//                                            var block: BlocksClass = GroupIds.get(0)
//                                            GroupName = block.Name
//                                            chat_txt_group_id.text = GroupName
//                                            if (block.Color.equals("RED")) {
//                                                allowSwipe = false
//                                                rel_click_to_participate!!.visibility = View.VISIBLE
//                                                layout_chat_text_box!!.visibility = View.GONE
//                                                emojiButton!!.visibility = View.GONE
//                                            } else {
//                                                allowSwipe = true
//                                                rel_click_to_participate!!.visibility = View.GONE
//                                                layout_chat_text_box!!.visibility = View.VISIBLE
//                                                emojiButton!!.visibility = View.VISIBLE
//                                            }
//                                        }
//                                        getFirebaseData()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetContentGroupRes>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            progress_lyt!!.visibility = View.GONE
        }
    }

    private fun getImageAd(str: String) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(str + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.getImageAd("")
        call.enqueue(object : Callback<AdButlerImgResponse> {
            override fun onResponse(
                call: Call<AdButlerImgResponse>,
                response: Response<AdButlerImgResponse>
            ) {

                val res: AdButlerImgResponse = response.body();
                chat_banner_imageView.visibility = View.VISIBLE
//                brightcoveVideoView.visibility = View.GONE
                var placement: Placement? = null

                val placements: java.util.HashMap<String, Placement>? = res!!.placements
                if (res != null && res!!.placements != null) {
                    for ((key, value) in res!!.placements!!) {

                        placement = value
                    }
                }
                if (placement != null) {
//                    Picasso.with(this@ChatActivity)
//                        .load(placement!!.image_url)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                        .placeholder(R.drawable.image_placeholder_1)
//                        .into(chat_banner_imageView)

                    Handler().postDelayed({
                        //                        brightcoveVideoView.visibility = View.VISIBLE
                    }, 10000)
                }

            }

            override fun onFailure(call: Call<AdButlerImgResponse>, t: Throwable) {

            }
        })
    }


    private fun ValidateChatContent(msg: String) {
        try {
            CommonMethods.hideKeyboardFrom(this@ChatActivity, editText)
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.ValidateChatContent(editText.text.toString())
            call.enqueue(object : Callback<ServerMessageData> {
                override fun onResponse(
                    call: Call<ServerMessageData>,
                    response: Response<ServerMessageData>
                ) {
                    val res = response.body()
                    if (res!!.Status.equals(AppConstant.SUCCESS)) {
                        TextChatContent(msg)
                    } else {
                        CommonMethods.SnackBar(layout, res!!.DisplayMsg, false)
                    }
                }

                override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun TextChatContent(CommentText: String) {
        try {
            val strID = firebaseIDToPost!!.get(0)
            val mGroupId: String? =
                FirebaseDatabase.getInstance().reference.child(FirebaseId!!).child(strID).push().key

            firebaseIDToPost!!.removeAt(0)

            val rating: RatingJavaData = RatingJavaData()
            rating.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            rating.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            rating.AvatharPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
            val ratingschildUpdates = ArrayList<RatingJavaData>()
            ratingschildUpdates.add(rating)

            val mZero: Int = 0

            val childUpdates = HashMap<String, Any>()
            childUpdates["/" + mGroupId!! + "/CommentText"] = CommentText
            childUpdates["/" + mGroupId!! + "/PostedOn"] = CommonMethods.getDateCreated()
            childUpdates["/" + mGroupId!! + "/FireBaseID"] = mGroupId
            childUpdates["/" + mGroupId!! + "/ParentCommentID"] = mGroupId
            childUpdates["/" + mGroupId!! + "/CommentType"] = "TXT"
            childUpdates["/" + mGroupId!! + "/RatingSum"] = mZero
            childUpdates["/" + mGroupId!! + "/NoteWorthyClicks"] = mZero
            childUpdates["/" + mGroupId!! + "/OffenseCount"] = mZero
//                childUpdates["/" + mGroupId!! + "/OffenseMarkedBy"] = ratingschildUpdates
            childUpdates["/" + mGroupId!! + "/PostedBy/UserID"] =
                mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            childUpdates["/" + mGroupId!! + "/PostedBy/Name"] =
                mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            childUpdates["/" + mGroupId!! + "/PostedBy/AvatarPath"] =
                mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
//                childUpdates["/" + mGroupId!! + "/RatingBy"] = ratingschildUpdates


            if (isReplyClicked) {
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentText"] =
                    replyChatData!!.CommentText!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedOn"] =
                    replyChatData!!.PostedOn!!
                childUpdates["/" + mGroupId!! + "/ParentComment/FireBaseID"] =
                    replyChatData!!.FireBaseID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/ParentCommentID"] =
                    replyChatData!!.FireBaseID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentType"] =
                    replyChatData!!.CommentType!!
                childUpdates["/" + mGroupId!! + "/ParentComment/RatingSum"] =
                    replyChatData!!.AvgRating!!
                childUpdates["/" + mGroupId!! + "/ParentComment/NoteWorthyClicks"] =
                    replyChatData!!.NoteWorthyClicks!!
                childUpdates["/" + mGroupId!! + "/ParentComment/OffenseCount"] =
                    replyChatData!!.OffenseCount!!
                childUpdates["/" + mGroupId!! + "/ParentComment/Rating"] =
                    replyChatData!!.RatingCount!!

                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/UserID"] =
                    replyChatData!!.PostedBy!!.UserID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/Name"] =
                    replyChatData!!.PostedBy!!.Name!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/AvatarPath"] =
                    replyChatData!!.PostedBy!!.AvatharPath!!
            } else {
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentText"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedOn"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/FireBaseID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/ParentCommentID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentType"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/RatingSum"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/NoteWorthyClicks"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/OffenseCount"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/Rating"] = mZero

                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/UserID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/Name"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/AvatarPath"] = ""
            }
            isReplyClicked = false
            layout_main_reply_view!!.visibility = View.GONE
            textview_main_user_name!!.text = ""
            textview_main_reply_message!!.text = ""
            var mDbase: DatabaseReference? =
                FirebaseDatabase.getInstance()!!.reference.child(FirebaseId!!).child(strID!!)
            mDbase!!.updateChildren(childUpdates)

        } catch (e: Exception) {
        }
    }

    private fun PostImageContent() {
        try {


            val strID = firebaseIDToPost!!.get(0)
            val childUpdates = HashMap<String, Any>()
            val message: FirebaseChatData = FirebaseChatData()
            val mGroupId: String? =
                FirebaseDatabase.getInstance().reference.child(FirebaseId!!).child(strID).push().key
            firebaseIDToPost!!.removeAt(0)
            val rating: RatingJavaData = RatingJavaData()
            rating.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            rating.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            rating.AvatharPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
            val ratingschildUpdates = ArrayList<RatingJavaData>()
            ratingschildUpdates.add(rating)

            val mZero: Int = 0



            childUpdates["/" + mGroupId!! + "/PostedOn"] = CommonMethods.getDateCreated()
            childUpdates["/" + mGroupId!! + "/FireBaseID"] = mGroupId
            childUpdates["/" + mGroupId!! + "/ParentCommentID"] = mGroupId

            childUpdates["/" + mGroupId!! + "/RatingSum"] = mZero
            childUpdates["/" + mGroupId!! + "/NoteWorthyClicks"] = mZero
            childUpdates["/" + mGroupId!! + "/OffenseCount"] = mZero
//                childUpdates["/" + mGroupId!! + "/OffenseMarkedBy"] = ratingschildUpdates
            childUpdates["/" + mGroupId!! + "/PostedBy/UserID"] =
                mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            childUpdates["/" + mGroupId!! + "/PostedBy/Name"] =
                mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            childUpdates["/" + mGroupId!! + "/PostedBy/AvatarPath"] =
                mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
//                childUpdates["/" + mGroupId!! + "/RatingBy"] = ratingschildUpdates


            if (isReplyClicked) {
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentText"] =
                    replyChatData!!.CommentText!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedOn"] =
                    replyChatData!!.PostedOn!!
                childUpdates["/" + mGroupId!! + "/ParentComment/FireBaseID"] =
                    replyChatData!!.FireBaseID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/ParentCommentID"] =
                    replyChatData!!.FireBaseID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentType"] =
                    replyChatData!!.CommentType!!
                childUpdates["/" + mGroupId!! + "/ParentComment/RatingSum"] =
                    replyChatData!!.AvgRating!!
                childUpdates["/" + mGroupId!! + "/ParentComment/NoteWorthyClicks"] =
                    replyChatData!!.NoteWorthyClicks!!
                childUpdates["/" + mGroupId!! + "/ParentComment/OffenseCount"] =
                    replyChatData!!.OffenseCount!!
                childUpdates["/" + mGroupId!! + "/ParentComment/Rating"] =
                    replyChatData!!.RatingCount!!

                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/UserID"] =
                    replyChatData!!.PostedBy!!.UserID!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/Name"] =
                    replyChatData!!.PostedBy!!.Name!!
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/AvatarPath"] =
                    replyChatData!!.PostedBy!!.AvatharPath!!
            } else {
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentText"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedOn"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/FireBaseID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/ParentCommentID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/CommentType"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/RatingSum"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/NoteWorthyClicks"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/OffenseCount"] = mZero
                childUpdates["/" + mGroupId!! + "/ParentComment/Rating"] = mZero

                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/UserID"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/Name"] = ""
                childUpdates["/" + mGroupId!! + "/ParentComment/PostedBy/AvatarPath"] = ""
            }
            isReplyClicked = false
            layout_main_reply_view!!.visibility = View.GONE
            textview_main_user_name!!.text = ""
            textview_main_reply_message!!.text = ""

            message.PostedOn = CommonMethods.getDateCreated()
            message.CommentType = "IMG"
            message.FireBaseID = mGroupId
            message.OffenseCount = 0L
            message.NoteWorthyClicks = 0L
            var PostedBy: PostedByClass? = PostedByClass()
            PostedBy!!.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            PostedBy!!.AvatharPath =
                mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
            PostedBy!!.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            message.PostedBy = PostedBy
//            firebaseChatData!!.add(message!!)
//            firebaseChatDataMap.put(mGroupId!!, message!!)
//
//            chatAdapter!!.setData(firebaseChatData!!)
//            chatAdapter!!.notifyDataSetChanged()
//            chat_list_view!!.scrollToPosition(firebaseChatData!!.size - 1)

            var input: PostImageReq = PostImageReq()
            input.MediaContent = base64String

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            CommonMethods.hideKeyboardFrom(this@ChatActivity, editText)
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.PostImageContent(input)
            call.enqueue(object : Callback<PostImageResponse> {
                override fun onResponse(
                    call: Call<PostImageResponse>,
                    response: Response<PostImageResponse>
                ) {
                    val res = response.body()
                    if (res != null) {
                        if (res!!.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {


                            childUpdates["/" + mGroupId!! + "/CommentText"] =
                                res!!.MediaPath.toString()
                            childUpdates["/" + mGroupId!! + "/CommentType"] = "IMG"
                            message.CommentText = res!!.MediaPath.toString()
                            var mDbase: DatabaseReference? =
                                FirebaseDatabase.getInstance()!!.reference.child(FirebaseId!!)
                                    .child(strID!!)
                            mDbase!!.updateChildren(childUpdates)
                            editText.setText("")
//                            firebaseChatData!!.remove(message!!)
                        } else {
                            CommonMethods.SnackBar(
                                layout,
                                res!!.ServerMessage!!.DisplayMsg,
                                false
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<PostImageResponse>, t: Throwable) {
                    CommonMethods.SnackBar(layout, "Try again", false)
                }
            })
        } catch (e: Exception) {
        }
    }

    public fun deleteData(fireBaseID: String, position: Int) {
        attach_layout!!.setVisibility(View.GONE)
        if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
            mDatabase!!.child(fireBaseID).removeValue();
        }
//        firebaseChatData!!.removeAt(position)
//        if (chatAdapter != null) {
//            if (firebaseChatData!!.size > 0) {
//                chatAdapter!!.setData(firebaseChatData!!)
//            }
//            chatAdapter!!.notifyDataSetChanged()
//        }

    }

    private fun GetShareDetails(data: String) {
        val querry = "UserGroupForSK=$data~UserGroupFor=DISTOP~Screen=GROUP"
        try {
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.EncryptSharingLink(querry)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    progress_lyt!!.visibility = View.GONE
                    val res = response.body()
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "http://letsjoinin.azurewebsites.net/HomePage/MobileApp?$res"
//                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
        }
    }

    public fun ReplyData(fireBase: FirebaseChatData, position: Int) {
        attach_layout!!.setVisibility(View.GONE)
        replyChatData = fireBase
        isReplyClicked = true;
        textview_main_user_name!!.text = fireBase.PostedBy!!.Name
        if (fireBase.CommentType.equals("TXT")) {
            reply_img.setVisibility(View.GONE)
            textview_main_reply_message!!.setVisibility(View.VISIBLE)
            textview_main_reply_message!!.text = fireBase.CommentText
        } else {
            reply_img.setVisibility(View.VISIBLE)
            textview_main_reply_message!!.text = "Photo"
            Picasso.with(this)
                .load(fireBase.CommentText)
                .into(reply_img, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                    }

                    override fun onError() {
                    }
                })
        }


        layout_main_reply_view!!.visibility = View.VISIBLE

    }

    public fun MoveToOffense(position: Int) {
        if (chat_list_view != null) {
            chat_list_view!!.smoothScrollToPosition(position)
            chatAdapter!!.notifyDataSetChanged()
        }
    }

    public fun offensecountData(data: FirebaseChatData) {
        attach_layout!!.setVisibility(View.GONE)


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.report_dialog)


        val dialogButton =
            dialog.findViewById(R.id.btn_dialog) as Button
        dialogButton.setOnClickListener {
            if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
                val rating: RatingJavaData = RatingJavaData()

                rating.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                rating.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
                rating.AvatharPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
//        mDatabase!!.child(fireBaseID!!).child("OffenseCount").setValue()


                mDatabase!!.child(data.FireBaseID!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var exit = dataSnapshot.exists()
                            if (dataSnapshot.exists()) {
                                val OffenseCount = dataSnapshot.child("OffenseCount").value as Any?
                                val NoteWorthyClicks =
                                    dataSnapshot.child("NoteWorthyClicks").value as Any?
                                val FireBaseID = dataSnapshot.child("FireBaseID").value as Any?
                                val childUpdates = HashMap<String, Any>()
                                childUpdates["/" + FireBaseID!! + "/OffenseCount"] =
                                    OffenseCount as Long + 1
                                childUpdates["/" + FireBaseID!! + "/NoteWorthyClicks"] =
                                    NoteWorthyClicks as Long + 1
                                childUpdates["/" + FireBaseID!! + "/OffenseMarkedBy/" + UserID] =
                                    rating
                                data.NoteWorthyClicks = NoteWorthyClicks as Long + 1
                                data.OffenseCount = OffenseCount as Long + 1
                                mDatabase!!.updateChildren(childUpdates)
                                if (chatAdapter != null) {
                                    if (firebaseChatData!!.size > 0) {
                                        chatAdapter!!.setData(firebaseChatData!!)
                                    }
                                    chatAdapter!!.notifyDataSetChanged()
                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                        }
                    })
//        mDatabase!!.child(fireBaseID!!).child("OffenseMarkedBy").child("dfj099-asd9-as_490").setValue(rating)
            } else {
                if (layout != null) {
                    CommonMethods.SnackBar(layout, "Network not available", false)
                }
            }
            dialog.dismiss()

        }

        dialog.show()


    }


    public fun Rating(data: FirebaseChatData, ratingLong: Long) {
        if (CommonMethods.isNetworkAvailable(this@ChatActivity)) {
            val rating: RatingJavaData = RatingJavaData()
            rating.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            rating.Name = mPrefMgr!!.getString(AppConstant.UserName, AppConstant.EMPTY)
            rating.AvatharPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
            rating.Rating = ratingLong
            mDatabase!!.child(data.FireBaseID!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(ds: DataSnapshot) {
                        var exit = ds.exists()
                        if (ds.exists()) {
                            val childUpdates = HashMap<String, Any>()
                            val RatingSum = ds.child("RatingSum").value as Any?
                            val NoteWorthyClicks = ds.child("NoteWorthyClicks").value as Any?
                            val FireBaseID = ds.child("FireBaseID").value as Any?
                            childUpdates["/" + FireBaseID!! + "/RatingSum"] =
                                ratingLong - RatingSum as Long
                            childUpdates["/" + FireBaseID!! + "/NoteWorthyClicks"] =
                                NoteWorthyClicks as Long + 1
                            childUpdates["/" + FireBaseID!! + "/RatingBy/" + UserID] = rating
                            data.NoteWorthyClicks = NoteWorthyClicks as Long + 1
                            data.AvgRating = ratingLong - RatingSum as Long
                            mDatabase!!.updateChildren(childUpdates)
                            if (chatAdapter != null) {
                                if (firebaseChatData!!.size > 0) {
                                    chatAdapter!!.setData(firebaseChatData!!)
                                }
                                chatAdapter!!.notifyDataSetChanged()
                            }


                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                    }
                })

        }
    }

    private fun GetShareDetails(ContentId: String, ContentType: String) {
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
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun getFirebaseData() {
        if (myCountDownTimer != null) {
            myCountDownTimer!!.cancel()
        }
        if (myCountDownTimer != null) {
            Log.w("GroupChatActivity", "MyCountDownTimer onTick")
            myCountDownTimer!!.start()
        }
        CommonMethods.hideKeyboardFrom(this@ChatActivity, editText)
        attach_layout!!.setVisibility(View.GONE)
        isDataFetchDone = false
        layout_main_reply_view!!.visibility = View.GONE
        textview_main_user_name!!.text = ""
        textview_main_reply_message!!.text = ""
        editText.setText("")
        isReplyClicked = false;
        if (mMessageListener != null) {
            mMessageReference!!.removeEventListener(mMessageListener!!)
        }
        if (mShimmerViewContainer != null) {
            layout_no_comments!!.visibility = View.GONE
            mShimmerViewContainer!!.startShimmerAnimation()
            mShimmerViewContainer!!.visibility = View.VISIBLE
        }
        if (chat_list_view != null) {
            chat_list_view!!.visibility = View.GONE
        }
        isReceivedAll = false
        firebaseChatData!!.clear()
        firebaseChatDataID!!.clear()
        firebaseChatDataMap!!.clear()
        myCountDownTimer!!.Chat_Group_ID = GroupName
        mDatabase = database!!.reference.child(FirebaseId!!).child(GroupName!!)
        mMessageReference = database!!.reference.child(FirebaseId!!).child(GroupName!!)
        if (chatAdapter != null) {
            chatAdapter!!.notifyDataSetChanged()
        }
        val query: Query = mDatabase!!


        // copy for removing at onStop()
        val mChildListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                if (dataSnapshot.exists()) {

                }

            }

            override fun onChildChanged(ds: DataSnapshot, p1: String?) {
                if (ds.exists()) {

                    if (!TextUtils.isEmpty(ds.key) && !ds.key.equals("ContentID") && !ds.key.equals(
                            "GroupName"
                        )
                    ) {


                        val message: FirebaseChatData = FirebaseChatData()

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
                            if (ds.child("/ParentComment/OffenseCount").value != null && ds.child("/ParentComment/OffenseCount").value is Long) {
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


                            if (firebaseChatDataID!!.contains(ds.key)) {
                                var fData: FirebaseChatData? = firebaseChatDataMap!!.get(ds.key!!)
                                if (fData != null) {
                                    val p = firebaseChatData!!.indexOf(fData!!)
                                    firebaseChatData!!.set(p, message!!)
                                }
                                firebaseChatDataMap.put(ds.key!!, message!!)
                            } else {
                                firebaseChatDataID!!.add(ds.key!!)
                                firebaseChatData!!.add(message!!)
                                firebaseChatDataMap.put(ds.key!!, message!!)
                            }
                            if (chatAdapter != null) {
                                if (firebaseChatData!!.size > 0) {
                                    if (mShimmerViewContainer != null) {
                                        mShimmerViewContainer!!.stopShimmerAnimation()
                                        mShimmerViewContainer!!.visibility = View.GONE
                                    }
                                    chat_list_view!!.visibility = View.VISIBLE
                                    layout_no_comments!!.visibility = View.GONE


                                    chatAdapter!!.setData(firebaseChatData!!)
                                }
                                chatAdapter!!.notifyDataSetChanged()
//                                chat_list_view!!.scrollToPosition(firebaseChatData!!.size - 1)
                            }
                        }
                    }
                }
            }

            override fun onChildAdded(ds: DataSnapshot, p1: String?) {
                if (ds.exists()) {

                    if (!TextUtils.isEmpty(ds.key) && !ds.key.equals("ContentID") && !ds.key.equals(
                            "GroupName"
                        )
                    ) {
                        if (!firebaseChatDataID!!.contains(ds.key)) {

                            val message: FirebaseChatData = FirebaseChatData()

                            val CommentText = ds.child("CommentText").value as String?
                            val PostedOn = ds.child("PostedOn").value as String?
                            var FireBaseID = ds.child("FireBaseID").value as String?
                            if (FireBaseID != null) {
                                if (!TextUtils.isEmpty(FireBaseID)) {
                                    firebaseChatDataID!!.add(FireBaseID!!)
                                }
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

                                firebaseChatData!!.add(message!!)
                                firebaseChatDataMap.put(FireBaseID!!, message!!)
                                if (chatAdapter != null) {
                                    if (firebaseChatData!!.size > 0) {
                                        if (mShimmerViewContainer != null) {
                                            mShimmerViewContainer!!.stopShimmerAnimation()
                                            mShimmerViewContainer!!.visibility = View.GONE
                                        }
                                        chat_list_view!!.visibility = View.VISIBLE
                                        layout_no_comments!!.visibility = View.GONE
                                        chatAdapter!!.setData(firebaseChatData!!)
                                    }
                                    chatAdapter!!.notifyDataSetChanged()
                                    chat_list_view!!.scrollToPosition(firebaseChatData!!.size - 1)
                                }

                            }
                        }
                    }
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseChatDataMap.get(dataSnapshot.key)
                    firebaseChatData!!.remove(firebaseChatDataMap.get(dataSnapshot.key))
                    if (chatAdapter != null) {
                        if (firebaseChatData!!.size > 0) {
                            chatAdapter!!.setData(firebaseChatData!!)
                        } else {
                            if (chat_list_view != null) {
                                chat_list_view!!.visibility = View.GONE
                                layout_no_comments!!.visibility = View.VISIBLE
                            }
                        }
                        firebaseChatDataMap.remove(dataSnapshot.key)
                        chatAdapter!!.notifyDataSetChanged()
                        if (firebaseChatData!!.size == 1) {
                            val item = firebaseChatData!!.get(0)
                            if (!(item is StickyHeader)) {
                                if (chat_list_view != null) {
                                    chat_list_view!!.visibility = View.GONE
                                    layout_no_comments!!.visibility = View.VISIBLE
                                }
                            }
                        } else if (firebaseChatData!!.size == 0) {
                            if (chat_list_view != null) {
                                chat_list_view!!.visibility = View.GONE
                                layout_no_comments!!.visibility = View.VISIBLE
                            }
                        }
                    }

                }
            }
        }


        val mValueEvent = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val p = p0.childrenCount
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }



        Handler().postDelayed({
            //orderByKey().startAt("0").limitToLast(15)
            mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (mShimmerViewContainer != null) {
                        mShimmerViewContainer!!.stopShimmerAnimation()
                        mShimmerViewContainer!!.visibility = View.GONE
                    }
                    if (chat_list_view != null) {
                        chat_list_view!!.visibility = View.VISIBLE
                        layout_no_comments!!.visibility = View.GONE
                    }
                    isDataFetchDone = true
                    firebaseChatData!!.clear()
                    firebaseChatDataID!!.clear()
                    firebaseChatDataMap!!.clear()
                    if (dataSnapshot.exists()) {
                        for (ds in dataSnapshot.children) {

                            if (!TextUtils.isEmpty(ds.key) && !ds.key.equals("ContentID") && !ds.key.equals(
                                    "GroupName"
                                )
                            ) {

                                if (!firebaseChatDataID!!.contains(ds.key)) {
                                    val message: FirebaseChatData = FirebaseChatData()

                                    val CommentText = ds.child("CommentText").value as String?
                                    val PostedOn = ds.child("PostedOn").value as String?
                                    var FireBaseID = ds.child("FireBaseID").value as String?
                                    if (!TextUtils.isEmpty(FireBaseID)) {
                                        firebaseChatDataID!!.add(FireBaseID!!)
                                        val ParentCommentID =
                                            ds.child("ParentCommentID").value as String?
                                        val CommentType = ds.child("CommentType").value as String?
                                        val AvgRating = ds.child("RatingSum").value as Any?
                                        var NoteWorthyClicks: Long = 0
                                        if (ds.child("NoteWorthyClicks").value != null) {
                                            NoteWorthyClicks =
                                                ds.child("NoteWorthyClicks").value as Long
                                        }
                                        var OffenseCount: Any = 0
                                        if (ds.child("OffenseCount").value != null) {
                                            OffenseCount = ds.child("OffenseCount").value as Any
                                        }
                                        val PostedBy_UserID =
                                            ds.child("/PostedBy/UserID").value as String?
                                        val PostedBy_Name =
                                            ds.child("/PostedBy/Name").value as String?
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
                                        var ParentComment_NoteWorthyClicks: Int = 0
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
                                            var Rating =
                                                ds.child("RatingBy").value as HashMap<String, Any>
                                            if (Rating != null) {
                                                val ratingDataList =
                                                    Rating.get(UserID)
                                                if (ratingDataList != null) {
                                                    ratingDataList as HashMap<String, Any>
                                                    message.RatingCount =
                                                        ratingDataList.get("Rating")
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
                                        ParentComment_PostedBy!!.UserID =
                                            ParentComment_PostedBy_UserID
//                                PostedBy!!.MyRating = PostedBy_Name
                                        var ParentComment: FirebaseChatParentCommentData? =
                                            FirebaseChatParentCommentData()

                                        ParentComment!!.CommentText = ParentComment_CommentText
                                        ParentComment!!.AvgRating = ParentComment_AvgRating
                                        ParentComment!!.CommentType = ParentComment_CommentType
                                        ParentComment!!.FireBaseID = ParentComment_FireBaseID
                                        ParentComment!!.NoteWorthyClicks =
                                            ParentComment_NoteWorthyClicks
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
                                        firebaseChatData!!.add(message!!)
                                        firebaseChatDataMap.put(FireBaseID!!, message!!)
                                    }

                                }


                            }


                        }
                        if (chat_list_view != null) {
                            if (firebaseChatData!!.size > 0) {
                                chat_list_view!!.visibility = View.VISIBLE
                                layout_no_comments!!.visibility = View.GONE

                                val ad1 = FirebaseChatData()
                                ad1.CommentType = "AD"
                                ad1.CommentText =
                                    "https://www.designyourway.net/blog/wp-content/uploads/2017/12/CeaG_rH64.jpg"
                                if (firebaseChatData!!.size > 2) {
                                    firebaseChatData!!.add(1, ad1)
                                }

                                val ad2 = FirebaseChatData()
                                ad2.CommentType = "AD"
                                ad2.CommentText =
                                    "https://d3nuqriibqh3vw.cloudfront.net/styles/aotw_card_ir/s3/media-vimeo/162638981.jpg"
                                if (firebaseChatData!!.size > 7) {
                                    firebaseChatData!!.add(6, ad2)
                                }

                                val ad3 = FirebaseChatData()
                                ad3.CommentType = "AD"
                                ad3.CommentText =
                                    "https://blog.printsome.com/wp-content/uploads/coca-cola-marketing.jpg"
                                if (firebaseChatData!!.size > 12) {
                                    firebaseChatData!!.add(11, ad3)
                                }


                                val ad4 = FirebaseChatData()
                                ad4.CommentType = "AD"
                                ad4.CommentText =
                                    "https://www.adgully.com/img/400/201704/screen-shot-2017-04-04-at-12-51-15-pm.jpg"
                                if (firebaseChatData!!.size > 20) {
                                    firebaseChatData!!.add(19, ad4)
                                }
                                chatAdapter = ChatAdapter(
                                    firebaseChatData,
                                    this@ChatActivity
                                )
                                chat_list_view!!.adapter = chatAdapter
                                chatAdapter!!.setData(firebaseChatData!!)
//                                    chat_list_view!!.adapter!!.notifyDataSetChanged()
                                chat_list_view!!.scrollToPosition(firebaseChatData!!.size - 1)

                                if (!TextUtils.isEmpty(NotificationItemFireBaseID) && !NotificationItemFireBaseID.equals(
                                        "null"
                                    )
                                ) {

                                    for (data in firebaseChatData!!) {
                                        if (!(data is StickyHeader)) {
                                            if (data.FireBaseID != null) {
                                                if (data.FireBaseID!!.equals(
                                                        NotificationItemFireBaseID
                                                    )
                                                ) {
                                                    chat_list_view!!.scrollToPosition(
                                                        firebaseChatData!!.indexOf(
                                                            data
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }


                                }


                            } else {
                                layout_no_comments!!.visibility = View.VISIBLE

                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
        }, 4000)

        mMessageReference!!.addChildEventListener(mChildListener)

        mMessageListener = mChildListener
        mDatabase!!
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var exit = dataSnapshot.exists()
                    if (!dataSnapshot.exists()) {
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/ContentID"] = FirebaseId!!
                        childUpdates["/GroupName"] = chat_txt_group_id.text.toString()
                        mDatabase!!.updateChildren(childUpdates)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })


    }

    override fun onSaveInstanceState(outState: Bundle?) {

        if (mExoPlayerHelper != null) {
            mExoPlayerHelper!!.onSaveInstanceState(outState)
        }

        if (mExoPlayerHelperAD != null) {
            mExoPlayerHelperAD!!.onSaveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        if (mExoPlayerHelper != null) {
            mExoPlayerHelper!!.onActivityStart()
        }

        if (mExoPlayerHelperAD != null) {
            mExoPlayerHelperAD!!.onActivityStart()
        }

        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        try {
            if (mExoPlayerHelper != null) {
                mExoPlayerHelper!!.onActivityResume()
            }

            if (mExoPlayerHelperAD != null) {
                mExoPlayerHelperAD!!.onActivityResume()
            }
            if (networkStateReceiver!! != null) {
                networkStateReceiver!!.addListener(this)
            }
        } catch (e: Exception) {
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
                    try {
//                        val imageUri: Uri? = Uri.fromFile(File(currentPhotoPath));
                        val file = File(currentPhotoPath)

                        val compressedImage = Compressor(this@ChatActivity)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                            .compressToFile(file)
                        val photo: Bitmap =
                            BitmapFactory.decodeFile(compressedImage.getAbsolutePath())
//                        val imageStream: InputStream? = getContentResolver().openInputStream(imageUri!!);
//                        val photo: Bitmap = BitmapFactory.decodeStream(imageStream);
                        base64String = CommonMethods.encodeTobase64(photo)
                        firebaseIDToPost!!.add(GroupName!!)
                        doAsync {
                            PostImageContent()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

                    if (data != null) {
                        val contentURI = data!!.data
                        try {
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
                            base64String = CommonMethods.encodeTobase64(bitmap)
                            firebaseIDToPost!!.add(GroupName!!)
                            doAsync {
                                PostImageContent()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else if (requestCode == UCrop.REQUEST_CROP) {
                    if (data != null) {
                        handleCropResult(data!!)
                    }
                }
            }

            if (resultCode != RESULT_CANCELED) {
            }
        } catch (e: Exception) {
            if (layout != null) {
                CommonMethods.SnackBar(layout, getString(R.string.error), false)
            }
        }
    }


    fun setAreaOfInterestRatio(ratio: Double, fast: Boolean) {
        var ratio = ratio
        ratio = constraintsOnAreaOfInterestRatio(ratio)
        setAreaOfInterestRatio(ratio)

    }

    val REQUEST_CAMERA = 1
    val REQUEST_GALLERY = 2


    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun TakePictureIntent() {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this@ChatActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                    }
                }
            }
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun startCropActivity(@NonNull uri: Uri) {
        var destinationFileName = "CropImage"
        val inputFileName = "SampleCropImage_Dup.jpg"
        destinationFileName += ".jpg"
        var uCrop = UCrop.of(
            uri,
            Uri.fromFile(File(getCacheDir(), inputFileName)),
            Uri.fromFile(File(getCacheDir(), destinationFileName))
        )
        uCrop = basisConfig(uCrop)
        uCrop = advancedConfig(uCrop)
        UCrop.CROP_PAGE = 3
        uCrop.start(this@ChatActivity)
    }

    private fun basisConfig(@NonNull uCrop: UCrop): UCrop {
        var uCrop = uCrop
        uCrop = uCrop.withAspectRatio(1f, 1f)
        return uCrop
    }

    private fun advancedConfig(@NonNull uCrop: UCrop): UCrop {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setToolbarColor(ContextCompat.getColor(this@ChatActivity, R.color.colorPrimaryDark))
        options.setStatusBarColor(
            ContextCompat.getColor(
                this@ChatActivity,
                R.color.colorPrimaryDark
            )
        )
        options.setActiveWidgetColor(
            ContextCompat.getColor(
                this@ChatActivity,
                R.color.colorPrimaryDark
            )
        )
        options.setToolbarWidgetColor(ContextCompat.getColor(this@ChatActivity, R.color.white))
        options.setTitleColor(ContextCompat.getColor(this@ChatActivity, R.color.white))
        options.setTitleColor(ContextCompat.getColor(this@ChatActivity, R.color.white))

        return uCrop.withOptions(options)
    }

    private fun galleryImageIntent() {
        try {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(galleryIntent, REQUEST_GALLERY)
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    fun handleCropResult(@NonNull result: Intent) {

        try {
            val resultUri = UCrop.getOutput(result)
            if (resultUri != null) {

                object : AsyncTask<Void, Void, String>() {
                    private var result_thumbnail: Bitmap? = null

                    override fun doInBackground(vararg params: Void): String {
                        var msg = ""
                        try {
                            val file = File(resultUri.path!!)
                            val compressedImage = Compressor(this@ChatActivity)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.PNG)
                                .compressToFile(file)
                            result_thumbnail =
                                BitmapFactory.decodeFile(compressedImage.getAbsolutePath())
                            if (result_thumbnail != null) {
                                base64String = CommonMethods.encodeTobase64(result_thumbnail)
                            }
                        } catch (ex: Exception) {
                            msg = "Error :" + ex.message
                        }

                        return msg
                    }

                    override fun onPostExecute(msg: String) {

                        val del = File(resultUri.path!!)
                        if (del.exists()) {
                            del.delete();
                        }
                    }
                }.execute(null, null, null)
            } else {
                Toast.makeText(this@ChatActivity, "Cannot retrive croped image", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            if (layout != null) {
                CommonMethods.SnackBar(layout, getString(R.string.error), false)
            }
        }

    }

    fun fetchIndvidualGroupData(position: Int) {
        try {
            if (isDataFetchDone) {
                bsheet = 1
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                chatPosition = position
                isReplyClicked = false;
//                img_previous_chat.setColorFilter(
//                    ContextCompat.getColor(this@ChatActivity, R.color.white),
//                    android.graphics.PorterDuff.Mode.MULTIPLY
//                );
                firebaseChatData!!.clear()
                firebaseChatDataID!!.clear()
                firebaseChatDataMap!!.clear()
                if (chatPosition <= ContentGroups!!.size - 1) {
                    img_next_chat.setColorFilter(
                        ContextCompat.getColor(this@ChatActivity, R.color.white),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                    );
                }
                var block: GetContentGroupList = ContentGroups!!.get(position)
                GroupName = block.Value!!.Name
                chat_txt_group_id.text = block.Value!!.Name
//                if (block.Color.equals("RED")) {
//                    allowSwipe = false
//                    rel_click_to_participate!!.visibility = View.VISIBLE
//                    layout_chat_text_box!!.visibility = View.GONE
//                    emojiButton!!.visibility = View.GONE
//                } else {
                    allowSwipe = true
                    rel_click_to_participate!!.visibility = View.GONE
                    layout_chat_text_box!!.visibility = View.VISIBLE
                    emojiButton!!.visibility = View.VISIBLE
//                }

                getFirebaseData()

                if (chatPosition == 0) {
                    img_previous_chat.setColorFilter(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.darkgry
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    );
                }
                if (chatPosition == ContentGroups!!.size - 1) {
                    img_next_chat.setColorFilter(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.darkgry
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    );
                }

                chatPosition = chatPosition + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun fetchGroupData(position: Int) {
        try {
                fetchIndvidualGroupData(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun setAreaOfInterestRatio(ratio: Double) {
        this.areaOfInterestRatio = ratio.toFloat()

        this.emptyLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        this.resultsLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        this.emptyLayoutParams!!.weight = 1.0f - this.areaOfInterestRatio
        this.resultsLayoutParams!!.weight = this.areaOfInterestRatio

        emptyLayout!!.setLayoutParams(emptyLayoutParams)
        resultsLayout!!.setLayoutParams(resultsLayoutParams)


        Log.d("AreaOfInteres", "emptyLayoutParams - " + emptyLayoutParams!!.weight)
        Log.d("AreaOfInteres", "resultsLayoutParams - " + resultsLayoutParams!!.weight)


    }

    fun constraintsOnAreaOfInterestRatio(ratio: Double): Double {
        var ratio = ratio
        if (ratio < MIN_AREA_OF_INTEREST_RATIO) {
            ratio = MIN_AREA_OF_INTEREST_RATIO
        }
        return if (ratio > MAX_AREA_OF_INTEREST_RATIO) {
            MAX_AREA_OF_INTEREST_RATIO
        } else ratio
    }

    private inner class MoveButtonTouchListener() : View.OnTouchListener {
        internal var isMoving: Boolean = false
        internal var touchDelta: Float = 0.toFloat()

        init {
            this.isMoving = false
            this.touchDelta = 0.0f
        }

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action != 0) {
                var newRatio: Double
                if (landscape) {
                    newRatio =
                        ((event.rawX - this.touchDelta) / layout!!.getWidth().toFloat()).toDouble()
                } else {
                    newRatio =
                        ((event.rawY - this.touchDelta) / layout!!.getHeight().toFloat()).toDouble()
                }
                newRatio = constraintsOnAreaOfInterestRatio(newRatio)
                when (event.action) {
                    1, 3 -> if (this.isMoving) {
                        this.isMoving = false
                        screen_ratio = newRatio
                        setAreaOfInterestRatio(newRatio, false)

                    }
                    2 -> if (this.isMoving) {
                        setAreaOfInterestRatio(newRatio, true)
                    }
                    else -> {
                    }
                }
            }
            val rawX: Float
            if (landscape) {
                rawX = event.rawX - areaOfInterestRatio * layout!!.getWidth().toFloat()
            } else {
                rawX = event.rawY - areaOfInterestRatio * layout!!.getHeight().toFloat()
            }
            this.touchDelta = rawX
            this.isMoving = true
            return true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // MyLog.e("onActivityDestroy");
            if (mExoPlayerHelper != null) {
                mExoPlayerHelper!!.onActivityDestroy()
            }

            if (mExoPlayerHelperAD != null) {
                mExoPlayerHelperAD!!.onActivityDestroy()
            }
            if (myCountDownTimer != null) {
                myCountDownTimer!!.cancel()
                myCountDownTimer = null
            }
            if (networkStateReceiver!! != null) {
                networkStateReceiver!!.removeListener(this)
                unregisterNetworkChanges()
            }
        } catch (e: Exception) {
        }
        if (mShimmerViewContainer != null)
            mShimmerViewContainer!!.stopShimmerAnimation()

        if (mMessageListener != null) {
            mMessageReference!!.removeEventListener(mMessageListener!!)
        }
    }

    override fun onPause() {


        if (mExoPlayerHelper != null) {
            mExoPlayerHelper!!.onActivityPause()
        }
        if (mExoPlayerHelperAD != null) {
            mExoPlayerHelperAD!!.onActivityPause()
        }
        if (mShimmerViewContainer != null)
            mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }

    class BlockAdapter(
        context: Context,
        ContentGroupList: ArrayList<ArrayList<GetContentGroupList>>,
        h: Int,
        frag: ChatActivity
    ) :
        BaseAdapter() {
        var context = context
        var ContentGroupList = ContentGroupList
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
//                fragment.addListener(holder.parent!!, position)

            } else {

                //If Our View in not Null than Just get View using Tag and pass to holder Object.

                holder = myView.getTag() as ViewHolder

            }


//            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
//            );
            holder.parent!!.layoutParams.height = height


            val block = ContentGroupList.get(position)


            holder.parent!!.setOnClickListener(View.OnClickListener { view: View? ->
                fragment.fetchGroupData(position)

            })

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
                holder.G7!!.visibility = View.INVISIBLE
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


//        //Auto Generated Method
        override fun getItem(p0: Int): Any {

            return ContentGroupList.get(p0)
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

//            var count : Int = 0
//            if(ContentGroupList.size>0)
//            {
//                count = ContentGroupList.size/9
//                count = count + 1
//            }
//            return count
            return ContentGroupList.size
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

    class GroupSingleBlockAdapter(
        context: Context,
        arrayList: ArrayList<GetContentGroupList>,
        h: Int,
        frag: ChatActivity
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
                myView = mInflater.inflate(R.layout.chat_block_grid_item, parent, false)

                //Create Object of ViewHolder Class and set our View to it

                holder = ViewHolder()


                //Find view By Id For all our Widget taken in grid_item.

                /*Here !! are use for non-null asserted two prevent From null.
                 you can also use Only Safe (?.)

                */


                holder.parent = myView!!.findViewById<LinearLayout>(R.id.parent) as LinearLayout
                holder.chat_block_group_id =
                    myView!!.findViewById<TextView>(R.id.chat_block_group_id) as TextView


                //Set A Tag to Identify our view.
                myView.setTag(holder)
//                fragment.addListener(holder.parent!!, position)

            } else {

                //If Our View in not Null than Just get View using Tag and pass to holder Object.

                holder = myView.getTag() as ViewHolder

            }

            val block: GetContentGroupList = arrayList!!.get(position)
//            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
//            );
            holder.parent!!.layoutParams.height = height

            holder.parent!!.setOnClickListener(View.OnClickListener { view: View? ->
                fragment.JoinGroup(block,position)

            })

            holder.chat_block_group_id!!.text = block.Value!!.Name
//            blockColor(holder.chat_block_group_id!!, block.Color!!)+
            blockColor(holder.chat_block_group_id!!, "GREEN")
//

            return myView

        }

        fun changeSelectedColor(textView: TextView, color: String) {


            if (color.equals("AMBER")) {
                ViewCompat.setBackgroundTintList(
                    textView,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context, R.color.chat_group_amber
                        )
                    )
                )

            } else if (color.equals("RED")) {

                ViewCompat.setBackgroundTintList(
                    textView,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.chat_group_red))
                )
            } else if (color.equals("GREEN")) {

                ViewCompat.setBackgroundTintList(
                    textView,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.chat_group_green
                        )
                    )
                )
            }
        }

        fun blockColor(v: TextView, color: String) {
            val sdk = android.os.Build.VERSION.SDK_INT;

            if (color.equals("AMBER")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_amber
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_amber
                        )
                    );
                }

            } else if (color.equals("RED")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_red
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_red
                        )
                    );
                }

            } else if (color.equals("GREEN")) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_green
                        )
                    );
                } else {
                    v.setBackground(
                        ContextCompat.getDrawable(
                            context,
                            R.color.chat_group_green
                        )
                    );
                }

            }
        }

        //Auto Generated Method
        override fun getItem(p0: Int): Any {
            return arrayList.get(p0)
        }


        //Auto Generated Method
        override fun getItemId(p0: Int): Long {
            return 0
        }

        //Auto Generated Method
        override fun getCount(): Int {

            if (arrayList != null) {
                return arrayList.size
            }
            return 0
        }


        //Create A class To hold over View like we taken in grid_item.xml

        class ViewHolder {

            var parent: LinearLayout? = null
            var chat_block_group_id: TextView? = null

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


    protected override fun onStop() {
        if (emojiPopup != null) {
            emojiPopup!!.dismiss()
        }
        if (mExoPlayerHelper != null) {
            mExoPlayerHelper!!.onActivityStop()
        }
        if (mExoPlayerHelperAD != null) {
            mExoPlayerHelperAD!!.onActivityStop()
        }
//        if (mMessageListener != null) {
//            mMessageReference!!.removeEventListener(mMessageListener!!)
//        }
        super.onStop()
    }

    internal val TAG = "ChatActivity"
    private fun setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
            .setOnEmojiBackspaceClickListener { ignore -> Log.d(TAG, "Clicked on Backspace") }
            .setOnEmojiClickListener { ignore, ignore2 -> Log.d(TAG, "Clicked on emoji") }
            .setOnEmojiPopupShownListener { emojiButton.setImageResource(R.drawable.ic_keyboard) }
            .setOnSoftKeyboardOpenListener { ignore -> Log.d(TAG, "Opened soft keyboard") }
            .setOnEmojiPopupDismissListener { emojiButton.setImageResource(R.drawable.smile_color) }
            .setOnSoftKeyboardCloseListener { Log.d(TAG, "Closed soft keyboard") }
            .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
            .setPageTransformer(PageTransformer())
            .build(editText)
    }

    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )


    protected fun checkPermissions() {
        val missingPermissions = java.util.ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this@ChatActivity, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(
                this@ChatActivity,
                permissions,
                REQUEST_CODE_ASK_PERMISSIONS
            )
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> for (index in permissions.indices.reversed()) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    // exit the app if one permission is not granted
                    //                        finish();
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                    return
                }
            }
        }// all permissions were granted
    }

    override fun onLoadingStatusChanged(
        isLoading: Boolean,
        bufferedPosition: Long,
        bufferedPercentage: Int
    ) {
    }

    override fun onTracksChanged(
        currentWindowIndex: Int,
        nextWindowIndex: Int,
        isPlayBackStateReady: Boolean
    ) {
    }

    override fun onPlayerError(errorString: String?) {
    }

    override fun onPlayerPaused(currentWindowIndex: Int) {
    }

    override fun onVideoResumeDataLoaded(window: Int, position: Long, isResumeWhenReady: Boolean) {
    }

    override fun onPlayerPlaying(currentWindowIndex: Int) {
    }

    override fun createExoPlayerCalled(isToPrepare: Boolean) {
    }

    override fun onMuteStateChanged(isMuted: Boolean) {
    }

    override fun onPauseBtnTap(): Boolean {
        // MyLog.d("onPauseBtnTap");
        return false
    }

    override fun onVideoTapped() {
    }

    override fun onFullScreenBtnTap() {
    }

    override fun onPlayerStateEnded(currentWindowIndex: Int) {
        if (adPlaying!!) {
            adPlaying = false
            exoPlayerView!!.visibility = View.VISIBLE
            mExoPlayerHelper!!.playerPlay()
            exoPlayerViewAD!!.visibility = View.GONE
        } else {
            play_exo_player_ad!!.visibility = View.VISIBLE
            exoPlayerView!!.visibility = View.GONE
            exoPlayerViewAD!!.visibility = View.VISIBLE
        }
    }

    override fun onPlayBtnTap(): Boolean {
        return false
    }

    override fun releaseExoPlayerCalled() {
    }

    override fun onPlayerStateIdle(currentWindowIndex: Int) {
    }

    override fun onPlayerBuffering(currentWindowIndex: Int) {
    }

    override fun onAdPause() {
    }

    override fun onAdResume() {
    }

    override fun onAdClicked() {
    }

    override fun onAdPlay() {
    }

    override fun onBuffering() {
    }

    override fun onAdTapped() {
    }

    override fun onAdEnded() {
    }

    override fun onAdError() {
    }

    override fun onThumbImageViewReady(imageView: ImageView?) {


        var path: String =
            mPrefMgr.getString(AppConstant.ADChatBannerImagePath, null)
        if (!TextUtils.isEmpty(path)) {
            Picasso.with(this@ChatActivity)
                .load(path)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.image_placeholder_1)
                .into(imageView)
        }
    }
}
