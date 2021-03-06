package me.carc.btown.tours.top_pick_lists.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.Category;
import me.carc.btown.data.all4squ.entities.ItemsListItem;
import me.carc.btown.ui.CompassView;

/**
 * Display the different stops of the tour
 */
public class ListDetailsAdapter extends RecyclerView.Adapter<ListDetailsAdapter.MyViewHolder> {

    private static final String TAG = ListDetailsAdapter.class.getName();
    private final ArrayList<ItemsListItem> mList;
    private final GeoPoint lastLocation;

    public ListDetailsAdapter(ArrayList<ItemsListItem> list, GeoPoint location) {
        this.mList = list;
        this.lastLocation = location;
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_list_detail_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ItemsListItem item = mList.get(position);

        try {
            // Use the saved image
            String photo = item.getPhoto().getPrefix() + "width300" + item.getPhoto().getSuffix();
            Glide.with(holder.mView.getContext())
                    .load(photo)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.icon);
        } catch (Exception e) {
            holder.icon.setImageResource(R.drawable.no_image);
        }

        holder.title.setText(item.getVenue().getName());
        ArrayList<Category> categories = item.getVenue().getCategories();

        StringBuilder sb = new StringBuilder();
        for (Category cat : categories) {
            sb.append(cat.getShortName()).append(", ");
        }

        String info = sb.toString().substring(0, sb.toString().lastIndexOf(","));
        holder.summary.setText(info);

        if (Commons.isNotNull(item.getVenue()) && item.getVenue().getRating() > 0.0) {
            int color;
            double ratingRaw = item.getVenue().getRating();
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
            holder.ratingText.setText("---");
            holder.ratingText.getBackground().setTint(ContextCompat.getColor(holder.mView.getContext(), R.color.tintLight));
        }

        if(Commons.isNotNull(lastLocation))
            holder.distanceText.setText(item.getVenue().getLocation().getDistanceFormatted());
    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView title;
        final TextView summary;
        final ImageView icon;
        final TextView ratingText;
        final CompassView compassIcon;
        final TextView distanceText;


        MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.title = itemView.findViewById(R.id.textViewStoryTitle);
            this.summary = itemView.findViewById(R.id.textViewSummary);
            this.icon = itemView.findViewById(R.id.cardview_icon);
            this.ratingText = itemView.findViewById(R.id.ratingText);
            this.compassIcon = itemView.findViewById(R.id.compassIcon);
            this.distanceText = itemView.findViewById(R.id.distanceText);
        }
    }
}
