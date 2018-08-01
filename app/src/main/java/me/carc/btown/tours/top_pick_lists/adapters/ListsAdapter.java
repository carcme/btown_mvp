package me.carc.btown.tours.top_pick_lists.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.ItemsUserList;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.CatalogueViewHolder> {

    private ArrayList<ItemsUserList> lists;

    public ListsAdapter(ArrayList<ItemsUserList> lists) {
        this.lists = lists;
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.foursquare_list_card, viewGroup, false);
        return new CatalogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogueViewHolder holder, final int pos) {
        final ItemsUserList item = lists.get(pos);

        holder.itemTitle.setText(item.getName());
        holder.itemText.setText(item.getDescription());
        holder.itemStops.setText(String.valueOf(item.getListItems().getCount()));

        RequestOptions opts = new RequestOptions()
                .placeholder(R.drawable.checkered_background)
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        if (Commons.isNotNull(item.getPhoto())) {
            try {
                String photoUrl = item.getPhoto().getPrefix() + "width300" + item.getPhoto().getSuffix();
                Glide.with(holder.itemImage.getContext())
                        .load(photoUrl)
                        .transition(withCrossFade(500))
                        .apply(opts)
                        .into(new DrawableImageViewTarget(holder.itemImage) {
                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                Commons.Toast(holder.itemImage.getContext(), R.string.network_not_available_error, Color.RED, Toast.LENGTH_SHORT);
                            }

                            @Override
                            protected void setResource(@Nullable Drawable resource) {
                                super.setResource(resource);
                            }
                        });
            } catch (Exception e) {
                holder.itemImage.setImageResource(R.drawable.no_image);
            }
        } else
            Commons.Toast(holder.itemImage.getContext(), R.string.network_not_available_error, Color.RED, Toast.LENGTH_SHORT);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public ItemsUserList getItem(int position) { return lists.get(position); }

    static class CatalogueViewHolder extends RecyclerView.ViewHolder {

        View mView;

        FrameLayout mainFrame;
        TextView itemTitle;
        TextView itemStops;
        TextView itemText;
        ImageView itemImage;

        private CatalogueViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            itemView.setOnCreateContextMenuListener((FourSquareListsActivity) itemView.getContext());

            this.mainFrame = itemView.findViewById(R.id.mainFrame);
            this.itemImage = itemView.findViewById(R.id.backgroundImage);
            this.itemTitle = itemView.findViewById(R.id.headingTitle);
            this.itemText = itemView.findViewById(R.id.bottomText);
            this.itemStops = itemView.findViewById(R.id.headingStops);
        }
    }
}
