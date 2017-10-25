package me.carc.btown.tours;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.tours.model.Attraction;


/**
 *
 */
public class TourDataAdapter extends RecyclerView.Adapter<TourDataAdapter.MyViewHolder> {

    private static final String TAG = C.DEBUG + Commons.getTag();
    boolean userDE;
    private final ArrayList<Attraction> mList;
    private StorageReference mCoverImageStorageRef;


    public TourDataAdapter(ArrayList<Attraction> list, boolean isGermanLanguage) {
        this.mList = list;
        this.userDE = isGermanLanguage;
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_stop_recycler_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final int pos = holder.getAdapterPosition();

        // Load the image using Glide
        Glide.with(holder.mView.getContext())
                .using(new FirebaseImageLoader())
                .load(mCoverImageStorageRef.child(mList.get(pos).getImage()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.icon);
/*
todo add palette stuff

        if (tourDataSet.get(pos).hasCachedImage()) {
            Picasso.with(ctx)
                    .load(tourDataSet.get(pos).getImageCacheFile())
                    .transform(PaletteTransformation.instance())
                    .into(cardviewIcon, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {

                            GalleryItem gallery = ActivityTourTabs.galleryItems.get(pos);
                            if(gallery != null && gallery.getBitmap() == null) {
                                Bitmap bitmap = ((BitmapDrawable) cardviewIcon.getDrawable()).getBitmap();

                                if (bitmap != null && !bitmap.isRecycled()) {
                                    Palette palette = PaletteTransformation.getPalette(bitmap);
                                    if (palette != null) {

                                        gallery.setValidSwatchs(palette.getSwatches());

                                        Palette.Swatch s = palette.getVibrantSwatch();

                                        if (s == null) {
                                            s = palette.getDarkVibrantSwatch();
                                        }
                                        if (s == null) {
                                            s = palette.getLightVibrantSwatch();
                                        }
                                        if (s == null) {
                                            s = palette.getMutedSwatch();
                                        }
                                        if (s == null) {
                                            s = palette.getDarkMutedSwatch();
                                        }
                                        if (s == null) {
                                            s = palette.getLightMutedSwatch();
                                        }

                                        // Want pale background for text - revert to almost white otherwise
                                        if (palette.getLightMutedSwatch() != null)
                                            gallery.setCardColor(palette.getLightMutedColor(
                                                    ContextCompat.getColor(ctx, R.color.catalogue_card_almostWhite)
                                            ));
                                        else
                                            gallery.setCardColor(palette.getLightVibrantColor(
                                                    ContextCompat.getColor(ctx, R.color.catalogue_card_almostWhite)
                                            ));

                                        gallery.setBitmap(bitmap);
                                        gallery.setSwatch(s);
                                        ActivityTourTabs.galleryItems.put(pos, gallery);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            cardviewIcon.setImageResource(R.drawable.placeholder);
                        }
                    });
        } else {
 */
//        }

        holder.title.setText(String.valueOf(mList.get(pos).getStopName()));
        holder.summary.setText(mList.get(pos).getBusStop(userDE));

    }

    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView title;
        final TextView summary;
        final ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.title = (TextView) itemView.findViewById(R.id.textViewStoryTitle);
            this.summary = (TextView) itemView.findViewById(R.id.textViewSummary);
            this.icon = (ImageView) itemView.findViewById(R.id.cardview_icon);
        }
    }
}
