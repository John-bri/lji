package au.com.letsjoinin.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.UI.EasyFlipView

class SampleAdapter internal constructor(
    private val list: List<TestModel>
) : RecyclerView.Adapter<SampleAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var flipView: EasyFlipView

        init {
            flipView =
                view.findViewById<View>(R.id.flipView) as EasyFlipView
           flipView.setToHorizontalType()
           flipView.setFlipTypeFromLeft()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timeline_adapter_flip, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        if (holder.flipView.currentFlipState == EasyFlipView.FlipState.FRONT_SIDE && list[position].isFlipped
        ) {
            holder.flipView.flipDuration = 0
            holder.flipView.flipTheView()
        } else if (holder.flipView.currentFlipState == EasyFlipView.FlipState.BACK_SIDE
            && !list[position].isFlipped
        ) {
            holder.flipView.flipDuration = 0
            holder.flipView.flipTheView()
        }
        holder.flipView.setOnClickListener {
            if (list[position].isFlipped) {
                list[position].isFlipped = false
            } else {
                list[position].isFlipped = true
            }
            holder.flipView.flipDuration = 700
            holder.flipView.flipTheView()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}