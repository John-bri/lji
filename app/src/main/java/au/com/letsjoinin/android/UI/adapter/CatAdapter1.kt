package au.com.letsjoinin.android.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.model.GetAllCategoryDataList
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.CategoryActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_layout_1.view.*


/**
 */
class CatAdapter1(var items: ArrayList<GetAllCategoryDataList>?, val context: CategoryActivity) :
    RecyclerView.Adapter<CatAdapter1Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatAdapter1Holder {
        return CatAdapter1Holder(
            LayoutInflater.from(context).inflate(
                R.layout.category_layout_1,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CatAdapter1Holder, position: Int) {
        try {
            val item = items!!.get(position)

            holder.img_small.visibility = View.GONE
            holder.txt_cat_name.text = item.Name
            holder.txt_cat_code.text = item.CategoryCode

            Picasso.with(context)
                .load(item.ImagePath)
                .error(R.drawable.image_placeholder_1)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.img_large, object : Callback {
                    override fun onSuccess() {}
                    override fun onError() {}
                })

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

class CatAdapter1Holder(view: View) : RecyclerView.ViewHolder(view) {
    val rel_large_view = view.rel_large_view
    val txt_cat_name = view.txt_cat_name
    val txt_cat_code = view.txt_cat_code
    val img_small = view.img_small
    val img_large = view.img_large


}
