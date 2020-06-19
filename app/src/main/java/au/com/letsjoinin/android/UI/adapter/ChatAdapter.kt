package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.model.ChatOffensePositionClass
import au.com.letsjoinin.android.MVP.model.FirebaseChatData
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.ChatActivity
import au.com.letsjoinin.android.UI.view.HeaderItem
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.brandongogetap.stickyheaders.exposed.StickyHeader
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils
import kotlinx.android.synthetic.main.groupchat_layout.view.*
import ru.rambler.libs.swipe_layout.SwipeLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 */
class ChatAdapter(var items: ArrayList<FirebaseChatData>?, val context: Context) :
    RecyclerView.Adapter<ChatViewHolder>(), StickyHeaderHandler {
    private var defaultcolorID: Int = 0
    var tooltip: SimpleTooltip? = null
    var swiped: SwipeLayout? = null
    val ParentfirebaseMap: HashMap<String, ChatOffensePositionClass?> = HashMap<String, ChatOffensePositionClass?>()
    var mPrefMgr = PreferenceManager.getInstance();
    override fun getAdapterData(): MutableList<*> {
        return items!!
    }


    //    private val binderHelper = ViewBinderHelper()
    lateinit var yo: YoYo.YoYoString
    internal var left_emoji_click = false
    internal var right_emoji_click = false

    init {
//        binderHelper.setOpenOnlyOne(true)
    }

    fun pxTodp(dip: Float): Float {
        val resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groupchat_layout, parent, false)
        val viewHolder: ChatViewHolder
        if (viewType == 0) {
            viewHolder = MyViewHolder(view)
        } else {
            viewHolder = MyOtherViewHolder(view)
        }
        view.setOnClickListener { v ->
            val position = viewHolder.adapterPosition
        }
        return viewHolder
    }

    open fun setData(list: ArrayList<FirebaseChatData>) {
        Collections.sort(list, object : Comparator<FirebaseChatData> {
            internal var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            override fun compare(lhs: FirebaseChatData, rhs: FirebaseChatData): Int {
                try {
                    return if (lhs.PostedOn == null || rhs.PostedOn == null) 0 else dateFormat.parse(lhs.PostedOn!!)!!.compareTo(
                        dateFormat.parse(rhs.PostedOn!!)
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                return 0
            }
        })

        val itemsList: ArrayList<FirebaseChatData>? = ArrayList()
        var prevDate: String? = null
        for (i in 0 until list!!.size) {
            val item = list!!.get(i)
            if (!(item is StickyHeader)) {
                if(!TextUtils.isEmpty(item!!.PostedOn)) {
                    var head: Boolean = false
                    if ((!TextUtils.isEmpty(prevDate) && !item!!.PostedOn!!.split(" ")[0].equals(
                            prevDate
                        )) || TextUtils.isEmpty(
                            prevDate
                        )
                    ) {
                        head = true
                        itemsList!!.add(HeaderItem(CommonMethods.getDate(item!!.PostedOn!!.split(" ")[0])))
                        prevDate = item!!.PostedOn!!.split(" ")[0]
                    }
                }
                itemsList!!.add(item);
            }
        }
        val diffResult = DiffUtil.calculateDiff(SimpleDiffCallback(items!!, itemsList!!))
        items!!.clear()
        items!!.addAll(itemsList!!)

//        val a = TypedValue()
//        context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true)
//        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
//            // windowBackground is a color
//            defaultcolorID = a.data
//        }
        for (i in 0 until itemsList!!.size) {
            val item = itemsList!!.get(i)
            if (!(item is StickyHeader)) {
                if (!TextUtils.isEmpty(item.FireBaseID)) {
                    var offensePosition: ChatOffensePositionClass = ChatOffensePositionClass()
                    offensePosition.Count = item.OffenseCount!! as Long
                    offensePosition.Position = i
                    item.replyViewSelected = false
                    ParentfirebaseMap.put(item.FireBaseID!!, offensePosition)
                }
            }
        }
        diffResult!!.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        try {

            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.placeholder)
            val item = items!!.get(position)
            val UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            if (item is StickyHeader) {
                holder.swipeLayout.visibility = View.GONE
                holder.txt_date_header.visibility = View.VISIBLE
                holder.txt_date_header.text = (item as HeaderItem).date

            } else {


                if(item.CommentType.equals("AD")) {
                    holder.layout_chat_item.visibility = View.GONE
                    holder.ad_layout.visibility = View.VISIBLE

                    var path: String =
                        mPrefMgr.getString(AppConstant.ADChatBannerImagePath, AppConstant.EMPTY)
                    if(path == null) {
                        path = item.CommentText!!
                    }
                    Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(path)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(holder.ad_img)

                }else{
                    holder.layout_chat_item.visibility = View.VISIBLE
                    holder.ad_layout.visibility = View.GONE
                if (item.replyViewSelected!!) {
                    item.replyViewSelected = false
                    holder.layout_chat_item.setBackgroundColor(Color.parseColor("#EAF4FC"));
                    Handler().postDelayed({
                        holder.layout_chat_item.setBackgroundColor(Color.WHITE);
                    }, 1000)

                    holder.layout_chat_item.setOnClickListener(View.OnClickListener {

                        holder.layout_chat_item.setBackgroundColor(Color.WHITE);
                    })
                }


                holder?.img_avatar_chat.bringToFront()
                holder.txt_date_header.visibility = View.GONE

                if (item.OffenseCount as Long >= 5) {
                    holder.swipeLayout.visibility = View.GONE
                    holder.offensive_bubble.visibility = View.VISIBLE
                    val params = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        pxTodp(50f).toInt()
                    )
                    if (item.PostedBy!!.UserID!!.equals(UserID, true)) {
                        params.setMargins(
                            pxTodp(55f).toInt(),
                            pxTodp(4f).toInt(),
                            pxTodp(1f).toInt(),
                            pxTodp(1f).toInt()
                        );

                    } else {
                        params.setMargins(
                            pxTodp(1f).toInt(),
                            pxTodp(4f).toInt(),
                            pxTodp(55f).toInt(),
                            pxTodp(1f).toInt()
                        );
                    }

                    holder.offensive_bubble.setLayoutParams(params);
                    holder.offensive_bubble.setOnClickListener(View.OnClickListener {
                        Log.d("TooltipClick", "Txt - " + item.CommentText!!)

                        if (item.CommentType.equals("TXT")) {
                            tooltip = SimpleTooltip.Builder(context)
                                .anchorView(holder.offensive_bubble)
                                .text(item.CommentText!!)
                                .gravity(Gravity.TOP)
                                .dismissOnOutsideTouch(true)
                                .dismissOnInsideTouch(true)
                                .modal(true)
                                .animated(false)
                                .arrowColor(context.resources.getColor(R.color.colorPrimaryDark))
                                .animationDuration(2000)
                                .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
                                .contentView(R.layout.tooltip_image_view, R.id.txt_tooltip)
                                .focusable(true)
                                .build();

                            val iv = tooltip!!.findViewById<ImageView>(R.id.tooltip_imageview_media)
                            val tooltip_layout_media =
                                tooltip!!.findViewById<RelativeLayout>(R.id.tooltip_layout_media)
                            val txt_tooltip = tooltip!!.findViewById<TextView>(R.id.txt_tooltip)

                            txt_tooltip.visibility = View.VISIBLE
                            tooltip_layout_media.visibility = View.GONE
                            tooltip!!.show()
                        } else {
                            fullSizeImage(item.CommentText!!, item.PostedBy!!.Name!!)
                        }

                    })
                } else {
//                    binderHelper.bind(holder.swipeLayout, position.toString())
                    holder.swipeLayout.visibility = View.VISIBLE


                    if ((context as ChatActivity).allowSwipe) {
                        holder.img_reply.visibility = View.VISIBLE
                    } else {
                        holder.img_reply.visibility = View.GONE
                    }

                    holder.offensive_bubble.visibility = View.GONE

                    holder.swipeLayout.setOnSwipeListener(object : SwipeLayout.OnSwipeListener {
                        override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                            if (swiped != null && swiped !== swipeLayout) {
                                swiped!!.animateReset()
                            }
                            swiped = swipeLayout
                        }

                        override fun onSwipeClampReached(
                            swipeLayout: SwipeLayout,
                            moveToRight: Boolean
                        ) {
                            if (moveToRight && swipeLayout != null ) {
                                    holder.swipeLayout.animateReset()
                                (context as ChatActivity).ReplyData(item!!, position)
                            }
                        }

                        override fun onLeftStickyEdge(
                            swipeLayout: SwipeLayout,
                            moveToRight: Boolean
                        ) {
                            if (item.CommentType.equals("TXT")) {}
                        }

                        override fun onRightStickyEdge(
                            swipeLayout: SwipeLayout,
                            moveToRight: Boolean
                        ) {

                        }
                    })
                    if (item.PostedBy!!.UserID!!.equals(UserID, true)) {
                        holder.right_txt_chat_time.text = CommonMethods.ChatTime(item.PostedOn!!)

                        if (item.CommentType.equals("TXT")) {
                            holder.right_layout_image_view.visibility = View.GONE
                            holder.right_txt_msg_content.visibility = View.VISIBLE
                            holder.right_txt_msg_content.text = item.CommentText
                        } else {
                            holder.right_layout_image_view.visibility = View.VISIBLE
                            holder.right_txt_msg_content.visibility = View.GONE
//                            Picasso.with(context)
//                                .load(item.CommentText)
//                                .error(R.drawable.image_placeholder_1)
//                                .into(holder.right_imageview_media, object : Callback {
//                                    override fun onSuccess() {
//                                        holder.right_progress.visibility = View.GONE
//                                    }
//
//                                    override fun onError() {
//                                        holder.right_progress.visibility = View.GONE
//                                    }
//                                })
                            holder.right_imageview_media.setOnClickListener(View.OnClickListener {
                                if (!TextUtils.isEmpty(item.CommentText) && item.CommentText!!.startsWith(
                                        "http"
                                    )
                                ) {
                                    fullSizeImage(item.CommentText!!, item.PostedBy!!.Name!!)
                                }
                            })


                            if (!TextUtils.isEmpty(item.CommentText) && item.CommentText!!.startsWith(
                                    "http"
                                )
                            ) {
                                Glide.with(context)
                                    .setDefaultRequestOptions(requestOptions)
                                    .load(item.CommentText)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any,
                                            target: Target<Drawable>,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            holder.right_progress.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable,
                                            model: Any,
                                            target: Target<Drawable>,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            holder.right_progress.visibility = View.GONE
                                            return false
                                        }
                                    })
                                    .into(holder.right_imageview_media)
                            }

                        }
                        holder.right_txt_chat_username.text = item.PostedBy!!.Name
                        holder.right_layout_chat.visibility = View.VISIBLE
                        holder.right_reply_content.visibility = View.GONE
                        holder.right_offensive_bubble.visibility = View.GONE

                        holder.left_layout_chat.visibility = View.GONE
                        holder.img_delete.visibility = View.VISIBLE
                        holder.img_offensive.visibility = View.GONE
                        holder.right_layout_reply_message.visibility = View.GONE
                        if (item.ParentComment != null) {
                            if (!TextUtils.isEmpty(item.ParentComment!!.FireBaseID)) {
                                if (!TextUtils.isEmpty(item.ParentComment!!.FireBaseID!!.trim())) {

                                    holder.right_layout_reply_message.visibility = View.VISIBLE
                                    holder.right_layout_reply_message.setOnClickListener(View.OnClickListener {
                                        if (ParentfirebaseMap.containsKey(item.ParentComment!!.FireBaseID)) {
                                            val pos =
                                                ParentfirebaseMap.get(item.ParentComment!!.FireBaseID)!!.Position!!
                                            items!!.get(pos).replyViewSelected = true
                                            (context as ChatActivity).MoveToOffense(pos)
                                        }
                                    })
                                    holder.right_reply_content.visibility = View.VISIBLE
                                    holder.right_offensive_bubble.visibility = View.GONE
                                    holder.right_layout_reply_message.visibility = View.VISIBLE
                                    if (item.ParentComment!!.PostedBy != null) {
                                        if (!TextUtils.isEmpty(item.ParentComment!!.PostedBy!!.Name)) {
                                            holder.right_textview_replied_user_name.text =
                                                item.ParentComment!!.PostedBy!!.Name

                                            holder.right_textview_replied_time.text =
                                                CommonMethods.ChatTime(item.ParentComment!!.PostedOn)
                                        }
                                    }

                                    if (item.ParentComment!!.CommentType.equals("TXT")) {
                                        holder.right_textview_replied_message.visibility =
                                            View.VISIBLE
                                        holder.right_reply_media.visibility = View.GONE
                                        holder.right_textview_replied_message.text =
                                            item.ParentComment!!.CommentText
                                    } else {
                                        holder.right_reply_img_media.setOnClickListener(View.OnClickListener {

                                            fullSizeImage(
                                                item.ParentComment!!.CommentText!!,
                                                item.ParentComment!!.PostedBy!!.Name!!
                                            )
                                        })
                                        holder.right_reply_media.visibility = View.VISIBLE
                                        holder.right_textview_replied_message.visibility = View.GONE
//                                                Picasso.with(context)
//                                                    .load(item.ParentComment!!.CommentText)
//                                                    .into(holder.right_reply_img_media)

                                        if (!TextUtils.isEmpty(item.ParentComment!!.CommentText) && item.ParentComment!!.CommentText!!.startsWith(
                                                "http"
                                            )
                                        ) {
                                            Glide.with(context)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(item.ParentComment!!.CommentText)
                                                .listener(object : RequestListener<Drawable> {
                                                    override fun onLoadFailed(
                                                        e: GlideException?,
                                                        model: Any,
                                                        target: Target<Drawable>,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        return false
                                                    }

                                                    override fun onResourceReady(
                                                        resource: Drawable,
                                                        model: Any,
                                                        target: Target<Drawable>,
                                                        dataSource: DataSource,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        return false
                                                    }
                                                })
                                                .into(holder.right_reply_img_media)
                                        }
                                    }
                                    if (ParentfirebaseMap.containsKey(item.ParentComment!!.FireBaseID)) {
                                        if (ParentfirebaseMap.get(item.ParentComment!!.FireBaseID)!!.Count as Long >= 5) {
                                            holder.right_reply_content.visibility = View.GONE
                                            holder.right_offensive_bubble.visibility = View.VISIBLE
                                        } else {


                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        holder.left_txt_chat_time.text = CommonMethods.ChatTime(item.PostedOn!!)
                        holder.left_txt_chat_username.text = item.PostedBy!!.Name
                        holder.left_txt_msg_content.text = item.CommentText
                        holder.right_layout_chat.visibility = View.GONE
                        holder.left_layout_chat.visibility = View.VISIBLE
                        holder.img_delete.visibility = View.GONE
                        holder.img_offensive.visibility = View.VISIBLE
                        holder.left_reply_content.visibility = View.GONE
                        holder.left_offensive_bubble.visibility = View.GONE
                        holder.left_layout_reply_message.visibility = View.GONE
                        if (!TextUtils.isEmpty(item!!.PostedBy!!.AvatharPath)) {
                            Picasso.with(context)
                                .load(item!!.PostedBy!!.AvatharPath)
//                                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
//                                .noFade()
                                .into(holder.img_avatar_chat)
                        }
                        if (item.CommentType.equals("TXT")) {
                            holder.left_layout_image_view.visibility = View.GONE
                            holder.left_txt_msg_content.visibility = View.VISIBLE
                            holder.left_txt_msg_content.text = item.CommentText
                        } else {
                            holder.left_layout_image_view.visibility = View.VISIBLE
                            holder.left_txt_msg_content.visibility = View.GONE
//                            Picasso.with(context)
//                                .load(item.CommentText)
//                                .into(holder.left_imageview_media, object : Callback {
//                                    override fun onSuccess() {
//                                        holder.left_progress.visibility = View.GONE
//                                    }
//
//                                    override fun onError() {
//                                        holder.left_progress.visibility = View.GONE
//                                    }
//                                })
                            if (!TextUtils.isEmpty(item.CommentText) && item.CommentText!!.startsWith(
                                    "http"
                                )
                            ) {
                                Glide.with(context)
                                    .setDefaultRequestOptions(requestOptions)
                                    .load(item.CommentText)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any,
                                            target: Target<Drawable>,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            holder.left_progress.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable,
                                            model: Any,
                                            target: Target<Drawable>,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            holder.left_progress.visibility = View.GONE
                                            return false
                                        }
                                    })
                                    .into(holder.left_imageview_media)
                            }

                            holder.left_imageview_media.setOnClickListener(View.OnClickListener {

                                fullSizeImage(item.CommentText!!, item.PostedBy!!.Name!!)
                            })

                        }
                        if (item.ParentComment != null) {
                            if (!TextUtils.isEmpty(item.ParentComment!!.FireBaseID)) {
                                if (!TextUtils.isEmpty(item.ParentComment!!.FireBaseID!!.trim())) {
                                    holder.left_layout_reply_message.visibility = View.VISIBLE
                                    holder.left_layout_reply_message.setOnClickListener(View.OnClickListener {
                                        if (ParentfirebaseMap.containsKey(item.ParentComment!!.FireBaseID)) {
                                            val pos =
                                                ParentfirebaseMap.get(item.ParentComment!!.FireBaseID)!!.Position!!
                                            items!!.get(pos).replyViewSelected = true
                                            (context as ChatActivity).MoveToOffense(pos)
                                        }
//                                        (context as ChatActivity).MoveToOffense(ParentfirebaseMap.get(item.ParentComment!!.FireBaseID)!!.Position!!)
                                    })


                                    holder.left_reply_content.visibility = View.VISIBLE
                                    holder.left_offensive_bubble.visibility = View.GONE
                                    if (item.ParentComment!!.PostedBy != null) {
                                        if (!TextUtils.isEmpty(item.ParentComment!!.PostedBy!!.Name)) {
                                            holder.left_textview_replied_user_name.text =
                                                item.ParentComment!!.PostedBy!!.Name

                                            holder.left_textview_replied_time.text =
                                                CommonMethods.ChatTime(item.ParentComment!!.PostedOn)
                                        }
                                    }

                                    if (item.ParentComment!!.CommentType.equals("TXT")) {
                                        holder.left_textview_replied_message.visibility =
                                            View.VISIBLE
                                        holder.left_reply_media.visibility = View.GONE
                                        holder.left_textview_replied_message.text =
                                            item.ParentComment!!.CommentText

                                    } else {
                                        holder.left_textview_replied_message.visibility = View.GONE
                                        holder.left_reply_media.visibility = View.VISIBLE
//                                                Picasso.with(context)
//                                                    .load(item.ParentComment!!.CommentText)
//                                                    .into(holder.left_reply_img_media)
                                        if (!TextUtils.isEmpty(item.ParentComment!!.CommentText) && item.ParentComment!!.CommentText!!.startsWith(
                                                "http"
                                            )
                                        ) {
                                            Glide.with(context)
                                                .setDefaultRequestOptions(requestOptions)
                                                .load(item.ParentComment!!.CommentText)
                                                .listener(object : RequestListener<Drawable> {
                                                    override fun onLoadFailed(
                                                        e: GlideException?,
                                                        model: Any,
                                                        target: Target<Drawable>,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        return false
                                                    }

                                                    override fun onResourceReady(
                                                        resource: Drawable,
                                                        model: Any,
                                                        target: Target<Drawable>,
                                                        dataSource: DataSource,
                                                        isFirstResource: Boolean
                                                    ): Boolean {
                                                        return false
                                                    }
                                                })
                                                .into(holder.left_reply_img_media)
                                        }
                                        holder.left_reply_img_media.setOnClickListener(View.OnClickListener {

                                            fullSizeImage(
                                                item.ParentComment!!.CommentText!!,
                                                item.ParentComment!!.PostedBy!!.Name!!
                                            )
                                        })
                                    }
                                    if (ParentfirebaseMap.containsKey(item.ParentComment!!.FireBaseID)) {
                                        if (ParentfirebaseMap.get(item.ParentComment!!.FireBaseID)!!.Count as Long >= 5) {
                                            holder.left_reply_content.visibility = View.GONE
                                            holder.left_offensive_bubble.visibility = View.VISIBLE
                                        } else {


                                        }
                                    }
                                }
                            }
                        }

                        setEmoji(holder.left_emoji_default, item.RatingCount as Long)

                        holder.left_emoji_dark_blue.setOnClickListener(View.OnClickListener {

                            // holder.swipeLayout.close(true)
                            (context as ChatActivity).Rating(item, 3)
                            item.RatingCount = 3L
                            setEmoji(holder.left_emoji_default, item.RatingCount as Long)
                            holder.left_emoji_default.rotation = 0f
                            holder.left_emoji_default.setTag(0)
                            SlideOutRight(holder.left_emoji_dark_blue)
                            SlideOutRight(holder.left_emoji_yellow)
                            SlideOutRight(holder.left_emoji_green)
                            SlideOutRight(holder.left_emoji_light_blue)
                            SlideOutRight(holder.left_emoji_red)
                            notifyDataSetChanged()
                        })
                        holder.left_emoji_yellow.setOnClickListener(View.OnClickListener {

                            // holder.swipeLayout.close(true)
                            (context as ChatActivity).Rating(item, 2)
                            item.RatingCount = 2L
                            setEmoji(holder.left_emoji_default, item.RatingCount as Long)
                            holder.left_emoji_default.rotation = 0f
                            holder.left_emoji_default.setTag(0)
                            SlideOutRight(holder.left_emoji_dark_blue)
                            SlideOutRight(holder.left_emoji_yellow)
                            SlideOutRight(holder.left_emoji_green)
                            SlideOutRight(holder.left_emoji_light_blue)
                            SlideOutRight(holder.left_emoji_red)
                            notifyDataSetChanged()
                        })
                        holder.left_emoji_green.setOnClickListener(View.OnClickListener {

                            // holder.swipeLayout.close(true)
                            (context as ChatActivity).Rating(item, 4)
                            item.RatingCount = 4L
                            setEmoji(holder.left_emoji_default, item.RatingCount as Long)
                            holder.left_emoji_default.rotation = 0f
                            holder.left_emoji_default.setTag(0)
                            SlideOutRight(holder.left_emoji_dark_blue)
                            SlideOutRight(holder.left_emoji_yellow)
                            SlideOutRight(holder.left_emoji_green)
                            SlideOutRight(holder.left_emoji_light_blue)
                            SlideOutRight(holder.left_emoji_red)
                            notifyDataSetChanged()
                        })
                        holder.left_emoji_light_blue.setOnClickListener(View.OnClickListener {

                            // holder.swipeLayout.close(true)
                            (context as ChatActivity).Rating(item, 5)
                            item.RatingCount = 5L
                            setEmoji(holder.left_emoji_default, item.RatingCount as Long)
                            holder.left_emoji_default.rotation = 0f
                            holder.left_emoji_default.setTag(0)
                            SlideOutRight(holder.left_emoji_dark_blue)
                            SlideOutRight(holder.left_emoji_yellow)
                            SlideOutRight(holder.left_emoji_green)
                            SlideOutRight(holder.left_emoji_light_blue)
                            SlideOutRight(holder.left_emoji_red)
                            notifyDataSetChanged()
                        })
                        holder.left_emoji_red.setOnClickListener(View.OnClickListener {

                            // holder.swipeLayout.close(true)
                            (context as ChatActivity).Rating(item, 1)
                            item.RatingCount = 1L
                            setEmoji(holder.left_emoji_default, item.RatingCount as Long)
                            holder.left_emoji_default.rotation = 0f
                            holder.left_emoji_default.setTag(0)
                            SlideOutRight(holder.left_emoji_dark_blue)
                            SlideOutRight(holder.left_emoji_yellow)
                            SlideOutRight(holder.left_emoji_green)
                            SlideOutRight(holder.left_emoji_light_blue)
                            SlideOutRight(holder.left_emoji_red)
                            notifyDataSetChanged()
                        })




                        holder.left_emoji_default.bringToFront()

                        holder.left_emoji_default.setOnClickListener(View.OnClickListener {

                            if (holder.left_emoji_default.getTag() == 0) {
                                Rotate(holder.left_emoji_default)
                                holder.left_emoji_default.setTag(1)
                                SlideInRight(holder.left_emoji_dark_blue)
                                SlideInRight(holder.left_emoji_yellow)
                                SlideInRight(holder.left_emoji_green)
                                SlideInRight(holder.left_emoji_light_blue)
                                SlideInRight(holder.left_emoji_red)

                                Handler().postDelayed({
                                    Pulse(holder.left_emoji_dark_blue)
                                }, 500)
                                Handler().postDelayed({
                                    Pulse(holder.left_emoji_yellow)
                                }, 800)
                                Handler().postDelayed({
                                    Pulse(holder.left_emoji_green)
                                }, 1200)
                                Handler().postDelayed({
                                    Pulse(holder.left_emoji_light_blue)
                                }, 1500)
                                Handler().postDelayed({
                                    Pulse(holder.left_emoji_red)
                                }, 1800)

                            } else {
                                holder.left_emoji_default.rotation = 0f
                                holder.left_emoji_default.setTag(0)
                                SlideOutRight(holder.left_emoji_dark_blue)
                                SlideOutRight(holder.left_emoji_yellow)
                                SlideOutRight(holder.left_emoji_green)
                                SlideOutRight(holder.left_emoji_light_blue)
                                SlideOutRight(holder.left_emoji_red)
                            }

                        })
                    }
                    if (item.OffensiveClicked as Boolean) {
                        holder.img_offensive.visibility = View.GONE
                    }


                    holder.left_emoji_default.setTag(0)


                    holder.img_delete.setOnClickListener(View.OnClickListener {
                        holder.swipeLayout.postDelayed({
                            holder.swipeLayout.animateReset()
                        }, 50)
                        (context as ChatActivity).deleteData(item.FireBaseID!!, position)
                    })
                    holder.img_offensive.setOnClickListener(View.OnClickListener {
                        //                        mItemManger.closeAllItems()
                        holder.swipeLayout.postDelayed({
                            holder.swipeLayout.animateReset()
                        }, 50)





                        val popup =
                            PopupMenu(context, holder.layout_chat_item)
                        popup.setOnMenuItemClickListener { ite ->
                            when (ite.itemId) {
                                R.id.mark_as_item ->  // do your code
                                    true
                                R.id.report_item -> {
                                 // do your code
                                (context as ChatActivity).offensecountData(item!!)
                                    true
                            }
                                else -> false
                            }
                        }
                        popup.inflate(R.menu.chat_offensive_menu)
                        popup.show()

                    })

                    holder.img_reply.setOnClickListener(View.OnClickListener {

                        holder.swipeLayout.postDelayed({
                            holder.swipeLayout.animateReset()
                        }, 50)
                        (context as ChatActivity).ReplyData(item!!, position)
                    })


                }


            }
            }


//        holder?.show_name?.text = items.get(position)
        } catch (e: Exception) {

            e.printStackTrace()
            if (holder.layout_chat_item != null && context != null) {
            }
        }

    }

    override fun getItemCount(): Int {
        if (items != null) {
            return items!!.size
        }
        return 0
    }

    private fun displayPopupWindow(anchorView: View, txt: String, type: String) {
        val popup = PopupWindow(context)
        val layout = LayoutInflater.from(context).inflate(R.layout.popup_content, null)
        var txt_pop: TextView = layout.findViewById(R.id.txt_pop)
        var layout_image_view: RelativeLayout = layout.findViewById(R.id.layout_image_view)
        var imageview_media: ImageView = layout.findViewById(R.id.imageview_media)

        if (type.equals("TXT")) {
            layout_image_view.visibility = View.GONE
            txt_pop.visibility = View.VISIBLE
            txt_pop.setText(txt)
        } else {
            layout_image_view.visibility = View.VISIBLE
            txt_pop.visibility = View.GONE
            Picasso.with(context)
                .load(txt)
                .error(R.drawable.image_placeholder_1)
                .into(imageview_media, object : Callback {
                    override fun onSuccess() {
//                        holder.right_progress.visibility = View.GONE
                    }

                    override fun onError() {
//                        holder.right_progress.visibility = View.GONE
                    }
                })
        }
        popup.setContentView(layout)
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true)
        popup.setFocusable(true)
        popup.setBackgroundDrawable(BitmapDrawable())
        // Show anchored to button
        popup.showAtLocation(
            anchorView, Gravity.BOTTOM, 0,
            anchorView.bottom - 60
        )

        popup.showAsDropDown(anchorView)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun fullSizeImage(path: String, name: String) {
        val imageDialog = Dialog(context, android.R.style.Theme_Light)
        imageDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val newView = inflater.inflate(R.layout.fullsizeimagedialog, null) as View
        imageDialog.setContentView(newView)
        val iv = newView.findViewById<View>(R.id.image) as ImageView
        val back = newView.findViewById<View>(R.id.imageView40) as ImageView
        val username = newView.findViewById<View>(R.id.textView49) as TextView
        username.text = name
        if (!TextUtils.isEmpty(path) && path.startsWith("http")) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.placeholder)
//            Picasso.with(context).load(path).into(iv)
            Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(iv)
        } else {
            try {
                val bit = Base64toImage(path)
                iv.setImageBitmap(bit)
            } catch (r: Exception) {
            }

        }
        //        previewCapturedImage(path, iv);

        back.setOnClickListener { imageDialog.dismiss() }
        imageDialog.show()


    }

    private fun Base64toImage(encodedImage: String): Bitmap {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

private class MyViewHolder internal constructor(itemView: View) : ChatViewHolder(itemView)

private class MyOtherViewHolder internal constructor(itemView: View) : ChatViewHolder(itemView)


private fun Pulse(emoji: View) {
    YoYo.with(Techniques.Pulse)
        .duration(1000)
        .repeat(8)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);
}

private fun Rotate(emoji: View) {
    YoYo.with(Techniques.Wave)
        .duration(900)
        .repeat(3)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);
}

private fun SlideInLeft(emoji: View) {
    YoYo.with(Techniques.SlideInLeft)
        .duration(1000)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);
}

private fun SlideInRight(emoji: View) {
    YoYo.with(Techniques.SlideInRight)
        .duration(1000)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);


}

private fun SlideOutLeft(emoji: View) {
    YoYo.with(Techniques.SlideOutLeft)
        .duration(2000)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);
}


private fun SlideOutRight(emoji: View) {
    YoYo.with(Techniques.SlideOutRight)
        .duration(2000)
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
                emoji.setAlpha(1f)
            }

        })
        .playOn(emoji);

}

private fun setEmoji(emoji: ImageView, rating: Long) {

    if (rating == 1L) {
        emoji!!.setImageResource(R.drawable.emoji_red)
    } else if (rating == 2L) {
        emoji!!.setImageResource(R.drawable.emoji_yellow)
    } else if (rating == 3L) {
        emoji!!.setImageResource(R.drawable.emoji_dark_blue)
    } else if (rating == 5L) {
        emoji!!.setImageResource(R.drawable.emoji_light_blue)
    } else if (rating == 4L) {
        emoji!!.setImageResource(R.drawable.emoji_green)
    } else {
        emoji!!.setImageResource(R.drawable.emoji_default)
    }

}


open class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    val swipeLayout = view.swipe
    val ad_img = view.img
    val ad_layout = view.ad_layout
    val layout_msg_option = view.layout_msg_option
    val img_avatar_chat = view.img_avatar_chat
    val img_delete = view.img_delete
    val img_reply = view.img_reply
    val img_offensive = view.img_offensive


    //right
    val right_layout_chat = view.right_layout_chat
    val right_txt_msg_content = view.right_txt_msg_content
    val right_txt_chat_username = view.right_txt_chat_username
    val right_txt_chat_time = view.right_txt_chat_time
    val right_layout_reply_message = view.right_layout_reply_message
    val right_textview_replied_user_name = view.right_textview_replied_user_name
    val right_textview_replied_message = view.right_textview_replied_message
    val right_layout_image_view = view.right_layout_image_view
    val right_imageview_media = view.right_imageview_media
    val right_control = view.right_control
    val right_textview_replied_time = view.right_textview_replied_time
    val right_reply_content = view.right_reply_content
    val right_offensive_bubble = view.right_offensive_bubble
    val right_progress = view.right_progress
    val right_reply_img_media = view.right_reply_img_media
    val right_reply_media = view.right_reply_media

    //left
    val left_layout_chat = view.left_layout_chat
    val left_reply_content = view.left_reply_content
    val left_offensive_bubble = view.left_offensive_bubble
    val left_txt_chat_username = view.left_txt_chat_username
    val left_txt_msg_content = view.left_txt_msg_content
    val left_reaction_layout = view.left_reaction_layout
    val left_emoji_default = view.left_emoji_default
    val left_emoji_red = view.left_emoji_red
    val left_emoji_green = view.left_emoji_green
    val left_emoji_yellow = view.left_emoji_yellow
    val left_emoji_dark_blue = view.left_emoji_dark_blue
    val left_emoji_light_blue = view.left_emoji_light_blue
    val left_txt_chat_time = view.left_txt_chat_time
    val left_layout_reply_message = view.left_layout_reply_message
    val left_textview_replied_user_name = view.left_textview_replied_user_name
    val left_textview_replied_message = view.left_textview_replied_message
    val left_layout_image_view = view.left_layout_image_view
    val left_imageview_media = view.left_imageview_media
    val left_control = view.left_control
    val left_textview_replied_time = view.left_textview_replied_time
    val left_progress = view.left_progress
    val left_reply_img_media = view.left_reply_img_media
    val left_reply_media = view.left_reply_media


    val layout_chat_item = view.layout_chat_item
    val offensive_bubble = view.offensive_bubble
    val txt_date_header = view.txt_date_header


    val chat_content_parent = view.chat_content_parent


}
