package me.carc.btown.ui.front_page;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;

/**
 * Created by bamptonm on 18/12/2017.
 */

class FrontPageAdapter extends RecyclerView.Adapter<FrontPageAdapter.ViewHolder> {

    private List<MenuItem> mMenuItems;

    @Inject
    public FrontPageAdapter() {
        mMenuItems = new ArrayList<>();
    }


    public void setData(List<MenuItem> profileData) {
        mMenuItems = profileData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.front_page_grid_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MenuItem data = mMenuItems.get(position);
        Context ctx = holder.icon.getContext();

        if (data.getIconDrawable() == 0) {
            Drawable icon = new IconicsDrawable(ctx, data.getIconResourceId()).color(ContextCompat.getColor(ctx, R.color.white)).sizeDp(48);
            holder.icon.setImageDrawable(icon);

        } else
            holder.icon.setImageResource(data.getIconDrawable());

            holder.detailsDescription.setText(data.getSubTitleResourceId());
        holder.detailsTitle.setText(data.getTitleResourceId());
        holder.elementHolder.setBackgroundColor(ContextCompat.getColor(ctx, data.geticonColorId()));
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }

    public MenuItem getItem(int pos) {
        return mMenuItems.get(pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;  // change from View to ImageView
        @BindView(R.id.detailsDescription)
        TextView detailsDescription;
        @BindView(R.id.detailsTitle)
        TextView detailsTitle;

        @BindView(R.id.elementHolder)
        LinearLayout elementHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
