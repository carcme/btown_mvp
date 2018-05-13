package me.carc.btown.map.markers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import me.carc.btown.R;
import me.carc.btown.Utils.ImageUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.results.OverpassQueryResult;
import me.carc.btown.map.IconManager;
import me.carc.btown.db.tours.model.POIs;

/**
 * Overlay for handling markers
 * Created by bamptonm on 5/4/17.
 */

public class MarkersOverlay extends RadiusMarkerClusterer {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private RadiusMarkerClusterer mMarkers;
    private FolderOverlay mTourDestination;
    protected MapView map;
    private ArrayList<OverpassQueryResult.Element> elements;
    private Context mContext;

    private IconManager iconManager;
    protected Marker.OnMarkerClickListener mOnMarkerClickListener;



    public MarkersOverlay(Context ctx, MapView mapView, String name) {
        super(ctx);
        mContext = ctx;

        mMarkers = new RadiusMarkerClusterer(ctx);
        mMarkers.setName(name);
        mMarkers.setMaxClusteringZoomLevel(15);
        map = mapView;
        map.getOverlays().add(mMarkers);

        mTourDestination = new FolderOverlay();
        mTourDestination.setName("Destination");
        map.getOverlays().add(mTourDestination);

        iconManager = new IconManager(ctx);
    }

    public void invalidate() {
        mMarkers.invalidate();
        map.invalidate();
    }

    public void setOnMarkerClickListener(Marker.OnMarkerClickListener listener){
        mOnMarkerClickListener = listener;
    }

    @Override
    public int getItemsLength() {
        return super.getItemsLength();
    }

    public boolean clear() {
        boolean cleared = false;
        if (getElementsListLen() > 0) {
            elements.clear();
            cleared = true;
        }
        if (getSize() > 0) {
            mMarkers.getItems().clear();
            cleared = true;
        }
        invalidate();
        return cleared;
    }

    private int getElementsListLen() {
        return elements != null ? elements.size() : 0;
    }


    public ArrayList<OverpassQueryResult.Element> getElemets() {
        if (elements != null)
            return elements;
        return null;
    }

    public int getSize() {
        if (mMarkers != null)
            return mMarkers.getItemsLength();
        return 0;
    }

    public ArrayList<Marker> getItems() {
        if (mMarkers != null)
            return mMarkers.getItems();
        return null;
    }

    public Marker get(int id) {
        return mMarkers.getItem(id);
    }

    public void add(Marker marker) {
        if (mMarkers != null) {
            mMarkers.add(marker);
        }
    }

    public void closeInfoWidows() {
        for (Marker marker : mMarkers.getItems()) {
            marker.closeInfoWindow();
        }
    }

    public void addTourDestination(GeoPoint point, String title, Bitmap bm, String file, Resources res) {
        new setMarkerIconAsPhoto(point, res, title).execute(bm, file);
    }


    private class setMarkerIconAsPhoto extends AsyncTask<Object, Void, RoundedBitmapDrawable> {
        Marker destination;
        GeoPoint point;
        Resources res;
        String title;

        private setMarkerIconAsPhoto(GeoPoint p, Resources r, String t) {
            destination = new Marker(map);

            point = p;
            this.res = r;
            title = t;
        }

        @Override
        protected RoundedBitmapDrawable doInBackground(Object... params) {
            Bitmap thumbnail = (Bitmap) params[0];
            String file = (String) params[1];

            int width = 100;
            int height = 100;
            if (Commons.isNull(thumbnail) && Commons.isNull(file))
                thumbnail = ImageUtils.decodeSampledBitmapFromPath(file, width, height);

            if (Commons.isNotNull(thumbnail)) {

//                thumbnail = ImageUtils.resizeBitmap(res, thumbnail, 100, 100);
                thumbnail = Bitmap.createScaledBitmap(thumbnail, width, height, true);
                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(res, thumbnail);
                roundDrawable.setCornerRadius(Math.max(thumbnail.getWidth(), thumbnail.getHeight()) / 2.0f);
                thumbnail.recycle();

//                RoundedBitmapDrawable roundDrawable = ImageUtils.resizeRoundedBitmap(res, thumbnail, 100, 100, true);
                destination.setIcon(roundDrawable);

                return roundDrawable;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(RoundedBitmapDrawable drawable) {
            super.onPostExecute(drawable);
            if (Commons.isNotNull(destination)) {
                if (Commons.isNull(drawable))
                    destination.setIcon(ResourcesCompat.getDrawable(res, R.drawable.btown_round_place_icon, null));
                destination.setPosition(point);
                destination.setTitle(title);
                // Make this separate from the clusters
                mTourDestination.add(destination);

                invalidate();
            }
        }
    }

    public void addTourPOIs(ArrayList<POIs> pois, Resources res, String packageName) {
        if(pois != null) // allow for the json tag to be missing.
            new addTourPOIs().execute(map, pois, res, packageName);
    }


    private class addTourPOIs extends AsyncTask<Object, Void, Void> {

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Object... params) {
            MapView view = (MapView)params[0];
            ArrayList<POIs> tourPOIs = (ArrayList<POIs>)params[1];
            Resources res = (Resources)params[2];
            String packageName = (String)params[3];

            for(POIs item : tourPOIs) {
                String title = item.title;
                double lat = item.lat;
                double lng = item.lng;

                // Add destination location marker
                Marker poi = new Marker(view);
                poi.setPosition(new GeoPoint(lat, lng));
                poi.setTitle(title);

                int iconRes = res.getIdentifier(title, "raw", packageName);

                if(iconRes == 0) {
                    //tour map point of interest
                    poi.setIcon(ResourcesCompat.getDrawable(res, R.drawable.btown_round_place_icon, null));
                } else {
                    Drawable icon = ResourcesCompat.getDrawable(res, iconRes, null);
                    if(icon != null) {
                        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                        Drawable d = new BitmapDrawable(res, ImageUtils.resizeBitmap(res, bitmap, 50, 50));
                        poi.setIcon(d);
                    }
                }
                add(poi);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            invalidate();
        }
    }

    public void updateWithResults(OverpassQueryResult.Element node, String tag) {
        List<OverpassQueryResult.Element> singleElement = new ArrayList<>();
        singleElement.add(node);
        new updateUIWithNodePOI().execute(singleElement, tag);
    }

    public void updateWithResults(List<OverpassQueryResult.Element> nodes, String tag) {
        new updateUIWithNodePOI().execute(nodes, tag);
    }

    /**
     * If no click listener defined, show POI info window
     */
    private class updateUIWithNodePOI extends AsyncTask<Object, Void, String> {

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(Object... params) {
            ArrayList<OverpassQueryResult.Element> nodes = (ArrayList<OverpassQueryResult.Element>) params[0];
            String featureTag = (String) params[1];

            if (nodes != null) {

//                POIInfoWindow poiInfoWindow = null;
//                MarkerInfoWindow markerWindow = null;

//                if (featureTag.equals("TourMapRequest"))
//                    markerWindow = new MarkerInfoWindow(R.layout.btown_bubble, map);
//                else
//                    poiInfoWindow = new POIInfoWindow(mContext, map);

                for (OverpassQueryResult.Element node : nodes) {
                    Marker poiMarker = new Marker(map);

                    if(node.tags.isIcon) {
                        // TODO: 16/11/2017 use the icon supplied - may need to save the FSQ icon to store somewhere
                    }

                    String primaryType = node.tags.getPrimaryType();

                    Drawable icon = iconManager.getRoundedIcon(primaryType);

                    if (Commons.isNull(icon)) {
                        icon = ContextCompat.getDrawable(mContext, R.drawable.btown_round_place_icon);
                    }

                        // Filer entries with incomplete information
                    if (Commons.isNotNull(icon)) {

                        if(Commons.isNull(elements))
                            elements = new ArrayList<>();

                        if (!Commons.isEmpty(node.tags.name))
                            poiMarker.setTitle(node.tags.name);

                        poiMarker.setAnchor(Marker.ANCHOR_CENTER, 0.5f);
                        poiMarker.setSnippet(primaryType);
                        poiMarker.setPosition(new GeoPoint(node.lat, node.lon));
                        poiMarker.setIcon(icon);
//                        poiMarker.setIcon(icon.getIcon(node.getIconString()));
                        poiMarker.setRelatedObject(node);
                        if(mOnMarkerClickListener != null)
                            poiMarker.setOnMarkerClickListener(mOnMarkerClickListener);
//                        else
//                            poiMarker.setInfoWindow(poiInfoWindow != null ? poiInfoWindow : markerWindow);

                        elements.add(node);

                        add(poiMarker);
                    } else
                        Log.d("DEAD", "updateUIWithNodePOI: Icon not found " + primaryType);
                }/**/
            }
            return featureTag;
        }

        @Override
        protected void onPostExecute(String tag) {

            if (mMarkers.getItemsLength() == 1) {
                mOnMarkerClickListener.onMarkerClick(mMarkers.getItem(0), map);

            } else if(mMarkers.getItemsLength() > 1) {
                // showing multi POI's - catch the null in the MapPresenter#onMarkerClick()
                Marker indicator = new Marker(map);
                indicator.setTitle("SEARCH_INDICATOR");
                mOnMarkerClickListener.onMarkerClick(indicator, map);
            }

            mMarkers.setName(tag);
            invalidate();
        }
    }
}
