package me.carc.btown.tours.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    private static final String TAG = C.DEBUG + Commons.getTag();


    private Context ctx;
    private StorageReference mCoverImageStorageRef;

    //    private int mScreenWidth;
//    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AttractionGalleryViewerAdapter() {
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.ctx = parent.getContext();
        View rowView = LayoutInflater.from(ctx).inflate(R.layout.gallery_image_item, parent, false);

//        mScreenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
        //get the colors
//        mDefaultTextColor = ctx.getResources().getColor(R.color.text_without_palette);
        mDefaultBackgroundColor = ctx.getResources().getColor(R.color.imageWithoutPaletteColor);

        return new MyViewHolder(rowView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        GalleryItem galleryItem = AttractionTabsActivity.galleryItems.get(position);
        holder.gallleryTitle.setText(galleryItem.getTitle());

        if (galleryItem.hasCachedFile() && Commons.isNull(galleryItem.getBitmap())) {
            Glide.with(holder.mView.getContext())
                    .load(galleryItem.getCachedFile())
                    .asBitmap()
                    .into(new BitmapImageViewTarget(holder.gallleryImage) {
                        @Override
                        public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {

                                    if (!bitmap.isRecycled()) {
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
                                            gallery.setBitmap(bitmap);
                                            gallery.setSwatch(s);
                                            AttractionTabsActivity.galleryItems.put(position, gallery);
                                        }
                                    } else
                                        Log.d(TAG, "onGenerated: BITMAP RECYCLED");
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
            Glide.with(holder.mView.getContext())
                    .using(new FirebaseImageLoader())
                    .load(mCoverImageStorageRef.child(galleryItem.getFilename()))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.gallleryImage);
        }
    }

    public boolean isEmpty() {
        return AttractionTabsActivity.galleryItems.size() == 0;
    }

    @Override
    public int getItemCount() {
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
            onItemClickListener.onClick(v, getPosition());
        }
    }
}