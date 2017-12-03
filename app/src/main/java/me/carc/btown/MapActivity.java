package me.carc.btown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.camera.CameraActivity;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.ExploreItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.IMap;
import me.carc.btown.map.MapPresenter;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.map.sheets.WikiPoiSheetDialog;
import me.carc.btown.map.sheets.share.ShareDialog;
import me.carc.btown.map.sheets.wiki.WikiReadingListDialogFragment;
import me.carc.btown.settings.Preferences;
import me.carc.btown.tours.ToursLaunchActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareSearchResultActivity;
import me.carc.btown.tours.top_pick_lists.VenueTabsActivity;

public class MapActivity extends BaseActivity implements
        IMap.View,
        SearchDialogFragment.SearchListener,
        WikiPoiSheetDialog.WikiCallback,
        WikiReadingListDialogFragment.BookmarksCallback {

    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final int PERMISSION_FINE_LOCATION = 100;

    public static final String PREFKEY_NEVER_ASK_GPS = "PREFKEY_NEVER_ASK_GPS";

    public static final int RESULT_GPS_REQ = 4009;
    public static final int RESULT_CAMERA = 4010;
    public static final int RESULT_TOURS = 4011;
    public static final int RESULT_EXPLORE = 4012;


    private IMap.Presenter presenter;

    private boolean isActive;  // remove this and replace with instanceof 'this'
    private Unbinder unbinder;


    @BindView(R.id.mapView)
    MapView mMap;

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

/*


    // DEBUG STUFF
    @BindView(R.id.debugText)
    TextView debugText;
    @BindView(R.id.proximityBtn)
    Button proximityBtn;

    private long dbgLocationUpdateCounter;
    private long dbgLocationUpdateTime  = 0;
    private GeoPoint dbgPoint;

    @OnClick(R.id.proximityBtn)
    void debugBtn() {
        presenter.debugBtn();
    }

    @Override
    public void showLocationSettings(GeoPoint point, LocationRequest locationRequest, Location location) {

        if(BuildConfig.DEBUG) {
           proximityBtn.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            builder.append("Accuracy: ").append(location.getAccuracy()).append("\n");
            builder.append("Bearing: ").append(location.getBearing()).append("\n");
            if (C.HAS_O)
                builder.append("AccuracyDegrees: ").append(String.valueOf(location.getBearingAccuracyDegrees())).append("\n");
            builder.append("Speed: ").append(String.valueOf(location.getSpeed())).append("\n");
            builder.append("Displacement: ").append(locationRequest.getSmallestDisplacement()).append("\n");
            builder.append("Interval: ").append(locationRequest.getInterval()).append("\n");
            builder.append("MaxWaitTime: ").append(locationRequest.getMaxWaitTime()).append("\n");
            builder.append("Counter: ").append(String.valueOf(dbgLocationUpdateCounter++)).append("\n");

            if (dbgPoint != null) {
                builder.append("OPoint: ").append(dbgPoint.getLatitude() + " " + dbgPoint.getLongitude()).append("\n");
                builder.append("NPoint: ").append(point.getLatitude() + " " + point.getLongitude()).append("\n");
            }

            builder.append("TimeDiff: ").append((double) (System.currentTimeMillis() - dbgLocationUpdateTime) / 1000);

            dbgLocationUpdateTime = System.currentTimeMillis();
            dbgPoint = point;
            debugText.setVisibility(View.VISIBLE);
            debugText.setText(builder.toString());
        } else {
            proximityBtn.setVisibility(View.GONE);
            debugText.setVisibility(View.GONE);
        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        presenter = new MapPresenter(this, this, mMap);
        presenter.initMap(savedInstanceState);
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

        view.findViewById(R.id.itemFsqSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFsqSearch();
                dlg.dismiss();
            }
        });


        view.findViewById(R.id.itemFsqExplore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFsqExplore();
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemFsqExploreMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/11/2017
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
                ShareDialog.sendMessage(MapActivity.this, MapUtils.buildOsmMapLink(point, mMap.getMaxZoomLevel()));
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

        if (Preferences.showTours(this))
            fabTours.setVisibility(View.VISIBLE);
        else
            fabTours.setVisibility(View.GONE);
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

            case RESULT_CAMERA:
                break;

            case RESULT_TOURS:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(VenueTabsActivity.EXTRA_VENUE)) {
                        // show single item from FSQ
                        VenueResult mVenueResult = data.getParcelableExtra(VenueTabsActivity.EXTRA_VENUE);
                        presenter.showFsqVenue(mVenueResult);

                    } else if (data.hasExtra(FourSquareListsActivity.EXTRA_LISTS)) {
                        // show list items from FSQ
                        ListItems items = data.getParcelableExtra(FourSquareListsActivity.EXTRA_LISTS);
                        presenter.showFsqList(items);
                    } else
                        Log.d(TAG, "onActivityResult: SHOULD NOT BE HERE!!");
                }
                break;
            case RESULT_EXPLORE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(FourSquareSearchResultActivity.EXTRA_EXPLORE)) {
                        // show single item from FSQ
                        ArrayList<VenueResult> venues = data.getParcelableArrayListExtra(FourSquareSearchResultActivity.EXTRA_EXPLORE);
                        presenter.showFsqVenues(venues);
                    } else if (data.hasExtra(VenueTabsActivity.EXTRA_VENUE)) {
                        // show single item from FSQ
                        VenueResult mVenueResult = data.getParcelableExtra(VenueTabsActivity.EXTRA_VENUE);
                        presenter.showFsqVenue(mVenueResult);
                    } else
                        Log.d(TAG, "onActivityResult: ");
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
                    // permission denied, boo!
                    Toast.makeText(MapActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
    public void showUserMsg(@StringRes int msg) {
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
        if (isActive)
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
    void onMenuFab() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.fab_location)
    void onLocationFab() {
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
        startActivityForResult(new Intent(MapActivity.this, ToursLaunchActivity.class), RESULT_TOURS);
    }

    @Override
    public void showFsqSearchResults(ArrayList<VenueResult> results) {
        Intent intent = new Intent(MapActivity.this, FourSquareSearchResultActivity.class);
        intent.putParcelableArrayListExtra("SEARCH_RESULTS", results);
        startActivityForResult(intent, RESULT_TOURS);
    }

    @Override
    public void showFsqSExploreResults(String header, ArrayList<ExploreItem> results) {
        Intent intent = new Intent(MapActivity.this, FourSquareSearchResultActivity.class);
        intent.putExtra("HEADER", header);
        intent.putParcelableArrayListExtra(FourSquareSearchResultActivity.EXTRA_EXPLORE, results);
        startActivityForResult(intent, RESULT_EXPLORE);
    }

    @Override
    public void onCameraLaunch() {
        startActivityForResult(new Intent(MapActivity.this, CameraActivity.class), RESULT_CAMERA);
    }

    public void showPoiDlg(Object obj) {
        presenter.showPoiDialog(obj);
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


    @Override
    public void requestGpsEnable() {
        if(!db.getBoolean(PREFKEY_NEVER_ASK_GPS)) {
            new AlertDialog.Builder(MapActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(R.string.shared_string_location)
                    .setMessage(R.string.enable_gps_request)
                    .setIcon(R.drawable.ic_gps_off)
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
}
