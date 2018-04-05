package me.carc.btown.tours.top_pick_lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.DrawableClickListener;
import me.carc.btown.data.all4squ.entities.ItemsUserList;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.CatalogueViewHolder> {

    private ArrayList<ItemsUserList> lists;
    private StorageReference mCoverImageStorageRef;

    public DrawableClickListener onClickListener;

    public ListsAdapter(ArrayList<ItemsUserList> lists, DrawableClickListener listener) {
        this.lists = lists;
        onClickListener = listener;
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
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

        if (Commons.isNotNull(item.getPhoto())) {

            try {
                String photoUrl = item.getPhoto().getPrefix() + "width300" + item.getPhoto().getSuffix();

                Glide.with(holder.mView.getContext())
                        .load(photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .placeholder(R.drawable.background_jewish_memorial)
//                        .crossFade()
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                holder.itemImage.setImageDrawable(resource);
                                holder.mainFrame.animate().setDuration(1800);
                    //            holder.mainFrame.setVisibility(View.VISIBLE);
                            }
                        });
            } catch (Exception e) {
                holder.itemImage.setImageResource(R.drawable.no_image);
            }
        }

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.itemImage.getDrawable(), holder.getAdapterPosition());
            }
        });
        holder.itemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
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
