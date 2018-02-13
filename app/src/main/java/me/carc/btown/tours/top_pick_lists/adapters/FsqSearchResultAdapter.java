package me.carc.btown.tours.top_pick_lists.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.Category;
import me.carc.btown.data.all4squ.entities.VenueResult;

/**
 * Display the different stops of the tour
 */
public class FsqSearchResultAdapter extends RecyclerView.Adapter<FsqSearchResultAdapter.FsqExploreViewHolder> {

    private final ArrayList<VenueResult> mList;

    public FsqSearchResultAdapter(ArrayList<VenueResult> list) {
        this.mList = list;
    }

    @Override
    public FsqExploreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_search_item_layout, parent, false);
        return new FsqExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FsqExploreViewHolder holder, final int position) {
        VenueResult item = mList.get(position);

        try {

            for (Category cat : item.getCategories()) {
                if (Commons.isNotNull(cat.getIcon())) {
                    holder.searchDesc.setText(cat.getName());

                    String imageUrl = cat.getIcon().getPrefix() + "64" + cat.getIcon().getSuffix();
                    Glide.with(holder.mView.getContext())
                            .load(imageUrl)
                            .into(holder.searchIcon);
                    break;
                }

            }
        } catch (Exception e) {
            holder.searchIcon.setImageResource(R.drawable.no_image);
        }

        holder.searchDistance.setText(MapUtils.getFormattedDistance(item.getLocation().getDistance()));
        holder.searchTitle.setText(item.getName());

        if (Commons.isNotNull(item) && item.getRating() > 0.0) {
            double ratingRaw = item.getRating();
            int color;
            if (ratingRaw >= 9.0) {
                color = R.color.FSQKale;
            } else if (ratingRaw >= 8.0) {
                color = R.color.FSQGuacamole;
            } else if (ratingRaw >= 7.0) {
                color = R.color.FSQLime;
            } else if (ratingRaw >= 6.0) {
                color = R.color.FSQBanana;
            } else if (ratingRaw >= 5.0) {
                color = R.color.FSQOrange;
            } else if (ratingRaw >= 4.0) {
                color = R.color.FSQMacCheese;
            } else {
                color = R.color.FSQStrawberry;
            }
            holder.ratingText.setText(String.valueOf(ratingRaw));
            holder.ratingText.getBackground().setTint(ContextCompat.getColor(holder.mView.getContext(), color));
        } else {
            holder.ratingText.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class FsqExploreViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView searchTitle;
        final TextView searchDesc;
        final ImageView searchIcon;
        final TextView ratingText;
        final TextView searchDistance;
        final ImageView searchMore;


        public FsqExploreViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.searchIcon = itemView.findViewById(R.id.searchIcon);

            this.searchTitle = itemView.findViewById(R.id.searchTitle);
            this.searchDesc = itemView.findViewById(R.id.searchDesc);
            this.ratingText = itemView.findViewById(R.id.ratingText);
            searchDistance = itemView.findViewById(R.id.searchDistance);
            this.searchMore = itemView.findViewById(R.id.searchMore);
            this.searchMore.setVisibility(View.GONE);
        }

        public View getView() {
            return mView;
        }
    }
}
