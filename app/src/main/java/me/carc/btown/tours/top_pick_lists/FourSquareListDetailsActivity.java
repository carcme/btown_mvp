package me.carc.btown.tours.top_pick_lists;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.App;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.RecyclerClickListener;
import me.carc.btown.common.interfaces.RecyclerTouchListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.ItemsListItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.map.MapActivity;
import me.carc.btown.tours.top_pick_lists.adapters.ListDetailsAdapter;
import me.carc.btown.ui.custom.PreDrawLayoutManager;
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

        setupUI();

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

    private void setupUI() {
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
            return Integer.compare(d1.compareTo(d2), 0);
        }
    }

    private void launchVenue(String url, VenueResult venue) {
        Intent intent = new Intent(FourSquareListDetailsActivity.this, VenueTabsActivity.class);
        intent.putExtra(VenueTabsActivity.EXTRA_VENUE_URL, url);
        intent.putExtra(VenueTabsActivity.EXTRA_VENUE, (Parcelable) venue);
        startActivity(intent);
        progressLayout.setVisibility(View.GONE);
    }

    private void setupRecyclerView(ArrayList<ItemsListItem> items, GeoPoint mapLocation) {
        if (Commons.isNotNull(items)) {

            recyclerView.setLayoutManager(new PreDrawLayoutManager(recyclerView.getContext()));

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

                    String localVenue = db.getString(item.getVenue().getId());
                    if (!Commons.isEmpty(localVenue)) {
                        VenueResult venue = new Gson().fromJson(localVenue, new TypeToken<VenueResult>() {}.getType());
                        launchVenue(item.getVenue().getVenueUrl(), venue);
                    } else {
                        //Check if we have a Firebase version, if not query FSQ for data and then save to Firebase and SP

                        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("venues/" + item.getVenue().getId());
                        final File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), item.getVenue().getId());
                        imageRef.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // File exists on Firebase
                                        try {
                                            VenueResult venue = new Gson().fromJson(FileUtils.readFile(localFile),
                                                    new TypeToken<VenueResult>() {}.getType());

                                            launchVenue(item.getVenue().getVenueUrl(), venue);
                                            db.putString(venue.getId(), new Gson().toJson(venue));
                                        } catch (IOException e) {
                                            //You'll need to add proper error handling here
                                            Log.d(TAG, "File read erro");
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // File not present on Firebase - get from FSQ and save to Firebase and SP
                                FourSquareApi service = FourSquareServiceProvider.get();
                                Call<FourSquResult> listsCall = service.getVenueDetails(item.getVenue().getId());

                                listsCall.enqueue(new Callback<FourSquResult>() {
                                    @SuppressWarnings({"ConstantConditions"})
                                    @Override
                                    public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                                        FourSquResult body = response.body();
                                        if (Commons.isNotNull(body) && Commons.isNotNull(body.getResponse())) {
                                            VenueResult venue = body.getResponse().getVenueResult();

                                            if (Commons.isNotNull(venue)) {
                                                launchVenue(item.getVenue().getVenueUrl(), venue);
                                                uploadVenue(venue);

                                            } else if(response.raw().code() == 429) {
                                                new AlertDialog.Builder(FourSquareListDetailsActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                                                        .setTitle(R.string.error_fsq_quote_title)
                                                        .setMessage(R.string.error_fsq_quote_reached)
                                                        .setIcon(android.R.drawable.stat_sys_warning)
                                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .show();
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
                        });
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                }
            }));
        }

        progressLayout.setVisibility(View.GONE);
    }


    private void uploadVenue(final VenueResult venue) {

        Gson gson = new Gson();
        final String json = gson.toJson(venue);

        final File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), venue.getId());
        Uri uploadUri = Uri.fromFile(localFile);
        try {
            // write json data to file
            Writer output = new BufferedWriter(new FileWriter(localFile));
            output.write(json);
            output.close();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/json")
                    .setCustomMetadata("venueID", venue.getId())
                    .setCustomMetadata("venueName", venue.getName())
                    .build();
            UploadTask uploadTask = storageRef.child("venues/" + uploadUri.getLastPathSegment()).putFile(uploadUri, metadata);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "onFailure: " + exception.getMessage());
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // always save local version
                    db.putString(venue.getId(), json);
                }
            });

        } catch (Exception ignore) { }
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
//                setResult(RESULT_OK, getIntent());

                getIntent().setClass(this, MapActivity.class);
//                setResult(RESULT_OK, getIntent());
                startActivity(getIntent());


  //              finish();
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