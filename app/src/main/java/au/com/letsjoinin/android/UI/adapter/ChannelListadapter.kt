package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.ChatActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.model.Video
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.program_list_adapter.view.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 */
class ChannelListadapter(val items: ArrayList<ContentsClass>?, val context: Context, val channelName: String, val channelDesc: String, val parent: View) :
    RecyclerView.Adapter<ProgramListViewHolder>() {
    var mPrefMgr = PreferenceManager.getInstance();
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramListViewHolder {
        return ProgramListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.program_list_adapter,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ProgramListViewHolder, position: Int) {
//        holder?.show_name?.text = items.get(position)

//        val livePrograms = items.get(position)
        try {
            if (items != null) {
                val programListData = items!!.get(position)




                if (programListData != null) {
                    holder.parent.setOnClickListener(View.OnClickListener {

                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("ProgramDataID", programListData.ContentID)
                        intent.putExtra("BlockPosition", 0)
                        intent.putExtra("ContentType", programListData.ContentType)
                        context.startActivity(intent)
//                        val fragment = AllProgramFragment()
//                        if ((context as AppCompatActivity).supportFragmentManager != null) {
//                            val transaction = (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
//                            transaction.replace(R.id.container_fragment, fragment)
//                            transaction.addToBackStack(null)
//                            val args = Bundle()
//                            args.putString("ProgramDataID", programListData.id)
//                            fragment.setArguments(args)
//                            transaction.commit()
//                        }
                    })
                    var isFavourite = false
                    holder.channel_item_favourite.setOnClickListener(View.OnClickListener {


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
//                            if (programListData.FavouritesBy != null) {
//                                programListData.FavouritesBy.remove(
//                                    mPrefMgr!!.getString(
//                                        AppConstant.USERID,
//                                        AppConstant.EMPTY
//                                    )
//                                )
//                            }
                            RemoveFavourite(input)
                        }

                    })
                    holder.channel_item_share_image.setOnClickListener(View.OnClickListener {
                        GetShareDetails(programListData.ContentID!!,programListData.ContentType!!)
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


                    })


//                    if(programListData.Channel != null) {
//                        holder.channel_txt_program_item_type.text = programListData.Channel!!.Name
//                    }else if(programListData.CreatedBy != null) {
//                        holder.channel_txt_program_item_type.text = programListData.CreatedBy!!.Name
//                    }
//                    holder.channel_txt_program_item_part_count.text = programListData.CreditPoints
                    holder.channel_txt_program_item_title.text = programListData.Title
                    holder.channel_txt_program_item_name.text = programListData.ContentType
                     holder.txt_program_item_desc.text = programListData.Description

                    if (!TextUtils.isEmpty(programListData.MediaPath)) {
                        holder.channel_ivImageView.visibility = View.VISIBLE
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
                            .into(holder.channel_ivImageView, object : Callback {
                                override fun onSuccess() {
                                    holder.channel_loading.visibility = View.GONE
                                }
                                override fun onError() {
                                    holder.channel_loading.visibility = View.GONE
                                }
                            })
                    } else {
                        holder.channel_loading.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            if (holder.parent != null && context != null) {
//                CommonMethods.SnackBar( holder.parent, context.getString(R.string.error), false)
            }
        }

    }

    override fun getItemCount(): Int {
        if (items != null) {
            return items!!.size
        }
        return 0
    }

    private fun SetFavourite(input: SetFavReqClass, programListData: ContentsClass) {
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

            }
        })
    }
    private fun GetShareDetails(ContentId : String,ContentType : String) {
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
}

class ProgramListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
//    val layout_img_and_count = view.layout_img_count
    val txt_program_item_desc = view.txt_program_item_desc
    val channel_loading = view.channel_loading
    val channel_ivImageView = view.program_item_imageView
    val parent = view.layout_content_all
    val channel_lay_item_favourite = view.lay_item_favourite
    val channel_item_share_image = view.channel_item_share_image
    val channel_item_favourite = view.channel_item_favourite
    val channel_txt_program_item_name = view.txt_program_item_name
    val channel_txt_program_item_title = view.txt_program_item_title
}
