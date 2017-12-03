package me.carc.btown.extras.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.common.interfaces.DrawableClickListener;
import me.carc.btown.common.viewHolders.CatalogueViewHolder;
import me.carc.btown.extras.bahns.Bahns;

/**
 * Created by bamptonm on 02/12/2017.
 */

public class TransportPlanSelectionAdapter extends RecyclerView.Adapter<CatalogueViewHolder>{

    private List<Bahns.BahnItem> plans;

    public DrawableClickListener onClickListener;

    public TransportPlanSelectionAdapter(DrawableClickListener listener) {
        this.plans = new Bahns().getItems();
        onClickListener = listener;
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_card, viewGroup, false);
        return new CatalogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogueViewHolder holder, final int pos) {

        final Bahns.BahnItem plan = plans.get(pos);
        Context ctx = holder.mView.getContext();

        holder.supportingText.setText(plan.getDescriptionResourceId());
        holder.iconsHolder.setVisibility(View.GONE);

        if(plan.hasLocalFile(plan.getFileName())) {
            holder.catalogueTitle.setVisibility(View.GONE);
            holder.catalogueImage.setVisibility(View.VISIBLE);
            holder.catalogueImage.setScaleType(ImageView.ScaleType.FIT_START);
            Glide.with(ctx)
                    .load(plan.getImageUrl())
                    .into(holder.catalogueImage);
        } else {
            Drawable icon = new IconicsDrawable(ctx, plan.getIconResourceId()).color(ContextCompat.getColor(ctx, R.color.white)).sizeDp(48);
            holder.catalogueTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            holder.catalogueTitle.setText(plan.getTitleResourceId());
            holder.catalogueImage.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Resources r = ctx.getResources();
        int paddingDp = AndroidUtils.getPixelsFromDPs(r, 8);
        params.setMargins(paddingDp, pos == 0 ? paddingDp: 0, paddingDp, paddingDp);
        holder.card.setLayoutParams(params);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.catalogueImage.getDrawable(), holder.getAdapterPosition());
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public Bahns.BahnItem getItem(int pos) { return plans.get(pos); }
}