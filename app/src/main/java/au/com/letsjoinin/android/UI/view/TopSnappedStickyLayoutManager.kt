package au.com.letsjoinin.android.UI.view

import android.content.Context
import com.brandongogetap.stickyheaders.StickyLayoutManager
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler

class TopSnappedStickyLayoutManager  constructor(context: Context, headerHandler: StickyHeaderHandler) :
    StickyLayoutManager(context, headerHandler) {

    override fun scrollToPosition(position: Int) {
        super.scrollToPositionWithOffset(position, 0)
    }
}
