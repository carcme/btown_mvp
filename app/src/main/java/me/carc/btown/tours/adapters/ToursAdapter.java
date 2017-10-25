package me.carc.btown.tours.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.interfaces.DrawableClickListener;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.model.TourCatalogue;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.CatalogueViewHolder> {

    private ArrayList<TourCatalogue> tours;
    private StorageReference mCoverImageStorageRef;
    private boolean isGermanLanguage;

    public DrawableClickListener onClickListener;

    public ToursAdapter(ArrayList<TourCatalogue> tours, boolean language, DrawableClickListener listener) {
        this.tours = tours;
        isGermanLanguage = language;
        onClickListener = listener;
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_card, viewGroup, false);
        return new CatalogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogueViewHolder holder, final int pos) {
        final TourCatalogue card = tours.get(pos);

        holder.catalogueTitle.setText(card.getCatalogueName(isGermanLanguage));
        holder.supportingText.setText(card.getCatalogueBrief(isGermanLanguage));
        holder.time.setText(card.getCatalogueTourTime());
        holder.rating.setText(String.valueOf(Double.toString(card.getCatalogueRating())));
        holder.distance.setText(String.valueOf(Double.toString(card.getCatalogueDistance()) + "km"));
        holder.stops.setText(String.valueOf(card.getCatalogueNumberOfStops()));

        // Load image from Local storeage using Glide
        Glide.with(holder.mView.getContext())
                .load(CacheDir.getCachePath() + card.getCatalogueImage())
                .into(holder.catalogueImage);
/*
        // Load image from Firebase using Glide
        Glide.with(holder.mView.getContext())
                .using(new FirebaseImageLoader()) // cannot resolve method using!
                .load(mCoverImageStorageRef.child(card.getCatalogueImage()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.catalogueImage);
*/
        if(!C.HAS_M) {
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

        holder.catalogueImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.catalogueImage.getDrawable(), holder.getAdapterPosition());
            }
        });
        holder.catalogueImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public TourCatalogue getItem(int position) {
        return tours.get(position);
    }

    static class CatalogueViewHolder extends RecyclerView.ViewHolder {

        View mView;

        CardView card;
        TextView catalogueTitle;
        TextView supportingText;
        ImageView catalogueImage;

        TextView time;
        TextView rating;
        TextView stops;
        TextView distance;

        private CatalogueViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.card = (CardView) itemView.findViewById(R.id.tourCardView);

            // enable long press on cardview
            itemView.setOnCreateContextMenuListener((CatalogueActivity)itemView.getContext());

            this.catalogueImage = (ImageView) itemView.findViewById(R.id.catalogueImage);
            this.catalogueTitle = (TextView) itemView.findViewById(R.id.catalogueTitle);
            this.supportingText = (TextView) itemView.findViewById(R.id.supportingText);

            this.time = (TextView) itemView.findViewById(R.id.clock);
            this.rating = (TextView) itemView.findViewById(R.id.rating);
            this.stops = (TextView) itemView.findViewById(R.id.stops);
            this.distance = (TextView) itemView.findViewById(R.id.distance);
        }
    }
}
