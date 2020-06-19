package au.com.letsjoinin.android.UI.adapter

import androidx.recyclerview.widget.DiffUtil
import au.com.letsjoinin.android.MVP.model.FirebaseChatData

class SimpleDiffCallback internal constructor(
    private val oldList: ArrayList<FirebaseChatData>,
    private val newList: ArrayList<FirebaseChatData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
    }
}
