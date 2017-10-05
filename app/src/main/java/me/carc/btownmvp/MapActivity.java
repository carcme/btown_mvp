package me.carc.btownmvp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btownmvp.Utils.ViewUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.map.IMap;
import me.carc.btownmvp.map.MapPresenter;
import me.carc.btownmvp.map.markers.MyInfoWindow;
import me.carc.btownmvp.map.search.SearchDialogFragment;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.sheets.RouteDialog;
import me.carc.btownmvp.map.sheets.SinglePoiOptionsDialog;
import me.carc.btownmvp.map.sheets.WikiPoiSheetDialog;
import me.carc.btownmvp.map.sheets.model.RouteInfo;

public class MapActivity extends BaseActivity implements
        IMap.View,
        SearchDialogFragment.SearchListener,
        WikiPoiSheetDialog.WikiCallback,
        SinglePoiOptionsDialog.SinglePoiCallback,
        RouteDialog.RouteDialogCallback{

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final int PERMISSION_FINE_LOCATION = 100;

    private IMap.Presenter presenter;
    //    private Drawer drawer = null;
//    private AccountHeader drawerHeader = null;
    private Unbinder unbinder;

    @BindView(R.id.mapView)
    MapView mMap;

    @BindView(R.id.featureProgressDialog)
    ProgressBar featureProgressDialog;

    @BindView(R.id.routeInfo)
    TextView routeInfo;

    @BindView(R.id.fab_map_friend)
    FloatingActionButton fab_map_friend;

    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;

    @BindView(R.id.fab_CameraAndPoiList)
    FloatingActionButton fabCameraAndPoiList;


    @BindView(R.id.fab_menu)
    FloatingActionButton fabMenu;

    @BindView(R.id.fab_ZoomIn)
    FloatingActionButton fabZoomIn;

    @BindView(R.id.fab_ZoomOut)
    FloatingActionButton fabZoomOut;

    @BindView(R.id.fab_location)
    FloatingActionButton trackingModeFab;


    @Override
    public void searchItemSelected(Place poi) {
        presenter.onSearchItemSelected(poi);
    }

    @Override
    public void showPlaceItem(Place poi) {
        presenter.onShowPlaceItem(poi);
    }

    @Override
    public void showFavoriteItem(Place poi) {
        presenter.onShowFavoriteItem(poi);
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
    public void showSearching(boolean show) {
        if (show) {
            featureProgressDialog.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.fabSearchProgressColor),
                            android.graphics.PorterDuff.Mode.MULTIPLY);
            featureProgressDialog.setVisibility(View.VISIBLE);
        } else {
            featureProgressDialog.setVisibility(View.GONE);
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
     * @param searchgComplete true = search finished and showing icons, false = clear search items
     */
    @Override
    public void setSearchgMode(boolean searchgComplete) {
        if (searchgComplete) {
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
        fabSearch.setTag(searchgComplete);
        fabCameraAndPoiList.setTag(searchgComplete);
        mMap.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        unbinder = ButterKnife.bind(this);

        getBaseFunctions();

        presenter = new MapPresenter(this, this, mMap);
        presenter.start();

        presenter.initMap();
        setControlListeners();
        setupDrawer(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.stop();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(fabSearch.getTag().equals(true))
            presenter.showOrClearSearchDialog();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    /**
     * Set up button listeners
     */
    private void setControlListeners() {
        trackingModeFab.setOnClickListener(onTrackingFabClickListener);
        fabZoomIn.setOnClickListener(onZoomFabClickListener);
        fabZoomOut.setOnClickListener(onZoomFabClickListener);
        fabSearch.setOnClickListener(onSearchFabClickListener);
        fabCameraAndPoiList.setOnClickListener(onCameraOrPoiListFabClickListener);
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapActivity.this, "Show Drawer", Toast.LENGTH_SHORT).show();
//                drawer.openDrawer();
            }
        });
    }

    private void setupDrawer(/*Toolbar toolbar, */Bundle savedInstanceState) {

    }


    /**
     * Location fab handler - also tracker and location return depending on usage
     */
    private View.OnClickListener onTrackingFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.zoomToMyLocation();
        }
    };

    /**
     * Zoom fab handler
     */
    private View.OnClickListener onZoomFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_ZoomIn:
                    presenter.zoomIn();
                    break;
                case R.id.fab_ZoomOut:
                    presenter.zoomOut();
                    break;
            }
        }
    };

    /**
     * Search FAB
     */
    private View.OnClickListener onSearchFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.showOrClearSearchDialog();
        }
    };

    /**
     * Search FAB
     */
    private View.OnClickListener onCameraOrPoiListFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.showCameraOrPoiMarkerListDialog();
        }
    };


    public void showPoiDlg(Object obj) {
        presenter.showPoiDialog(obj);
    }

    public void routeToPoi(RouteInfo info) {
        MyInfoWindow.closeAllInfoWindows(mMap);
        presenter.routeToPoi(info);
    }

    /**
     * SinglePoiOptionsDialog callbacks
     * @param info routing information
     */
    @Override
    public void onRouteTo(RouteInfo info) {
        routeToPoi(info);
    }

    /**
     * RouteDialog callbacks
     * @param info routing information
     */
    @Override
    public void onRouteChange(RouteInfo info) { routeToPoi(info); }

    /**
     * WikiPoiSheetDialog callbacks
     */
    @Override
    public void onLinkPreviewLoadPage() {
        Log.d(TAG, "onLinkPreviewLoadPage: ");
    }

    @Override
    public void onLinkPreviewCopyLink() {
        Log.d(TAG, "onLinkPreviewCopyLink: ");
    }

    @Override
    public void onLinkPreviewAddToList() {
        Log.d(TAG, "onLinkPreviewAddToList: ");
    }

    @Override
    public void onLinkPreviewShareLink() {
        Log.d(TAG, "onLinkPreviewShareLink: ");
    }

}
