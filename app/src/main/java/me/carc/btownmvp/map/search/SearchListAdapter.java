package me.carc.btownmvp.map.search;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.map.IconManager;
import me.carc.btownmvp.map.search.model.Place;

/**
 *
 */
public class SearchListAdapter extends ArrayAdapter<Place> {
    private static final String TAG = C.DEBUG + Commons.getTag();

	private Context ctx;
	private GeoPoint location;
    private Float heading;
    private IconManager iconManager;

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

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
        Place place = getItem(position);

		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.search_list_item, viewGroup, false);
		} else {
			view = convertView;
		}

		ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
		TextView distance = (TextView) view.findViewById(R.id.distance);

        if(place.getIconRes() != 0 || !Commons.isEmpty(place.getOsmKey()))  {

            if(place.getPlaceId()== SearchDialogFragment.SEARCH_ITEM_MAIN)
                Log.d(TAG, "getView: ");
            
            Drawable drawable = iconManager.getRoundedIcon(place.getOsmKey());
            if(Commons.isNull(drawable))
                drawable = ContextCompat.getDrawable(ctx, place.getIconRes());

            imageView.setImageDrawable(drawable);
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
            distance.setText(MapUtils.getFormattedDistance(d));

        } else {
			distance.setVisibility(View.INVISIBLE);
		}

		// todo add bearing arrow

		return view;
	}


}
