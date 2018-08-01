package me.carc.btown.ui.front_page.externalLinks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;

/**
 * Created by bamptonm on 18/12/2017.
 */

public class ExternalLinksAdapter extends RecyclerView.Adapter<ExternalLinksAdapter.ViewHolder> {

    private List<ExternalLinkItem> mItems;

    @Inject
    public ExternalLinksAdapter() {
        mItems = new ArrayList<>();
    }


    public void setData(List<ExternalLinkItem> profileData) {
        mItems = profileData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.external_link_list_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ExternalLinkItem data = mItems.get(position);
        Context ctx = holder.siteImage.getContext();

        RequestOptions opts = new RequestOptions()
                .error(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        Glide.with(holder.siteImage.getContext())
                .load(ctx.getString(data.getIconLink()))
                .apply(opts)
                .into(holder.siteImage);

        holder.siteDesc.setText(data.getSubTitleResourceId());
        holder.siteTitle.setText(data.getTitleResourceId());
        holder.siteOwner.setText(data.getSiteOwner());

        switch (data.getSiteLanguage()) {
            case 0:
                holder.langDE.setVisibility(View.GONE);
                holder.langEN.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.langEN.setVisibility(View.GONE);
                holder.langDE.setVisibility(View.VISIBLE);
                break;
            default:
                holder.langEN.setVisibility(View.VISIBLE);
                holder.langDE.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ExternalLinkItem getItem(int pos) {
        return mItems.get(pos);
    }


    /**
     * ViewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.siteImage) ImageView siteImage;
        @BindView(R.id.langEN) ImageView langEN;
        @BindView(R.id.langDE) ImageView langDE;
        @BindView(R.id.siteTitle) TextView siteTitle;
        @BindView(R.id.siteDesc) TextView siteDesc;
        @BindView(R.id.siteOwner) TextView siteOwner;
        @BindView(R.id.siteTextFrame) FrameLayout siteTextFrame;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
