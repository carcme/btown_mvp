package me.carc.btown.tours.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.viewHolders.CatalogueViewHolder;
import me.carc.btown.db.tours.model.TourCatalogueItem;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ToursAdapter extends RecyclerView.Adapter<CatalogueViewHolder> {

    private ArrayList<TourCatalogueItem> tours = new ArrayList<>();
    private StorageReference mCoverImageStorageRef;
    private boolean isGermanLanguage;

    public ToursAdapter(boolean language) {
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
        isGermanLanguage = language;
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_card, viewGroup, false);
        return new CatalogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogueViewHolder holder, final int pos) {
        final TourCatalogueItem card = tours.get(pos);

        holder.catalogueTitle.setText(card.getCatalogueName(isGermanLanguage));
        holder.supportingText.setText(card.getCatalogueBrief(isGermanLanguage));
        holder.time.setText(card.getCatalogueTourTime());
        holder.rating.setText(String.valueOf(Double.toString(card.getCatalogueRating())));
        holder.distance.setText(String.valueOf(Double.toString(card.getCatalogueDistance()) + "km"));
        holder.stops.setText(String.valueOf(card.getCatalogueNumberOfStops()));

        // Load image from Local storeage using Glide
        Glide.with(holder.catalogueImage.getContext())
                .load(CacheDir.getInstance().getCachePath() + card.getCatalogueImage())
                .crossFade(500)
                .placeholder(R.drawable.checkered_background)
                .into(new GlideDrawableImageViewTarget(holder.catalogueImage) {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        // Load image from Firebase using Glide
                        Glide.with(holder.catalogueImage.getContext())
                                .using(new FirebaseImageLoader()) // cannot resolve method using!
                                .load(mCoverImageStorageRef.child(card.getCatalogueImage()))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(holder.catalogueImage);
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                    }
                });


        if (!C.HAS_M) {
            int colorRes = R.color.card_text_color;
            if (C.HAS_L) {
                Drawable[] drawables = holder.time.getCompoundDrawablesRelative();
                drawables[0].setTint(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
                drawables = holder.distance.getCompoundDrawablesRelative();
                drawables[0].setTint(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
                drawables = holder.stops.getCompoundDrawablesRelative();
                drawables[0].setTint(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
            } else {
                Drawable[] drawables = holder.time.getCompoundDrawablesRelative();
                drawables[0].setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), colorRes), PorterDuff.Mode.MULTIPLY);
                drawables = holder.distance.getCompoundDrawablesRelative();
                drawables[0].setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), colorRes), PorterDuff.Mode.MULTIPLY);
                drawables = holder.stops.getCompoundDrawablesRelative();
                drawables[0].setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), colorRes), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public void setTours(List<TourCatalogueItem> tours) {
        this.tours = new ArrayList<>(tours);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public TourCatalogueItem getItem(int position) {
        return tours.get(position);
    }

}
