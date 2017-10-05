package me.carc.btownmvp.map;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

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
import me.carc.btownmvp.map.markers.MarkersOverlay;
import me.carc.btownmvp.map.markers.MyInfoWindow;
import me.carc.btownmvp.map.markers.RadiusMarkerClusterer;
import me.carc.btownmvp.map.overlays.MyDirectedLocationOverlay;
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

    public static final String OVERLAY_SINGLE_TAP = "OVERLAY_SINGLE_TAP";
    public static final String OVERLAY_SEARCH = "OVERLAY_SEARCH";

    private static final int DEFAULT_WIKI_SEARCH_RADIUS = 10000;
    private static final int DEFAULT_WIKI_THUMBNAIL_SIZE = 320;

    public final static float BERLIN_LAT = 52.517f;
    public final static float BERLIN_LNG = 13.350f;

    protected MyDirectedLocationOverlay myLocationOverlay;
    public MarkersOverlay mMarkersOverlay;
    public MarkersOverlay mSearchOverlay;

    private Context mContext;
    private MapView mMap;
    private final IMap.View view;
    private FusedLocation fusedLocation;
    private CompassSensor compassSensor;
    private Location mLocation;
    private GeomagneticField geoField;

    FolderOverlay itineraryMarkers;
    RadiusMarkerClusterer mPoiMarkers;
    protected Marker singleMarker, markerStart, markerDestination, markerSearch;

    private GeoPoint mTouchPoint;

    private Call<OverpassQueryResult> overpassCall;
    private IconManager iconManager;

    private BrowsingLocation browsingLocation;
    private boolean mTrackingMode;
    private boolean bAllowReturnLocation;

    public MapPresenter(Context context, IMap.View view, MapView map) {
        this.mContext = context;
        this.view = view;
        this.mMap = map;
        view.setPresenter(this);

        fusedLocation = new FusedLocation(context, onLocationChanged);

        compassSensor = new CompassSensor(mContext, onCompassCallback);
        compassSensor.enableSensors();
    }

    @Override
    public void start() {
//        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {

        itineraryMarkers = new FolderOverlay();
        mMap.getOverlays().add(itineraryMarkers);

        iconManager = new IconManager(mContext);
//        } else
//            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapActivity.PERMISSION_FINE_LOCATION);
    }

    @Override
    public void stop() {
        compassSensor.disableSensors();
        fusedLocation.stopLocationUpdates();

        if (Commons.isNotNull(myLocationOverlay)) {
            TinyDB db = TinyDB.getTinyDB();

            GeoPoint location = myLocationOverlay.getLocation();
            db.putInt(C.MAP_ZOOM_LEVEL, mMap.getZoomLevel());
            db.putDouble(C.MAP_CENTER_LAT, location.getLatitude());
            db.putDouble(C.MAP_CENTER_LNG, location.getLongitude());

            db.putInt(C.LAST_ZOOM_LEVEL, mMap.getZoomLevel());
            db.putDouble(C.LAST_CENTER_LAT, mMap.getMapCenter().getLatitude());
            db.putDouble(C.LAST_CENTER_LNG, mMap.getMapCenter().getLongitude());
        }
    }

    private FusedLocation.Callback onLocationChanged = new FusedLocation.Callback() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            myLocationOverlay.setLocation(point);

            if (location.hasAccuracy()) {
                myLocationOverlay.setShowAccuracy(true);
                myLocationOverlay.setAccuracy((int) location.getAccuracy());
            }
            mMap.getController().animateTo(point);
            mMap.getController().setZoom(TinyDB.getTinyDB().getInt(C.LAST_ZOOM_LEVEL, 2));


            geoField = new GeomagneticField(
                    Double.valueOf(location.getLatitude()).floatValue(),
                    Double.valueOf(location.getLongitude()).floatValue(),
                    Double.valueOf(location.getAltitude()).floatValue(),
                    System.currentTimeMillis());
        }

        public void onConnected(boolean connected) {
            if (connected)
                fusedLocation.startLocationUpdates();
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
    };

    @Override
    public void initMap() {
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);
        mMap.setTilesScaledToDpi(true);

        // Needed to get map events
        MapEventsOverlay overlay = new MapEventsOverlay(this);
        mMap.getOverlays().add(overlay);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mMap);
        mMap.getOverlays().add(scaleBarOverlay);

        addOverlays();

        mMap.setOnTouchListener(onMapTouchListener);

        TinyDB db = TinyDB.getTinyDB();

        mMap.getController().setZoom(db.getInt(C.LAST_ZOOM_LEVEL, 2));
        GeoPoint savedCenter = new GeoPoint(db.getDouble(C.LAST_CENTER_LAT, 0.0), db.getDouble(C.LAST_CENTER_LNG, 0.0));
        mMap.getController().animateTo(savedCenter);
    }

    /**
     * Defina and add the map overlays
     */
    private void addOverlays() {
        // My Location Overlay
        myLocationOverlay = new MyDirectedLocationOverlay(mContext, R.drawable.ic_navigation);
        myLocationOverlay.setEnabled(true);

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
     * @param poi
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

    @Override
    public void onShowPlaceItem(Place poi) {
        mSearchOverlay.clear();
        OverpassQueryResult.Element singleItem = new PlaceToOverpass(mMap.getMapCenter()).convertPlace(poi);
        mSearchOverlay.updateWithResults(singleItem, "");
        view.setSearchgMode(true);
        closeQuickSearch();
    }

    @Override
    public void onShowFavoriteItem(final Place poi) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FavoriteEntry entry = App.get().getDB().favoriteDao().findByOsmId(poi.getOsmId());

                if(Commons.isNotNull(entry)){
                    mMarkersOverlay.updateWithResults(entry.getOsmPojo(), "");
                    closeQuickSearch();
                } else
                    throw new RuntimeException("Favorite entry is null!");

            }
        });
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
                if (response.isSuccessful() && (Commons.isNotNull(response.body()) || response.body().elements.size() > 0)) {
                    OverpassQueryResult result = response.body() == null ? null : response.body();

                    // performing single search, if multi items returned, get one closest the tap point
                    if (singleSearch) {
                        double closestItemLat = 100;
                        double closestItemLng = 100;

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
                        // Calculate the distance to the POI
                        element.distance = MapUtils.getDistance((GeoPoint) mMap.getMapCenter(), element.lat, element.lon);
                        // Check for wiki image, format URL if found
                        if (!Commons.isEmpty(element.tags.image)) {
                            String commonsLink = WikiUtils.buildWikiCommonsLink(element.tags.image, 0);
                            element.tags.image = commonsLink;
                        }
                    }

                    // Different overlays for single tap and search functions
                    if (singleSearch) {
                        mMarkersOverlay.updateWithResults(result.elements, "");
                    } else {
                        if (result.elements.size() > 0) {
                            mSearchOverlay.updateWithResults(result.elements, "");
                            view.setSearchgMode(true);
                            closeQuickSearch();
                        } else {
                            view.showUserMsg(R.string.search_no_results_found);
                        }
                    }
                }

                view.onLoadFinish();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("DEAD", "onFailure: ");
                if (!call.isCanceled()) {
                    view.showSearching(false);
                } else {
                    view.onLoadFinish();
                }
                if (!singleSearch)
                    view.setSearchgMode(false);
            }
        });
    }

    @Override
    public void routeToPoi(final RouteInfo routeInfo) {

        if (Commons.isNull(routeInfo.getFrom())) {
            routeInfo.setFrom(myLocationOverlay.getLocation());
            routeInfo.setAddressFrom(mContext.getString(R.string.shared_my_location));
        }

        String routeFrom = String.format(Locale.ROOT, "%f,%f", routeInfo.getFrom().getLatitude(), routeInfo.getFrom().getLongitude());
        String routeTo = String.format(Locale.ROOT, "%f,%f", routeInfo.getTo().getLatitude(), routeInfo.getTo().getLongitude());

        RouteApi routeService = RouteServiceProvider.get();
        Call<RouteResult> call = routeService.route(BuildConfig.GRAPHHOPPER_API_KEY,
                routeFrom, routeTo, Locale.getDefault().getLanguage(), mContext.getString(routeInfo.getVehicle()));
        call.enqueue(new Callback<RouteResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<RouteResult> call, @NonNull Response<RouteResult> response) {
                hidePoiDialog();
                closeRouteDialog();
                RouteDialog.showInstance((MapActivity) mContext, routeInfo, response.body());


                // Draw directions on mMap


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
        Call<WikiQueryResponse> wikiCall = wikiService.requestNearBy(searchLocation, DEFAULT_WIKI_SEARCH_RADIUS, DEFAULT_WIKI_THUMBNAIL_SIZE);
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

                    view.setSearchgMode(true);
                    view.onLoadFinish();

                    mSearchOverlay.invalidate();

                    closeQuickSearch();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                view.onLoadFailed();
                view.showSearching(false);
                view.setSearchgMode(false);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        if (marker.getRelatedObject() instanceof OverpassQueryResult.Element) {
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
                    Log.d(TAG, "Press event");
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
                    Log.d(TAG, "Release event");
                    break;

                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "Cancel event");
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
    public void showCameraOrPoiMarkerListDialog() {
        Toast.makeText(mContext, "TODO - Show POI list", Toast.LENGTH_SHORT).show();

        if (mSearchOverlay.getItems().size() > 0) {

            MarkerListDialogFragment markerList = getMarkerListDialogFragment();
            if (Commons.isNotNull(markerList)) {
                markerList.show();
            } else
                MarkerListDialogFragment.showInstance((MapActivity) mContext, myLocationOverlay.getLocation(), mSearchOverlay.getItems());
        } else
            Toast.makeText(mContext, "Show Camera", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showOrClearSearchDialog() {

        mMarkersOverlay.clear();

        if (mSearchOverlay.getItems().size() > 0) {
            MyInfoWindow.closeAllInfoWindows(mMap);
            mSearchOverlay.clear();
            view.setSearchgMode(false);
        } else {
            showQuickSearch((GeoPoint) mMap.getMapCenter(), true);
        }
    }

    public void showQuickSearch(GeoPoint mapCenter, boolean show) {
        SearchDialogFragment fragment = getSearchFragment();
        if (Commons.isNotNull(fragment)) {
            fragment.dismiss();
            mMap.refreshDrawableState();
        }
        if (Commons.isNotNull(mLocation)) {
            GeoPoint whereAmI = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            SearchDialogFragment.showInstance((MapActivity) mContext, show, mapCenter, whereAmI);
        }
    }

    public void showQuickSearch() {
        SearchDialogFragment fragment = getSearchFragment();
        if (fragment != null) {
            fragment.show();
        }
    }

    public void closeQuickSearch() {
        SearchDialogFragment fragment = getSearchFragment();
        if (fragment != null) {
            fragment.closeSearch();
            mMap.refreshDrawableState();
        }
    }

    public SearchDialogFragment getSearchFragment() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(SearchDialogFragment.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (SearchDialogFragment) fragment : null;
    }

    public SinglePoiOptionsDialog getPoiDialog() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(SinglePoiOptionsDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (SinglePoiOptionsDialog) fragment : null;
    }

    public void hidePoiDialog() {
        SinglePoiOptionsDialog fragment = getPoiDialog();
        if (fragment != null) {
            fragment.hide();
        }
    }


    public MarkerListDialogFragment getMarkerListDialogFragment() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(MarkerListDialogFragment.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (MarkerListDialogFragment) fragment : null;
    }

    public RouteDialog getRouteDialog() {
        Fragment fragment = ((MapActivity) mContext).getSupportFragmentManager().findFragmentByTag(RouteDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (RouteDialog) fragment : null;
    }

    public void closeRouteDialog() {
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
