package me.carc.btown.map.search;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.map.IconManager;
import me.carc.btown.map.search.model.Place;

/**
 *
 */
public class SearchListAdapter extends ArrayAdapter<Place> {
    private static final String TAG = SearchListAdapter.class.getName();

	private Context ctx;
	private GeoPoint location;
    private Float heading;
    private IconManager iconManager;

    private boolean newestFirst = true;
    private boolean closestFirst = true;


    public SearchListAdapter(Context ctx) {
        super(ctx, R.layout.search_list_item);
        this.ctx = ctx;

        iconManager = new IconManager(ctx);
    }

    public void addItems(List<Place> items){
        clear();
        addAll(items);
        notifyDataSetChanged();
    }

    public void setListItems(List<Place> items) {
        setNotifyOnChange(false);
        clear();
        for (Place item : items) {
            add(item);
        }
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public void addListItem(Place item) {
        setNotifyOnChange(false);
        add(item);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public void sortList(int sortBy){
        if(sortBy == SearchDialogFragment.SORT_TIME) {
            sort(new MapUtils.TimeStampComparator(newestFirst));
//            newestFirst = !newestFirst;

        } else if(sortBy == SearchDialogFragment.SORT_DATE) {
            sort(new MapUtils.DistanceComparator(closestFirst));
//            closestFirst = !closestFirst;
        }
        notifyDataSetChanged();
    }

    public void insertListItem(Place item, int index) {
        setNotifyOnChange(false);
        insert(item, index);
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

    public GeoPoint getLocation() {
		return location;
	}

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public void setLocation(GeoPoint location) {
		this.location = location;
	}

	@Override
	public Place getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup viewGroup) {
        Place place = getItem(position);

		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.search_list_item, viewGroup, false);
		} else {
			view = convertView;
		}

		ImageView imageView = view.findViewById(R.id.imageView);
		TextView title = view.findViewById(R.id.title);
		TextView subtitle = view.findViewById(R.id.subtitle);
        TextView distance = view.findViewById(R.id.distance);
        TextView timestamp = view.findViewById(R.id.timestamp);

        assert place != null;
        if(place.getIconRes() != 0 || !Commons.isEmpty(place.getOsmKey()))  {

            if(place.getPlaceId()== SearchDialogFragment.SEARCH_ITEM_MAIN)
                Log.d(TAG, "getView: ");
            
            Drawable drawable = iconManager.getRoundedIcon(place.getOsmKey());
            if(Commons.isNull(drawable) && place.getIconRes() != 0)
                drawable = ContextCompat.getDrawable(ctx, place.getIconRes());

            if(Commons.isNotNull(drawable))
                imageView.setImageDrawable(drawable);
            else
                imageView.setImageResource(R.drawable.no_image);  // todo find better default/fall back icon
            imageView.setVisibility(View.VISIBLE);
        } else
            imageView.setVisibility(View.INVISIBLE);

		title.setText(place.getName());
        title.setTypeface(Typeface.DEFAULT_BOLD);

        if(Commons.isEmpty(place.getUserComment()))
            subtitle.setText(place.getAddress());
        else
            subtitle.setText(place.getUserComment());

        if (place.getDistance() > 0) {
			double dist = place.getDistance();
			if (dist == 0) {
				distance.setText("");
			} else {
				distance.setText(MapUtils.getFormattedDistance(dist));
			}
			distance.setVisibility(View.VISIBLE);

		} else if(Commons.isNotNull(location) && place.getLat() != 0 && place.getLng() != 0 ) {
            double d = MapUtils.getDistance(location, place.getLat(), place.getLng());
            place.setDistance(d);  // update place - faster if needed later
            distance.setText(MapUtils.getFormattedDistance(d));

        } else {
			distance.setVisibility(View.INVISIBLE);
		}

		if(place.getTimestamp() > 0) {
            timestamp.setText(AndroidUtils.formatDateMedium(view.getContext(), place.getTimestamp() ));
        } else{
            timestamp.setVisibility(View.INVISIBLE);
        }

		return view;
	}
}
