package me.carc.btown.tours.top_pick_lists.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.data.all4squ.entities.Photo;
import me.carc.btown.extras.messaging.viewholders.PhotoViewHolder;

/**
 * Created by bamptonm on 08/11/2017.
 */

public class VenuePhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private ArrayList<Photo> photos;

    public VenuePhotoAdapter(@NonNull ArrayList<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int pos) {

        Photo photo = photos.get(pos);

        holder.author.setText(photo.getUser().getFullName());

        Uri uri = Uri.parse(photo.getPrefix() + "cap" + 200 + photo.getSuffix());
        Glide.with(holder.mView.getContext())
                .load(uri)
                .apply(new RequestOptions().placeholder(R.drawable.checkered_background))
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public long getItemId(int position) { return Integer.parseInt(photos.get(position).getId()); }

    public Photo getPhoto(int pos) { return photos.get(pos); }

    public void updatePhotoList(ArrayList<Photo> photos) {
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }
}
