package me.carc.btown.tours.top_pick_lists.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.carc.btown.R;

/**
 * Created by bamptonm on 08/11/2017.
 */

public class VenueFeaturesAdapter extends RecyclerView.Adapter<VenueFeaturesAdapter.FeaturesViewHolder> {

    private ArrayList<String> features;

    public VenueFeaturesAdapter(@NonNull ArrayList<String> list) {
        this.features = list;
    }

    @Override
    public FeaturesViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_features_item, parent, false);
        return new FeaturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FeaturesViewHolder holder, final int pos) {
        String  feature = features.get(pos);
        holder.item.setText(feature);
    }

    @Override
    public int getItemCount() {
        return features.size();
    }


    public static class FeaturesViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        final TextView item;

        public FeaturesViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            this.item = itemView.findViewById(R.id.item);
        }
    }
}
