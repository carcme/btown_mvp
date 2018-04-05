package me.carc.btown.tours.top_pick_lists;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.ExploreItem;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.tours.top_pick_lists.adapters.FsqSearchResultAdapter;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import me.carc.btown.ui.custom.MyRecyclerItemClickListener;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FourSquareSearchResultActivity extends BaseActivity {

    private static final String TAG = FourSquareSearchResultActivity.class.getName();
    public static final String EXTRA_EXPLORE = "EXTRA_EXPLORE";


    public static final int RESULT_SHOW_ITEM = 148;
    private static final String SHOWN_FIRST_LAUNCH = "SHOWN_FIRST_LAUNCH" + TAG;  // show reason for sign in

    ArrayList<VenueResult> mVenues; // todo make this local??

    @BindView(R.id.catalogue_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.toursToolbar)
    Toolbar toolbar;

    @BindView(R.id.inventoryProgressBar)
    ProgressBar progressLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fabMapAll)
    FloatingActionButton fabMapAll;

    @OnClick(R.id.fab)
    void done() {
        onBackPressed();
    }

    @OnClick(R.id.fabMapAll)
    void showAll() {
        getIntent().putParcelableArrayListExtra(EXTRA_EXPLORE, mVenues);
        setResult(RESULT_OK, getIntent());
        onBackPressed();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_fsq_main_recycler);
        ButterKnife.bind(this);

        setupUI(savedInstanceState);

        if (getIntent().hasExtra("SEARCH_RESULTS")) {
            mVenues = getIntent().getParcelableArrayListExtra("SEARCH_RESULTS");

            if (Commons.isNotNull(mVenues))
                sortList(mVenues);
            else
                finish();
        } else if (getIntent().hasExtra(EXTRA_EXPLORE)) {
            toolbar.setTitle(getIntent().getStringExtra("HEADER"));
            ArrayList<ExploreItem> results = getIntent().getParcelableArrayListExtra(EXTRA_EXPLORE);
            mVenues = new ArrayList<>();
            for (ExploreItem item : results) {
                mVenues.add(item.getVenue());
            }
            sortList(mVenues);
        } else
            finish();

        // show fab (normally hidden when used in other views)
        fabMapAll.setVisibility(View.VISIBLE);
        ViewUtils.changeFabColour(this, fabMapAll, R.color.md_green_600);

        scrollHider(recyclerView, fab);
        scrollHider(recyclerView, fabMapAll);
    }


    private void setupUI(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // remove scroll flags... want toolbar to be visible always for this activity
        removeToolBarFlags(toolbar);

        // Set the padding to match the Status Bar height
//        if (C.HAS_L)
//            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private void sortList(ArrayList<VenueResult> venues) {
        GeoPoint point = getLastLocation();

        if (Commons.isNotNull(point)) {
            for (VenueResult venue : venues) {
                me.carc.btown.data.all4squ.entities.Location toWhere = venue.getLocation();
                double d = MapUtils.getDistance(point, toWhere.getLat(), toWhere.getLng());
                venue.getLocation().setDistance(d);
                venue.getLocation().setDistanceFormatted(MapUtils.getFormattedDistance(d));
            }
            Collections.sort(venues, new DistanceComparator());
        }
        setupRecyclerView(venues);
    }


    public static class DistanceComparator implements Comparator<VenueResult>, Serializable {

        @Override
        public int compare(VenueResult lhs, VenueResult rhs) {
            Double d1 = lhs.getLocation().getDistance();
            Double d2 = rhs.getLocation().getDistance();
            if (d1.compareTo(d2) < 0) {
                return -1;
            } else if (d1.compareTo(d2) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private void setupRecyclerView(ArrayList<VenueResult> items) {
        if (Commons.isNotNull(items)) {

            recyclerView.setLayoutManager(new MyCustomLayoutManager(recyclerView.getContext()));

            final FsqSearchResultAdapter adapter = new FsqSearchResultAdapter(items);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            // Is this needed
            final RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(this,
                    recyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    progressLayout.setVisibility(View.VISIBLE);


                    final VenueResult venue = mVenues.get(position);

                    FourSquareApi service = FourSquareServiceProvider.get();
                    Call<FourSquResult> listsCall = service.getVenueDetails(venue.getId());

                    listsCall.enqueue(new Callback<FourSquResult>() {
                        @SuppressWarnings({"ConstantConditions"})
                        @Override
                        public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                            FourSquResult body = response.body();
                            VenueResult resp = body.getResponse().getVenueResult();

                            if (Commons.isNotNull(resp)) {
                                Intent intent = new Intent(FourSquareSearchResultActivity.this, VenueTabsActivity.class);
                                intent.putExtra(VenueTabsActivity.EXTRA_VENUE, (Parcelable) resp);
                                startActivityForResult(intent, RESULT_SHOW_ITEM);
                                progressLayout.setVisibility(View.GONE);
                            } else
                                showError();
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                            showError();
                        }
                    });
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
        }

        progressLayout.setVisibility(View.GONE);
    }

    @AfterPermissionGranted(C.PERMISSION_LOCATION)
    @SuppressWarnings({"MissingPermission"})
    private GeoPoint getLastLocation() {

        Location location = null;

        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null)
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location != null ? new GeoPoint(location.getLatitude(), location.getLongitude()) : null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SHOW_ITEM:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(VenueTabsActivity.EXTRA_VENUE)) {
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
                break;
            default:
        }
    }

    private void showError() {
        progressLayout.setVisibility(View.GONE);
        if (Commons.isNetworkAvailable(this))
            Commons.Toast(this, R.string.network_not_available_error, Color.RED, Toast.LENGTH_SHORT);
        else
            Commons.Toast(this, R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!db.getBoolean(SHOWN_FIRST_LAUNCH) && mVenues.size() > 0) {
            recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    helpShowcase();
                    return true;
                }
            });
        }
    }

    /**
     * Show the help and what each fab/button does
     */
    private void helpShowcase() {

        int borderColor = ContextCompat.getColor(this, R.color.colorAccent);
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        View view = ((FsqSearchResultAdapter.FsqExploreViewHolder) recyclerView.findViewHolderForAdapterPosition(0)).getView();

        final FancyShowCaseView items = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_fsq_explore_item))
                .fitSystemWindows(false)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(view)
                .build();

        final FancyShowCaseView addAll = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_fsq_explore_map_all))
                .fitSystemWindows(false)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fabMapAll)
                .build();

        final FancyShowCaseView close = new FancyShowCaseView.Builder(this)
                .title(getString(R.string.help_fsq_explore_close))
                .fitSystemWindows(false)
                .focusBorderColor(borderColor)
                .focusBorderSize(5)
                .enterAnimation(enterAnimation)
                .focusOn(fab)
                .build();

        new FancyShowCaseQueue()
                .add(items)
                .add(addAll)
                .add(close)
                .show();

        db.putBoolean(SHOWN_FIRST_LAUNCH, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fsq_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_all:
                getIntent().putParcelableArrayListExtra(EXTRA_EXPLORE, mVenues);
                setResult(RESULT_OK, getIntent()); // todo this isn't correct info yet (need to get to an interview :/ )
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);

        ViewUtils.hideView(fab, duration, (int) temp);
        ViewUtils.hideView(fabMapAll, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }
}