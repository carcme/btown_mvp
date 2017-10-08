package me.carc.btownmvp.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import me.carc.btownmvp.App;
import me.carc.btownmvp.BuildConfig;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.ImageUtils;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.Utils.WikiUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.CompassSensor;
import me.carc.btownmvp.common.FusedLocation;
import me.carc.btownmvp.common.TinyDB;
import me.carc.btownmvp.data.model.OverpassDataTags;
import me.carc.btownmvp.data.model.OverpassQueryResult;
import me.carc.btownmvp.data.model.PlaceToOverpass;
import me.carc.btownmvp.data.model.RouteResult;
import me.carc.btownmvp.data.overpass.OverpassApi;
import me.carc.btownmvp.data.overpass.OverpassServiceProvider;
import me.carc.btownmvp.data.overpass.query.QueryGenerator;
import me.carc.btownmvp.data.route.RouteApi;
import me.carc.btownmvp.data.route.RouteServiceProvider;
import me.carc.btownmvp.data.wiki.WikiApi;
import me.carc.btownmvp.data.wiki.WikiQueryPage;
import me.carc.btownmvp.data.wiki.WikiQueryResponse;
import me.carc.btownmvp.data.wiki.WikiServiceProvider;
import me.carc.btownmvp.db.favorite.FavoriteEntry;
import me.carc.btownmvp.db.history.HistoryEntry;
import me.carc.btownmvp.map.markers.MarkersOverlay;
import me.carc.btownmvp.map.markers.MyInfoWindow;
import me.carc.btownmvp.map.overlays.MyDirectedLocationOverlay;
import me.carc.btownmvp.map.overlays.route.RouteOverlay;
import me.carc.btownmvp.map.search.SearchDialogFragment;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.sheets.MarkerListDialogFragment;
import me.carc.btownmvp.map.sheets.RouteDialog;
import me.carc.btownmvp.map.sheets.SinglePoiOptionsDialog;
import me.carc.btownmvp.map.sheets.WikiPoiSheetDialog;
import me.carc.btownmvp.map.sheets.model.RouteInfo;
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
    private static final int ZOOM_IN_TIME_DELAY = 250;

    private static final String LOCATION = "LOCATION";

    private static final String ROUTE_DISCLAIMER_ACCEPTED = "ROUTE_DISCLAIMER_ACCEPTED";
    public static final String OVERLAY_SINGLE_TAP = "OVERLAY_SINGLE_TAP";
    public static final String OVERLAY_SEARCH = "OVERLAY_SEARCH";
    public static final String OVERLAY_ROUTE = "OVERLAY_ROUTE";

    private static final int DEFAULT_WIKI_SEARCH_RADIUS = 10000;
    private static final int DEFAULT_WIKI_THUMBNAIL_SIZE = 320;

    public final static float BERLIN_LAT = 52.517f;
    public final static float BERLIN_LNG = 13.350f;


    protected MyDirectedLocationOverlay myLocationOverlay;
    private MarkersOverlay mMarkersOverlay;
    private MarkersOverlay mSearchOverlay;
    private RouteOverlay mRouteOverlay;

    private Context mContext;
    private MapView mMap;
    private final IMap.View view;
    private FusedLocation fusedLocation;
    private CompassSensor compassSensor;
    private Location mLocation;
    private GeomagneticField geoField;

    private GeoPoint mTouchPoint;

    private Call<OverpassQueryResult> overpassCall;
    private Call<RouteResult> routeCall;
    private Call<WikiQueryResponse> wikiCall;

    private TinyDB db;

    private BrowsingLocation browsingLocation;
    private boolean mTrackingMode;
    private boolean bAllowReturnLocation;


    public MapPresenter(Context context, IMap.View view, MapView map) {
        this.mContext = context;
        this.view = view;
        this.mMap = map;
        view.setPresenter(this);

        db = TinyDB.getTinyDB();

        compassSensor = new CompassSensor(mContext, onCompassCallback);

        fusedLocation = new FusedLocation(context, onLocationChanged);
        if (!fusedLocation.canGetLocation()) {
            view.enableLocationDependantFab(false);
            view.requestGpsEnable();
        } else {
            compassSensor.enableSensors();
        }
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

            if(!compassSensor.isEnabled()) {
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
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
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
        GeoPoint point = new GeoPoint(db.getDouble(C.LAST_CENTER_LAT, 0), db.getDouble(C.LAST_CENTER_LNG, 0));
        mMap.getController().animateTo(point);
        mMap.getController().setZoom(TinyDB.getTinyDB().getInt(C.LAST_ZOOM_LEVEL, 2));
    }

    @Override
    public void stop() {
        compassSensor.disableSensors();
        fusedLocation.stopLocationUpdates();

        if (Commons.isNotNull(overpassCall)) overpassCall.cancel();
        if (Commons.isNotNull(routeCall)) routeCall.cancel();
        if (Commons.isNotNull(wikiCall)) wikiCall.cancel();

        if (Commons.isNotNull(myLocationOverlay)) {

            TinyDB db = TinyDB.getTinyDB();

            GeoPoint location = myLocationOverlay.getLocation();

            if (Commons.isNotNull(location)) {

                db.putInt(C.MAP_ZOOM_LEVEL, mMap.getZoomLevel());
                db.putDouble(C.MAP_CENTER_LAT, location.getLatitude());
                db.putDouble(C.MAP_CENTER_LNG, location.getLongitude());
            }

            if (Commons.isNotNull(mMap)) {
                db.putInt(C.LAST_ZOOM_LEVEL, mMap.getZoomLevel());
                db.putDouble(C.LAST_CENTER_LAT, mMap.getMapCenter().getLatitude());
                db.putDouble(C.LAST_CENTER_LNG, mMap.getMapCenter().getLongitude());
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
        mMap.setTilesScaledToDpi(true);

        // Needed to get map events
        MapEventsOverlay overlay = new MapEventsOverlay(this);
        mMap.getOverlays().add(overlay);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        addOverlays(savedInstanceState);

        mMap.setOnTouchListener(onMapTouchListener);

        TinyDB db = TinyDB.getTinyDB();

        mMap.setMinZoomLevel(2);
        mMap.getController().setZoom(db.getInt(C.LAST_ZOOM_LEVEL, 2));
        GeoPoint savedCenter = new GeoPoint(db.getDouble(C.LAST_CENTER_LAT, 0.0), db.getDouble(C.LAST_CENTER_LNG, 0.0));
        mMap.getController().animateTo(savedCenter);
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
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        mMarkersOverlay.setIcon(clusterIcon);
        mMarkersOverlay.mAnchorV = Marker.ANCHOR_BOTTOM;
        mMarkersOverlay.mTextAnchorU = 0.70f;
        mMarkersOverlay.mTextAnchorV = 0.27f;
        mMarkersOverlay.getTextPaint().setTextSize(12 * mContext.getResources().getDisplayMetrics().density);

        // Search Icon Overlay
        mSearchOverlay = new MarkersOverlay(mContext, mMap, OVERLAY_SEARCH);
        clusterIconD = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.marker_cluster, null);
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
        // Clear existing icons and return
        if (mMarkersOverlay.clear()) return true;

        view.onLoadStart();

        // if search is running, allow cancel option
        if (Commons.isNotNull(overpassCall)) overpassCall.cancel();

        // Build query and run search - // TODO: 30/09/2017 configure the default search parameters
        String query = new QueryGenerator().generator(OverpassDataTags.SinglePoi(), p).build();
        findPOI(query, true);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        mTouchPoint = p;
        String query = new QueryGenerator().generator(OverpassDataTags.FoodDrinkData(), mMap.getBoundingBox()).build();
        findPOI(query, false);
        return false;
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

    /**
     * Show items
     * @param dbType  which database to query
     * @param poi null = display all database entries,  poi = display single item
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
                            HistoryEntry entry = App.get().getDB().historyDao().findByOsmId(poi.getOsmId());
                            if (Commons.isNotNull(entry))
                                element = entry.getOsmPojo();
                        } else {  // get all from favorites database
                            List<HistoryEntry> entries = App.get().getDB().historyDao().getAllHistories();
                            if (Commons.isNotNull(entries))
                                for (HistoryEntry entry : entries)
                                    elements.add(entry.getOsmPojo());
                        }
                        break;

                    case SearchDialogFragment.SEARCH_ITEM_FAVORITE:
                        if (Commons.isNotNull(poi)) {  // get single item from database
                            FavoriteEntry entry = App.get().getDB().favoriteDao().findByOsmId(poi.getOsmId());
                            if (Commons.isNotNull(entry))
                                element = entry.getOsmPojo();
                        } else {  // get all from favorites database
                            List<FavoriteEntry> entries = App.get().getDB().favoriteDao().getAllFavorites();
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
                            element.distance = MapUtils.getDistance(myLocationOverlay.getLocation(), element.lat, element.lon);
                        } else
                            element.distance = 0;

                        // Check for wiki image, format URL if found
                        if (!Commons.isEmpty(element.tags.image)) {
                            element.tags.image = WikiUtils.buildWikiCommonsLink(element.tags.image, 0);
                        }
                    }

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
            public void onFailure(Call call, Throwable t) {
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

    private void checkRoadDisclaimer(final RouteInfo routeInfo) {
        if (!db.getBoolean(ROUTE_DISCLAIMER_ACCEPTED)) {
            new AlertDialog.Builder(mContext)
                    .setMessage(R.string.routing_disclaimer)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.putBoolean(ROUTE_DISCLAIMER_ACCEPTED, true);
                            dialog.dismiss();
                            routeToPoi(true, routeInfo);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


    @Override
    public void routeToPoi(boolean newRoute, final RouteInfo routeInfo) {

        // Show route disclaimer
        if (!db.getBoolean(ROUTE_DISCLAIMER_ACCEPTED)) {
            checkRoadDisclaimer(routeInfo);
            return;
        }

        // clear old route
        if (newRoute) {
            // Close and clean up the route info
            if (Commons.isNotNull(mRouteOverlay)) {
                OverlayManager mngr = mRouteOverlay.clearRoute();
                mngr.remove(mRouteOverlay);
                mRouteOverlay = null;
                closeRouteDialog();
                mMap.invalidate();
            }
        }
        // check we have the information needed
        if (Commons.isNull(routeInfo)) {
            return;
        }

        // set the destination // TODO: 07/10/2017 check the location is on
        if (Commons.isNull(routeInfo.getFrom())) {
            routeInfo.setFrom(myLocationOverlay.getLocation());
            routeInfo.setAddressFrom(mContext.getString(R.string.shared_my_location));
        }

        String routeFrom = String.format(Locale.ROOT, "%f,%f", routeInfo.getFrom().getLatitude(), routeInfo.getFrom().getLongitude());
        String routeTo = String.format(Locale.ROOT, "%f,%f", routeInfo.getTo().getLatitude(), routeInfo.getTo().getLongitude());

        RouteApi routeService = RouteServiceProvider.get();
        routeCall = routeService.route(BuildConfig.GRAPHHOPPER_API_KEY,
                routeFrom, routeTo, Locale.getDefault().getLanguage(), mContext.getString(routeInfo.getVehicle()));
        routeCall.enqueue(new Callback<RouteResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<RouteResult> call, @NonNull final Response<RouteResult> response) {
                hidePoiDialog();
                closeRouteDialog();


                view.showRouteBottomSheet(routeInfo, response.body());

//                RouteDialog.showInstance((MapActivity) mContext, routeInfo, response.body());


                mRouteOverlay = new RouteOverlay(mContext, mMap, routeInfo, response.body());


            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
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

                    for (WikiQueryPage page : pages) {
                        WikiQueryPage.Coordinates p = page.coordinates().get(0);
                        double d = MapUtils.getDistance((GeoPoint) mMap.getMapCenter(), p.lat(), p.lon());
                        page.setDistance(MapUtils.getFormattedDistance(d));

                        Marker poiMarker = new Marker(mMap);
                        poiMarker.setTitle(page.title());
                        poiMarker.setPosition(new GeoPoint(page.coordinates().get(0).lat(), page.coordinates().get(0).lon()));
                        poiMarker.setIcon(ImageUtils.drawableFromVectorDrawable(mContext, R.drawable.ic_wiki_map_marker));
                        poiMarker.setRelatedObject(page);
                        poiMarker.setOnMarkerClickListener(MapPresenter.this);

                        mSearchOverlay.add(poiMarker);
                    }

                    view.setListMode(true);
                    view.onLoadFinish();

                    mSearchOverlay.invalidate();

                    closeQuickSearch();
                } catch (NullPointerException e) {
                    e.printStackTrace();
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
    public boolean onMarkerClick(Marker marker, MapView mapView) {
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
            mMap.getController().animateTo(((OverpassQueryResult.Element) obj).getGeoPoint());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SinglePoiOptionsDialog.showInstance((MapActivity) mContext, (OverpassQueryResult.Element) obj);
                }
            }, ZOOM_IN_TIME_DELAY);
        } else if (obj instanceof WikiQueryPage) {
            GeoPoint p = new GeoPoint(((WikiQueryPage) obj).getLat(), ((WikiQueryPage) obj).getLon());
            mMap.getController().animateTo(p);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WikiPoiSheetDialog.showInstance((MapActivity) mContext, (WikiQueryPage) obj);
                }
            }, ZOOM_IN_TIME_DELAY);
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
            } else
                MarkerListDialogFragment.showInstance((MapActivity) mContext, whereAmI(), mSearchOverlay.getItems());
        } else
            Toast.makeText(mContext, "Show Camera", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showOrClearSearchDialog() {

        mMarkersOverlay.clear();

        if (mSearchOverlay.getSize() > 0) {
            MyInfoWindow.closeAllInfoWindows(mMap);
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
        SearchDialogFragment.showInstance((MapActivity) mContext, show, (GeoPoint) mMap.getMapCenter(), whereAmI());
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

    private RouteDialog getRouteDialog() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(RouteDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (RouteDialog) fragment : null;
    }

    private void closeRouteDialog() {
        RouteDialog fragment = getRouteDialog();
        if (fragment != null) {
            fragment.closeRoute();
            mMap.refreshDrawableState();
        }
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

                if (mMap.getZoomLevel() < MIN_POI_LOOKUP_ZOOM_LVL) {
                    // hide location arrow
                    myLocationOverlay.setEnabled(false);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.getController().zoomTo(MIN_POI_LOOKUP_ZOOM_LVL);
                        }
                    }, ZOOM_IN_TIME_DELAY);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // show location arrow
                            myLocationOverlay.setEnabled(true);
                        }
                    }, ZOOM_IN_TIME_DELAY * 2);

                }
                mMap.getController().animateTo(myLocation);
            }
        } else {
            view.setTrackingMode(false);

            if (bAllowReturnLocation) {
                mMap.getController().animateTo(browsingLocation.getBrowsingLocation());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.getController().zoomTo(browsingLocation.getZoomLvl());
                        browsingLocation = null;
                    }
                }, ZOOM_IN_TIME_DELAY);
            }
        }
    }

}
