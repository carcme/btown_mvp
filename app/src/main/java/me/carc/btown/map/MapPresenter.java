package me.carc.btown.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationResult;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.ImageUtils;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.MapZoom;
import me.carc.btown.Utils.WikiUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.CompassSensor;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.location.BTownFusedLocation;
import me.carc.btown.common.location.BTownLocationCallback;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.ExploreItem;
import me.carc.btown.data.all4squ.entities.GroupExplore;
import me.carc.btown.data.all4squ.entities.ItemsListItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.data.overpass.OverpassServiceProvider;
import me.carc.btown.data.overpass.query.QueryGenerator;
import me.carc.btown.data.results.OverpassDataTags;
import me.carc.btown.data.results.OverpassQueryResult;
import me.carc.btown.data.results.PlaceToOverpass;
import me.carc.btown.data.results.ReverseResult;
import me.carc.btown.data.results.VenueToOverpass;
import me.carc.btown.data.reverse.ReverseApi;
import me.carc.btown.data.reverse.ReverseServiceProvider;
import me.carc.btown.data.wiki.WikiApi;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.data.wiki.WikiQueryResponse;
import me.carc.btown.data.wiki.WikiServiceProvider;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.db.history.HistoryEntry;
import me.carc.btown.map.markers.MarkersOverlay;
import me.carc.btown.map.markers.RadiusMarkerClusterer;
import me.carc.btown.map.overlays.MyDirectedLocationOverlay;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.map.sheets.SinglePoiOptionsDialog;
import me.carc.btown.map.sheets.TourSheetDialog;
import me.carc.btown.map.sheets.WikiPoiSheetDialog;
import me.carc.btown.map.sheets.marker_list.MarkerListDialogFragment;
import me.carc.btown.tours.model.Attraction;
import me.carc.btown.tours.model.TourCatalogue;
import me.carc.btown.ui.custom.MySimplePointOverlayOptions;
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
    private static final String OVERLAY_SEARCH  = "OVERLAY_SEARCH";
    private static final String OVERLAY_FSQ     = "OVERLAY_FSQ";
    private static final String OVERLAY_PIN     = "OVERLAY_PIN";
    private static final String OVERLAY_TOUR    = "OVERLAY_TOUR";


    private static final int DEFAULT_WIKI_SEARCH_RADIUS = 10000;
    private static final int DEFAULT_WIKI_THUMBNAIL_SIZE = C.SCREEN_WIDTH;

    private static final int MAX_VALID_ACCRACY = 100;

    private final static float BERLIN_LAT = 52.517f;
    private final static float BERLIN_LNG = 13.350f;

    private MyDirectedLocationOverlay myLocationOverlay;
    private MarkersOverlay mMarkersOverlay;
    private MarkersOverlay mSearchOverlay;
    private MarkersOverlay mFsqOverlay;
    private MarkersOverlay mPinOverlay;
    private SimpleFastPointOverlay mTourOverlay;
    private Polyline tourLine;

    private TourCatalogue mCatalogue;

    private Context mContext;
    private MapView mMap;
    private final IMap.View view;
    private BTownFusedLocation btLocation;
    private CompassSensor compassSensor;

    private GeoPoint mLocation;
    private GeoPoint mTouchPoint;

    private Call<OverpassQueryResult> overpassCall;
    private Call<WikiQueryResponse> wikiCall;
    private Call<FourSquResult> listsCall;

    private BrowsingLocation browsingLocation;
    private boolean mTrackingMode;
    private boolean bAllowReturnLocation;
    private boolean showNoResultsMsg;

    public MapPresenter(Context context, IMap.View view, MapView map) {
        this.mContext = context;
        this.view = view;
        this.mMap = map;
        view.setPresenter(this);
    }

    @Override
    public void onUpdateLocation() {
        if(Commons.isNull(btLocation)) {
            btLocation = new BTownFusedLocation(mContext);
            btLocation.setCallback(locationCallbackListener);
        } else btLocation.setCallback(locationCallbackListener);

        if(Commons.isNotNull(compassSensor))
            compassSensor = new CompassSensor(mContext, onCompassCallback);
        compassSensor.enableSensors();
    }

    private BTownLocationCallback locationCallbackListener = new BTownLocationCallback() {
/*
        @Override
        public void onLocationChanged(Location location) {
            updateLocationOverlay(location);            // update the location overlay
            view.enableLocationDependantFab(true);      // hide UI dependant controls

            if (mTrackingMode)
                mMap.getController().animateTo(mLocation); //keep the map view centered on current location:
            else
                mMap.invalidate(); // re-draw
        }
*/

        @Override
        public void onLocationResult(LocationResult result) {
            updateLocationOverlay(result.getLocations().get(0));    // update the location overlay
            view.enableLocationDependantFab(true);                  // hide UI dependant controls
            ((App)mContext.getApplicationContext()).setLatestLocation(result.getLocations().get(0));

            if (mTrackingMode)
                mMap.getController().animateTo(mLocation); //keep the map view centered on current location:
            else
                mMap.invalidate(); // re-draw
        }

        @Override
        public void onLocationAvailability(LocationAvailability availability) {
            if (!availability.isLocationAvailable()) {
                view.enableLocationDependantFab(false);
            }
        }

        @Override
        public void onLastKnownLocation(Location location) {
            updateLocationOverlay(location);    // update the location overlay
        }

/*
        public void onConnected(boolean connected) {
            if (connected) {
                btLocation.startLocationUpdates();
                btLocation.redownloadAGPS();
            } else {
                compassSensor.disableSensors();
                view.enableLocationDependantFab(false);
            }
        }
*/
    };

    private void updateLocationOverlay(Location location) {
        mLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        if (!compassSensor.isEnabled())
            compassSensor.enableSensors();

        if (!myLocationOverlay.isEnabled())
            myLocationOverlay.setEnabled(true);

        myLocationOverlay.setLocation(mLocation);

        if (location.hasAccuracy() && location.getAccuracy() < MAX_VALID_ACCRACY) {
            myLocationOverlay.setShowAccuracy(true);
            myLocationOverlay.setAccuracy((int) location.getAccuracy());
        }
    }

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
                btLocation.calcGeoMagneticCorrection(deg);

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
        mMap.setMinZoomLevel(2.0);
        mMap.getController().setZoom(db.getInt(LAST_ZOOM_LEVEL, 9));
        GeoPoint savedCenter = new GeoPoint(db.getDouble(LAST_CENTER_LAT, BERLIN_LAT), db.getDouble(LAST_CENTER_LNG, BERLIN_LNG));
        mMap.getController().animateTo(savedCenter);
        compassSensor = new CompassSensor(mContext, onCompassCallback);

        initLocationProvider();
    }

    @Override
    public void stop() {
        stopLocationProvider();
        compassSensor.disableSensors();

        if (Commons.isNotNull(overpassCall)) overpassCall.cancel();
        if (Commons.isNotNull(wikiCall)) wikiCall.cancel();

        if (Commons.isNotNull(myLocationOverlay)) {
            TinyDB db = TinyDB.getTinyDB();
            if (Commons.isNotNull(mMap)) {
                db.putInt(LAST_ZOOM_LEVEL, mMap.getZoomLevel());
                db.putDouble(LAST_CENTER_LAT, mMap.getMapCenter().getLatitude());
                db.putDouble(LAST_CENTER_LNG, mMap.getMapCenter().getLongitude());
            }
        }

        closePoiDialog();
        closeTourDialog();
        clearBackStack();
    }


    private void initLocationProvider() {
        if(Commons.isNull(btLocation)) {
            btLocation = new BTownFusedLocation(mContext);
            btLocation.setCallback(locationCallbackListener);
        }

        if (!btLocation.canGetLocation()) {
            view.enableLocationDependantFab(false);
            view.requestGpsEnable();
        } else {
            btLocation.getLastLocation();
        }
    }

    private void stopLocationProvider() {
        btLocation.stopLocationUpdates();
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
        bundle.putParcelableArrayList(OVERLAY_SEARCH, mFsqOverlay.getElemets());
        bundle.putParcelableArrayList(OVERLAY_PIN, mPinOverlay.getElemets());
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
        myLocationOverlay = new MyDirectedLocationOverlay(mContext, CompassSensor.hasCompass(mContext)
                ? R.drawable.ic_navigation : R.drawable.ic_navigation_no_compass);

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
/*
        clusterIconD = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.marker_cluster, null);
        assert clusterIconD != null;
        clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
*/
        mSearchOverlay.setIcon(clusterIcon);
        mSearchOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mSearchOverlay.mTextAnchorU = 0.70f;
        mSearchOverlay.mTextAnchorV = 0.27f;
        mSearchOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);


        mFsqOverlay = new MarkersOverlay(mContext, mMap, OVERLAY_FSQ);
        mFsqOverlay.setIcon(clusterIcon);
        mFsqOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mFsqOverlay.mTextAnchorU = 0.70f;
        mFsqOverlay.mTextAnchorV = 0.27f;
        mFsqOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);


        mPinOverlay = new MarkersOverlay(mContext, mMap, OVERLAY_PIN);
        mPinOverlay.setIcon(clusterIcon);
        mPinOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mPinOverlay.mTextAnchorU = 0.70f;
        mPinOverlay.mTextAnchorV = 0.27f;
        mPinOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);


        // Add the overlays to the map
        mMap.getOverlays().add(myLocationOverlay);
        mMap.getOverlays().add(mMarkersOverlay);
        mMap.getOverlays().add(mSearchOverlay);
        mMap.getOverlays().add(mFsqOverlay);
        mMap.getOverlays().add(mPinOverlay);


        mMarkersOverlay.setOnMarkerClickListener(MapPresenter.this);
        mSearchOverlay.setOnMarkerClickListener(MapPresenter.this);
        mFsqOverlay.setOnMarkerClickListener(MapPresenter.this);
        mPinOverlay.setOnMarkerClickListener(MapPresenter.this);


        if (Commons.isNotNull(inState)) {
            // single tap POI
            ArrayList<OverpassQueryResult.Element> elements = inState.getParcelableArrayList(OVERLAY_SINGLE_TAP);
            mMarkersOverlay.updateWithResults(elements, OVERLAY_SINGLE_TAP);

            // search POI's
            elements = inState.getParcelableArrayList(OVERLAY_SEARCH);
            if (Commons.isNotNull(elements) && elements.size() > 0) {
                mSearchOverlay.updateWithResults(elements, OVERLAY_SEARCH);
            }

            // search POI's
            elements = inState.getParcelableArrayList(OVERLAY_FSQ);
            if (Commons.isNotNull(elements) && elements.size() > 0) {
                mFsqOverlay.updateWithResults(elements, OVERLAY_FSQ);
            }

            // pins
            elements = inState.getParcelableArrayList(OVERLAY_PIN);
            if (Commons.isNotNull(elements) && elements.size() > 0) {
                mPinOverlay.updateWithResults(elements, OVERLAY_PIN);
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
            whereAmI = mLocation;
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

        // no look up from certain level... can't see the poi icons from this zoom lvl
        if (mMap.getZoomLevel() < MIN_POI_LOOKUP_ZOOM_LVL) {
            view.onLoadFinish();
            return false;
        }

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
        Log.d(TAG, "clearLayers: " + mMarkersOverlay.getSize());
        if (mMarkersOverlay.getSize() == 1)  // dont show msg when already poi on screen
            showNoResultsMsg = false;
        else
            showNoResultsMsg = true;

        mMarkersOverlay.clear();
        if (clearSearchLayer) mSearchOverlay.clear();
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

        OverpassServiceProvider overpassServiceProvider = new OverpassServiceProvider();
        overpassCall = overpassServiceProvider.createAndRunService(query, new OverpassServiceProvider.OverpassCallback() {
            @Override
            public void onSucess(OverpassQueryResult result) {
                overpassSucess(result, singleSearch);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.showSearching(false);
                if (!call.isCanceled()) {
                    showQuickSearch();
                    view.onLoadFailed();
//                } else {
//                    view.onLoadFinish();

                    if (!singleSearch)
                        view.setListMode(false);
                }
            }
        });
    }

    private void overpassSucess(OverpassQueryResult result, boolean singleSearch) {
        if (Commons.isNotNull(result) && result.elements.size() > 0) {

            // performing single search, if multi items returned, get one closest the tap point
            if (singleSearch) {
                double closestItemLat = 100;
                double closestItemLng = 100;

                OverpassQueryResult.Element relevantNode = null;
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

            for (OverpassQueryResult.Element element : result.elements) {

                if (Commons.isNotNull(myLocationOverlay.getLocation())) {
                    // Calculate the distance to the POI
                    element.distance = MapUtils.getDistance(whereAmI(), element.lat, element.lon);
                } else
                    element.distance = 0;

                // Check for wiki image, format URL if found
                if (!Commons.isEmpty(element.tags.image)) {
                    // TODO: 13/10/2017 if has image and is wiki link, extract the wiki link from the image url. If no wiki provided, add the new link
                    if (Commons.isEmpty(element.tags.wikipedia) && element.tags.image.contains("/wiki/"))
                        element.tags.wikipedia = element.tags.image;

                    if (C.DEBUG_ENABLED) {
                        if (element.tags.image.contains("Stolperstein")) {
                            String test = WikiUtils.buildWikiCommonsLink(element.tags.image, C.THUMBNAIL_SIZE);
                            Log.d(TAG, "overpassSucess: " + test);
                        }
                    }

                    element.tags.image = WikiUtils.buildWikiCommonsLink(element.tags.image, C.SCREEN_WIDTH);
                    element.tags.thumbnail = WikiUtils.buildWikiCommonsLink(element.tags.image, C.THUMBNAIL_SIZE);
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
        } else {
            showQuickSearch();
            if (showNoResultsMsg) view.showUserMsg(R.string.search_no_results_found);
        }


        view.onLoadFinish();
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

                    clearLayers(true);

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
                    if (pages.size() > 1)
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
                    Toast.makeText(mContext, "Wiki ERROR:: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.showSearching(false);
                if (!call.isCanceled()) {
                    view.onLoadFailed();
                    view.setListMode(false);
                }
            }
        });
    }


    @Override
    public void onDropPin(GeoPoint p) {
        view.showSearching(true);
        ReverseApi service = ReverseServiceProvider.get();
        Call<ReverseResult> reverseCall = service.reverseDetails(p.getLatitude(), p.getLongitude());
        reverseCall.enqueue(new Callback<ReverseResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<ReverseResult> call, @NonNull Response<ReverseResult> response) {
                try {
                    if (Commons.isNotNull(response.body())) {
                        ReverseResult result = response.body();
                        if(Commons.isNotNull(result.osm_id)) {
                            OverpassQueryResult.Element element = new OverpassQueryResult.Element();

                            element.id = Long.parseLong(result.osm_id);
                            element.lat = result.lat;
                            element.lon = result.lon;
                            element.iconId = R.drawable.ic_pin;
                            element.tags.name = result.namedetails.name != null ? result.namedetails.name : result.name;

                            element.tags.addressHouseNumber = result.address.house_number;
                            element.tags.addressStreet = result.address.road;
                            element.tags.addressSuburb = result.address.city_district;
                            element.tags.addressCity = result.address.city;
                            element.tags.addressPostCode = result.address.postcode;

                            // use reflection to populate the class members
                            Field[] resFields = ReverseResult.ExtraTags.class.getDeclaredFields();
                            Field[] tagFields = OverpassQueryResult.Element.Tags.class.getDeclaredFields();
                            for (Field field : resFields) {
                                for (Field tag : tagFields) {
                                    if (tag.getName().equals(result.tagCategory)) {
                                        try {
                                            tag.set(element.tags, result.tagType);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onResponse: WRONG TYPE:: " + field.getName());
                                        }
                                    } else if (field.getName().equals(tag.getName())) {
                                        try {
                                            tag.set(element.tags, field.get(result.extratags));
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onResponse: WRONG TYPE:: " + field.getName());
                                        }
                                    }
                                }
                            }

                            element.distance = MapUtils.getDistance(whereAmI(), element.lat, element.lon);

                            // put this here to overwrite the reflection assignments and format the image strings
                            if (Commons.isNotNull(result.extratags.image)) {
                                element.tags.thumbnail = WikiUtils.buildWikiCommonsLink(result.extratags.image, C.THUMBNAIL_SIZE);
                                element.tags.image = WikiUtils.buildWikiCommonsLink(result.extratags.image, C.SCREEN_WIDTH);
                            } else {
                                element.tags.thumbnail = WikiUtils.buildWikiCommonsLink(result.extratags.image, C.THUMBNAIL_SIZE);
                            }

                            Marker poiMarker = new Marker(mMap);
                            poiMarker.setTitle("DROP_PIN");
                            poiMarker.setPosition(new GeoPoint(element.lat, element.lon));
                            poiMarker.setIcon(ImageUtils.drawableFromVectorDrawable(mContext, R.drawable.ic_pin));
                            poiMarker.setAnchor(0.5f, 0.7f);
                            poiMarker.setRelatedObject(element);
                            poiMarker.setDraggable(true);
                            poiMarker.setOnMarkerDragListener(mPinDragListener);
                            poiMarker.setOnMarkerClickListener(MapPresenter.this);

                            mPinOverlay.add(poiMarker);
                            mPinOverlay.invalidate();

                            onMarkerClick(poiMarker, mMap);

                            view.addClearDropMenuItem(true);
                        } else
                            view.onLoadFailed();
                    } else {
                        view.showSearching(false);
                        view.onLoadFailed();
                        view.setListMode(false);
                    }

                } catch (NullPointerException e) {
                    Log.d(TAG, "onResponse: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.showSearching(false);
                if (!call.isCanceled()) {
                    view.onLoadFailed();
                    view.setListMode(false);
                }
            }
        });
    }


    @Override
    public void onClearPins() {
        mPinOverlay.clear();
        view.addClearDropMenuItem(false);
    }

    private final OnPinMarkerDragListener mPinDragListener = new OnPinMarkerDragListener();

    class OnPinMarkerDragListener implements Marker.OnMarkerDragListener {

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
        }

        @Override
        public void onMarkerDragStart(Marker marker) {
            List<Overlay> overlays = mMap.getOverlayManager().overlays();

            for(Overlay overlay : overlays) {
                if(overlay instanceof RadiusMarkerClusterer) {
                    RadiusMarkerClusterer obj = (RadiusMarkerClusterer)overlay;
                    if(obj.getName().equals(OVERLAY_PIN)){
                        view.addClearDropMenuItem(obj.getItems().size() > 1);
                        obj.getItems().remove(marker);
                        break;
                    }
                }
            }
            mPinOverlay.invalidate();
        }
    }

    @Override
    public void onFsqSearch() {

        String latLon = mMap.getMapCenter().getLatitude() + "," + mMap.getMapCenter().getLongitude();

        FourSquareApi service = FourSquareServiceProvider.get();
        listsCall = service.searchArea(latLon, "checkin", 50, 500);

        listsCall.enqueue(new Callback<FourSquResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                FourSquResult body = response.body();
                ArrayList<VenueResult> resp = body.getResponse().getSearchResult();

                if (Commons.isNotNull(resp) && resp.size() > 0) {
                    view.showFsqSearchResults(resp);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.onLoadFailed();
            }
        });

    }

    @Override
    public void onFsqExplore() {

        String latLon = mMap.getMapCenter().getLatitude() + "," + mMap.getMapCenter().getLongitude();

        FourSquareApi service = FourSquareServiceProvider.get();
        listsCall = service.explore(latLon, 50, 500);

        if(listsCall.isExecuted()) {
            listsCall.cancel();
            view.showSearching(false);
            return;
        }

        listsCall.enqueue(new Callback<FourSquResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                FourSquResult body = response.body();
                if(body != null && body.getResponse() != null) {
                    ArrayList<GroupExplore> resp = body.getResponse().getExplore();

                    if (Commons.isNotNull(resp) && resp.size() > 0) {
                        ArrayList<ExploreItem> items = resp.get(0).getItems();
                        view.showFsqSExploreResults(body.getResponse().headerFullLocation, items);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.onLoadFailed();
            }
        });
    }

    @Override
    public void showFsqVenue(VenueResult venue) {
        VenueToOverpass converter = new VenueToOverpass();
        mFsqOverlay.updateWithResults(converter.convertFsqToElement(venue, venue.getBestPhoto()), "");
        mMap.getController().animateTo(new GeoPoint(venue.getLocation().getLat(), venue.getLocation().getLng()));
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS") //  Does FindBugs recognise the @Nullable annotation??
    @Override
    public void showFsqVenues(ArrayList<VenueResult> venues) {
        VenueToOverpass converter = new VenueToOverpass();
        ArrayList<OverpassQueryResult.Element> elements = new ArrayList<>();

        for (VenueResult venue : venues) {
            elements.add(converter.convertFsqToElement(venue, null)); //  Does FindBugs recognise the @Nullable annotation??
        }

        if (elements.size() > 0) {
            mFsqOverlay.updateWithResults(elements, "");
            mMap.zoomToBoundingBox(converter.findBoundingBoxForGivenLocations(elements), true);
        }
    }

    @Override
    public void showFsqList(ListItems items) {
        VenueToOverpass converter = new VenueToOverpass();
        ArrayList<OverpassQueryResult.Element> elements = new ArrayList<>();

        for (ItemsListItem item : items.getItemsListItems()) {
            VenueResult venue = item.getVenue();
            elements.add(converter.convertFsqToElement(venue, item.getPhoto()));
        }
        if (elements.size() > 0) {
            mFsqOverlay.updateWithResults(elements, "");
            mMap.zoomToBoundingBox(converter.findBoundingBoxForGivenLocations(elements), true);
        }
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

        } else if (marker.getTitle().equals("DROP_PIN")) {
            showPoiDialog(marker.getRelatedObject());
            return true;

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
//                    if(mMap.getZoomLevel() < MIN_POI_LOOKUP_ZOOM_LVL)
//                    mMap.getController().zoomTo(MIN_POI_LOOKUP_ZOOM_LVL);
                }
            }, ZOOM_IN_TIME_DELAY);
            mMap.getController().animateTo(((OverpassQueryResult.Element) obj).getGeoPoint());
        } else if (obj instanceof WikiQueryPage) {
            GeoPoint p = new GeoPoint(((WikiQueryPage) obj).getLat(), ((WikiQueryPage) obj).getLon());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WikiPoiSheetDialog.showInstance(mContext.getApplicationContext(), (WikiQueryPage) obj);
//                    if(mMap.getZoomLevel() < MIN_POI_LOOKUP_ZOOM_LVL)
//                    mMap.getController().zoomTo(MIN_POI_LOOKUP_ZOOM_LVL);
                }
            }, ZOOM_IN_TIME_DELAY);
            mMap.getController().animateTo(p);
        }
    }

    /**
     * Override the map touch listener
     */
    private MapView.OnTouchListener onMapTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mTrackingMode) {
                        mTrackingMode = false;
//                        BTownFusedLocation loc = ((App) mContext.getApplicationContext()).getBTownLocation();
//                        loc.updateLocationRequest(false);
                        btLocation.updateLocationRequest(false);
                        view.setTrackingMode(false);
                        bAllowReturnLocation = false;
                        browsingLocation = null;
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    break;

                case MotionEvent.ACTION_CANCEL:
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
        if (mSearchOverlay.getSize() > 0 || mFsqOverlay.getSize() > 0) {
            MarkerListDialogFragment markerList = getMarkerListDialogFragment();
            if (Commons.isNotNull(markerList)) {
                markerList.show();
            } else {
                ArrayList<Object> objects = new ArrayList<>();

                for (Marker marker : mSearchOverlay.getItems()) {
                    objects.add(marker.getRelatedObject());
                }
                for (Marker marker : mFsqOverlay.getItems()) {
                    objects.add(marker.getRelatedObject());
                }
                MarkerListDialogFragment.showInstance(mContext.getApplicationContext(), whereAmI(), objects);
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
            if(mFsqOverlay.getSize() == 0)
                view.setListMode(false);

        } else if (mFsqOverlay.getSize() > 0) {
            mFsqOverlay.clear();
            view.setListMode(false);

        } else {
            showQuickSearch(true);
        }
    }

    @Override
    public void clearPoiMarkers() {
        mMarkersOverlay.clear();
        mSearchOverlay.clear();
        mFsqOverlay.clear();
        mPinOverlay.clear();
        mMap.getOverlays().remove(mTourOverlay);
        mMap.getOverlayManager().remove(tourLine);
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

    @Override
    public void hidePoiDialog() {
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
        btLocation.updateLocationRequest(mTrackingMode);
        view.setTrackingMode(mTrackingMode);

        if (mTrackingMode) {
            bAllowReturnLocation = true;

            GeoPoint myLocation = myLocationOverlay.getLocation();

            if (myLocationOverlay.isEnabled() && Commons.isNotNull(myLocation)) {
                double currentZoomLvl = mMap.getZoomLevelDouble();

                if (currentZoomLvl < MIN_POI_LOOKUP_ZOOM_LVL) {

                    // Dont like this little hack - does make the animateTo more accurate though
                    if (currentZoomLvl < 9)
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
            if (bAllowReturnLocation && Commons.isNotNull(browsingLocation)) {
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
     * - zooming too early will cancel the panning animation
     */
    private Runnable animateTo = new Runnable() {
        public void run() {
            if (!mMap.isAnimating()) {
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

    @Override
    public void clearTourIcons() {
        mMap.getOverlays().remove(mTourOverlay);
        mMap.getOverlayManager().remove(tourLine);
        mMap.invalidate();
        view.resetToursBtn();
    }


    @Override
    public void showTour(TourCatalogue catalogue) {

        mCatalogue = catalogue;
        List<GeoPoint> geoPoints = new ArrayList<>();   // tour lines
        List<IGeoPoint> points = new ArrayList<>();     // tour points
        for (Attraction attraction: mCatalogue.getAttractions()) {
            points.add(new GeoPoint(attraction.getLocation().lat, attraction.getLocation().lon));
            geoPoints.add(new GeoPoint(attraction.getLocation().lat, attraction.getLocation().lon));
        }

        // wrap them in a theme
        SimplePointTheme theme = new SimplePointTheme(points, false);

        // Draw the lines between tour stops
        tourLine = new Polyline();
        tourLine.setColor(ContextCompat.getColor(mContext, R.color.roadOverlayBlue));
        tourLine.setPoints(geoPoints);

        // Add the tour stops (should be below the lines)
        MySimplePointOverlayOptions opt = new MySimplePointOverlayOptions()
                .setRadius(24)
                .setSelectedRadius(24)
                .setIsClickable(true)
                .setCellSize(20);

        mTourOverlay = new SimpleFastPointOverlay(theme, opt);
        mTourOverlay.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, final Integer point) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TourSheetDialog.showInstance(mContext.getApplicationContext(), mCatalogue.getAttractions().get(point));

                   }
                }, 200);

                // Show the selected point in top quarter of screen
                Projection projection = mMap.getProjection();
                Point p = new Point();
                projection.toPixels(points.get(point), p);
                int y = projection.getScreenRect().height() / 4;

                IGeoPoint geoPoint = projection.fromPixels(p.x, p.y + y) ;
                mMap.getController().animateTo(geoPoint);

            }
        });

        mTourOverlay.setSelectedPoint(0);
        mMap.getOverlayManager().add(tourLine);
        mMap.getOverlays().add(mTourOverlay);

        mMap.zoomToBoundingBox(mTourOverlay.getBoundingBox(), true);
    }


    private TourSheetDialog getTourDialog() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(TourSheetDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (TourSheetDialog) fragment : null;
    }

    private void closeTourDialog() {
        TourSheetDialog fragment = getTourDialog();
        if (fragment != null) {
            fragment.close();
            mMap.refreshDrawableState();
        }
    }
}
