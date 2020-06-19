package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.EasyFlipView
import au.com.letsjoinin.android.UI.activity.ChatActivity
import au.com.letsjoinin.android.UI.activity.RedeemActivity
import au.com.letsjoinin.android.UI.fragment.ChannelFragment
import au.com.letsjoinin.android.UI.view.DynamicHeightImageView
import au.com.letsjoinin.android.exoplayer.MainActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.JustifiedTextView
import au.com.letsjoinin.android.utils.PreferenceManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.exoplayer2.ui.PlayerView
import com.sackcentury.shinebuttonlib.ShineButton
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import net.alexandroid.utils.exoplayerhelper.ExoAdListener
import net.alexandroid.utils.exoplayerhelper.ExoPlayerHelper
import net.alexandroid.utils.exoplayerhelper.ExoPlayerListener
import net.alexandroid.utils.exoplayerhelper.ExoThumbListener
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


/**
 */


class SearchListAdapter(
    val items: ArrayList<SearchUserDocData>?,
    val context: Context,
    val parent: View
) :
    RecyclerView.Adapter<SearchListAdapter.MyViewHolder>() ,
    ExoPlayerListener, ExoAdListener, ExoThumbListener {
    var mExoPlayerHelper: ExoPlayerHelper? = null
    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var flipView: EasyFlipView? = null
        //        var back_content: LinearLayout
//        var front_content: RelativeLayout
        var ivImageView: DynamicHeightImageView
        var parent: LinearLayout
        var lock_view: LinearLayout
        var layout_front_view: RelativeLayout
        var ad_layout: RelativeLayout
        var lay_item_favourite: RelativeLayout
        var lay_item_share: RelativeLayout
        var txt_invitation_code: TextView
        var item_favourite: ShineButton
        var item_share: ImageView
        var play_exo_player_ad: ImageView
        var title: TextView
        var txt_timeline_item_type: TextView
        var txt_timeline_item_name: TextView
        var txt_timeline_item_desc: JustifiedTextView
        var txt_timeline_item_part_count: TextView
        var timeline_loading: GifImageView
        init {

            ivImageView =
                view.findViewById<View>(R.id.timeline_item_imageView) as DynamicHeightImageView
            flipView = view.findViewById(R.id.flipView) as EasyFlipView
            lock_view = view.findViewById(R.id.lock_view) as LinearLayout


            flipView!!.setToHorizontalType()
            flipView!!.setFlipTypeFromLeft()


//            back_content =
//                view.findViewById<View>(R.id.back_content) as LinearLayout
//
//
//            front_content =
//                view.findViewById<View>(R.id.front_content) as RelativeLayout


            parent =
                view.findViewById<View>(R.id.layout_content_all) as LinearLayout


            layout_front_view =
                view.findViewById<View>(R.id.layout_front_view) as RelativeLayout

            txt_invitation_code =
                view.findViewById<View>(R.id.txt_invitation_code) as TextView
            ad_layout =
                view.findViewById<View>(R.id.ad_layout) as RelativeLayout


            lay_item_favourite =
                view.findViewById<View>(R.id.lay_item_favourite) as RelativeLayout

            lay_item_share =
                view.findViewById<View>(R.id.lay_timeline_item_share) as RelativeLayout


            item_favourite =
                view.findViewById<View>(R.id.item_favourite) as ShineButton


            item_share =
                view.findViewById<View>(R.id.item_share) as ImageView

            play_exo_player_ad =
                view.findViewById<View>(R.id.play_exo_player_ad) as ImageView

            txt_timeline_item_type =
                view.findViewById<View>(R.id.txt_timeline_item_type) as TextView

            title =
                view.findViewById<View>(R.id.title) as TextView


            txt_timeline_item_name =
                view.findViewById<View>(R.id.txt_timeline_item_name) as TextView


            txt_timeline_item_desc =
                view.findViewById<View>(R.id.txt_timeline_item_desc) as JustifiedTextView


            txt_timeline_item_part_count =
                view.findViewById<View>(R.id.txt_timeline_item_part_count) as TextView


            timeline_loading =
                view.findViewById<View>(R.id.timeline_loading) as GifImageView

            val distance = 8000
            val scale: Float = context.getResources().getDisplayMetrics().density * distance
//            back_content.setCameraDistance(scale)
//            front_content.setCameraDistance(scale)


        }
    }


    var mPrefMgr = PreferenceManager.getInstance();
    val str: ArrayList<String> = ArrayList()
    @Throws(java.lang.Exception::class)
    fun flipCard(
        flipView: EasyFlipView?, layout_front_view: RelativeLayout, lock_view: LinearLayout
    ) {

        val params: ViewGroup.LayoutParams =
            lock_view.getLayoutParams()
        params.height = layout_front_view.height
        lock_view.setLayoutParams(params)
        flipView!!.flipDuration = 700
        flipView!!.flipTheView()
        Handler().postDelayed({
            flipView!!.flipDuration = 500
            flipView!!.flipTheView()
        }, 4000)

    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return position
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timeline_adapter_flip, parent, false)
        return MyViewHolder(itemView)
    }


    fun remove(position: Int) {
        items!!.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: SearchListAdapter.MyViewHolder, position: Int) {

        try {


            if (items != null) {
                val programListData = items!!.get(position)




                if (programListData != null) {


                    if (programListData.ContentType.equals("AD")) {


                        holder.ad_layout.visibility = View.VISIBLE
                        holder.play_exo_player_ad.visibility = View.GONE
                        holder.play_exo_player_ad.bringToFront()
                        holder.parent.visibility = View.GONE
                        var titleTxt: String =
                            mPrefMgr.getString(AppConstant.ADTitle,AppConstant.EMPTY)
                        if(titleTxt != null) {
                            holder.title.text = titleTxt
                        }

                    } else {

                        holder.txt_invitation_code.setOnClickListener(View.OnClickListener {



                            val intent = Intent(context, RedeemActivity::class.java)
//                            intent.putExtra("ProgramDataID", programListData.id)
//                            intent.putExtra("BlockPosition", 0)
//                            intent.putExtra("ContentType", programListData.ContentType)
                            context.startActivity(intent)

                        })
                        holder.ad_layout.visibility = View.GONE
                        holder.parent.visibility = View.VISIBLE
                        holder.parent.setOnClickListener(View.OnClickListener {

                            if (programListData!!.isFlipped!!) {
                                flipCard(
                                    holder.flipView!!, holder.layout_front_view, holder.lock_view
                                )


                            } else {
                                if (CommonMethods.isNetworkAvailable(context)) {
                                    val intent = Intent(context, ChatActivity::class.java)
                                    intent.putExtra("ProgramDataID", programListData.id)
                                    intent.putExtra("ContentType", programListData.ContentType)
                                    intent.putExtra("BlockPosition", 0)
                                    context.startActivity(intent)
                                } else {
                                    if (parent != null) {
                                        CommonMethods.SnackBar(
                                            parent,
                                            context.getString(R.string.network_error),
                                            false
                                        )
                                    }
                                }
                            }


                        })

                        holder.txt_timeline_item_type.setOnClickListener(View.OnClickListener {
                            if (CommonMethods.isNetworkAvailable(context)) {
                                if (!programListData.ContentType!!.equals("TOPIC")) {
                                    val fragment = ChannelFragment()
                                    if ((context as AppCompatActivity).supportFragmentManager != null) {
                                        val transaction =
                                            (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
                                        transaction.replace(R.id.container_fragment, fragment)
                                        transaction.addToBackStack(null)
                                        val args = Bundle()
                                        args.putString(
                                            "ChannelID",
                                            programListData.ChannelInfo!!.ChannelID
                                        )
                                        args.putString(
                                            "ProgramPKCountry",
                                            programListData.ChannelInfo!!.PKCountry
                                        )
                                        args.putString(
                                            "ContentType",
                                            programListData.ContentType
                                        )
                                        fragment.setArguments(args)
                                        transaction.commit()
                                    }
                                }
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                        parent,
                                        context.getString(R.string.network_error),
                                        false
                                    )
                                }
                            }

                        })


                        var isFavourite = false

                        if (programListData.FavouritesBy != null) {
                            if (programListData.FavouritesBy.containsKey(
                                    mPrefMgr!!.getString(
                                        AppConstant.USERID,
                                        AppConstant.EMPTY
                                    )
                                )
                            ) {
                                holder.item_favourite.isChecked = true
                                isFavourite = true

                            } else {
                                holder.item_favourite.isChecked = false
                            }
                        } else {
                            holder.item_favourite.isChecked = false
                        }
                        holder.lay_item_share.setOnClickListener(View.OnClickListener {

                            if (CommonMethods.isNetworkAvailable(context)) {
                                GetShareDetails(programListData.id!!, programListData.ContentType!!)


                                YoYo.with(Techniques.Bounce)
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
                                    .playOn(holder.item_share)
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                        parent,
                                        context.getString(R.string.network_error),
                                        false
                                    )
                                }
                            }

                        })
                        holder.item_favourite.setOnClickListener(View.OnClickListener {


                            if (CommonMethods.isNetworkAvailable(context)) {
                                if (!isFavourite) {
                                    isFavourite = true
                                    var input: SetFavReqClass = SetFavReqClass()
                                    var FavUserRefData: FavUserRef = FavUserRef()
                                    FavUserRefData.AvatarPath =
                                        mPrefMgr!!.getString(
                                            AppConstant.AvatarPath,
                                            AppConstant.EMPTY
                                        )
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
                                    input.ContentID = programListData.id
                                    input.PKContentType = programListData.ContentType
                                    input.PKUserCountry =
                                        mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                    input.FavouriteBy = FavUserRefData
                                    SetFavourite(input, programListData)
                                } else {
                                    isFavourite = false
                                    var input: RemoveFavReqClass = RemoveFavReqClass()
                                    input.ContentID = programListData.id
                                    input.PKContentType = programListData.ContentType
                                    input.PKUserCountry =
                                        mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                    input.UserID =
                                        mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                                    if (programListData.FavouritesBy != null) {
                                        programListData.FavouritesBy.remove(
                                            mPrefMgr!!.getString(
                                                AppConstant.USERID,
                                                AppConstant.EMPTY
                                            )
                                        )
                                    }
                                    RemoveFavourite(input)
                                }
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                        parent,
                                        context.getString(R.string.network_error),
                                        false
                                    )
                                }
                            }


                        })


                        if (programListData.ChannelInfo != null) {
                            programListData!!.isFlipped = true
                            holder.txt_timeline_item_type.text = programListData.ChannelInfo!!.Name
                        } else if (programListData.CreatedBy != null) {
                            holder.txt_timeline_item_type.text = programListData.CreatedBy!!.Name
                        }
                        holder.txt_timeline_item_part_count.text = programListData.CreditPoints
                        holder.txt_timeline_item_desc.text = programListData.Description
                        holder.txt_timeline_item_name.text = programListData.Title



                        if (!TextUtils.isEmpty(programListData.Description)) {

                            if (programListData.Description!!.length > 100) {
                                singleTextView(
                                    holder.txt_timeline_item_desc,
                                    programListData
                                )
                            }
                        }

                        if (!TextUtils.isEmpty(programListData.MediaPath)) {
                            holder.ivImageView.visibility = View.VISIBLE
                            var path: String = programListData.MediaPath.toString()
                            if (programListData.ContentType.equals("VIDEO") || programListData.ContentType.equals(
                                    "LIVE"
                                ) || programListData.ContentType.equals(
                                    "NEWS"
                                )
                            ) {
                                if (!TextUtils.isEmpty(programListData.CoverImagePath)) {
                                    path = programListData.CoverImagePath.toString()
                                }
                            }
                            Picasso.with(context)
                                .load(path)
                                .error(R.drawable.image_placeholder_1)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(holder.ivImageView, object : Callback {
                                    override fun onSuccess() {
                                        holder.timeline_loading.visibility = View.GONE
                                    }

                                    override fun onError() {
                                        holder.timeline_loading.visibility = View.GONE
                                    }
                                })
                        } else {
                            holder.timeline_loading.visibility = View.GONE
                        }
                    }
                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
            if (parent != null && context != null) {
                CommonMethods.SnackBar(parent, context.getString(R.string.error), false)
            }
        }

    }

    fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString)


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
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun SetFavourite(input: SetFavReqClass, programListData: SearchUserDocData) {
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
                if (context != null) {
                    if (res != null) {
                        if (res.Status.equals(AppConstant.SUCCESS)) {
                            CommonMethods.SnackBar(parent, res.DisplayMsg, false)
                            if (programListData.FavouritesBy != null) {
                                var FavUserRefData: ProgramListAdminClass = ProgramListAdminClass()
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
                                programListData.FavouritesBy.put(
                                    mPrefMgr!!.getString(
                                        AppConstant.USERID,
                                        AppConstant.EMPTY
                                    ), FavUserRefData
                                )
                            }
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
        call.enqueue(object : retrofit2.Callback<ServerMessageData> {
            override fun onResponse(
                call: Call<ServerMessageData>,
                response: Response<ServerMessageData>
            ) {
                val res = response.body()
                if (parent != null && context != null) {
                    CommonMethods.SnackBar(parent, res.DisplayMsg, false)
                    notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {

            }
        })
    }

    private fun singleTextView(textView: TextView, programListData: SearchUserDocData) {
        val str: String = programListData.Description!!.substring(0, 100)

        val spanText = SpannableStringBuilder()
        spanText.append(str)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("ProgramDataID", programListData.id)
                intent.putExtra("BlockPosition", 0)
                intent.putExtra("ContentType", programListData.ContentType)
                context.startActivity(intent)
//                val fragment = AllProgramFragment()
//                if ((context as AppCompatActivity).supportFragmentManager != null) {
//                    val transaction = (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
//                    transaction.replace(R.id.container_fragment, fragment)
//                    transaction.addToBackStack(null)
//                    val args = Bundle()
//                    args.putString("ProgramDataID", id)
//                    args.putString("ContentType", "image")
//                    fragment.setArguments(args)
//                    transaction.commit()
//                }
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.isUnderlineText = false    // this remove the underline
            }
        }, 0, spanText.length, 0)



        spanText.append("...see more")
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {

                SimpleTooltip.Builder(context)
                    .anchorView(widget)
                    .modal(true)
                    .text(programListData.Description!!)
                    .gravity(Gravity.TOP)
                    .arrowColor(context.resources.getColor(R.color.colorPrimaryDark))
                    .dismissOnOutsideTouch(true)
                    .dismissOnInsideTouch(true)
                    .contentView(R.layout.tooltip_image_view, R.id.txt_tooltip)
                    .build()
                    .show()

            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor    // you can use custom color
                textPaint.isUnderlineText = true    // this remove the underline
            }
        }, spanText.length - "...see more".length, spanText.length, 0)

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spanText, TextView.BufferType.SPANNABLE)

    }

    fun ellipsizeText(tv: TextView) {
        val maxLength = 100
        if (tv.getText().length > maxLength) {
            val currentText = tv.getText().toString()
            val ellipsizedText = currentText.substring(0, maxLength - 3) + "..."
            val styledText = ellipsizedText + "<font color='#ff592384'>see more</font>."
            tv.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        if (items == null) {
            return 0
        } else {
            return items.size
        }
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
        return false
    }

    override fun onVideoTapped() {

    }

    override fun onFullScreenBtnTap() {

    }

    override fun onPlayerStateEnded(currentWindowIndex: Int) {

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
            mPrefMgr.getString(AppConstant.ADTimeLineImagePath, null)
        if (!TextUtils.isEmpty(path)) {
//            Picasso.with(context)
//                .load(path)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
//                .placeholder(R.drawable.image_placeholder_1)
//                .into(imageView)
        }
    }


}

