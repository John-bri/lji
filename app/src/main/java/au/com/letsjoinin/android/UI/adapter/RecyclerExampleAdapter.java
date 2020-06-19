package au.com.letsjoinin.android.UI.adapter;

import android.content.Context;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FILM on 01.02.2016.
 */
public class RecyclerExampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> items = new ArrayList<String>();

    private Context mContext;

    public RecyclerExampleAdapter(Context context){
        mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        RecyclerViewExampleItem rvei = (RecyclerViewExampleItem) viewHolder.itemView;
//        String str = getItem(position);
//        rvei.bind(str);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getItem(int position) {
        return items.get(position);
    }

    public void addAll(List<String> lst){
        items.addAll(lst);
    }

    public void setItem(int index, String item){
        items.set(index, item);
        notifyItemChanged(index);
    }

    public void setItems(int startPosition, int count, String item){
        int last = startPosition+count;
        for (int i = startPosition; i < last; i++)
            items.set(i, item);
        notifyItemRangeChanged(startPosition, count);
    }
}
