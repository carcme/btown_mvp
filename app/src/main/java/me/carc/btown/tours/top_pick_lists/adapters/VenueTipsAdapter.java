package me.carc.btown.tours.top_pick_lists.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.ItemsGroupsTip;
import me.carc.btown.data.all4squ.entities.UserPhoto;

/**
 * Created by bamptonm on 08/11/2017.
 */

public class VenueTipsAdapter extends RecyclerView.Adapter<VenueTipsAdapter.TipHolder> {

    private ArrayList<ItemsGroupsTip> tips;

    public VenueTipsAdapter(@NonNull ArrayList<ItemsGroupsTip> tips) {
        this.tips = tips;
    }

    @Override
    public TipHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_tip, parent, false);
        return new TipHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipHolder holder, final int pos) {

        ItemsGroupsTip tip = tips.get(pos);

        try {
            if (tip.getUser().getFirstName().equals(holder.mView.getContext().getString(R.string.app_name))) {
                holder.userPhoto.setImageResource(R.mipmap.ic_launcher_rnd);
            } else {
                UserPhoto photo = tip.getUser().getPhoto();
                if (Commons.isNotNull(tip.getPhotourl())) {
                    Uri uri = Uri.parse(photo.getPrefix() + "width200" + photo.getSuffix());
                    Glide.with(holder.mView.getContext())
                            .load(uri)
                            .placeholder(R.drawable.checkered_background)
                            .into(holder.userPhoto);
                } else
                    holder.userPhoto.setImageResource(R.drawable.no_image);
            }
        } catch (Exception e) {
            holder.userPhoto.setImageResource(R.drawable.no_image);
        }

        holder.userName.setText(tip.getUser().getFullName());
        holder.date.setText(String.valueOf(pos));
//        holder.date.setText(Commons.readableDate(tip.getCreatedAt() * 1000));
        holder.tipText.setText(tip.getText());

        if (Commons.isNotNull(tip.getPhotourl())) {
            Uri uri = Uri.parse(tip.getPhoto().getPrefix() + "width300" + tip.getPhoto().getSuffix());
            Glide.with(holder.mView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.checkered_background)
                    .into(holder.tipPhoto);
            holder.tipPhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(tips.get(position).getId());
    }

    public ItemsGroupsTip getTip(int pos) {
        return tips.get(pos);
    }

    public void updateTipList(ArrayList<ItemsGroupsTip> tips) {
        this.tips.addAll(tips);
        notifyDataSetChanged();
    }

    static class TipHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageView userPhoto;
        TextView userName;
        TextView date;
        TextView tipText;
        ImageView tipPhoto;

        private TipHolder(View itemView) {
            super(itemView);

            mView = itemView;

            this.userPhoto = itemView.findViewById(R.id.tipsUserPhoto);
            this.userName = itemView.findViewById(R.id.tipsUserName);
            this.date = itemView.findViewById(R.id.tipsDate);
            this.tipText = itemView.findViewById(R.id.tipsTip);
            this.tipPhoto = itemView.findViewById(R.id.tipsTipPhoto);
        }
    }
}
