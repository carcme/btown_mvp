package me.carc.btown.tours.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.carc.btown.GlideApp;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.OnItemClickListener;
import me.carc.btown.tours.AttractionTabsActivity;
import me.carc.btown.tours.GalleryItem;


/**
 *
 */
public class AttractionGalleryViewerAdapter extends RecyclerView.Adapter<AttractionGalleryViewerAdapter.MyViewHolder> {

    private static final String TAG = AttractionGalleryViewerAdapter.class.getName();

    private StorageReference mCoverImageStorageRef;
    private int mDefaultBackgroundColor;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AttractionGalleryViewerAdapter() {
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");

        int mMaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d(TAG, "Memory Management: MaxMemory = " + mMaxMemory);
        Log.d(TAG, "Memory Management: 1 / 8 = " + mMaxMemory / 8);
    }

    @Override
    public @NonNull MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        Context ctx = parent.getContext();
        View rowView = LayoutInflater.from(ctx).inflate(R.layout.gallery_image_item, parent, false);

        mDefaultBackgroundColor = ctx.getResources().getColor(R.color.imageWithoutPaletteColor);

        return new MyViewHolder(rowView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int pos) {
        final int position = holder.getAdapterPosition();

        final GalleryItem galleryItem = AttractionTabsActivity.galleryItems.get(position);
        holder.gallleryTitle.setText(galleryItem.getTitle());

        if(Commons.isNotNull(galleryItem.getBitmap()) && galleryItem.getBitmap().isRecycled())
            galleryItem.setBitmap(null);

        if (galleryItem.hasCachedFile() && Commons.isNull(galleryItem.getBitmap())) {
            Glide.with(holder.mView.getContext())
                    .asBitmap()
                    .apply(new RequestOptions()
                            .override(C.IMAGE_WIDTH/6, C.IMAGE_HEIGHT/6)
                            .dontAnimate())
                    .load(galleryItem.getCachedFile())
                    .into(new BitmapImageViewTarget(holder.gallleryImage) {
                        @Override
                        protected void setResource(final Bitmap bitmap) {
                            super.setResource(bitmap);
                            if(bitmap == null)
                                return;
                            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    if (!bitmap.isRecycled()) {

                                        Log.d(TAG, "Memory Management: Name = " + galleryItem.getFilename() +
                                                ". W = " + bitmap.getWidth() +
                                                ". H = " + bitmap.getHeight() +
                                                ". S = " + (bitmap.getByteCount() / 1000) + " kb");

                                        if (palette != null) {
                                            Palette.Swatch s = palette.getVibrantSwatch();
                                            if (s == null)
                                                s = palette.getDarkVibrantSwatch();
                                            if (s == null)
                                                s = palette.getLightVibrantSwatch();
                                            if (s == null)
                                                s = palette.getMutedSwatch();

                                            GalleryItem gallery = AttractionTabsActivity.galleryItems.get(position);
                                            holder.gallleryTitle.setTextColor(s.getTitleTextColor());
                                            holder.imageDesc.setTextColor(s.getTitleTextColor());
                                            ViewUtils.animateViewColor(holder.imageTextContainer, mDefaultBackgroundColor, s.getRgb());
                                            gallery.setBitmap(Bitmap.createBitmap(bitmap));
                                            gallery.setSwatch(s);
                                            AttractionTabsActivity.galleryItems.put(position, gallery);
                                        }
                                    } else {
                                        Log.d(TAG, "onGenerated: BITMAP RECYCLED");
                                        galleryItem.setBitmap(null);
                                    }
                                }
                            });
                        }
                    });
        } else if (Commons.isNotNull(galleryItem.getSwatch()) && Commons.isNotNull(galleryItem.getBitmap())) {
            // Already cached the data
            holder.gallleryImage.setImageBitmap(galleryItem.getBitmap());
            holder.gallleryTitle.setTextColor(galleryItem.getSwatch().getTitleTextColor());
            holder.imageDesc.setTextColor(galleryItem.getSwatch().getTitleTextColor());
            ViewUtils.animateViewColor(holder.imageTextContainer, mDefaultBackgroundColor, galleryItem.getSwatch().getRgb());
        } else {
            // hmmm... something interesting!! Get the image from the server again :/
            GlideApp.with(holder.mView.getContext())
                    .load(mCoverImageStorageRef.child(galleryItem.getFilename()))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.gallleryImage);
        }
    }

    public boolean isEmpty() {
        return AttractionTabsActivity.galleryItems.size() == 0;
    }

    @Override
    public int getItemCount() {
        if(Commons.isNull(AttractionTabsActivity.galleryItems)) {
            Answers.getInstance().logCustom(new CustomEvent("ERROR:AttractionGalleryViewerAdapter list is empty"));
            return 0;
        }
        return AttractionTabsActivity.galleryItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final FrameLayout imageTextContainer;
        final ImageView gallleryImage;
        final TextView gallleryTitle;
        final TextView imageDesc;
        final View mView;
        private final OnItemClickListener onItemClickListener;

        MyViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            mView = itemView;
            imageTextContainer = itemView.findViewById(R.id.item_image_text_container);
            this.gallleryImage = itemView.findViewById(R.id.item_image_img);
            this.gallleryTitle = itemView.findViewById(R.id.item_image_title);
            this.imageDesc = itemView.findViewById(R.id.item_image_desc);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(v, getAdapterPosition());
        }
    }
}