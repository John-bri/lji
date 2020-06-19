package au.com.letsjoinin.android.UI.adapter

import android.animation.Animator
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.GroupCommentsFireBase
import au.com.letsjoinin.android.MVP.model.FirebaseChatData
import au.com.letsjoinin.android.MVP.model.FirebaseChatDataWithGroupName
import au.com.letsjoinin.android.MVP.model.TopOpinionsData
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.CommonMethods
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.top_opinion_item.view.*


/**
 */
class TopOpinionAdapter(var items: ArrayList<FirebaseChatDataWithGroupName>?, val context: Context) :
    RecyclerView.Adapter<au.com.letsjoinin.android.UI.adapter.TopOpinionViewHolder>() {

    init {
//        items = createList(6)
//        mInflater = LayoutInflater.from(mContext)

        // uncomment if you want to open only one row at a time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopOpinionViewHolder {
        return au.com.letsjoinin.android.UI.adapter.TopOpinionViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.top_opinion_item,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: TopOpinionViewHolder, position: Int) {
        try {
            val item = items!!.get(position)
            holder.txt_chat_time.text = CommonMethods.ChatTime(item.PostedOn!!)
            holder.txt_chat_username.text =  item.PostedBy!!.Name // item.ContentName
            holder.txt_msg_content.text = item.CommentText
            holder.txt_group_name.text = item.GroupName
            if (!TextUtils.isEmpty(item!!.PostedBy!!.AvatharPath)) {
                Picasso.with(context)
                    .load(item!!.PostedBy!!.AvatharPath)
//                                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                .networkPolicy(NetworkPolicy.NO_CACHE)
//                                .noFade()
                    .into(holder.img_avatar_chat)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
//        if(items != null) {
            return items!!.size
//        }else
    }


}


private fun SlideInLeft(emoji: View) {
    YoYo.with(Techniques.SlideInLeft)
        .duration(1200)
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

private fun SlideOutLeft(emoji: View) {
    YoYo.with(Techniques.SlideOutLeft)
        .duration(1200)
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

private fun createList(n: Int): ArrayList<GroupCommentsFireBase> {
    val list = java.util.ArrayList<GroupCommentsFireBase>()
    val groupCommentsFireBase = GroupCommentsFireBase()
    for (i in 0 until n) {
        list.add(groupCommentsFireBase)
    }

    return list
}

class TopOpinionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txt_chat_username = view.txt_chat_username
    val txt_chat_time = view.txt_chat_time
    val txt_msg_content = view.txt_msg_content
    val img_avatar_chat = view.img_avatar_chat
    val txt_group_name = view.txt_group_name


}
