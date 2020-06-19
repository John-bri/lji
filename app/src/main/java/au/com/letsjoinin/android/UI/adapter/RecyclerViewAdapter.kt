package au.com.letsjoinin.android.UI.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.R
import com.google.android.exoplayer2.ui.PlayerView
import net.alexandroid.utils.exoplayerhelper.ExoAdListener
import net.alexandroid.utils.exoplayerhelper.ExoPlayerHelper
import net.alexandroid.utils.exoplayerhelper.ExoPlayerListener
import net.alexandroid.utils.exoplayerhelper.ExoThumbListener
import java.util.*

class RecyclerViewAdapter(
    private val mList: ArrayList<VideoItem>,
    private val mToolbar: Toolbar
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(),
    LifecycleObserver, View.OnClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var isFirstItemPlayed = false
    private var currentSelected = 0
    private var mLayoutManager: SliderLayoutManager? = null
    private var mIsFirstItemSelected = false
    private var mIsInFullScreen = false
    private var mPositionInFullScreen = 0
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        mLayoutManager = mRecyclerView!!.layoutManager as SliderLayoutManager?
        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager =
                    recyclerView.layoutManager as LinearLayoutManager?
                var firstVisible = layoutManager!!.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()
//                val top = mRecyclerView!!.getChildAt(0).top
//                val height = mRecyclerView!!.getChildAt(0).height
//                //// MyLog.d("firstVisible: " + firstVisible + "  top: " + top + "  height: " + height);
//                if (top < height / 3 * -1) {
//                    firstVisible++
//                }
//                if (lastVisible == itemCount - 1) {
//                    val lastViewTop =
//                        mRecyclerView!!.getChildAt(mRecyclerView!!.childCount - 1).bottom
//                    val listHeight = mRecyclerView!!.height
//                    if (lastViewTop - listHeight < height / 4) {
//                        firstVisible++
//                    }
//                    /*                    // MyLog.d("getChildCount: " + mRecyclerView.getChildCount()
//                            + "  lastViewTop: " + lastViewTop
//                            + "  listHeight: " + listHeight
//                            + "  lastViewTop - listHeight: " + (lastViewTop - listHeight)
//                    );*/
//                }
                if (firstVisible != currentSelected) {
                    onSelectedItemChanged(firstVisible)
                }
            }
        })
    }

    private fun onSelectedItemChanged(newSelected: Int) { // MyLog.d("New first visible is: " + newSelected);
        changeAlphaToVisible(currentSelected, false)
        pausePlayerByPosition(currentSelected)
        blockPlayerByPosition(currentSelected)
        //---------
        changeAlphaToVisible(newSelected, true)
        prepareAndPlayByPosition(newSelected)
        unBlockPlayerByPosition(newSelected)
        currentSelected = newSelected
    }

    private fun unBlockPlayerByPosition(newSelected: Int) {
        val viewHolder =
            getViewHolder(newSelected)
        if (viewHolder != null) {
            viewHolder.mExoPlayerHelper!!.playerUnBlock()
        }
    }

    private fun prepareAndPlayByPosition(position: Int) {
        val newPlayer = getExoPlayerByPosition(position)
        if (newPlayer != null) {
            newPlayer.preparePlayer()
            newPlayer.playerPlay()
        }
    }

    private fun blockPlayerByPosition(position: Int) {
        val viewHolder =
            getViewHolder(position)
        if (viewHolder != null) {
            viewHolder.mExoPlayerHelper!!.playerBlock()
        }
    }

    private fun pausePlayerByPosition(position: Int) {
        val oldPlayer = getExoPlayerByPosition(position)
        oldPlayer?.playerPause()
    }

    private fun changeAlphaToVisible(position: Int, isVisible: Boolean) {
        val viewHolder =
            getViewHolder(position)
        if (viewHolder != null) {
            viewHolder.mView.alpha = if (isVisible) 1.0f else 0.2f
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // MyLog.e("onDetachedFromRecyclerView");
    }

    private fun getExoPlayerByPosition(firstVisible: Int): ExoPlayerHelper? {
        val holder =
            getViewHolder(firstVisible)
        return if (holder != null) {
            getViewHolder(firstVisible)!!.mExoPlayerHelper
        } else {
            null
        }
    }

    private fun getViewHolder(position: Int): ViewHolder? {
        return mRecyclerView!!.findViewHolderForAdapterPosition(position) as ViewHolder?
    }

    private val allExoPlayers: ArrayList<ExoPlayerHelper>
        private get() {
            val list = ArrayList<ExoPlayerHelper>()
            for (i in mList.indices) {
                val exoPlayerHelper = getExoPlayerByPosition(i)
                if (exoPlayerHelper != null) {
                    list.add(exoPlayerHelper)
                }
            }
            return list
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater =
            LayoutInflater.from(parent.context)
        val view =
            inflater.inflate(R.layout.item_list, parent, false)
        return ViewHolder(
            view,
            mLayoutManager!!
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.mTextView.text = mList[position].getVideoUrl()
        holder.mVideoUrl = mList[position].getVideoUrl()
        holder.mThumbUrl = mList[position].getThumbUrl()
        holder.mPosition = position
        setClickListenerAndPositionAsTag(position, holder.mBtnFullScreen)
        if (!mIsFirstItemSelected) {
            mIsFirstItemSelected = true
            holder.mView.alpha = 1.0f
        } else {
            holder.mView.alpha = 0.2f
        }
    }

    private fun setClickListenerAndPositionAsTag(
        position: Int,
        vararg views: View
    ) {
        for (view in views) {
            view.setOnClickListener(this)
            view.tag = position
        }
    }

    override fun onClick(v: View) {
        val position = v.tag as Int
        when (v.id) {
            R.id.btnFullScreen -> onBtnFullScreen(position)
        }
    }

    private fun onBtnFullScreen(position: Int) {
        val holder =
            getViewHolder(position)
        if (mIsInFullScreen) {
            mIsInFullScreen = false
            mPositionInFullScreen = 0
            mToolbar.visibility = View.VISIBLE
            holder!!.changeToNormalScreen()
        } else if (position == currentSelected) {
            mIsInFullScreen = true
            mPositionInFullScreen = position
            mToolbar.visibility = View.GONE
            holder!!.changeToFullScreen()
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // MyLog.i("Position: " + holder.mPosition + " - onViewAttachedToWindow");
        holder.createPlayer()
        if (!isFirstItemPlayed && holder.adapterPosition == 0) {
            isFirstItemPlayed = true
            holder.mExoPlayerHelper!!.preparePlayer()
            holder.mExoPlayerHelper!!.playerPlay()
            holder.mExoPlayerHelper!!.playerUnBlock()
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        // MyLog.i("Position: " + holder.mPosition + " - onViewDetachedFromWindow");
        holder.mExoPlayerHelper!!.releasePlayer()
        holder.mExoPlayerHelper!!.onActivityDestroy()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(
        val mView: View,
        private val mLayouManager: SliderLayoutManager
    ) : RecyclerView.ViewHolder(mView), ExoPlayerListener, ExoAdListener, ExoThumbListener {
        val mTextView: TextView
        val mExoPlayerView: PlayerView
        val mBtnFullScreen: ImageView
        var mExoPlayerHelper: ExoPlayerHelper? = null
        var mThumbUrl: String? = null
        var mVideoUrl: String? = null
         var mPosition = 0
        private var mItemHeight = 0
        private var mItemWidth = 0
        private var mItemTopMargin = 0
        private var mItemBottomMargin = 0
        private var mVideoHeight = 0
        private var mVideoWidth = 0
        private var mItemLeftMargin = 0
        private var mItemRightMargin = 0
        fun createPlayer() {
            mExoPlayerHelper = ExoPlayerHelper.Builder(
                mExoPlayerView.context,
                mExoPlayerView
            ) //.enableCache(50)
                .setUiControllersVisibility(true)
                .setAutoPlayOn(false)
                .setToPrepareOnResume(false)
                .setVideoUrls(mVideoUrl) //.setTagUrl(TEST_TAG_URL)
                .setExoPlayerEventsListener(this)
                .setExoAdEventsListener(this)
                .setThumbImageViewEnabled(this)
                .addProgressBarWithColor(Color.RED)
                .create()
        }

        fun changeToFullScreen() {
            mLayouManager.disableScroll()
            val layoutParamsItem =
                mView.layoutParams as RecyclerView.LayoutParams
            mItemHeight = layoutParamsItem.height
            mItemWidth = layoutParamsItem.width
            mItemTopMargin = layoutParamsItem.topMargin
            mItemBottomMargin = layoutParamsItem.bottomMargin
            mItemLeftMargin = layoutParamsItem.leftMargin
            mItemRightMargin = layoutParamsItem.rightMargin
            layoutParamsItem.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParamsItem.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParamsItem.topMargin = 0
            layoutParamsItem.bottomMargin = 0
            layoutParamsItem.leftMargin = 0
            layoutParamsItem.rightMargin = 0
            mView.layoutParams = layoutParamsItem
            val videoParams =
                mExoPlayerView.layoutParams as ConstraintLayout.LayoutParams
            mVideoHeight = videoParams.height
            mVideoWidth = videoParams.width
            videoParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            videoParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            //videoParams.bottomToBottom = R.id.parent;
            mExoPlayerView.layoutParams = videoParams
            mExoPlayerView.post { mLayouManager.scrollToPosition(adapterPosition) }
            mTextView.visibility = View.GONE
            //            ((Activity) mTextView.getContext()).setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        }

        fun changeToNormalScreen() { //            ((Activity) mTextView.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mLayouManager.enableScroll()
            val layoutParamsItem =
                mView.layoutParams as RecyclerView.LayoutParams
            layoutParamsItem.height = mItemHeight
            layoutParamsItem.width = mItemWidth
            layoutParamsItem.topMargin = mItemTopMargin
            layoutParamsItem.bottomMargin = mItemBottomMargin
            layoutParamsItem.leftMargin = mItemLeftMargin
            layoutParamsItem.rightMargin = mItemRightMargin
            mView.layoutParams = layoutParamsItem
            val videoParams =
                mExoPlayerView.layoutParams as ConstraintLayout.LayoutParams
            videoParams.height = mVideoHeight
            videoParams.width = mVideoWidth
            //videoParams.bottomToBottom = 0;
            mExoPlayerView.layoutParams = videoParams
            mTextView.visibility = View.VISIBLE
        }

        /**
         * ExoPlayerListener
         */
        override fun onThumbImageViewReady(imageView: ImageView) { //            Picasso.get()
//                    .load(mThumbUrl)
//                    .placeholder(R.drawable.place_holder)
//                    .error(R.drawable.error_image)
//                    .into(imageView);
        }

        override fun onLoadingStatusChanged(
            isLoading: Boolean,
            bufferedPosition: Long,
            bufferedPercentage: Int
        ) { /*
        // MyLog.d("onLoadingStatusChanged, isLoading: " + isLoading +
                "   Buffered Position: " + bufferedPosition +
                "   Buffered Percentage: " + bufferedPercentage);
*/
        }

        override fun onPlayerPlaying(currentWindowIndex: Int) { // MyLog.d("Position: " + mPosition + " - onPlayerPlaying, currentWindowIndex: " + currentWindowIndex);
        }

        override fun onPlayerPaused(currentWindowIndex: Int) { // MyLog.d("Position: " + mPosition + " - onPlayerPaused, currentWindowIndex: " + currentWindowIndex);
        }

        override fun onPlayerBuffering(currentWindowIndex: Int) { // MyLog.d("Position: " + mPosition + " - onPlayerBuffering, currentWindowIndex: " + currentWindowIndex);
        }

        override fun onPlayerStateEnded(currentWindowIndex: Int) { // MyLog.d("Position: " + mPosition + " - onPlayerStateEnded, currentWindowIndex: " + currentWindowIndex);
        }

        override fun onPlayerStateIdle(currentWindowIndex: Int) { // MyLog.d("Position: " + mPosition + " - onPlayerStateIdle, currentWindowIndex: " + currentWindowIndex);
        }

        override fun onPlayerError(errorString: String) { // MyLog.e("Position: " + mPosition + " - onPlayerError");
        }

        override fun createExoPlayerCalled(isToPrepare: Boolean) { // MyLog.d("Position: " + mPosition + " - createExoPlayerCalled");
        }

        override fun releaseExoPlayerCalled() { // MyLog.d("Position: " + mPosition + " - releaseExoPlayerCalled");
        }

        override fun onVideoResumeDataLoaded(
            window: Int,
            position: Long,
            isResumeWhenReady: Boolean
        ) { // MyLog.d("Position: " + mPosition + " - window: " + window + "  position: " + position + " autoPlay: " + isResumeWhenReady);
        }

        override fun onVideoTapped() { // MyLog.d("Position: " + mPosition + " - onVideoTapped");
        }

        override fun onPlayBtnTap(): Boolean { // MyLog.d("Position: " + mPosition + " - onPlayBtnTap");
            return false
        }

        override fun onPauseBtnTap(): Boolean { // MyLog.d("Position: " + mPosition + " - onPauseBtnTap");
            return false
        }

        override fun onFullScreenBtnTap() {}
        override fun onTracksChanged(
            currentWindowIndex: Int,
            nextWindowIndex: Int,
            isPlayBackStateReady: Boolean
        ) { // MyLog.d("currentWindowIndex: " + currentWindowIndex + "  nextWindowIndex: " + nextWindowIndex + " isPlayBackStateReady: " + isPlayBackStateReady);
        }

        override fun onMuteStateChanged(isMuted: Boolean) {}
        /**
         * ExoAdListener
         */
        override fun onAdPlay() { // MyLog.d("Position: " + mPosition + " - onAdPlay");
        }

        override fun onAdPause() { // MyLog.d("Position: " + mPosition + " - onAdPause");
        }

        override fun onAdResume() { // MyLog.d("Position: " + mPosition + " - onAdResume");
        }

        override fun onAdEnded() { // MyLog.d("Position: " + mPosition + " - onAdEnded");
        }

        override fun onAdError() { // MyLog.d("Position: " + mPosition + " - onAdError");
        }

        override fun onAdClicked() { // MyLog.d("Position: " + mPosition + " - onAdClicked");
        }

        override fun onAdTapped() { // MyLog.d("Position: " + mPosition + " - onAdTapped");
        }

        override fun onBuffering() { // MyLog.d("Position: " + mPosition + " - onBuffering");
        }

        init {
            mTextView =
                mView.findViewById(R.id.textView)
            mExoPlayerView = mView.findViewById(R.id.exoPlayerView)
            mBtnFullScreen =
                mView.findViewById(R.id.btnFullScreen)
        }
    }

    // Activity LifeCycle
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onStart() { // MyLog.i("onActivityStart");
        for (exoPlayerHelper in allExoPlayers) {
            exoPlayerHelper.onActivityStart()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun onResume() { // MyLog.i("onActivityResume");
        for (exoPlayerHelper in allExoPlayers) {
            exoPlayerHelper.onActivityResume()
        }
        val newPlayer = getExoPlayerByPosition(currentSelected)
        if (newPlayer != null) {
            newPlayer.preparePlayer()
            newPlayer.playerPlay()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected fun onPause() { // MyLog.i("onActivityPause");
        for (exoPlayerHelper in allExoPlayers) {
            exoPlayerHelper.onActivityPause()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected fun onStop() { // MyLog.i("onActivityStop");
        for (exoPlayerHelper in allExoPlayers) {
            exoPlayerHelper.onActivityStop()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected fun onDestroy() { // MyLog.e("onActivityDestroy");
        for (exoPlayerHelper in allExoPlayers) {
            exoPlayerHelper.releaseAdsLoader()
        }
    }

    fun onBack(): Boolean {
        return if (mIsInFullScreen) {
            onBtnFullScreen(mPositionInFullScreen)
            true
        } else {
            false
        }
    }

    companion object {
        const val TEST_TAG_URL =
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
    }

}