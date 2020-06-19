package au.com.letsjoinin.android.UI.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import au.com.letsjoinin.android.MVP.model.GetAllCategoryDataList;
import au.com.letsjoinin.android.R;
import au.com.letsjoinin.android.UI.activity.CategoryActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.MyViewHolder> {
    private ArrayList<GetAllCategoryDataList> mDataset;

    public CategoryActivity context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout parent_view;
        public RelativeLayout rel_large_view;
        public TextView txt_cat_name;
        public TextView txt_cat_code;
        public CircleImageView img_small;
        public CircleImageView img_large;

        public MyViewHolder(View layout2) {
            super(layout2);
            parent_view = (RelativeLayout) layout2.findViewById(R.id.parent_view);
            rel_large_view = (RelativeLayout) layout2.findViewById(R.id.rel_large_view);
            txt_cat_name = (TextView) layout2.findViewById(R.id.txt_cat_name);
            txt_cat_code = (TextView) layout2.findViewById(R.id.txt_cat_code);
            img_small = (CircleImageView) layout2.findViewById(R.id.img_small);
            img_large = (CircleImageView) layout2.findViewById(R.id.img_large);
        }
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter1(ArrayList<GetAllCategoryDataList> myDataset,CategoryActivity cntx) {
        mDataset = myDataset;
        context = cntx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.category_layout_1, parent, false);

        MyViewHolder vh = new MyViewHolder(contactView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        GetAllCategoryDataList item = mDataset.get(position);
        holder.img_small.setVisibility(View.GONE);
        holder.rel_large_view.setVisibility(View.VISIBLE);
        if(context.layB1Expand){
            holder.rel_large_view.setVisibility(View.GONE);
            holder.img_small.setVisibility(View.VISIBLE);

        }
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
        holder.txt_cat_name.setText(item.Name);
        holder.txt_cat_code.setText(item.CategoryCode);

        Picasso.with(context)
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


        holder.parent_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                context.callSubCategory(item.id);
//

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
