package me.carc.btown.map;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.camera.CameraActivity;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.ExploreItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.map.sheets.WikiPoiSheetDialog;
import me.carc.btown.map.sheets.wiki.WikiReadingListDialogFragment;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareSearchResultActivity;
import me.carc.btown.tours.top_pick_lists.VenueTabsActivity;
import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class MapActivity extends BaseActivity implements
        IMap.View,
        SearchDialogFragment.SearchListener,
        WikiPoiSheetDialog.WikiCallback,
        WikiReadingListDialogFragment.BookmarksCallback {

    private static final String TAG = MapActivity.class.getName();

    public static final int PERMISSION_FINE_LOCATION = 100;

    public static final String PREFKEY_NEVER_ASK_GPS = "PREFKEY_NEVER_ASK_GPS";
    private static final String SHOWN_FIRST_LAUNCH = "SHOWN_FIRST_LAUNCH";  // show reason for sign in
    private static final String SHOWN_FIRST_PIN_DROP = "SHOWN_FIRST_PIN_DROP";  // show reason for sign in

    public final static int REQUEST_CHECK_SETTINGS = 4008;
    public static final int RESULT_GPS_REQ = 4009;
    public static final int RESULT_CAMERA = 4010;
    public static final int RESULT_TOURS = 4011;
    public static final int RESULT_EXPLORE = 4012;
    public static final int RESULT_SHOW_TOURS_MAP = 4013;


    private IMap.Presenter presenter;

    private boolean isActive;  // remove this and replace with instanceof 'this'
    private Unbinder unbinder;
    private boolean hasDropPinsVisible;

    @BindView(R.id.mapView)                 MapView mMap;
    @BindView(R.id.featureProgressDialog)   ProgressBar featureProgressDialog;
    @BindView(R.id.routeInfoText)           TextView routeInfoText;
    @BindView(R.id.fab_map_friend)          FloatingActionButton fab_map_friend;
    @BindView(R.id.fab_search)              FloatingActionButton fabSearch;
    @BindView(R.id.fab_CameraAndPoiList)    FloatingActionButton fabCameraAndPoiList;
    @BindView(R.id.fab_menu)                FloatingActionButton fabMenu;
    @BindView(R.id.fab_location)            FloatingActionButton trackingModeFab;
    @BindView(R.id.crosshairIcon)           ImageView crosshairIcon;
    @BindView(R.id.fabBack)                 FloatingActionButton fabBack;
    @BindView(R.id.fabTours)                FloatingActionButton fabTours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        unbinder = ButterKnife.bind(this);

        presenter = new MapPresenter(this, this, mMap);
        presenter.initMap(savedInstanceState);

        if (getIntent().hasExtra(VenueTabsActivity.EXTRA_VENUE)) {
            // show single item from FSQ
            VenueResult mVenueResult = getIntent().getParcelableExtra(VenueTabsActivity.EXTRA_VENUE);
            presenter.showFsqVenue(mVenueResult);

        } else if (getIntent().hasExtra(FourSquareListsActivity.EXTRA_LISTS)) {
            // show list items from FSQ
            ListItems items = getIntent().getParcelableExtra(FourSquareListsActivity.EXTRA_LISTS);
            presenter.showFsqList(items);
        }
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
                showSearching(true);
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
                showSearching(true);
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
                showSearching(true);
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemTourist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTouristLookupFromLongPress(point);
                showSearching(true);
                dlg.dismiss();
            }
        });

        view.findViewById(R.id.itemPoint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDropPin(point);
                dlg.dismiss();
            }
        });

        if (hasDropPinsVisible) {
            view.findViewById(R.id.clearDropPins).setVisibility(View.VISIBLE);
            view.findViewById(R.id.clearDropPins).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onClearPins();
                    dlg.dismiss();
                }
            });
        } else
            view.findViewById(R.id.clearDropPins).setVisibility(View.GONE);

        view.findViewById(R.id.itemHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                helpShowcase();
            }
        });

        dlg.show();
    }

    @Override
    public void addClearDropMenuItem(boolean hasDropPins) {
        hasDropPinsVisible = hasDropPins;

        showSearching(false);
        if (!db.getBoolean(SHOWN_FIRST_PIN_DROP)) {
            Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            new FancyShowCaseView.Builder(this)
                    .title(getString(R.string.help_pin_drop))
                    .titleStyle(0, Gravity.TOP | Gravity.CENTER)
                    .fitSystemWindows(true)
                    .focusOn(crosshairIcon)
                    .focusCircleRadiusFactor(2)
                    .dismissListener(new DismissListener() {
                        @Override
                        public void onDismiss(String id) {
                            db.putBoolean(SHOWN_FIRST_PIN_DROP, true);
                        }

                        @Override
                        public void onSkipped(String id) { /*EMPTY*/ }
                    })
                    .enterAnimation(enterAnimation)
                    .build()
                    .show();
        }
    }

    /**
     * Show the help and what each fab/button does
     */
    private void helpShowcase() {

        int borderColor = ContextCompat.getColor(this, R.color.colorAccent);
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        final FancyShowCaseView tours = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_tours))
                .fitSystemWindows(true)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fabTours)
                .build();

        final FancyShowCaseView search = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_search))
                .fitSystemWindows(true)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fabSearch)
                .build();

        final FancyShowCaseView tracking = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_tracking))
                .fitSystemWindows(true)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(trackingModeFab)
                .build();

        final FancyShowCaseView camera = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_camera))
                .fitSystemWindows(true)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fabCameraAndPoiList)
                .build();

        final FancyShowCaseView menu = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_menu))
                .fitSystemWindows(true)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fabMenu)
                .build();

        new FancyShowCaseQueue()
                .add(tours)
                .add(search)
                .add(tracking)
                .add(camera)
                .add(menu)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        presenter.start();

        if (!db.getBoolean(SHOWN_FIRST_LAUNCH)) {
            Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            new FancyShowCaseView.Builder(this)
                    .title(getString(R.string.help_intro))
                    .fitSystemWindows(true)
                    .dismissListener(new DismissListener() {
                        @Override
                        public void onDismiss(String id) {
                            db.putBoolean(SHOWN_FIRST_LAUNCH, true);
                        }

                        @Override
                        public void onSkipped(String id) { /*EMPTY*/ }
                    })
                    .enterAnimation(enterAnimation)
                    .build()
                    .show();
        }
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
        presenter.destroy();
        super.onDestroy();
    }

    /**
     * Bug #164 - Make sure intent is {@link javax.annotation.Nonnull}
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent == null) {
            intent = new Intent();
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                //Reference: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG, "User enabled location");
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "User Cancelled enabling location");
                        break;
                    default:
                        break;
                }
                break;

            case RESULT_GPS_REQ:
                if (((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    presenter.onUpdateLocation();
                }
                break;

            case RESULT_CAMERA:
                break;

            case RESULT_TOURS:
                if (resultCode == RESULT_OK) {
                    showSearching(false);
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

            case RESULT_SHOW_TOURS_MAP:
                if (resultCode == RESULT_OK) {
                    int id = data.getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1);
                    if(id != -1) {
                        ViewUtils.changeFabColour(this, fabTours, R.color.fabSearchCancelColor);
                        ViewUtils.changeFabIcon(this, fabTours, R.drawable.ic_times_white);

                        TourViewModel tourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
                        tourViewModel.getTour(id).observe(this, new Observer<TourCatalogueItem>() {
                            @Override
                            public void onChanged(@Nullable final TourCatalogueItem tour) {
                                presenter.showTour(tour);
                            }
                        });
                    }
                }
                break;
            default:
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
        else {
            presenter.clearPoiMarkers();
            super.onBackPressed();  // clear all markers to avoid mem leaks - maybe save them to put them back on restart
        }
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
            default:
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
        Commons.Toast(this, R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
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


    @OnClick(R.id.fabBack)
    void back() { super.onBackPressed(); }

//    @OnClick(R.id.fab_menu)
//    void onMenuFab() {
//        mDrawerLayout.openDrawer(GravityCompat.START);
//    }

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
    void pickTour() {
        ColorStateList color = fabTours.getBackgroundTintList();
        if(color.getDefaultColor() == ContextCompat.getColor(this, R.color.fabSearchCancelColor)) {
            presenter.clearTourIcons();
        } else {
            Intent intent = new Intent(MapActivity.this, CatalogueActivity.class);
            intent.putExtra(CatalogueActivity.EXTRA_SHOW_ON_MAP, true);
            startActivityForResult(intent, RESULT_SHOW_TOURS_MAP);
        }
    }

    @Override
    public void resetToursBtn() {
        ViewUtils.changeFabColour(this, fabTours, R.color.fabColorAlmostClear);
        ViewUtils.changeFabIcon(this, fabTours, R.drawable.ic_tours_white);
    }



    @Override
    public void showFsqSearchResults(ArrayList<VenueResult> results) {
        showSearching(false);
        Intent intent = new Intent(MapActivity.this, FourSquareSearchResultActivity.class);
        intent.putParcelableArrayListExtra("SEARCH_RESULTS", results);
        startActivityForResult(intent, RESULT_TOURS);
    }

    @Override
    public void showFsqSExploreResults(String header, ArrayList<ExploreItem> results) {
        showSearching(false);
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
        if (!db.getBoolean(PREFKEY_NEVER_ASK_GPS)) {
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
