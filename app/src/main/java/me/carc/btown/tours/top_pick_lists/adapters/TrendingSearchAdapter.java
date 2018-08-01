package me.carc.btown.tours.top_pick_lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.tours.top_pick_lists.TrendingSelectionItem;
import me.carc.btown.tours.top_pick_lists.TrendingSettingsActivity;


/**
 * Show other apps from Carc
 * Created by bamptonm on 20/01/2018.
 */

public class TrendingSearchAdapter extends RecyclerView.Adapter<TrendingSearchAdapter.ViewHolder> {

    private List<TrendingSelectionItem> mMenuItems;
    private TrendingSettingsActivity.ClickListener clickListener;

    public TrendingSearchAdapter(List<TrendingSelectionItem> items, TrendingSettingsActivity.ClickListener listener){
        mMenuItems = items;
        clickListener = listener;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_search_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrendingSelectionItem data = mMenuItems.get(position);

        holder.label.setText(data.getTitleResourceId());
        Glide.with(holder.icon.getContext())
                .asBitmap()
                .load(data.getIconUrl().concat("bg_120.png"))
                .into(holder.icon);
/*
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bm, GlideAnimation glideAnimation) {
                        holder.searchItemBtn.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                new BitmapDrawable(holder.searchItemBtn.getResources(), bm),
                                null,
                                null);
                    }
                });
*/

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.searchItemBtn) ImageView icon;
        @BindView(R.id.label) TextView label;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
