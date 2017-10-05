package me.carc.btownmvp.map.sheets.model.adpater;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.R;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.map.sheets.model.InfoCard;
import me.carc.btownmvp.ui.custom.CapitalisedTextView;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class PoiMoreRecyclerAdapter extends RecyclerView.Adapter<PoiMoreRecyclerAdapter.ViewHolder> {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private final ArrayList<InfoCard> list;
    public MyClickListener onClickListener;


    public PoiMoreRecyclerAdapter(ArrayList<InfoCard> tours, MyClickListener listener ) {
        this.list = tours;
        onClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sheet_more_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int pos) {

        IconicsDrawable drawable = new IconicsDrawable(holder.itemView.getContext(),
                list.get(pos).getIcon())
                .color(ContextCompat.getColor(holder.itemView.getContext(), R.color.sheet_icon_color))
                .sizeDp(20);

        holder.infoText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        holder.infoText.setText(list.get(pos).getData());

        switch (list.get(pos).getType()) {
            case EMAIL:
                holder.infoText.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                break;

            case WEB:
                holder.infoText.setAutoLinkMask(Linkify.WEB_URLS);
                break;

            case PHONE:
                holder.infoText.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                break;
        }


        // Click Handlers
        holder.infoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.getAdapterPosition());
            }
        });
        holder.infoText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CapitalisedTextView infoText;

        public ViewHolder(View v) {
            super(v);

            infoText = (CapitalisedTextView ) v.findViewById(R.id.infoText);
        }
    }
}
