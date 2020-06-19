package au.com.letsjoinin.android.UI.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.model.NotificationData
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.ChatActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notification_list_row_item.view.*


/**
 */
class NotificationAdapter(val items: ArrayList<NotificationData>?, val context: Context, val parent: View) :
    RecyclerView.Adapter<NotificationViewHolder>() {

    var mPrefMgr = PreferenceManager.getInstance();
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return au.com.letsjoinin.android.UI.adapter.NotificationViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.notification_list_row_item,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items!!.get(position)
        try {
            val userID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
            holder.layout_notification_media.visibility = View.GONE


            holder.notification_parent.setOnClickListener(View.OnClickListener {

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("ProgramDataID", item.id)
                intent.putExtra("ContentType", item.ContentType)
                intent.putExtra("BlockPosition", 0)
                context.startActivity(intent)
            })


            if (item.ContentType.equals("Topic", true)) {
                var text = "You has created a "+item.ContentType!!.toLowerCase()+" " + "<font color=#000000>" + item.Title + "</font>"
                Picasso.with(context)
                    .load( mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY))
                    .error(R.drawable.image_placeholder_1)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.img_avatar)
                if (item.Log != null) {
                    if (!item.Log[0].UserID.equals(userID)) {
                        holder.notification_txt_time.setText(CommonMethods.ChatTime(item.Log[0].ActionOn).replace("am", "AM").replace("pm","PM"))
                        holder.notification_txt_date.setText(CommonMethods.getDateFormat(item.Log[0].ActionOn.toString().split(" ")[0]).toUpperCase())

                        val name = "<font color=#000000><b><i>"+item.Log[0].Name+"</i></b></font>"

                        text = name+" has created a "+item.ContentType!!.toLowerCase()+" " + "<font color=#592384><b>" + item.Title + "</b></font>"
                        Picasso.with(context)
                            .load(item.Log[0].AvatarPath)
                            .error(R.drawable.image_placeholder_1)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.img_avatar)
                    }
                }
                holder.notification_txt_content_msg.setText(Html.fromHtml(text))
            }
            if (!TextUtils.isEmpty(item.MediaPath)) {
                holder.layout_notification_media.visibility = View.VISIBLE

                Picasso.with(context)
                    .load(item.MediaPath)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.img_notification)
//                    , object : Callback {
//                    override fun onSuccess() {
//                        holder.timeline_loading.visibility = View.GONE
//                    }
//
//                    override fun onError() {
//                        holder.timeline_loading.visibility = View.GONE
//                    }
//                })
            }
        } catch (e: Exception) {
            if (parent != null && context != null) {
                CommonMethods.SnackBar(parent, context.getString(R.string.error), false)
            }
        }

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items!!.size
//        return 8
    }


}

class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val img_avatar = view.notification_img_avatar
    val layout_notification_media = view.layout_notification_media
    val notification_txt_time = view.notification_txt_time
    val notification_txt_date = view.notification_txt_date
    val img_notification = view.img_notification
    val notification_parent = view.notification_parent
    val notification_txt_content_msg = view.notification_txt_content_msg
    val txt_followed = view.txt_followed
    val txt_favourite = view.txt_favourite
    val txt_participants = view.txt_participants
}
