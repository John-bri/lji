package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.*
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
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.ChatActivity
import au.com.letsjoinin.android.UI.fragment.ChannelFragment
import au.com.letsjoinin.android.UI.fragment.MainFragment
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import kotlinx.android.synthetic.main.timeline_adapter.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Created by FILM on 01.02.2016.
 */
class RecyclerAdapter(val items: ArrayList<SearchUserDocData>?, val context: Context, val parent: View) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mPrefMgr = PreferenceManager.getInstance();
    val str: ArrayList<String> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(context).inflate(
                R.layout.timeline_adapter,
                parent,
                false
            )
        )

    }

    fun remove(position: Int) {
        items!!.removeAt(position)
        notifyItemRemoved(position)
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
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
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
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
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
                                FavUserRefData.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
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
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
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
                    .anchorView( widget)
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

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        try {


            val holder: MainHolder = viewHolder.itemView as MainHolder


            if (items != null) {
                val programListData = items!!.get(position)




                if (programListData != null) {
                    holder.parent.setOnClickListener(View.OnClickListener {
                        if(CommonMethods.isNetworkAvailable(context)) {
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("ProgramDataID", programListData.id)
                            intent.putExtra("ContentType", programListData.ContentType)
                            intent.putExtra("BlockPosition", 0)
                            context.startActivity(intent)
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context.getString(R.string.network_error),false)
                            }
                        }
//                        val fragment = AllProgramFragment()
//                        if ((context as AppCompatActivity).supportFragmentManager != null) {
//                            val transaction = (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
//                            transaction.replace(R.id.container_fragment, fragment)
//                            transaction.addToBackStack(null)
//                            val args = Bundle()
//                            args.putString("ProgramDataID", programListData.id)
//                            args.putString("ProgramDescription", programListData.Description)
//                            args.putString("ProgramContentType", programListData.ContentType)
//                            args.putString("ProgramTitle", programListData.Title)
//                            args.putString("ProgramTitle", programListData.Title)
//                            if(programListData.ChannelInfo != null)
//                            {
//                                args.putString("ProgramChannelName", programListData.ChannelInfo!!.Name)
//                                args.putString("ProgramChannelId", programListData.ChannelInfo!!.ChannelID)
//                                args.putString("ProgramPKCountry", programListData.ChannelInfo!!.PKCountry)
//                            }
//                            if (programListData.ContentType!!.contains("http")) {
//                                args.putString("ContentType", "image")
//                            } else {
//                                args.putString("ContentType", "video")
//                            }
//                            fragment.setArguments(args)
//                            transaction.commit()
//                        }
                    })

                    holder.txt_timeline_item_type.setOnClickListener(View.OnClickListener {
                        if(CommonMethods.isNetworkAvailable(context)) {
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
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context.getString(R.string.network_error),false)
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

                        if(CommonMethods.isNetworkAvailable(context)) {
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
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context.getString(R.string.network_error),false)
                            }
                        }

                    })
                    holder.item_favourite.setOnClickListener(View.OnClickListener {




                        if(CommonMethods.isNetworkAvailable(context)) {
                            if (!isFavourite) {
                                isFavourite = true
                                var input: SetFavReqClass = SetFavReqClass()
                                var FavUserRefData: FavUserRef = FavUserRef()
                                FavUserRefData.AvatarPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
                                FavUserRefData.Name =
                                    mPrefMgr!!.getString(
                                        AppConstant.FirstName,
                                        AppConstant.EMPTY
                                    ) + " " + mPrefMgr!!.getString(
                                        AppConstant.LastName,
                                        AppConstant.EMPTY
                                    )
                                FavUserRefData.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                                FavUserRefData.SetFavouriteOn = null
                                input.ContentID = programListData.id
                                input.PKContentType = programListData.ContentType
                                input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                input.FavouriteBy = FavUserRefData
                                SetFavourite(input, programListData)
                            } else {
                                isFavourite = false
                                var input: RemoveFavReqClass = RemoveFavReqClass()
                                input.ContentID = programListData.id
                                input.PKContentType = programListData.ContentType
                                input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
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
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context.getString(R.string.network_error),false)
                            }
                        }


                    })


                    if (programListData.ChannelInfo != null) {
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
                        var path : String = programListData.MediaPath.toString()
                        if (programListData.ContentType.equals("VIDEO") || programListData.ContentType.equals("LIVE") || programListData.ContentType.equals(
                                "NEWS"
                            )
                        ) {
                            if(!TextUtils.isEmpty(programListData.CoverImagePath))
                            {
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

        } catch (e: Exception) {
            e.printStackTrace()
            if (parent != null && context != null) {
                CommonMethods.SnackBar(parent, context.getString(R.string.error), false)
            }
        }
    }


//    fun getItem(position: Int): String {
//        return items[position]
//    }
//
//
//    fun setItem(index: Int, item: String) {
//        items[index] = item
//        notifyItemChanged(index)
//    }
//
//    fun setItems(startPosition: Int, count: Int, item: String) {
//        val last = startPosition + count
//        for (i in startPosition until last) items[i] = item
//        notifyItemRangeChanged(startPosition, count)
//    }

}

class MainHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivImageView = view.timeline_item_imageView
    val parent = view.layout_content_all
    val lay_item_favourite = view.lay_item_favourite
    val lay_item_share = view.lay_timeline_item_share

    val item_favourite = view.item_favourite
    val item_share = view.item_share
    val txt_timeline_item_type = view.txt_timeline_item_type
    val txt_timeline_item_name = view.txt_timeline_item_name
    val txt_timeline_item_desc = view.txt_timeline_item_desc
    val txt_timeline_item_part_count = view.txt_timeline_item_part_count
    val timeline_loading = view.timeline_loading
}
