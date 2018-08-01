package me.carc.btown.map.sheets.marker_list;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.MarkerListListener;
import me.carc.btown.data.results.OverpassQueryResult;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.ui.CompassView;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.PoiListHolder> {

    private boolean allowUpdates = false;
    private GeoPoint currentLocation;
    private ArrayList<PoiAdpaterItem> items;
    MarkerListListener onClickListener;

    private SparseArray<CompassView> compassArray = new SparseArray<>();
    private SparseArray<TextView> distanceArray = new SparseArray<>();


    static class PoiAdpaterItem {
        String name;
        String poiType;
        double lat;
        double lon;
        String distance;
        Drawable icon;
        String thumb;
    }


    MarkerListAdapter(GeoPoint loc, ArrayList<PoiAdpaterItem> list, MarkerListListener listener) {
        currentLocation = loc;
        this.items = list;
        onClickListener = listener;
    }

    @Override
    public PoiListHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.markers_list_item_layout, viewGroup, false);
        return new PoiListHolder(view);
    }

    @Override
    public void onBindViewHolder(final PoiListHolder holder, int pos) {

        PoiAdpaterItem item = items.get(pos);

        holder.name.setText(item.name);
        holder.desc.setText(item.poiType);

        // find distance from my location
        holder.distance.setText(item.distance);
        distanceArray.put(pos, holder.distance);

        if(Commons.isNotNull(item.thumb)) {
            Glide.with(holder.mView.getContext())
                    .load(item.thumb)
                    .apply(new RequestOptions().error(R.drawable.no_image))
                    .into(holder.icon);
        } else {
            holder.icon.setImageDrawable(item.icon);
            holder.icon.setBackgroundResource(R.color.transparent);
        }


        // point out the direction - todo: needs to be updated on device rotation
        holder.direction.rotationFromLocations(currentLocation, new GeoPoint(item.lat, item.lon), true);
        compassArray.put(pos, holder.direction);

        startUpdates();


        // Click Handlers
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v, holder.getAdapterPosition());
            }
        });
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickImage(v, holder.getAdapterPosition());
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickOverflow(v, holder.getAdapterPosition());
            }
        });

        holder.compassHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickCompass(v, holder.getAdapterPosition());
            }
        });
    }

    public void updateItems(ArrayList<PoiAdpaterItem> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    boolean canUpdate() {
        return allowUpdates;
    }

    public void startUpdates() {
        allowUpdates = true;
    }

    public void stopUpdates() {
        allowUpdates = false;
    }

    public PoiAdpaterItem getNode(int index) {
        return items.get(index);
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
     * Update the distance and direction arrow
     *
     * @param myLocation my location
     * @param deg        the angle the device is aiming
     */
    public void rotationUpdate(GeoPoint myLocation, float deg) {

        if (BuildConfig.DEBUG && (compassArray.size() != distanceArray.size())) {
            throw new AssertionError();
        }

        currentLocation = myLocation;

        for (int i = 0; i < compassArray.size(); i++) {

            CompassView compassView = compassArray.get(i);
            TextView distanceView = distanceArray.get(i);

            if (compassView != null) {

                float normalisedBearing = MapUtils.normalizeDegree(compassView.getLastAngle());
                float poiArrowDir = normalisedBearing - deg;
                compassView.rotationUpdate(poiArrowDir, true);
            }
        }
    }


    /**
     * View Holder
     */
    static class PoiListHolder extends RecyclerView.ViewHolder {

        View mView;

        CircleImageView icon;
        TextView name;
        TextView desc;
        LinearLayout compassHolder;
        TextView distance;
        CompassView direction;
        ImageView more;

        private PoiListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            this.icon = itemView.findViewById(R.id.favListIcon);
            this.name = itemView.findViewById(R.id.favListName);
            this.desc = itemView.findViewById(R.id.favListDesc);
            this.distance = itemView.findViewById(R.id.favListDistance);
            this.direction = itemView.findViewById(R.id.favListNavigationIcon);
            this.compassHolder =itemView.findViewById(R.id.favListcompassHolder);
            more = itemView.findViewById(R.id.favListMore);
        }
    }
}
