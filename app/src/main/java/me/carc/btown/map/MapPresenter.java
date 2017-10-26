package me.carc.btown.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import me.carc.btown.App;
import me.carc.btown.MapActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.ImageUtils;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.MapZoom;
import me.carc.btown.Utils.WikiUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.CompassSensor;
import me.carc.btown.common.FusedLocation;
import me.carc.btown.common.TinyDB;
import me.carc.btown.data.model.OverpassDataTags;
import me.carc.btown.data.model.OverpassQueryResult;
import me.carc.btown.data.model.PlaceToOverpass;
import me.carc.btown.data.overpass.OverpassApi;
import me.carc.btown.data.overpass.OverpassServiceProvider;
import me.carc.btown.data.overpass.query.QueryGenerator;
import me.carc.btown.data.wiki.WikiApi;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.data.wiki.WikiQueryResponse;
import me.carc.btown.data.wiki.WikiServiceProvider;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.db.history.HistoryEntry;
import me.carc.btown.map.markers.MarkersOverlay;
import me.carc.btown.map.overlays.MyDirectedLocationOverlay;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.map.sheets.marker_list.MarkerListDialogFragment;
import me.carc.btown.map.sheets.SinglePoiOptionsDialog;
import me.carc.btown.map.sheets.WikiPoiSheetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Map functionality
 * Created by bamptonm on 19/09/2017.
 */

public class MapPresenter implements IMap.Presenter, MapEventsReceiver, org.osmdroid.views.overlay.Marker.OnMarkerClickListener {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private static int MIN_POI_LOOKUP_ZOOM_LVL = 16;
    private static final int ZOOM_IN_TIME_DELAY = 500;

    private final static String LAST_ZOOM_LEVEL = "LAST_ZOOM_LEVEL";
    private final static String LAST_CENTER_LAT = "LAST_CENTER_LAT";
    private final static String LAST_CENTER_LNG = "LAST_CENTER_LNG";


    private static final String LOCATION = "LOCATION";

    private static final String OVERLAY_SINGLE_TAP = "OVERLAY_SINGLE_TAP";
    private static final String OVERLAY_SEARCH = "OVERLAY_SEARCH";

    private static final int DEFAULT_WIKI_SEARCH_RADIUS = 10000;
    private static final int DEFAULT_WIKI_THUMBNAIL_SIZE = C.SCREEN_WIDTH;

    private final static float BERLIN_LAT = 52.517f;
    private final static float BERLIN_LNG = 13.350f;


    private MyDirectedLocationOverlay myLocationOverlay;
    private MarkersOverlay mMarkersOverlay;
    private MarkersOverlay mSearchOverlay;

    private Context mContext;
    private MapView mMap;
    private final IMap.View view;
    private FusedLocation fusedLocation;
    private CompassSensor compassSensor;
    private Location mLocation;
    private GeomagneticField geoField;

    private GeoPoint mTouchPoint;

    private Call<OverpassQueryResult> overpassCall;
    private Call<WikiQueryResponse> wikiCall;

    private BrowsingLocation browsingLocation;
    private boolean mTrackingMode;
    private boolean bAllowReturnLocation;


    public MapPresenter(Context context, IMap.View view, MapView map) {
        this.mContext = context;
        this.view = view;
        this.mMap = map;
        view.setPresenter(this);

/*
        compassSensor = new CompassSensor(mContext, onCompassCallback);

        fusedLocation = new FusedLocation(context, onLocationChanged);
        if (!fusedLocation.canGetLocation()) {
            view.enableLocationDependantFab(false);
            view.requestGpsEnable();
        } else {
            compassSensor.enableSensors();
        }
*/
    }

    @Override
    public void onUpdateLocation() {
        fusedLocation = new FusedLocation(mContext, onLocationChanged);
        compassSensor.enableSensors();
    }

    private FusedLocation.Callback onLocationChanged = new FusedLocation.Callback() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());

            myLocationOverlay.setLocation(point);
            myLocationOverlay.setEnabled(true);

            if (location.hasAccuracy()) {
                myLocationOverlay.setShowAccuracy(true);
                myLocationOverlay.setAccuracy((int) location.getAccuracy());
            }

            view.enableLocationDependantFab(true);

            geoField = new GeomagneticField(
                    Double.valueOf(location.getLatitude()).floatValue(),
                    Double.valueOf(location.getLongitude()).floatValue(),
                    Double.valueOf(location.getAltitude()).floatValue(),
                    System.currentTimeMillis());

            if (!compassSensor.isEnabled()) {
                compassSensor.enableSensors();
            }

            mMap.invalidate();
        }

        public void onConnected(boolean connected) {
            if (connected) {
                fusedLocation.startLocationUpdates();
            } else {
                compassSensor.disableSensors();
                view.enableLocationDependantFab(false);
            }
        }
    };

    private CompassSensor.Callback onCompassCallback = new CompassSensor.Callback() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                myLocationOverlay.setAccuracy(accuracy);
                myLocationOverlay.setShowAccuracy(true);
/*
            // check magnet is available, sensor is type magent and accuracy is not HIGH
            if (magnetometer != null && sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                showCompassCalibateDialog(accuracy);
*/
                mMap.invalidate();
            }
        }

        @Override
        public void onAngleCalculation(float deg) {
            if (!AndroidUtils.isPortrait(mContext))
                deg += 90;

            if (myLocationOverlay.isEnabled()) {
                SinglePoiOptionsDialog poiDlg = getPoiDialog();
                if (Commons.isNotNull(poiDlg)) {
                    poiDlg.onNewAngleCalculation(deg, myLocationOverlay.getLocation());
                }

                MarkerListDialogFragment listdlg = getMarkerListDialogFragment();
                if (Commons.isNotNull(listdlg)) {
                    listdlg.updatePoiDirection(myLocationOverlay.getLocation(), deg);
                }

                myLocationOverlay.rotationUpdate(deg, true);
                mMap.invalidate();
            }
        }
    };

    @Override
    public void start() {
        mMap.setTilesScaledToDpi(true);

        TinyDB db = TinyDB.getTinyDB();
        mMap.setMinZoomLevel(2);
        mMap.getController().setZoom(db.getInt(LAST_ZOOM_LEVEL, 9));
        GeoPoint savedCenter = new GeoPoint(db.getDouble(LAST_CENTER_LAT, BERLIN_LAT), db.getDouble(LAST_CENTER_LNG, BERLIN_LNG));
        mMap.getController().animateTo(savedCenter);

        compassSensor = new CompassSensor(mContext, onCompassCallback);

        fusedLocation = new FusedLocation(mContext, onLocationChanged);
        if (!fusedLocation.canGetLocation()) {
            view.enableLocationDependantFab(false);
            view.requestGpsEnable();
        } else {
            compassSensor.enableSensors();
        }
    }

    @Override
    public void stop() {
        fusedLocation.stopLocationUpdates();
        compassSensor.disableSensors();

        if (Commons.isNotNull(overpassCall)) overpassCall.cancel();
        if (Commons.isNotNull(wikiCall)) wikiCall.cancel();

        if (Commons.isNotNull(myLocationOverlay)) {

            TinyDB db = TinyDB.getTinyDB();

            GeoPoint location = myLocationOverlay.getLocation();

            if (Commons.isNotNull(mMap)) {
                db.putInt(LAST_ZOOM_LEVEL, mMap.getZoomLevel());
                db.putDouble(LAST_CENTER_LAT, mMap.getMapCenter().getLatitude());
                db.putDouble(LAST_CENTER_LNG, mMap.getMapCenter().getLongitude());
            }
        }

        closePoiDialog();
        clearBackStack();
    }

    private void clearBackStack() {
        FragmentManager manager = ((MapActivity) mContext).getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public Bundle getBundle(Bundle bundle) {
        bundle.putParcelable(LOCATION, myLocationOverlay.getLocation());
        bundle.putParcelableArrayList(OVERLAY_SINGLE_TAP, mMarkersOverlay.getElemets());
        bundle.putParcelableArrayList(OVERLAY_SEARCH, mSearchOverlay.getElemets());
        return bundle;
    }

    @Override
    public void initMap(Bundle savedInstanceState) {
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);

        // Needed to get map events
        MapEventsOverlay overlay = new MapEventsOverlay(this);
        mMap.getOverlays().add(overlay);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        addOverlays(savedInstanceState);

        mMap.setOnTouchListener(onMapTouchListener);
    }

    /**
     * Defina and add the map overlays
     */
    private void addOverlays(Bundle inState) {
        // My Location Overlay
        myLocationOverlay = new MyDirectedLocationOverlay(mContext, R.drawable.ic_navigation);

        if (Commons.isNotNull(inState))
            myLocationOverlay.setLocation((GeoPoint) inState.getParcelable(LOCATION));

        myLocationOverlay.setEnabled(false);

        // Single Tap Icon Overlay
        mMarkersOverlay = new MarkersOverlay(mContext, mMap, OVERLAY_SINGLE_TAP);
        Drawable clusterIconD = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.marker_cluster, null);
        assert clusterIconD != null;
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        mMarkersOverlay.setIcon(clusterIcon);
        mMarkersOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mMarkersOverlay.mTextAnchorU = 0.70f;
        mMarkersOverlay.mTextAnchorV = 0.27f;
        mMarkersOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);

        // Search Icon Overlay
        mSearchOverlay = new MarkersOverlay(mContext, mMap, OVERLAY_SEARCH);
        clusterIconD = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.marker_cluster, null);
        assert clusterIconD != null;
        clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        mSearchOverlay.setIcon(clusterIcon);
        mSearchOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mSearchOverlay.mTextAnchorU = 0.70f;
        mSearchOverlay.mTextAnchorV = 0.27f;
        mSearchOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);

        // Add the overlays to the map
        mMap.getOverlays().add(myLocationOverlay);
        mMap.getOverlays().add(mMarkersOverlay);
        mMap.getOverlays().add(mSearchOverlay);

        mMarkersOverlay.setOnMarkerClickListener(MapPresenter.this);
        mSearchOverlay.setOnMarkerClickListener(MapPresenter.this);

        if (Commons.isNotNull(inState)) {
            // single tap POI
            ArrayList<OverpassQueryResult.Element> elements = inState.getParcelableArrayList(OVERLAY_SINGLE_TAP);
            mMarkersOverlay.updateWithResults(elements, OVERLAY_SINGLE_TAP);

            // search POI's
            elements = inState.getParcelableArrayList(OVERLAY_SEARCH);
            if (Commons.isNotNull(elements) && elements.size() > 0) {
                mSearchOverlay.updateWithResults(elements, OVERLAY_SEARCH);
            }
        }
    }

    /**
     * Get my Geopoint - revert to map center Geopoint otherwise
     *
     * @return GeoPoint location
     */
    private GeoPoint whereAmI() {
        GeoPoint whereAmI;
        if (Commons.isNotNull(mLocation))
            whereAmI = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
        else if (Commons.isNotNull(myLocationOverlay.getLocation()))
            whereAmI = myLocationOverlay.getLocation();
        else
            whereAmI = (GeoPoint) mMap.getMapCenter();

        return whereAmI;
    }

    /**
     * User has touched the map
     *
     * @param p the point of the touch
     * @return true if handled
     */
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        mTouchPoint = p;

        clearLayers(false);

        // Build query and run search - // TODO: 30/09/2017 make search parameters configurable
        String query = new QueryGenerator().generator(OverpassDataTags.SinglePoi(), p).build();
        findPOI(query, true);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {

        view.showNavigationPopup(p);

        return true;
    }

    @Override
    public void onFoodLookupFromLongPress(GeoPoint p) {
        clearLayers(true);
        String query = new QueryGenerator().generator(OverpassDataTags.FoodDrinkData(), mMap.getBoundingBox()).build();
        findPOI(query, false);
    }

    @Override
    public void onTouristLookupFromLongPress(GeoPoint p) {
        clearLayers(true);
        String query = new QueryGenerator().generator(OverpassDataTags.TouristData(), mMap.getBoundingBox()).build();
        findPOI(query, false);
    }

    private void clearLayers(boolean clearSearchLayer) {
        mMarkersOverlay.clear();
        if(clearSearchLayer) mSearchOverlay.clear();
        view.onLoadStart();

        // if search is running, allow cancel option
        if (Commons.isNotNull(overpassCall)) {
            overpassCall.cancel();
        }
    }

    /**
     * Callback from the search dialog - build the search query and run the search
     *
     * @param poi use the tags to get the list of POIs to lookup
     */
    @Override
    public void onSearchItemSelected(Place poi) {

        HashMap<String, String> tags = new HashMap<>();

        String[] array = poi.getTags();
        for (int i = 2; i < array.length; i++) {
            String[] tag = array[i].split("=");
            tags.put(tag[1], tag[0]);
        }

        String query = new QueryGenerator().generator(tags, mMap.getBoundingBox()).build();
        findPOI(query, false);
    }

    private AppDatabase getDatabase() {
        return ((App) mContext.getApplicationContext()).getDB();
    }

    /**
     * Show items
     *
     * @param dbType which database to query
     * @param poi    null = display all database entries,  poi = display single item
     */
    @Override
    public void onShowFromDatabase(final int dbType, final Place poi) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                OverpassQueryResult.Element element = null;
                List<OverpassQueryResult.Element> elements = new ArrayList<>();

                switch (dbType) {
                    case SearchDialogFragment.SEARCH_ITEM_HISTORY:
                        if (Commons.isNotNull(poi)) {  // get single item from history database
                            HistoryEntry entry = getDatabase().historyDao().findByOsmId(poi.getOsmId());
                            if (Commons.isNotNull(entry))
                                element = entry.getOsmPojo();
                        } else {  // get all from favorites database
                            List<HistoryEntry> entries = getDatabase().historyDao().getAllHistories();
                            if (Commons.isNotNull(entries))
                                for (HistoryEntry entry : entries)
                                    elements.add(entry.getOsmPojo());
                        }
                        break;

                    case SearchDialogFragment.SEARCH_ITEM_FAVORITE:
                        if (Commons.isNotNull(poi)) {  // get single item from database
                            FavoriteEntry entry = getDatabase().favoriteDao().findByOsmId(poi.getOsmId());
                            if (Commons.isNotNull(entry))
                                element = entry.getOsmPojo();
                        } else {  // get all from favorites database
                            List<FavoriteEntry> entries = getDatabase().favoriteDao().getAllFavorites();
                            if (Commons.isNotNull(entries))
                                for (FavoriteEntry entry : entries)
                                    elements.add(entry.getOsmPojo());
                        }
                        break;
                }

                if (Commons.isNotNull(element)) {
                    mMarkersOverlay.updateWithResults(element, "");
                    closeQuickSearch();
                } else if (elements.size() > 0) {  //put on search overlay regardless of how many items there are (more than zero anyway :s )
                    mSearchOverlay.updateWithResults(elements, "");
                    closeQuickSearch();
                } else
                    throw new RuntimeException("Favorite entry is null!");
            }
        });
    }

    @Override
    public void onShowPlaceItem(Place poi) {
        mSearchOverlay.clear();
        OverpassQueryResult.Element singleItem = new PlaceToOverpass(mMap.getMapCenter()).convertPlace(poi);
        mSearchOverlay.updateWithResults(singleItem, "");
        closeQuickSearch();
    }

    /**
     * get POI info on a map area
     *
     * @param query        the query to run
     * @param singleSearch true  = find a single item at the point,
     *                     false = find all on the area of map shown
     */
    private void findPOI(String query, final boolean singleSearch) {
        view.onLoadStart();
        OverpassApi service = OverpassServiceProvider.get();
        overpassCall = service.interpreter(query);
        overpassCall.enqueue(new Callback<OverpassQueryResult>() {

            @Override
            @SuppressWarnings({"ConstantConditions"})
            public void onResponse(@NonNull Call<OverpassQueryResult> call, @NonNull Response<OverpassQueryResult> response) {

                OverpassQueryResult.Element relevantNode = null;
                assert response.body() != null;
                if (response.isSuccessful() && (Commons.isNotNull(response.body()) || response.body().elements.size() > 0)) {
                    OverpassQueryResult result = response.body() == null ? null : response.body();

                    // performing single search, if multi items returned, get one closest the tap point
                    if (singleSearch) {
                        double closestItemLat = 100;
                        double closestItemLng = 100;

                        assert result != null;
                        for (OverpassQueryResult.Element node : result.elements) {
                            double currentLat = Math.abs(mTouchPoint.getLatitude() - node.lat);
                            double currentLng = Math.abs(mTouchPoint.getLongitude() - node.lon);

                            if (currentLat < closestItemLat || currentLng < closestItemLng) {
                                closestItemLat = currentLat;
                                closestItemLng = currentLng;
                                relevantNode = node;
                            }
                        }

                        if (Commons.isNotNull(relevantNode)) {
                            result.elements.clear();
                            result.elements.add(relevantNode);
                        }
                    }

                    assert result != null;
                    for (OverpassQueryResult.Element element : result.elements) {

                        if (Commons.isNotNull(myLocationOverlay.getLocation())) {
                            // Calculate the distance to the POI
                            element.distance = MapUtils.getDistance(whereAmI(), element.lat, element.lon);
                        } else
                            element.distance = 0;

                        // Check for wiki image, format URL if found
                        if (!Commons.isEmpty(element.tags.image)) {
                            // TODO: 13/10/2017 if has image and is wiki link, extract the wiki link from the image url. If no wiki provided, add the new link
//                            if(Commons.isEmpty(element.tags.wikipedia) && element.tags.image.contains("/wiki/"))
//                                element.tags.wikipedia = element.tags.image;
                            element.tags.image = WikiUtils.buildWikiCommonsLink(element.tags.image, C.SCREEN_WIDTH);
                        }
                    }

                    // Sort the elements by distance (closest first)
                    Collections.sort(result.elements, new OverpassQueryResult.Element.DistanceComparator());

                    // Different overlays for single tap and search functions
                    if (singleSearch) {
                        mMarkersOverlay.updateWithResults(result.elements, "");
                    } else {
                        if (result.elements.size() > 0) {
                            mSearchOverlay.updateWithResults(result.elements, "");
                            closeQuickSearch();
                        } else {
                            showQuickSearch();
                            view.showUserMsg(R.string.search_no_results_found);
                        }
                    }
                }

                view.onLoadFinish();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
                if (!call.isCanceled()) {
                    showQuickSearch();
                    view.showSearching(false);
//                } else {
//                    view.onLoadFinish();

                    if (!singleSearch)
                        view.setListMode(false);
                }
            }
        });
    }

    @Override
    public void onShowWikiOnMap(BookmarkEntry entry) {
        WikiQueryPage page = new WikiQueryPage();

        page.setPageId((int) entry.getPageId());
        page.setCoordinates(new WikiQueryPage.Coordinates(entry.getLat(), entry.getLon()));
        page.setTitle(entry.getTitle());
        page.setFullurl(entry.getLinkUrl());
        page.setExtract(entry.getExtract());
        page.setThumbUrl(entry.getThumbnail());
        page.setDescription(entry.getDescription());
        page.userComment(entry.getUserComment());


        WikiQueryPage.Coordinates p = page.coordinates().get(0);
        page.dDist = MapUtils.getDistance(whereAmI(), p.lat(), p.lon());
        page.setDistance(MapUtils.getFormattedDistance(page.dDist));

        Marker poiMarker = new Marker(mMap);
        poiMarker.setTitle(page.title());
        poiMarker.setPosition(new GeoPoint(page.coordinates().get(0).lat(), page.coordinates().get(0).lon()));
        poiMarker.setIcon(ImageUtils.drawableFromVectorDrawable(mContext, R.drawable.ic_wiki_map_marker));
        poiMarker.setRelatedObject(page);
        poiMarker.setOnMarkerClickListener(MapPresenter.this);

        mMarkersOverlay.add(poiMarker);
        mMarkersOverlay.invalidate();

        onMarkerClick(poiMarker, mMap);
        closeQuickSearch();
    }

    @Override
    public void onWikiLookup() {

        view.onLoadStart();
        String searchLocation = String.format(Locale.ROOT, "%f|%f",
                mMap.getMapCenter().getLatitude(), mMap.getMapCenter().getLongitude());

        WikiApi wikiService = WikiServiceProvider.get();
        wikiCall = wikiService.requestNearBy(searchLocation, DEFAULT_WIKI_SEARCH_RADIUS, DEFAULT_WIKI_THUMBNAIL_SIZE);
        wikiCall.enqueue(new Callback<WikiQueryResponse>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<WikiQueryResponse> call, @NonNull Response<WikiQueryResponse> response) {

                try {
                    List<WikiQueryPage> pages = response.body().query().pages();

                    // Get the distances
                    for (WikiQueryPage page : pages) {
                        WikiQueryPage.Coordinates p = page.coordinates().get(0);
                        page.dDist = MapUtils.getDistance(whereAmI(), p.lat(), p.lon());
                        page.setDistance(MapUtils.getFormattedDistance(page.dDist));
                    }

                    // sort on the distance (just calculated)
                    Collections.sort(pages, new WikiQueryPage.DistanceComparator());

                    // build a marker
                    for (WikiQueryPage page : pages) {
                        Marker poiMarker = new Marker(mMap);
                        poiMarker.setTitle(page.title());
                        poiMarker.setPosition(new GeoPoint(page.coordinates().get(0).lat(), page.coordinates().get(0).lon()));
                        poiMarker.setIcon(ImageUtils.drawableFromVectorDrawable(mContext, R.drawable.ic_wiki_map_marker));
                        poiMarker.setRelatedObject(page);
                        poiMarker.setOnMarkerClickListener(MapPresenter.this);
                        mSearchOverlay.add(poiMarker);
                    }

                    // calculate and zoom to bounding box for all items
                    GeoPoint min = new GeoPoint(pages.get(0).getLat(), pages.get(0).getLon());
                    GeoPoint max = null;
                    if(pages.size() > 1)
                        max = new GeoPoint(pages.get(pages.size() - 1).getLat(), pages.get(pages.size() - 1).getLon());
                    MapZoom.zoomTo(mMap, min, Commons.isNotNull(max) ? max : whereAmI());

                    // udpate the UI
                    view.setListMode(true);
                    view.onLoadFinish();

                    // redraw
                    mSearchOverlay.invalidate();
                    closeQuickSearch();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Wiki ERROR:: " + e.getMessage() , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                if (!call.isCanceled()) {
                    view.onLoadFailed();
                    view.showSearching(false);
                    view.setListMode(false);
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker, MapView mapView) {
        if (marker.getTitle().equals("SEARCH_INDICATOR")) {
            // bit of a hack - using the callback thats already available
            ((MapActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setListMode(true);
                }
            });
        } else if (marker.getRelatedObject() instanceof OverpassQueryResult.Element) {
            showPoiDialog(marker.getRelatedObject());

            return true;
        } else if (marker.getRelatedObject() instanceof WikiQueryPage) {
            showPoiDialog(marker.getRelatedObject());
            return true;
        }
        return false;
    }

    @Override
    public void showPoiDialog(final Object obj) {

        if (obj instanceof OverpassQueryResult.Element) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SinglePoiOptionsDialog.showInstance(mContext.getApplicationContext(), (OverpassQueryResult.Element) obj);
                }
            }, ZOOM_IN_TIME_DELAY);
            mMap.getController().animateTo(((OverpassQueryResult.Element) obj).getGeoPoint());
        } else if (obj instanceof WikiQueryPage) {
            GeoPoint p = new GeoPoint(((WikiQueryPage) obj).getLat(), ((WikiQueryPage) obj).getLon());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WikiPoiSheetDialog.showInstance(mContext.getApplicationContext(), (WikiQueryPage) obj);
                }
            }, ZOOM_IN_TIME_DELAY);
            mMap.getController().animateTo(p);
        }
    }

    /**
     * Override the map touch listener
     */
    private MapView.OnTouchListener onMapTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "MotionEvent.ACTION_DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mTrackingMode) {
                        view.setTrackingMode(false);
                        mTrackingMode = false;
                        bAllowReturnLocation = false;
                        browsingLocation = null;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "MotionEvent.ACTION_UP");
                    break;

                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "MotionEvent.ACTION_CANCEL");
                    break;
            }
            return false;
        }
    };


    @Override
    public void zoomIn() {
        mMap.getController().zoomIn();
    }

    @Override
    public void zoomOut() {
        mMap.getController().zoomOut();
    }

    /**
     * Show list of active POI
     */
    @Override
    public void onShowCameraOrPoiMarkerListDialog() {
        if (mSearchOverlay.getSize() > 0) {
            MarkerListDialogFragment markerList = getMarkerListDialogFragment();
            if (Commons.isNotNull(markerList)) {
                markerList.show();
            } else {

                ArrayList<Object> objects = new ArrayList<>();

                for (Marker marker : mSearchOverlay.getItems()) {
                    objects.add(marker.getRelatedObject());
                }
                MarkerListDialogFragment.showInstance(mContext.getApplicationContext(), whereAmI(), objects);
//                MarkerListDialogFragment.showInstance(mContext.getApplicationContext(), whereAmI(), mSearchOverlay.getItems());
            }
        } else {
            view.onCameraLaunch();
        }
    }

    @Override
    public void showOrClearSearchDialog() {

        mMarkersOverlay.clear();

        if (mSearchOverlay.getSize() > 0) {
            mSearchOverlay.clear();
            view.setListMode(false);
        } else {
            showQuickSearch(true);
        }
    }

    private void showQuickSearch(boolean show) {
        SearchDialogFragment fragment = getSearchFragment();
        if (Commons.isNotNull(fragment)) {
            fragment.dismiss();
            mMap.refreshDrawableState();
        }
        SearchDialogFragment.showInstance(mContext.getApplicationContext(), show, (GeoPoint) mMap.getMapCenter(), whereAmI());
    }

    private void showQuickSearch() {
        SearchDialogFragment fragment = getSearchFragment();
        if (fragment != null) {
            fragment.show();
        }
    }

    private void closeQuickSearch() {
        SearchDialogFragment fragment = getSearchFragment();
        if (fragment != null) {
            fragment.closeSearch();
            mMap.refreshDrawableState();
        }
    }

    private SearchDialogFragment getSearchFragment() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(SearchDialogFragment.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (SearchDialogFragment) fragment : null;
    }

    private SinglePoiOptionsDialog getPoiDialog() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(SinglePoiOptionsDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (SinglePoiOptionsDialog) fragment : null;
    }

    private void hidePoiDialog() {
        SinglePoiOptionsDialog fragment = getPoiDialog();
        if (fragment != null) {
            fragment.hide();
        }
    }

    private void closePoiDialog() {
        SinglePoiOptionsDialog fragment = getPoiDialog();
        if (fragment != null) {
            fragment.close();
            mMap.refreshDrawableState();
        }
    }

    private MarkerListDialogFragment getMarkerListDialogFragment() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(MarkerListDialogFragment.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (MarkerListDialogFragment) fragment : null;
    }


    public BoundingBox getBoundingBox() {
        return mMap.getProjection().getBoundingBox();
    }

    @Override
    public void zoomToMyLocation() {
        mTrackingMode = !mTrackingMode;

        if (browsingLocation == null)
            browsingLocation = new BrowsingLocation((GeoPoint) mMap.getMapCenter(), mMap.getZoomLevel());

        updateUIWithTrackingMode();
    }


    private void updateUIWithTrackingMode() {
        if (mTrackingMode) {
            view.setTrackingMode(true);

            bAllowReturnLocation = true;

            GeoPoint myLocation = myLocationOverlay.getLocation();

            if (myLocationOverlay.isEnabled() && myLocation != null) {
                int currentZoomLvl = mMap.getZoomLevel();

                if (currentZoomLvl < MIN_POI_LOOKUP_ZOOM_LVL) {

                    // Dont like this little hack - does make the animateTo more accurate though
                    if(currentZoomLvl < 9 )
                        mMap.getController().setZoom(9);

                    mMap.getController().animateTo(myLocation);
                    myLocationOverlay.setEnabled(false);

                    animateToHandler.postDelayed(animateTo, ZOOM_IN_TIME_DELAY);
                } else {
                    // already at a decent zoom level - just animate
                    mMap.getController().animateTo(myLocation);
                }
            }
        } else {
            view.setTrackingMode(false);

            if (bAllowReturnLocation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.getController().zoomTo(browsingLocation.getZoomLvl());
                        browsingLocation = null;
                    }
                }, ZOOM_IN_TIME_DELAY);
                mMap.getController().animateTo(browsingLocation.getBrowsingLocation());
            }
        }
    }

    private Handler animateToHandler = new Handler();


    /**
     * Let map finish animating before processing the zoom controls
     *   - zooming too early will cancel the panning animation
     */
    private Runnable animateTo = new Runnable() {
        public void run() {
            if(!mMap.isAnimating()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myLocationOverlay.setEnabled(true);
                    }
                }, ZOOM_IN_TIME_DELAY);

                mMap.getController().zoomTo(MIN_POI_LOOKUP_ZOOM_LVL);

            } else
                animateToHandler.postDelayed(this, 200);
        }
    };
}
