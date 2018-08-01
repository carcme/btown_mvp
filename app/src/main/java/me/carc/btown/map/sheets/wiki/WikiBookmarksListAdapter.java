package me.carc.btown.map.sheets.wiki;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.WikiBookmarkClickListener;
import me.carc.btown.data.results.OverpassQueryResult;
import me.carc.btown.db.bookmark.BookmarkEntry;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class WikiBookmarksListAdapter extends RecyclerView.Adapter<WikiBookmarksListAdapter.BookmarkItemHolder> {

    private GeoPoint currentLocation;
    private ArrayList<BookmarkEntry> bookmarkEntries = new ArrayList<>();
    private WikiBookmarkClickListener onClickListener;

    WikiBookmarksListAdapter(GeoPoint loc, WikiBookmarkClickListener listener) {
        currentLocation = loc;
        onClickListener = listener;
    }

    WikiBookmarksListAdapter(GeoPoint loc, ArrayList<BookmarkEntry> list, WikiBookmarkClickListener listener) {
        currentLocation = loc;
        this.bookmarkEntries = list;
        onClickListener = listener;
    }

    @Override
    public BookmarkItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wiki_bookmark_list_item, viewGroup, false);
        return new BookmarkItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookmarkItemHolder holder, int pos) {
        BookmarkEntry item = bookmarkEntries.get(pos);

        holder.title.setText(item.getTitle());
        holder.desc.setText(Commons.isEmpty(item.getUserComment()) ? item.getExtract() : item.getUserComment());

        Glide.with(holder.mView.getContext())
                .load(item.getThumbnail())
                .apply(new RequestOptions().error(R.drawable.ic_wiki_map_marker))
                .into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnImageClick(bookmarkEntries.get(holder.getAdapterPosition()));
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
                onClickListener.OnClick(bookmarkEntries.get(holder.getAdapterPosition()));
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(bookmarkEntries.get(holder.getAdapterPosition()));
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookmarkEntries.size();
    }

    List<BookmarkEntry> getItems() {
        return bookmarkEntries;
    }

    BookmarkEntry addItem(int index) {
        return bookmarkEntries.get(index);
    }


    void addItems(List<BookmarkEntry> entries) {
        bookmarkEntries.addAll(entries);
        notifyDataSetChanged();
    }

    void removeItem(long pageId) {
        for (BookmarkEntry entry : bookmarkEntries) {
            if(entry.getPageId() == pageId) {
                int index = bookmarkEntries.indexOf(entry);
                bookmarkEntries.remove(entry);
                notifyItemRemoved(index);
                break;
            }
        }
    }

    /**
     * Calculate the distance to osmNode
     *
     * @param node the node
     * @return formatted distance
     */
    private String getDistance(OverpassQueryResult.Element node) {
        double d = MapUtils.getDistance(currentLocation, node.lat, node.lon);
        return MapUtils.getFormattedDistance(d);
    }

    private String getDistance(BookmarkEntry entry) {
        if(entry.distance == 0)
            entry.distance = MapUtils.getDistance(currentLocation, entry.getLat(), entry.getLon());
        return MapUtils.getFormattedDistance(entry.distance);
    }

    /**
     * View Holder
     */
    static class BookmarkItemHolder extends RecyclerView.ViewHolder {

        View mView;

        @BindView(R.id.bookmarkThumb) ImageView thumbnail;
        @BindView(R.id.bookmarkTitle) TextView title;
        @BindView(R.id.bookmarkSubtitle) TextView desc;
        @BindView(R.id.bookmarkMore) ImageView more;

        private BookmarkItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }
    }
}
