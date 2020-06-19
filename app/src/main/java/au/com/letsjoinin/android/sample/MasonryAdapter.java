package au.com.letsjoinin.android.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.exoplayer2.ui.PlayerView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import net.alexandroid.utils.exoplayerhelper.ExoAdListener;
import net.alexandroid.utils.exoplayerhelper.ExoPlayerHelper;
import net.alexandroid.utils.exoplayerhelper.ExoPlayerListener;
import net.alexandroid.utils.exoplayerhelper.ExoThumbListener;

import java.util.ArrayList;

import au.com.letsjoinin.android.MVP.interfaces.ApiService;
import au.com.letsjoinin.android.MVP.model.FavUserRef;
import au.com.letsjoinin.android.MVP.model.ProgramListAdminClass;
import au.com.letsjoinin.android.MVP.model.RemoveFavReqClass;
import au.com.letsjoinin.android.MVP.model.SearchUserDocData;
import au.com.letsjoinin.android.MVP.model.ServerMessageData;
import au.com.letsjoinin.android.MVP.model.SetFavReqClass;
import au.com.letsjoinin.android.R;
import au.com.letsjoinin.android.UI.EasyFlipView;
import au.com.letsjoinin.android.UI.activity.ChatActivity;
import au.com.letsjoinin.android.UI.activity.RedeemActivity;
import au.com.letsjoinin.android.UI.fragment.ChannelFragment;
import au.com.letsjoinin.android.UI.view.DynamicHeightImageView;
import au.com.letsjoinin.android.exoplayer.CustomTextureVideoView;
import au.com.letsjoinin.android.utils.AppConstant;
import au.com.letsjoinin.android.utils.CommonMethods;
import au.com.letsjoinin.android.utils.JustifiedTextView;
import au.com.letsjoinin.android.utils.PreferenceManager;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Suleiman on 26-07-2015.
 */
public class MasonryAdapter extends RecyclerView.Adapter<MasonryAdapter.MasonryView> {
    private StaggeredGridLayoutManager mLayoutManager;
    private Context context;
    PreferenceManager mPrefMgr = PreferenceManager.getInstance();
    ArrayList<SearchUserDocData> list = new ArrayList<>();

    String[] nameList = {"One", "Two", "Three", "Four", "Five", "Six",
            "Seven", "Eight", "Nine", "Ten"};
    private RecyclerView mRecyclerView;
    View parent ;
    public MasonryAdapter(ArrayList<SearchUserDocData> items, Context context, View v) {
        this.context = context;
        list = items;
        parent= v;
    }

//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mLayoutManager = ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int firstVisible = layoutManager.findFirstVisibleItemPosition();
//                int lastVisible = layoutManager.findLastVisibleItemPosition();
//                int top = mRecyclerView.getChildAt(0).getTop();
//                int height = mRecyclerView.getChildAt(0).getHeight();

                //MyLog.d("firstVisible: " + firstVisible + "  top: " + top + "  height: " + height);

//                if (top < height / 3 * (-1)) {
//                    firstVisible++;
//                }

//                if (lastVisible == getItemCount() - 1) {
//                    int lastViewTop = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1).getBottom();
//                    int listHeight = mRecyclerView.getHeight();
//
//                    if (lastViewTop - listHeight < height / 4) {
//                        firstVisible++;
//                    }
//
///*                    MyLog.d("getChildCount: " + mRecyclerView.getChildCount()
//                            + "  lastViewTop: " + lastViewTop
//                            + "  listHeight: " + listHeight
//                            + "  lastViewTop - listHeight: " + (lastViewTop - listHeight)
//                    );*/
//                }

//                if (firstVisible != currentSelected) {
//                    onSelectedItemChanged(firstVisible);
//                }
            }
        });
    }

    public void onDestroyViews() { // MyLog.e("onActivityDestroy");

        if(currentVideoViewHolder != null) {
            currentVideoViewHolder.stopVideo();
        }
//        for (ExoPlayerHelper exoPlayerHelper : getAllExoPlayers()) {
//            exoPlayerHelper.onActivityStop();
//        }
    }
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

//    private ExoPlayerHelper getExoPlayerByPosition(int firstVisible) {
//        MasonryView holder = getViewHolder(firstVisible);
//        if (holder != null) {
//            return getViewHolder(firstVisible).mExoPlayerHelper;
//        } else {
//            return null;
//        }
//    }

    private MasonryView getViewHolder(int position) {
        return (MasonryView) mRecyclerView.findViewHolderForAdapterPosition(position);
    }

//    private ArrayList<ExoPlayerHelper> getAllExoPlayers() {
//        ArrayList<ExoPlayerHelper> list = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            ExoPlayerHelper exoPlayerHelper = getExoPlayerByPosition(i);
//            if (exoPlayerHelper != null) {
//                list.add(exoPlayerHelper);
//            }
//        }
//        return list;
//    }

    @Override
    public void onViewAttachedToWindow(MasonryView holder) {
        super.onViewAttachedToWindow(holder);

//        if (!isFirstItemPlayed && holder.getAdapterPosition() == 0) {
//            isFirstItemPlayed = true;
//            holder.mExoPlayerHelper.preparePlayer();
//            holder.mExoPlayerHelper.playerPlay();
//            holder.mExoPlayerHelper.playerUnBlock();
//        }
    }
    @Override
    public void onViewRecycled(MasonryView holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewDetachedFromWindow(MasonryView holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_adapter_flip, parent, false);
        MasonryView masonryView = new MasonryView(layoutView);
        return masonryView;
    }
    void flipCard(
            EasyFlipView flipView, RelativeLayout layout_front_view, LinearLayout lock_view
    ) {

        ViewGroup.LayoutParams params =
                lock_view.getLayoutParams();
        params.height = layout_front_view.getHeight();
        lock_view.setLayoutParams(params);
        flipView.setFlipDuration(700);
        flipView.flipTheView();


        new android.os.Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                flipView.setFlipDuration(500);
                flipView.flipTheView();
            }
        }, 4000);

    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {

        SearchUserDocData programListData = list.get(position);
        try {
            if (programListData != null) {

                if (programListData.getContentType().equals("AD")) {
                    holder.ad_layout.setVisibility(View.VISIBLE);
                    holder.parent.setVisibility(View.GONE);
//                    Picasso.with(context)
//                            .load(programListData.getMediaPath())
//                            .error(R.drawable.image_placeholder_1)
//                            .memoryPolicy(MemoryPolicy.NO_CACHE)
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .into(holder.ivImageView);

//                    holder.mExoPlayerHelper = new ExoPlayerHelper.Builder(holder.exoPlayerView.getContext(), holder.exoPlayerView)
//                            .enableCache(50)
//                            .setUiControllersVisibility(true)
//                            .setAutoPlayOn(true)
//                            .setToPrepareOnResume(false)
//                            .setVideoUrls("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
//                            //.setTagUrl(TEST_TAG_URL)
//                            .addProgressBarWithColor(Color.RED)
//                            .createAndPrepare();


//                    RelativeLayout.LayoutParams newParams = new
//                            RelativeLayout.LayoutParams(
//                                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                                    RelativeLayout.LayoutParams.MATCH_PARENT
//                            );
////                        newParams.addRule(RelativeLayout.RIGHT_OF, 1)
//                    playerView.setLayoutParams(newParams);
//                    holder.media_layout1.addView(playerView);


                    if (currentVideoViewHolder != null && currentVideoViewHolder != holder) {
                        currentVideoViewHolder.videoView.pause();
//                        currentVideoViewHolder.videoImageView.setVisibility(View.INVISIBLE);
                        currentVideoViewHolder.play_exo_player_ad.setVisibility(View.VISIBLE);
//                        currentVideoViewHolder.imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                        if (currentVideoViewHolder.videoView.getVisibility() == View.VISIBLE)
                            currentVideoViewHolder.videoView.setVisibility(View.INVISIBLE);


                        currentVideoViewHolder = null;
                    }

                    currentVideoViewHolder = holder;
                    String videoPath  =
                            mPrefMgr.getString(AppConstant.AdVideoPath, null);
                    holder.play_exo_player_ad.setVisibility(View.INVISIBLE);
//                    imageLoaderProgressBar.setVisibility(View.VISIBLE);
                    holder.videoView.setVisibility(View.VISIBLE);
                    holder.videoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
                    holder.videoView.requestFocus();
                    holder.videoView.seekTo(0);
                    holder.videoView.start();

                    holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.v("Video", "onCompletion");

//                    videoImageView.setVisibility(View.VISIBLE);
                            holder.play_exo_player_ad.setVisibility(View.VISIBLE);

                            if ( holder.videoView.getVisibility() == View.VISIBLE)
                                holder.videoView.setVisibility(View.INVISIBLE);



                            list.remove(position);
                            notifyDataSetChanged();
//                    imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                            currentVideoViewHolder = null;
                        }
                    });
//                    videoImageView.setVisibility(View.INVISIBLE);
//                    if (!getVideoUrl().equals(videoView.getVideoPath())) {
//                        holder.videoView.setIsPrepared(false);
//                        holder.videoView.setVideoPath(getVideoUrl());
//                        holder.videoView.requestFocus();
//                    } else {
//                        if (holder.videoView.isPrepared()) {
//                        } else {
//                        }
//                        holder.videoView.requestFocus();
//                        holder.videoView.seekTo(0);
//                        holder.videoView.start();
//                    }
                } else {


                    holder.ad_layout.setVisibility(View.GONE);
                    holder.parent.setVisibility(View.VISIBLE);
                    boolean isFavourite = false;
                    holder.parent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (programListData.isFlipped()) {
                                flipCard(
                                        holder.flipView, holder.layout_front_view, holder.lock_view
                                );


                            } else {
                                if (CommonMethods.isNetworkAvailable(context)) {
                                    onDestroyViews();

                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra("ProgramDataID", programListData.getId());
                                    intent.putExtra("ContentType", programListData.getContentType());
                                    if( programListData.getChannelInfo() != null) {
                                        intent.putExtra("PKChannelId", programListData.getChannelInfo().ChannelID);
                                        intent.putExtra("BlockPosition", 0);
                                        context.startActivity(intent);
                                    }
                                } else {
                                    if (parent != null) {
                                        CommonMethods.SnackBar(
                                                parent,
                                                context.getString(R.string.network_error),
                                                false
                                        );
                                    }
                                }
                            }
                        }
                    });

                    holder.txt_invitation_code.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDestroyViews();
                            Intent intent = new Intent(context, RedeemActivity.class);
                            intent.putExtra("ProgramDataID", programListData.getId());
                            intent.putExtra("ContentType", programListData.getContentType());
                            intent.putExtra("PKChannelId", programListData.getChannelInfo().ChannelID);
                            intent.putExtra("BlockPosition", 0);
                            context.startActivity(intent);
                        }
                    });
                    holder.txt_timeline_item_type.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CommonMethods.isNetworkAvailable(context)) {
                                if (!programListData.getContentType().equals("TOPIC")) {
                                    if (!programListData.isFlipped()) {
                                        onDestroyViews();
                                        Fragment fragment = new ChannelFragment();
//                                        if ((context as AppCompatActivity).supportFragmentManager != null) {
//                                            val transaction =
//                                                    (context as AppCompatActivity).supportFragmentManager!!.beginTransaction()
//                                            transaction.replace(R.id.container_fragment, fragment)
//                                            transaction.addToBackStack(null)
//                                            val args = Bundle()
//                                            args.putString(
//                                                    "ChannelID",
//                                                    programListData.ChannelInfo!!.ChannelID
//                                            )
//                                            args.putString(
//                                                    "ProgramPKCountry",
//                                                    programListData.ChannelInfo!!.PKCountry
//                                            )
//                                            args.putString(
//                                                    "ContentType",
//                                                    programListData.ContentType
//                                            )
//                                            fragment.setArguments(args)
//                                            transaction.commit()
//                                        }
                                    }
                                }
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                            parent,
                                            context.getString(R.string.network_error),
                                            false
                                    );
                                }
                            }

                        }
                    });

                    holder.lay_item_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CommonMethods.isNetworkAvailable(context)) {
                                GetShareDetails(programListData.getId(), programListData.getContentType());


//                                YoYo.with(Techniques.Bounce)
//                                        .duration(1300)
//                                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
//                                        .interpolate(AccelerateDecelerateInterpolator())
//                                        .withListener(object : Animator.AnimatorListener {
//                                    override fun onAnimationRepeat(animation: Animator?) {
//                                    }
//
//                                    override fun onAnimationEnd(animation: Animator?) {
//                                    }
//
//                                    override fun onAnimationCancel(animation: Animator?) {
//                                    }
//
//                                    override fun onAnimationStart(animation: Animator?) {
//                                    }
//
//                                })
//                                    .playOn(holder.item_share)
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                            parent,
                                            context.getString(R.string.network_error),
                                            false
                                    );
                                }
                            }

                        }
                    });
//
                    holder.item_favourite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (CommonMethods.isNetworkAvailable(context)) {
                                if ( !programListData.isFavourite()) {
                                    programListData.setFavourite(true);
                                    SetFavReqClass input = new SetFavReqClass();
                                    FavUserRef FavUserRefData =new FavUserRef();
                                    FavUserRefData.AvatarPath =
                                            mPrefMgr.getString(
                                            AppConstant.AvatarPath,
                                            AppConstant.EMPTY
                                    );
                                    FavUserRefData.Name =
                                            mPrefMgr.getString(
                                            AppConstant.FirstName,
                                            AppConstant.EMPTY
                                    ) + " " + mPrefMgr.getString(
                                            AppConstant.LastName,
                                            AppConstant.EMPTY
                                    );
                                    FavUserRefData.UserID = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY).toString();
                                    FavUserRefData.SetFavouriteOn = null;
                                    input.setContentID(programListData.getId());
                                    input.setPKContentType(programListData.getContentType());
                                    input.setPKUserCountry(
                                            mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY));
                                    input.setFavouriteBy(  FavUserRefData);
                                    SetFavourite(input, programListData);
                                } else {
                                    programListData.setFavourite(false);
                                    RemoveFavReqClass input  =new RemoveFavReqClass();
                                    input.setContentID( programListData.getId());
                                    input.setPKContentType(programListData.getContentType());
                                    input.setPKUserCountry(
                                            mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY));
                                    input.setUserID(
                                            mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY));
                                    if (programListData.getFavouritesBy() != null) {
                                        programListData.getFavouritesBy().remove(
                                                mPrefMgr.getString(
                                                AppConstant.USERID,
                                                AppConstant.EMPTY
                                        )
                                        );
                                    }
                                    RemoveFavourite(input);
                                }
                            } else {
                                if (parent != null) {
                                    CommonMethods.SnackBar(
                                            parent,
                                            context.getString(R.string.network_error),
                                            false
                                    );
                                }
                            }

                        }
                    });


                    if (programListData.getFavouritesBy() != null) {
                        if (programListData.getFavouritesBy().containsKey(
                                mPrefMgr.getString(
                                        AppConstant.USERID,
                                        AppConstant.EMPTY
                                )
                        )
                        ) {
                            holder.item_favourite.setChecked(true);
                            programListData.setFavourite(true);

                        } else {
                            holder.item_favourite.setChecked(false);
                        }
                    } else {
                        holder.item_favourite.setChecked(false);
                    }


                    if (programListData.getChannelInfo() != null) {
                        programListData.setFlipped(true);
                        holder.txt_timeline_item_type.setText(programListData.getChannelInfo().Name);
                    } else if (programListData.getCreatedBy() != null) {
                        holder.txt_timeline_item_type.setText(programListData.getCreatedBy().getName());
                    }
                    holder.txt_timeline_item_part_count.setText(programListData.getCreditPoints());
                    holder.txt_timeline_item_desc.setText(programListData.getDescription());
                    holder.txt_timeline_item_name.setText(programListData.getTitle());


                    if (!TextUtils.isEmpty(programListData.getDescription())) {

                        if (programListData.getDescription().length() > 100) {
//                            singleTextView(
//                                    holder.txt_timeline_item_desc,
//                                    programListData
//                            )
                        }
                    }

                    if (!TextUtils.isEmpty(programListData.getMediaPath())) {
                        holder.ivImageView.setVisibility(View.VISIBLE);
                        String path = programListData.getMediaPath().toString();
                        if (programListData.getContentType().equals("VIDEO") || programListData.getContentType().equals(
                                "LIVE"
                        ) || programListData.getContentType().equals(
                                "NEWS"
                        )
                        ) {
                            if (!TextUtils.isEmpty(programListData.getCoverImagePath())) {
                                path = programListData.getCoverImagePath().toString();
                            }
                        }
                        Picasso.with(context)
                                .load(path)
                                .error(R.drawable.image_placeholder_1)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(holder.ivImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.timeline_loading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.timeline_loading.setVisibility(View.GONE);
                                    }
                                });

                    } else {
                        holder.timeline_loading.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        holder.textView.setText(nameList[position]);
    }

    private void SetFavourite(SetFavReqClass input, SearchUserDocData programListData) {
        try {
            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl(AppConstant.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service  = retrofit.create(ApiService.class);
            Call<ServerMessageData> call = service.SetFavourite(input);
            call.enqueue(new retrofit2.Callback<ServerMessageData>() {
                @Override
                public void onResponse(Call<ServerMessageData> call, Response<ServerMessageData> response) {
                    ServerMessageData res = response.body();
                    if (context != null) {
                        if (res != null) {
                            if (res.Status.equals(AppConstant.SUCCESS)) {
                                CommonMethods.SnackBar(parent, res.DisplayMsg, false);
                                if (programListData.getFavouritesBy() != null) {
                                    ProgramListAdminClass FavUserRefData =new  ProgramListAdminClass();
                                    FavUserRefData.setAvatarPath(
                                            mPrefMgr.getString(AppConstant.AvatarPath, AppConstant.EMPTY));
                                    FavUserRefData.setName(
                                            mPrefMgr.getString(
                                            AppConstant.FirstName,
                                            AppConstant.EMPTY
                                    ) + " " + mPrefMgr.getString(
                                            AppConstant.LastName,
                                            AppConstant.EMPTY
                                    ));
                                    FavUserRefData.setUserID(
                                            mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY));
                                    programListData.getFavouritesBy().put(
                                            mPrefMgr.getString(
                                            AppConstant.USERID,
                                            AppConstant.EMPTY
                                    ), FavUserRefData
                                );
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerMessageData> call, Throwable t) {

                }
            });

        } catch (Exception e) {
        }
    }
    private void RemoveFavourite(RemoveFavReqClass input) {
        try {
            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl(AppConstant.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service  = retrofit.create(ApiService.class);
            Call<ServerMessageData> call = service.RemoveFavourite(input);
            call.enqueue(new retrofit2.Callback<ServerMessageData>() {
                @Override
                public void onResponse(Call<ServerMessageData> call, Response<ServerMessageData> response) {
                    ServerMessageData res = response.body();
                    if (context != null) {
                        if (res != null) {
                            if (parent != null && context != null) {
                                CommonMethods.SnackBar(parent, res.DisplayMsg, false);
//                                notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServerMessageData> call, Throwable t) {

                }
            });

        } catch (Exception e) {
        }
    }

    private void GetShareDetails(String ContentId,String $ContentType) {
        String querry = "ContentId=$ContentId~ContentType=$ContentType";
        try {
            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl(AppConstant.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service  = retrofit.create(ApiService.class);
            Call<String> call = service.EncryptSharingLink(querry);
            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String res = response.body();
                    Intent sharingIntent =new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "http://letsjoinin.azurewebsites.net/HomePage/MobileApp?$res";
//                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });


        } catch (Exception e) {
        }
    }
    public void onScrolled(RecyclerView recyclerView) {
        if (currentVideoViewHolder != null) {
            currentVideoViewHolder.onScrolled(recyclerView);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    MasonryView currentVideoViewHolder;

    class MasonryView extends RecyclerView.ViewHolder {
        EasyFlipView flipView;
        RelativeLayout ad_layout;
        RelativeLayout lay_item_share;
        RelativeLayout lay_item_favourite;
        RelativeLayout media_layout1;
        RelativeLayout layout_front_view;
        LinearLayout parent;
        LinearLayout lock_view;
        ShineButton item_favourite;
        TextView title;
        TextView txt_timeline_item_name;
        TextView txt_invitation_code;
        TextView txt_timeline_item_part_count;
        TextView txt_timeline_item_type;
        JustifiedTextView txt_timeline_item_desc;
        GifImageView timeline_loading;
        ImageView item_share;
        ImageView play_exo_player_ad;
        DynamicHeightImageView ivImageView;
        CustomTextureVideoView videoView;;
//        public ExoPlayerHelper mExoPlayerHelper;
//        public PlayerView exoPlayerView;

        public void stopVideo() {
            Log.v("Video", "stopVideo");

            //imageView is within the visible window
            videoView.pause();
            if (videoView.getVisibility() == View.VISIBLE) {
                videoView.setVisibility(View.INVISIBLE);
            }
//            videoImageView.setVisibility(View.VISIBLE);
            play_exo_player_ad.setVisibility(View.VISIBLE);
//            imageLoaderProgressBar.setVisibility(View.INVISIBLE);
            currentVideoViewHolder = null;
        }

        public void onScrolled(RecyclerView recyclerView) {
            if (isViewNotVisible(play_exo_player_ad, recyclerView) ) {
                //imageView is within the visible window
                stopVideo();
            }
        }
        public boolean isViewNotVisible(View view, RecyclerView recyclerView) {
            Rect scrollBounds = new Rect();
            recyclerView.getHitRect(scrollBounds);
            return view.getVisibility() == View.VISIBLE && !view.getLocalVisibleRect(scrollBounds);
        }

        public void createPlayer() {
//            mExoPlayerHelper = new ExoPlayerHelper.Builder(exoPlayerView.getContext(), exoPlayerView)
////                            .enableCache(50)
//                            .setUiControllersVisibility(true)
//                            .setAutoPlayOn(true)
//                            .setToPrepareOnResume(false)
//                            .setVideoUrls("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
//                            //.setTagUrl(TEST_TAG_URL)
//                            .addProgressBarWithColor(Color.RED)
//                            .createAndPrepare();
        }

        public MasonryView(View itemView) {
            super(itemView);


            flipView = (EasyFlipView) itemView.findViewById(R.id.flipView);
//            exoPlayerView = (PlayerView) itemView.findViewById(R.id.exoPlayerView);
            flipView.setToHorizontalType();
            flipView.setFlipTypeFromLeft();
            videoView = (CustomTextureVideoView) itemView.findViewById(R.id.video_feed_item_video);
            ad_layout = (RelativeLayout) itemView.findViewById(R.id.ad_layout);
            parent = (LinearLayout) itemView.findViewById(R.id.layout_content_all);
            lock_view = (LinearLayout) itemView.findViewById(R.id.lock_view);
            ivImageView = (DynamicHeightImageView) itemView.findViewById(R.id.timeline_item_imageView);


            layout_front_view = (RelativeLayout)
                    itemView.findViewById(R.id.layout_front_view);


            media_layout1 = (RelativeLayout)
                    itemView.findViewById(R.id.media_layout1);


            ad_layout = (RelativeLayout)
                    itemView.findViewById(R.id.ad_layout);


            lay_item_favourite = (RelativeLayout)
                    itemView.findViewById(R.id.lay_item_favourite);

            lay_item_share = (RelativeLayout)
                    itemView.findViewById(R.id.lay_timeline_item_share);


            item_favourite = (ShineButton)
                    itemView.findViewById(R.id.item_favourite);


            item_share = (ImageView)
                    itemView.findViewById(R.id.item_share);

            play_exo_player_ad = (ImageView)
                    itemView.findViewById(R.id.play_exo_player_ad);
//            play_exo_player_ad.bringToFront();
            txt_timeline_item_type = (TextView)
                    itemView.findViewById(R.id.txt_timeline_item_type);

            txt_invitation_code = (TextView)
                    itemView.findViewById(R.id.txt_invitation_code);

            title = (TextView)
                    itemView.findViewById(R.id.title);


            txt_timeline_item_name = (TextView)
                    itemView.findViewById(R.id.txt_timeline_item_name);


            txt_timeline_item_desc = (JustifiedTextView)
                    itemView.findViewById(R.id.txt_timeline_item_desc);


            txt_timeline_item_part_count = (TextView)
                    itemView.findViewById(R.id.txt_timeline_item_part_count);


            timeline_loading = (GifImageView)
                    itemView.findViewById(R.id.timeline_loading);
//            createPlayer();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    Log.v("Video", "onPrepared" + videoView.getVideoPath());
                    int width = mp.getVideoWidth();
                    int height = mp.getVideoHeight();
                    videoView.setIsPrepared(true);
//                    UIUtils.resizeView(videoView, UIUtils.getScreenWidth(getActivity()), UIUtils.getScreenWidth(getActivity()) * height / width);
                    if (currentVideoViewHolder == MasonryView.this) {
//                        videoImageView.setVisibility(View.GONE);
//                        imageLoaderProgressBar.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.seekTo(0);
                        videoView.start();
                    }
                }
            });
            videoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.v("Video", "onFocusChange" + hasFocus);
                    if (!hasFocus && currentVideoViewHolder == MasonryView.this) {
                        stopVideo();
                    }

                }
            });
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.v("Video", "onInfo" + what + " " + extra);

                    return false;
                }
            });

        }

    }
}
