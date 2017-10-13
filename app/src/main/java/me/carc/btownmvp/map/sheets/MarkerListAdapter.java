package me.carc.btownmvp.map.sheets;

import android.graphics.drawable.Drawable;
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

import me.carc.btownmvp.BuildConfig;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.data.model.OverpassQueryResult;
import me.carc.btownmvp.data.wiki.WikiQueryPage;
import me.carc.btownmvp.ui.CompassView;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.PoiListHolder> {

    private boolean allowUpdates = false;
    private GeoPoint currentLocation;
    private ArrayList<PoiAdpaterItem> items;
    public MyClickListener onClickListener;

    private SparseArray<CompassView> compassArray = new SparseArray<>();
    private SparseArray<TextView> distanceArray = new SparseArray<>();


    public static class PoiAdpaterItem {
        String name;
        String poiType;
        double lat;
        double lon;
        String distance;
        Drawable icon;
        String image;
    }


    public MarkerListAdapter(GeoPoint loc, ArrayList<PoiAdpaterItem> list, MyClickListener listener) {
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

        if(Commons.isNotNull(item.image)) {
            Glide.with(holder.mView.getContext())
                    .load(item.image)
                    .placeholder(R.drawable.checkered_background)
                    .into(holder.icon);
        } else
            holder.icon.setImageDrawable(item.icon);


        // point out the direction - todo: needs to be updated on device rotation
        holder.direction.rotationFromLocations(currentLocation, new GeoPoint(item.lat, item.lon), true);
        compassArray.put(pos, holder.direction);

        startUpdates();

        // Click Handlers
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.getAdapterPosition());
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
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

    private PoiAdpaterItem getNode(int index) {
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

        ImageView icon;
        TextView name;
        TextView desc;
        TextView distance;
        CompassView direction;

        private PoiListHolder(View itemView) {
            super(itemView);
            mView = itemView;

            this.icon = (ImageView) itemView.findViewById(R.id.favListIcon);
            this.name = (TextView) itemView.findViewById(R.id.favListName);
            this.desc = (TextView) itemView.findViewById(R.id.favListDesc);
            this.distance = (TextView) itemView.findViewById(R.id.favListDistance);
            this.direction = (CompassView) itemView.findViewById(R.id.favListNavigationIcon);
        }
    }
}
