package me.carc.btown.tours.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.CacheDir;
import me.carc.btown.db.tours.model.Attraction;

/**
 * Display the different stops of the tour
 */
public class TourDataAdapter extends RecyclerView.Adapter<TourDataAdapter.MyViewHolder> {

    private static final String TAG =TourDataAdapter.class.getName();
    private boolean userDE;
    private final ArrayList<Attraction> mAttractions;
    private StorageReference mCoverImageStorageRef;

    public TourDataAdapter(ArrayList<Attraction> list, boolean isGermanLanguage) {
        this.mAttractions = list;
        this.userDE = isGermanLanguage;
        mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
    }

    public TourDataAdapter(boolean isGermanLanguage) {
        this.mAttractions = new ArrayList<>();
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

        String cachedImage = CacheDir.getInstance().getCachePath() + mAttractions.get(pos).getImage();
        // Use the saved image
        if (FileUtils.checkValidFilePath(cachedImage)) {
            Glide.with(holder.mView.getContext())
                    .load(cachedImage)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.icon);
        } else {
            // Load the image using Glide
            Glide.with(holder.mView.getContext())
                    .load(mCoverImageStorageRef.child(mAttractions.get(pos).getImage()))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                    .into(holder.icon);
        }

        holder.title.setText(String.valueOf(mAttractions.get(pos).getStopName()));
        holder.summary.setText(mAttractions.get(pos).getBusStop(userDE));

    }

    public boolean isEmpty() {
        return mAttractions.isEmpty();
    }

    public void setItems(List<Attraction> attractions) {
        mAttractions.addAll(attractions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mAttractions.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView title;
        final TextView summary;
        final ImageView icon;

        MyViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.title = (TextView) itemView.findViewById(R.id.textViewStoryTitle);
            this.summary = (TextView) itemView.findViewById(R.id.textViewSummary);
            this.icon = (ImageView) itemView.findViewById(R.id.cardview_icon);
        }
    }
}
