package me.carc.btown_map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btown_map.Utils.MapUtils;
import me.carc.btown_map.Utils.ViewUtils;
import me.carc.btown_map.camera.CameraActivity;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.data.model.RouteResult;
import me.carc.btown_map.data.reverse.ReverseLookupLoader;
import me.carc.btown_map.db.bookmark.BookmarkEntry;
import me.carc.btown_map.map.IMap;
import me.carc.btown_map.map.MapPresenter;
import me.carc.btown_map.map.interfaces.MyClickListener;
import me.carc.btown_map.map.markers.MyInfoWindow;
import me.carc.btown_map.map.search.SearchDialogFragment;
import me.carc.btown_map.map.search.model.Place;
import me.carc.btown_map.map.sheets.SinglePoiOptionsDialog;
import me.carc.btown_map.map.sheets.WikiPoiSheetDialog;
import me.carc.btown_map.map.sheets.model.RouteInfo;
import me.carc.btown_map.map.sheets.model.adpater.RouteInstructionsAdapter;
import me.carc.btown_map.map.sheets.share.ShareDialog;
import me.carc.btown_map.map.sheets.wiki.WikiReadingListDialogFragment;
import me.carc.btown_map.tours.ToursLaunchActivity;

public class MapActivity extends BaseActivity implements
        IMap.View,
        SearchDialogFragment.SearchListener,
        WikiPoiSheetDialog.WikiCallback,
        SinglePoiOptionsDialog.SinglePoiCallback,
        WikiReadingListDialogFragment.BookmarksCallback
        /*,
        RouteDialog.RouteDialogCallback*/ {

    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final int PERMISSION_FINE_LOCATION = 100;

    public static final String PREFKEY_NEVER_ASK_GPS = "PREFKEY_NEVER_ASK_GPS";

    public static final int RESULT_GPS_REQ = 4009;


    private IMap.Presenter presenter;
    //    private Drawer drawer = null;
//    private AccountHeader drawerHeader = null;

    private BottomSheetBehavior behavior;
    private RouteInfo routeInfo;
    private RouteInstructionsAdapter mAdapter;

    private boolean isActive;  // remove this and replace with instanceof 'this'
    private Unbinder unbinder;


    @BindView(R.id.mapView)
    MapView mMap;

    @BindView(R.id.bottomSheet)
    View bottomSheet;

    @BindView(R.id.featureProgressDialog)
    ProgressBar featureProgressDialog;

    @BindView(R.id.routeInfoText)
    TextView routeInfoText;

    @BindView(R.id.fab_map_friend)
    FloatingActionButton fab_map_friend;

    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;

    @BindView(R.id.fab_CameraAndPoiList)
    FloatingActionButton fabCameraAndPoiList;

    @BindView(R.id.fab_menu)
    FloatingActionButton fabMenu;

    @BindView(R.id.fabTours)
    ImageView fabTours;

    @BindView(R.id.fab_ZoomIn)
    FloatingActionButton fabZoomIn;

    @BindView(R.id.fab_ZoomOut)
    FloatingActionButton fabZoomOut;

    @BindView(R.id.fab_location)
    FloatingActionButton trackingModeFab;

    @BindView(R.id.routeRecyclerView)
    RecyclerView routeRecyclerView;

    /* Routing bottomsheet */
    @BindView(R.id.routeCar)
    TextView routeCar;
    @BindView(R.id.routeWalk)
    TextView routeWalk;
    @BindView(R.id.routeTrain)
    TextView routeTrain;
    @BindView(R.id.routeCancelBtn)
    TextView routeCancelBtn;
    @BindView(R.id.routeListBtn)
    TextView routeListBtn;
    @BindView(R.id.routeTime)
    TextView routeTime;
    @BindView(R.id.routeDistance)
    TextView routeDistance;
    @BindView(R.id.routeElevation)
    TextView routeElevation;
    @BindView(R.id.routeElevationText)
    TextView routeElevationText;
    @BindView(R.id.routeElevationIcon)
    ImageView routeElevationIcon;
    @BindView(R.id.routeDeparture)
    TextView routeDeparture;
    @BindView(R.id.routeDestination)
    TextView routeDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        presenter = new MapPresenter(this, this, mMap);
        presenter.initMap(savedInstanceState);


        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        setupDrawer(savedInstanceState);
    }


    @Override
    public void showNavigationPopup(final GeoPoint point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LinearLayout frameView = new LinearLayout(this);
        builder.setView(frameView);

        final AlertDialog dlg = builder.create();
        LayoutInflater inflater = dlg.getLayoutInflater();
        View view = inflater.inflate(R.layout.long_press_context_menu_layout, frameView);

        view.findViewById(R.id.itemWiki).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onWikiLookup();
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemFood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFoodLookupFromLongPress(point);
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemTourist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTouristLookupFromLongPress(point);
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemPoint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.sendMessage(MapActivity.this, MapUtils.buildOsmMapLink(point, mMap.getZoomLevel()));
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapActivity.this, "Todo - Add help screen ", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });

        dlg.show();


/*
        final Dialog dlg = new Dialog(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.long_press_context_menu_layout, null);
        dlg.setTitle("Options");

        dlg.setContentView(view);

        view.findViewById(R.id.longPressWiki).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWikiLookup();
                dlg.dismiss();

            }
        });

        dlg.show();
*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        presenter.start();
    }

    @Override
    protected void onPause() {
        isActive = false;
        presenter.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GPS_REQ:
                if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    presenter.onUpdateLocation();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.getBundle(outState);
    }

    @Override
    public void onBackPressed() {
        if (fabSearch.getTag().equals(true))
            presenter.showOrClearSearchDialog();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.start();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MapActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;

            case 101:
                break;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setupDrawer(/*Toolbar toolbar, */Bundle savedInstanceState) {

    }


    @Override
    public void searchItemSelected(Place poi) {
        presenter.onSearchItemSelected(poi);
    }

    @Override
    public void showPlaceItem(Place poi) {
        presenter.onShowPlaceItem(poi);
    }


    @Override
    public void showFromDatabase(int dbType, Place poi) {
        presenter.onShowFromDatabase(dbType, poi);
    }

    @Override
    public void doWikiLookup() {
        presenter.onWikiLookup();
    }

    @Override
    public void setPresenter(IMap.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showUserMsg(int msg) {
        Snackbar.make(mMap, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoadStart() {
        showSearching(true);
    }

    @Override
    public void onLoadFinish() {
        showSearching(false);
        mMap.invalidate();
    }

    @Override
    public void onLoadFailed() {
        showSearching(false);
    }


    @Override
    public void enableLocationDependantFab(boolean enable) {
        if(isActive)
            trackingModeFab.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSearching(boolean show) {
        if (isActive) {
            if (show) {
                featureProgressDialog.getIndeterminateDrawable()
                        .setColorFilter(ContextCompat.getColor(this, R.color.fabSearchProgressColor),
                                android.graphics.PorterDuff.Mode.MULTIPLY);
                featureProgressDialog.setVisibility(View.VISIBLE);
            } else {
                featureProgressDialog.setVisibility(View.GONE);
            }
        }
    }

    public MapView getMapView() {
        return mMap;
    }

    /**
     * Toggle the tracking FAB
     *
     * @param trackingMode true = follow my location, false = free map movement
     */
    @Override
    public void setTrackingMode(boolean trackingMode) {

        if (trackingMode) {
            ViewUtils.changeFabColour(this, trackingModeFab, R.color.fabColorTracking);
            ViewUtils.changeFabIcon(this, trackingModeFab, R.drawable.ic_compass);
            trackingModeFab.setKeepScreenOn(true);
        } else {
            ViewUtils.changeFabColour(this, trackingModeFab, R.color.fabColorAlmostClear);
            ViewUtils.changeFabIcon(this, trackingModeFab, R.drawable.ic_my_location);
            trackingModeFab.setKeepScreenOn(false);
        }
        trackingModeFab.setTag(trackingMode);
    }

    /**
     * Toggle the search Mode
     *
     * @param listMode true = search finished and showing icons, false = clear search items
     */
    @Override
    public void setListMode(boolean listMode) {
        if (listMode) {
            ViewUtils.changeFabColour(this, fabSearch, R.color.fabSearchCancelColor);
//            fabSearch.setImageDrawable(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_playlist_remove).color(ContextCompat.getColor(this, R.color.fabSearchCancelColor)).sizeDp(20));
            ViewUtils.changeFabIcon(this, fabSearch, R.drawable.ic_cancel);

            // show the poi list button
//            fabCameraAndPoiList.setImageDrawable(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_format_list_bulleted_type).color(ContextCompat.getColor(this, R.color.poiOpenTimesOpenColor)).sizeDp(20));
            ViewUtils.changeFabColour(this, fabCameraAndPoiList, R.color.poiOpenTimesOpenColor);
            ViewUtils.changeFabIcon(this, fabCameraAndPoiList, R.drawable.ic_view_list);
        } else {
            // POI list is empty
            ViewUtils.changeFabColour(this, fabSearch, R.color.fabColorAlmostClear);
            ViewUtils.changeFabIcon(this, fabSearch, R.drawable.ic_search);
            // show the camera
            ViewUtils.changeFabColour(this, fabCameraAndPoiList, R.color.fabColorAlmostClear);
            ViewUtils.changeFabIcon(this, fabCameraAndPoiList, R.drawable.ic_camera_white);
        }
        fabSearch.setTag(listMode);
        fabCameraAndPoiList.setTag(listMode);
        mMap.invalidate();
    }


    @OnClick(R.id.fab_menu)
    void onMenuFab(View v) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.fab_location)
    void onLocationFab(View v) {
        presenter.zoomToMyLocation();
    }

    @OnClick(R.id.fab_ZoomIn)
    void onZoomInFab() {
        presenter.zoomIn();
    }

    @OnClick(R.id.fab_ZoomOut)
    void onZoomOutFab() {
        presenter.zoomOut();
    }

    @OnClick(R.id.fab_search)
    void onSearchFab() {
        presenter.showOrClearSearchDialog();
    }

    @OnClick(R.id.fab_CameraAndPoiList)
    void onCameraFab() {
        presenter.onShowCameraOrPoiMarkerListDialog();
    }

    @OnClick(R.id.fabTours)
    void launchTours() {
        startActivity(new Intent(MapActivity.this, ToursLaunchActivity.class));
//        TourLaunchDialogDialog.showInstance(getApplicationContext());
    }

    @Override
    public void onCameraLaunch() {
        startActivity(new Intent(MapActivity.this, CameraActivity.class));
    }

    @Override
    public void showRouteBottomSheet(RouteInfo info, RouteResult routeResult) {
        routeInfo = info;
        new PopulateView(routeResult.getPath()).run();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    public void showPoiDlg(Object obj) {
        presenter.showPoiDialog(obj);
    }

    public void routeToPoi(boolean newRoute, RouteInfo info) {
        MyInfoWindow.closeAllInfoWindows(mMap);
        presenter.routeToPoi(newRoute, info);
    }

    /**
     * SinglePoiOptionsDialog callbacks
     *
     * @param info routing information
     */
    @Override
    public void onRouteTo(RouteInfo info) {
        routeToPoi(true, info);
    }

    /**
     * WikiPoiSheetDialog callbacks
     */
    @Override
    public void todo() {
        Toast.makeText(this, "ToDO - Show on Map", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "WikiCallback todo - is this needed");
    }


    /**
     * WikiReadingListDialogFragment callbacks
     */
    @Override
    public void showWikiOnMap(BookmarkEntry entry) {
        presenter.onShowWikiOnMap(entry);
    }


    private class PopulateView implements Runnable {

        RouteResult.Paths path;

        PopulateView(RouteResult.Paths path) {
            this.path = path;
        }

        @Override
        public void run() {
            populateRecycler();
        }

        private void populateRecycler() {

            double elevation = path.descend - path.ascend;
            int elevationRes;
            if (elevation < 0) {
                elevationRes = R.drawable.ic_expand_down;
                routeElevationText.setText(getResources().getText(R.string.altitude_descent));
            } else {
                elevationRes = R.drawable.ic_expand_up;
                routeElevationText.setText(getResources().getText(R.string.altitude_ascent));
            }

            routeElevationIcon.setImageDrawable(ContextCompat.getDrawable(MapActivity.this, elevationRes));
            routeElevation.setText(MapUtils.getFormattedAlt(elevation));
/*
            routeAccent.setText(MapUtils.getFormattedAlt(path.ascend));
            routeDecent.setText(MapUtils.getFormattedAlt(path.descend));
*/
            if ((path.time / C.TIME_ONE_SECOND) < C.TIME_ONE_MINUTE)
                routeTime.setText(R.string.route_less_than_minute);
            else
                routeTime.setText(MapUtils.getFormattedDuration((int) path.time / C.TIME_ONE_SECOND));

            routeDistance.setText(MapUtils.getFormattedDistance(path.distance));

            routeDeparture.setText(routeInfo.getAddressFrom());

            if (Commons.isNull(routeInfo.getAddressTo())) {
                new ReverseLookupLoader(routeDestination, routeInfo.getTo().getLatitude(), routeInfo.getTo().getLongitude());
            } else
                routeDestination.setText(routeInfo.getAddressTo());

            switch (routeInfo.getVehicle()) {
                case R.string.vehicle_car:
                    ViewUtils.changeTextViewIconColour(MapActivity.this, routeCar, R.color.fabColorTracking);
                    break;
                case R.string.vehicle_walk:
                    ViewUtils.changeTextViewIconColour(MapActivity.this, routeWalk, R.color.fabColorTracking);
                    break;
                case R.string.vehicle_train:
                    ViewUtils.changeTextViewIconColour(MapActivity.this, routeTrain, R.color.fabColorTracking);
                    break;
            }

            // Set TO color
            ViewUtils.changeTextViewIconColour(MapActivity.this, routeDestination, R.color.routeToColor);

            // Set FROM color
            ViewUtils.changeTextViewIconColour(MapActivity.this, routeDeparture, R.color.routeFromColor);

            // Set control button colors
            ViewUtils.changeTextViewIconColour(MapActivity.this, routeCancelBtn, R.color.black);
            ViewUtils.changeTextViewIconColour(MapActivity.this, routeListBtn, R.color.black);


            mAdapter = new RouteInstructionsAdapter(path.instructions, new MyClickListener() {
                @Override
                public void OnClick(View v, int position) {
                    Toast.makeText(MapActivity.this, "Position:" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnLongClick(View v, int position) {
                    Toast.makeText(MapActivity.this, "Position:" + position, Toast.LENGTH_SHORT).show();
                }
            });

            routeRecyclerView.setNestedScrollingEnabled(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MapActivity.this);
            routeRecyclerView.setLayoutManager(layoutManager);
            routeRecyclerView.setAdapter(mAdapter);

        }
    }

    @OnClick(R.id.routeListBtn)
    void onToggleList() {
        if (routeRecyclerView.getVisibility() == View.GONE)
            routeRecyclerView.setVisibility(View.VISIBLE);
        else
            routeRecyclerView.setVisibility(View.GONE);
    }

    @OnClick(R.id.routeCar)
    void onCarClick() {
        changeRouteMode(RouteInfo.Vehicle.CAR);
    }

    @OnClick(R.id.routeWalk)
    void onWalkClick() {
        changeRouteMode(RouteInfo.Vehicle.WALK);
    }

    @OnClick(R.id.routeTrain)
    void onTrainClick() {
    }

    @OnClick(R.id.routeCancelBtn)
    void onClose() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        routeToPoi(true, null);
    }

    private void changeRouteMode(RouteInfo.Vehicle vehicle) {
        if (Commons.isNotNull(routeInfo)) {
            if (routeInfo.getVehicle() != vehicle.getVehicle()) {
                // reset the vehicle icon colors
                ViewUtils.changeTextViewIconColour(this, routeCar, R.color.sheet_icon_color);
                ViewUtils.changeTextViewIconColour(this, routeWalk, R.color.sheet_icon_color);
                ViewUtils.changeTextViewIconColour(this, routeTrain, R.color.sheet_icon_color);

                routeInfo.setVehicle(vehicle);
                routeToPoi(true, routeInfo);
            }
        }
    }


    @Override
    public void requestGpsEnable() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.enable_gps_request)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, RESULT_GPS_REQ);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.shared_string_never_ask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.putBoolean(PREFKEY_NEVER_ASK_GPS, true);
                        dialog.dismiss();
                    }
                })
                .show();
    }


}
