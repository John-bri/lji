package au.com.letsjoinin.android.UI.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {
    private ArrayList<GetAllSubCategoryDataList> mDataset;

    CategoryActivity categoryActivity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout rel_large_view;
        public TextView txt_cat_name;
        public TextView txt_cat_code;
        public FloatingActionButton img_small;
        public ImageView img_large;
        public CardView img_card;

        public MyViewHolder(View layout2) {
            super(layout2);
            rel_large_view = (RelativeLayout) layout2.findViewById(R.id.rel_large_view);
            txt_cat_name = (TextView) layout2.findViewById(R.id.txt_cat_name);
            txt_cat_code = (TextView) layout2.findViewById(R.id.txt_cat_code);
            img_small = (FloatingActionButton) layout2.findViewById(R.id.img_small);
            img_large = (ImageView) layout2.findViewById(R.id.img_large);
            img_card = (CardView) layout2.findViewById(R.id.img_card);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter2(ArrayList<GetAllSubCategoryDataList> myDataset, CategoryActivity activity) {
        mDataset = myDataset;
        categoryActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout_2, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element




        holder.img_card.setVisibility(View.VISIBLE);
//        new android.os.Handler(categoryActivity.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                holder.img_card.setVisibility(View.VISIBLE);
//                ViewGroup.LayoutParams params =
//                        holder.img_card.getLayoutParams();
//                params.height = holder.img_card.getWidth();
//                holder.img_card.setLayoutParams(params);
//            }
//        }, 100);

        GetAllSubCategoryDataList item = mDataset.get(position);
        holder.txt_cat_name.setText(item.Name);
        holder.txt_cat_code.setText(item.CategoryCode);

        Picasso.with(categoryActivity)
                .load(item.ImagePath)
                .error(R.drawable.image_placeholder_1)
                .into(holder.img_large, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });


        holder.img_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                categoryActivity.addCategory(item);

            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
