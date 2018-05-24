package me.carc.btown.tours.top_pick_lists;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import me.carc.btown.App;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.RecyclerClickListener;
import me.carc.btown.common.interfaces.RecyclerTouchListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.ItemsListItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.tours.top_pick_lists.adapters.ListDetailsAdapter;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FourSquareListDetailsActivity extends BaseActivity {

    private static final String TAG = FourSquareListDetailsActivity.class.getName();
    public static final int RESULT_SHOW_ITEM = 148;

    @BindView(R.id.catalogue_recycler) RecyclerView recyclerView;
    @BindView(R.id.toursToolbar) Toolbar toolbar;
    @BindView(R.id.inventoryProgressBar) ProgressBar progressLayout;
    @BindView(R.id.appBarProgressBar) ProgressBar appBarProgressBar;
    @BindView(R.id.fab) FloatingActionButton fab;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_fsq_main_recycler);
        ButterKnife.bind(this);

        setupUI(savedInstanceState);

        if (getIntent().hasExtra(FourSquareListsActivity.EXTRA_LISTS)) {
            ListItems items = getIntent().getParcelableExtra(FourSquareListsActivity.EXTRA_LISTS);
            toolbar.setTitle(getIntent().getStringExtra(FourSquareListsActivity.EXTRA_TITLE));
            sortList(items.getItemsListItems());
        } else
            finish();

        scrollHider(recyclerView, fab);
    }

    @OnClick(R.id.fab)
    void done() {
        onBackPressed();
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

        removeToolBarFlags(toolbar);

        // Set the padding to match the Status Bar height
//        if (C.HAS_L)
//            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private void sortList(ArrayList<ItemsListItem> items) {
        GeoPoint point = getLastLocation();

        if (Commons.isNotNull(point)) {
            for (ItemsListItem item : items) {
                me.carc.btown.data.all4squ.entities.Location toWhere = item.getVenue().getLocation();
                double d = MapUtils.getDistance(point, toWhere.getLat(), toWhere.getLng());
                item.getVenue().getLocation().setDistance(d);
                item.getVenue().getLocation().setDistanceFormatted(MapUtils.getFormattedDistance(d));
            }
            Collections.sort(items, new DistanceComparator());
        }
        setupRecyclerView(items, point);
    }


    public static class DistanceComparator implements Comparator<ItemsListItem>, Serializable {

        @Override
        public int compare(ItemsListItem lhs, ItemsListItem rhs) {
            Double d1 = lhs.getVenue().getLocation().getDistance();
            Double d2 = rhs.getVenue().getLocation().getDistance();
            if (d1.compareTo(d2) < 0) {
                return -1;
            } else if (d1.compareTo(d2) > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private void setupRecyclerView(ArrayList<ItemsListItem> items, GeoPoint mapLocation) {
        if (Commons.isNotNull(items)) {

            recyclerView.setLayoutManager(new MyCustomLayoutManager(recyclerView.getContext()));

            final ListDetailsAdapter adapter = new ListDetailsAdapter(items, mapLocation);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            // Is this needed
            final RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                    recyclerView, new RecyclerClickListener() {
                @Override
                public void onClick(View view, int position) {

                    progressLayout.setVisibility(View.VISIBLE);

                    ListItems items = getIntent().getParcelableExtra(FourSquareListsActivity.EXTRA_LISTS);
                    final ItemsListItem item = items.getItemsListItems().get(position);

                    FourSquareApi service = FourSquareServiceProvider.get();
                    Call<FourSquResult> listsCall = service.getVenueDetails(item.getVenue().getId());

                    listsCall.enqueue(new Callback<FourSquResult>() {
                        @SuppressWarnings({"ConstantConditions"})
                        @Override
                        public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                            FourSquResult body = response.body();
                            if (Commons.isNotNull(body) && Commons.isNotNull(body.getResponse())) {
                                VenueResult resp = body.getResponse().getVenueResult();

                                if (Commons.isNotNull(resp)) {
                                    Intent intent = new Intent(FourSquareListDetailsActivity.this, VenueTabsActivity.class);
                                    intent.putExtra(VenueTabsActivity.EXTRA_VENUE_URL, item.getVenue().getVenueUrl());
                                    intent.putExtra(VenueTabsActivity.EXTRA_VENUE, (Parcelable) resp);
                                    startActivityForResult(intent, RESULT_SHOW_ITEM);
                                    progressLayout.setVisibility(View.GONE);
                                } else
                                    showError();
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
                public void onLongClick(View view, int position) {
                }
            }));
        }

        progressLayout.setVisibility(View.GONE);
    }

    @AfterPermissionGranted(C.PERMISSION_LOCATION)
    @SuppressWarnings({"MissingPermission"})
    private GeoPoint getLastLocation() {
        Location location = ((App) getApplication()).getLatestLocation();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fsq_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_all:
                setResult(RESULT_OK, getIntent());
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }
}