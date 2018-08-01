package me.carc.btown.tours.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import me.carc.btown.GlideApp;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.viewHolders.CatalogueViewHolder;
import me.carc.btown.db.tours.model.TourCatalogueItem;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

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
    public @NonNull CatalogueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_card, viewGroup, false);
        return new CatalogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CatalogueViewHolder holder, final int pos) {
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
                .transition(withCrossFade(500))
                .apply(new RequestOptions().placeholder(R.drawable.checkered_background))
                .into(new DrawableImageViewTarget(holder.catalogueImage) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        super.setResource(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        GlideApp.with(holder.catalogueImage.getContext())
                                .load(mCoverImageStorageRef.child(card.getCatalogueImage()))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                                .into(holder.catalogueImage);
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
