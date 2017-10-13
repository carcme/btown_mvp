package me.carc.btownmvp.map.sheets.wiki;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.data.model.OverpassQueryResult;
import me.carc.btownmvp.data.wiki.WikiQueryPage;
import me.carc.btownmvp.map.interfaces.WikiBookmarkClickListener;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class WikiBookmarksListAdapter extends RecyclerView.Adapter<WikiBookmarksListAdapter.BookmarkItemHolder> {

    private boolean allowUpdates = false;
    private GeoPoint currentLocation;
    private ArrayList<WikiItemAdpater> listItems;
    public WikiBookmarkClickListener onClickListener;

    private SparseArray<TextView> distanceArray = new SparseArray<>();


    public static class WikiItemAdpater {
        long pageId;
        String title;
        String desciption;
        double lat;
        double lon;
        String iconUrl;
        String fullUrl;
    }


    public WikiBookmarksListAdapter(GeoPoint loc, ArrayList<WikiItemAdpater> list, WikiBookmarkClickListener listener) {
        currentLocation = loc;
        this.listItems = list;
        onClickListener = listener;
    }

    @Override
    public BookmarkItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wiki_bookmark_list_item, viewGroup, false);
        return new BookmarkItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookmarkItemHolder holder, int pos) {

        WikiItemAdpater item = listItems.get(pos);

        holder.title.setText(item.title);
        holder.desc.setText(item.desciption);

        Glide.with(holder.mView.getContext())
                .load(item.iconUrl)
                .into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnImageClick(listItems.get(holder.getAdapterPosition()));
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnMoreClick(holder.getAdapterPosition());
            }
        });

        // Click Handlers
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(listItems.get(holder.getAdapterPosition()));
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(listItems.get(holder.getAdapterPosition()));
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void removeItem(long pageId) {

        for (WikiItemAdpater entry : listItems) {
            if(entry.pageId == pageId) {
                listItems.remove(entry);
                break;
            }
        }
        notifyDataSetChanged();
    }


    /**
     * Calculate the distance to osmNode
     *
     * @param node the node
     * @return formatted distance
     */
    private String getdistance(OverpassQueryResult.Element node) {
        double d = MapUtils.getDistance(currentLocation, node.lat, node.lon);
        return MapUtils.getFormattedDistance(d);
    }

    private String getdistance(WikiQueryPage page) {
        double d = MapUtils.getDistance(currentLocation, page.getLat(), page.getLon());
        return MapUtils.getFormattedDistance(d);
    }

    /**
     * View Holder
     */
    static class BookmarkItemHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView thumbnail;
        TextView title;
        TextView desc;
        ImageView more;

        private BookmarkItemHolder(View itemView) {
            super(itemView);
            mView = itemView;

            this.thumbnail = (ImageView) itemView.findViewById(R.id.bookmarkThumb);
            this.title = (TextView) itemView.findViewById(R.id.bookmarkTitle);
            this.desc = (TextView) itemView.findViewById(R.id.bookmarkSubtitle);
            this.more = (ImageView) itemView.findViewById(R.id.bookmarkMore);
        }
    }
}
