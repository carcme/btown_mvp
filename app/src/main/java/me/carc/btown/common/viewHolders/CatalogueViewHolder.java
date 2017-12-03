package me.carc.btown.common.viewHolders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.carc.btown.R;

/**
 * Created by bamptonm on 02/12/2017.
 */

public class CatalogueViewHolder extends RecyclerView.ViewHolder {

    public View mView;

    public CardView card;
    public TextView catalogueTitle;
    public TextView supportingText;
    public ImageView catalogueImage;

    public LinearLayout iconsHolder;
    public TextView time;
    public TextView rating;
    public TextView stops;
    public TextView distance;

    public CatalogueViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        this.card = itemView.findViewById(R.id.tourCardView);

        // enable long press on cardview
//        itemView.setOnCreateContextMenuListener((CatalogueActivity) itemView.getContext());

        this.catalogueImage = itemView.findViewById(R.id.catalogueImage);
        this.catalogueTitle = itemView.findViewById(R.id.catalogueTitle);
        this.supportingText = itemView.findViewById(R.id.supportingText);

        this.iconsHolder = itemView.findViewById(R.id.iconsHolder);
        this.time = itemView.findViewById(R.id.clock);
        this.rating = itemView.findViewById(R.id.rating);
        this.stops = itemView.findViewById(R.id.stops);
        this.distance = itemView.findViewById(R.id.distance);
    }
}