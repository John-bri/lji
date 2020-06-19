package au.com.letsjoinin.android.UI.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import au.com.letsjoinin.android.MVP.model.GetAllSubCategoryDataList;
import au.com.letsjoinin.android.R;
import au.com.letsjoinin.android.UI.activity.CategoryActivity;

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.MyViewHolder> {
    private ArrayList<GetAllSubCategoryDataList> mDataset;
    public CategoryActivity context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView img_large_card;
        public CardView img_small_card;
        public RelativeLayout rel_large_view;
        public ImageView img_small;
        public ImageView img_large;
        public CardView img_close;

        public MyViewHolder(View layout2) {
            super(layout2);
            img_large_card = (CardView) layout2.findViewById(R.id.img_large_card);
            img_small_card = (CardView) layout2.findViewById(R.id.img_small_card);
            rel_large_view = (RelativeLayout) layout2.findViewById(R.id.rel_large_view);
            img_small = (ImageView) layout2.findViewById(R.id.img_small);
            img_large = (ImageView) layout2.findViewById(R.id.img_large);
            img_close = (CardView) layout2.findViewById(R.id.img_close);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter3(ArrayList<GetAllSubCategoryDataList> myDataset, CategoryActivity activity) {
        mDataset = myDataset;
        context = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter3.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout_3, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        GetAllSubCategoryDataList item = mDataset.get(position);
        holder.img_close.bringToFront();
        if(context.layB3Expand) {
            holder.img_large_card.setVisibility(View.VISIBLE);
            holder.img_close.setVisibility(View.VISIBLE);
            holder.img_small_card.setVisibility(View.GONE);

//            new android.os.Handler(context.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    holder.img_large_card.setVisibility(View.VISIBLE);
//                    ViewGroup.LayoutParams params =
//                            holder.img_large_card.getLayoutParams();
//                    params.height = holder.img_large_card.getWidth();
//                    holder.img_large_card.setLayoutParams(params);
//                }
//            }, 100);

        }else{
            holder.img_close.setVisibility(View.GONE);
            holder.img_large_card.setVisibility(View.GONE);
            holder.img_small_card.setVisibility(View.VISIBLE);

//            new android.os.Handler(context.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    holder.img_small_card.setVisibility(View.VISIBLE);
//                    ViewGroup.LayoutParams params =
//                            holder.img_small_card.getLayoutParams();
//                    params.height = holder.img_small_card.getWidth();
//                    holder.img_small_card.setLayoutParams(params);
//                }
//            }, 1000);

        }

        holder.img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.remove(position);
                notifyDataSetChanged();
            }
        });
        Picasso.with(context)
                .load(item.ImagePath)
                .error(R.drawable.image_placeholder_1)
                .into(holder.img_small, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });

        Picasso.with(context)
                .load(item.ImagePath)
                .error(R.drawable.image_placeholder_1)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.img_large, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}