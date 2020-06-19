package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.ChatActivity
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
import kotlinx.android.synthetic.main.user_profile_list_adapter.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 */
class UserMyContentListAdapter(
    val favListStr: ArrayList<String>?, val items: ArrayList<ContentsClass>?,
    val context: Context,
    val parent: View,
    val frag: ProfileDetails
) :
    RecyclerView.Adapter<UserProfileMyContentViewHolder>() {
    var mPrefMgr = PreferenceManager.getInstance();
    var fragment = frag
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProfileMyContentViewHolder {
        return UserProfileMyContentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.user_profile_list_adapter,
                parent,
                false
            )
        )
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

    override fun onBindViewHolder(holder: UserProfileMyContentViewHolder, position: Int) {
//        holder?.show_name?.text = items.get(position)

//        val livePrograms = items.get(position)
        try {
            if (items != null) {
                val programListData = items!!.get(position)




                if (programListData != null) {
                    holder.parent.setOnClickListener(View.OnClickListener {

                        if(CommonMethods.isNetworkAvailable(context)) {
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("ProgramDataID", programListData.ContentID)
                            intent.putExtra("BlockPosition", 0)
                            intent.putExtra("ContentType", programListData.ContentType)
//                        val g = Gson()
//                        intent.putExtra("ChatTopicData", g.toJson(programListData))
                            context.startActivity(intent)
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }

//                        val fragment = AllProgramFragment()
//                        if ((context as AppCompatActivity).supportFragmentManager != null) {
//                            val transaction = (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
//                            transaction.replace(R.id.container_fragment, fragment)
//                            transaction.addToBackStack(null)
//                            val args = Bundle()
//                            args.putString("ProgramDataID", programListData.ContentID)
//                            fragment.setArguments(args)
//                            transaction.commit()
//                        }
                    })


                    holder.channel_lay_item_share.setOnClickListener(View.OnClickListener {
                        if(CommonMethods.isNetworkAvailable(context)) {
                            GetShareDetails(programListData.ContentID!!, programListData.ContentType!!)
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
                                .playOn(holder.channel_item_share_image)
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }



                    })

                    var isFavourite = false

                    if (favListStr != null) {
                        if (favListStr!!.contains(programListData.ContentID)) {
                            holder.item_favourite.isChecked = true
                            isFavourite = true

                        } else {
                            holder.item_favourite.isChecked = false
                        }
                    } else {
                        holder.item_favourite.isChecked = false
                    }
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
                                input.ContentID = programListData.ContentID
                                input.PKContentType = programListData.ContentType
                                input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                input.FavouriteBy = FavUserRefData
                                SetFavourite(input, programListData)
                            } else {
                                isFavourite = false
                                var input: RemoveFavReqClass = RemoveFavReqClass()
                                input.ContentID = programListData.ContentID
                                input.PKContentType = programListData.ContentType
                                input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                                input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                                favListStr!!.remove(programListData.ContentID)
                                RemoveFavourite(input)
                            }
                        }else{
                            if(parent != null) {
                                CommonMethods.SnackBar(parent,context!!.getString(R.string.network_error),false)
                            }
                        }


                    })
//                    if(programListData.Channel != null) {
//                        holder.channel_txt_program_item_type.text = programListData.Channel!!.Name
//                    }else if(programListData.CreatedBy != null) {
//                        holder.channel_txt_program_item_type.text = programListData.CreatedBy!!.Name
//                    }
                    holder.txt_program_item_part_count.text = programListData.GroupsCount
                    holder.txt_program_item_desc.text = programListData.Description
                    if(!TextUtils.isEmpty(programListData.Description)) {

                        if (programListData.Description!!.length > 100) {
                            singleTextView(
                                holder.txt_program_item_desc,
                                programListData.Description!!,
                                programListData.ContentID!!
                            )
                        }
                    }
                    holder.channel_txt_program_item_name.text = programListData.CreatedBy!!.Name
                    holder.channel_txt_program_item_title.text = programListData.Title
                    if (programListData.MediaPath != null) {
                        Picasso.with(context)
                            .load(programListData.MediaPath)
                            .error(R.drawable.image_placeholder_1)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.channel_ivImageView)
                    }
//                            ,object : Callback {
//                            override fun onSuccess() {
//                                holder.timeline_loading.visibility = View.GONE
//                            }
//
//                            override fun onError() {
//                                holder.timeline_loading.visibility = View.GONE
//                            }
//                        })
//                    if (programListData.ContentType.equals("VIDEO")) {
//                        holder.brightcoveVideoView.visibility = View.VISIBLE
//                        val mediaController: BrightcoveMediaController? = null
//                        holder.brightcoveVideoView.setMediaController(mediaController)
//                        holder.ivImageView.visibility = View.GONE
//                        val eventEmitter = holder.brightcoveVideoView.eventEmitter
//                        val catalog = Catalog(
//                            eventEmitter,
//                            "5854923465001",
//                            "BCpkADawqM32a1trLarQBq5VBf4IAV3MxzyPdh0arBbwudK1xspJpHvVSM6Uxkqgprq55mHZCGm6-4tN_sdgIbd3Pf_aDYN7LXjpNNhw-o95zFs0bXRH-pgUrv2w2lb7TLCuzT9oAfo_yRaw"
//                        )
//                        holder.timeline_loading.visibility = View.GONE
//                        catalog.findVideoByID(programListData.MediaPath.toString(), object : VideoListener() {
//
//                            override fun onVideo(video: Video) {
//                                holder.timeline_loading.visibility = View.GONE
//                                holder.brightcoveVideoView.add(video)
//                            }
//                        })
//                    } else {
//                        holder.brightcoveVideoView.visibility = View.GONE
//                        holder.ivImageView.visibility = View.VISIBLE
//                    }
                }
            }
        } catch (e: Exception) {
            if (holder.parent != null && context != null) {
//                CommonMethods.SnackBar( holder.parent, context.getString(R.string.error), false)
            }
        }

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        if (items != null) {
            return items!!.size
        }
        return 0
    }

    private fun SetFavourite(input: SetFavReqClass, programListData: ContentsClass) {
        if(frag != null && frag. progress_lyt != null) {
            frag.progress_lyt!!.visibility = View.VISIBLE
            frag.progress_lyt!!.bringToFront()
        }
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.SetFavourite(input)
        call.enqueue(object : retrofit2.Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                if(frag != null && frag. progress_lyt != null) {
                    frag.progress_lyt!!.visibility = View.GONE
                }
                val res = response.body()
                if (context != null) {
                    if (res != null) {
                        if (res.Status.equals(AppConstant.SUCCESS)) {
                            fragment.getprofileData()
                            CommonMethods.SnackBar(parent, res.DisplayMsg, false)
//                            if (programListData.FavouritesBy != null) {
//                                var FavUserRefData: ProgramListAdminClass = ProgramListAdminClass()
//                                FavUserRefData.AvatarPath =
//                                    mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY)
//                                FavUserRefData.Name =
//                                    mPrefMgr!!.getString(
//                                        AppConstant.FirstName,
//                                        AppConstant.EMPTY
//                                    ) + " " + mPrefMgr!!.getString(
//                                        AppConstant.LastName,
//                                        AppConstant.EMPTY
//                                    )
//                                FavUserRefData.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
//                                programListData.FavouritesBy.put(
//                                    mPrefMgr!!.getString(
//                                        AppConstant.USERID,
//                                        AppConstant.EMPTY
//                                    ), FavUserRefData
//                                )
//                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                if(frag != null && frag. progress_lyt != null) {
                    frag.progress_lyt!!.visibility = View.GONE
                }
            }
        })
    }

    private fun RemoveFavourite(input: RemoveFavReqClass) {
        if(frag != null && frag. progress_lyt != null) {
            frag.progress_lyt!!.visibility = View.VISIBLE
            frag.progress_lyt!!.bringToFront()
        }
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.RemoveFavourite(input)
        call.enqueue(object : retrofit2.Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                if(frag != null && frag. progress_lyt != null) {
                    frag.progress_lyt!!.visibility = View.GONE
                }
                val res = response.body()
                if (parent != null && context != null) {
                    fragment.getprofileData()
                    CommonMethods.SnackBar(parent, res.DisplayMsg, false)
                    notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                if(frag != null && frag. progress_lyt != null) {
                    frag.progress_lyt!!.visibility = View.GONE
                }
            }
        })
    }
}

class UserProfileMyContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val channel_ivImageView = view.program_item_imageView
    val parent = view.layout_content_all
    val channel_lay_item_favourite = view.lay_item_favourite
    val channel_lay_item_share = view.lay_item_share
    val item_favourite = view.item_favourite
    val channel_item_share_image = view.channel_item_share_image
    val channel_txt_program_item_name = view.txt_program_item_name
    val txt_program_item_desc = view.txt_program_item_desc
    val channel_txt_program_item_title = view.txt_program_item_title
    val txt_program_item_part_count = view.txt_program_item_part_count
}
