package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.ProgramListAdminClass
import au.com.letsjoinin.android.MVP.model.ServerMessageData
import au.com.letsjoinin.android.MVP.model.SetFollowReq
import au.com.letsjoinin.android.MVP.model.SetUnFollowReq
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.fragment.ChannelFollowing
import au.com.letsjoinin.android.UI.fragment.ChannelFragment
import au.com.letsjoinin.android.UI.fragment.ProfileDetails
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.user_following_list_adapter.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 */
class UserFollowingListAdapter(
    val items: ArrayList<ChannelFollowing>?,
    val context: Context,
    val parent: View,
    val frag: ProfileDetails
) :
    RecyclerView.Adapter<UserProfileFollowingListViewHolder>() {
    var mPrefMgr = PreferenceManager.getInstance();
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileFollowingListViewHolder {
        return UserProfileFollowingListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.user_following_list_adapter,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserProfileFollowingListViewHolder, position: Int) {
//        holder?.show_name?.text = items.get(position)

//        val livePrograms = items.get(position)
        try {
            if (items != null) {
                val programListData = items!!.get(position)




                if (programListData != null) {
                    holder.parent.setOnClickListener(View.OnClickListener {




                        if(CommonMethods.isNetworkAvailable(context)) {
                            val fragment = ChannelFragment()
                            if ((context as AppCompatActivity).supportFragmentManager != null) {
                                val transaction =
                                    (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
                                transaction.replace(R.id.container_fragment, fragment)
                                transaction.addToBackStack(null)
                                val args = Bundle()
                                args.putString(
                                    "ChannelID",
                                    programListData.ChannelID
                                )
                                args.putString(
                                    "ProgramPKCountry",
                                    programListData.PKCountry
                                )
                                args.putString(
                                    "ContentType",
                                    ""
                                )
                                fragment.setArguments(args)
                                transaction.commit()
                            }
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }

                    })


                    holder.lay_channel_item_share.setOnClickListener(View.OnClickListener {
                        if(CommonMethods.isNetworkAvailable(context)) {
                            YoYo.with(Techniques.Bounce)
                                .duration(1300)
                                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                                .interpolate(AccelerateDecelerateInterpolator())
                                .withListener(object : Animator.AnimatorListener {
                                    override fun onAnimationRepeat(animation: Animator?) {
                                    }

                                    override fun onAnimationEnd(animation: Animator?) {
                                        val sharingIntent = Intent(Intent.ACTION_SEND)
                                        sharingIntent.type = "text/plain"
//                            String shareBody = "http://lji.userportal.com/SharedLink";
//                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Msg")
                                        context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
                                    }

                                    override fun onAnimationCancel(animation: Animator?) {
                                    }

                                    override fun onAnimationStart(animation: Animator?) {
                                    }

                                })
                                .playOn(holder.item_share)
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }

                    })


                    holder.lay_channel_item_follow.setOnClickListener(View.OnClickListener {
                        if(CommonMethods.isNetworkAvailable(context)) {
                            frag.SetUnFollow( programListData.ChannelID!!,programListData.PKCountry!!)
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }

                    })
                    holder.text_count.text = "0 Program(s)"
                    if (!TextUtils.isEmpty(programListData.ProgramCount)) {
                        holder.text_count.text = programListData.ProgramCount + " Program(s)"
                    }
//                    holder.txt_program_item_content.text = programListData.Description
                    holder.text_chnl_name.text = programListData.Name
//                    holder.channel_txt_program_item_title.text = programListData.Name

                    if(!TextUtils.isEmpty(programListData.Description)) {

//                        if (programListData.Description!!.length > 100) {
//                            singleTextView(
//                                holder.txt_program_item_content,
//                                programListData.Description!!,
//                                programListData.ContentID!!
//                            )
//                        }
                    }
                    Picasso.with(context)
                        .load(programListData.LogoPath)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(holder.channel_ivImageView)
//                            ,object : Callback {
//                            override fun onSuccess() {
//                                holder.timeline_loading.visibility = View.GONE
//                            }
//
//                            override fun onError() {
//                                holder.timeline_loading.visibility = View.GONE
//                            }
//                        })
                }
            }
        } catch (e: Exception) {
            if (holder.parent != null && context != null) {
//                CommonMethods.SnackBar( holder.parent, context.getString(R.string.error), false)
            }
        }

    }
    private fun singleTextView(textView: TextView, ContentDesc: String, _id : String) {
        val str : String = ContentDesc.substring(0,100)

        val spanText = SpannableStringBuilder()
        spanText.append(str)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.isUnderlineText = false    // this remove the underline
            }
        }, 0, spanText.length, 0)
        spanText.append("...see more")
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {

                SimpleTooltip.Builder(context)
                    .anchorView( widget)
                    .modal(true)
                    .text(ContentDesc!!)
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

    private fun SetFollow(ChannelID: String, PKChannelCountry: String) {
        val input: SetFollowReq = SetFollowReq()
        val followBy: ProgramListAdminClass = ProgramListAdminClass()
        input.ChannelID = ChannelID
        input.PKChannelCountry = PKChannelCountry
        input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
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
                if (response.body() != null) {
                    var res = response.body()
                    if (res.Status.equals(AppConstant.SUCCESS)) {
                    }
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
            }
        })
    }



    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        if (items != null) {
            return items!!.size
        }
        return 0
    }

}

class UserProfileFollowingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val channel_ivImageView = view.channel_dash_image
    val parent = view.channel_item_parent
    val lay_channel_item_share = view.lay_channel_item_share
    val item_share = view.item_share
    val text_chnl_name = view.text_chnl_name
    val text_count = view.text_count
    val lay_channel_item_follow = view.lay_channel_item_follow

}
