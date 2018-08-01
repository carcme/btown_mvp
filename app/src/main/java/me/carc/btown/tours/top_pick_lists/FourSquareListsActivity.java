package me.carc.btown.tours.top_pick_lists;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.BtownListsResult;
import me.carc.btown.data.all4squ.entities.GroupsList;
import me.carc.btown.data.all4squ.entities.ItemsUserList;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.ListResult;
import me.carc.btown.tours.top_pick_lists.adapters.ListsAdapter;
import me.carc.btown.ui.custom.PreDrawLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FourSquareListsActivity extends BaseActivity {

    private static final String TAG = FourSquareListsActivity.class.getName();
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_LISTS = "EXTRA_LISTS";
    public static final int RESULT_SHOW_MAP_ALL = 147;

    private ListsAdapter mAdapter;

    @BindView(R.id.catalogue_recycler) RecyclerView recyclerView;
    @BindView(R.id.toursToolbar) Toolbar toolbar;
    @BindView(R.id.inventoryProgressBar) ProgressBar progressLayout;
    @BindView(R.id.fab) FloatingActionButton fab;

    @OnClick(R.id.fab) void done() {
        onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_fsq_main_recycler);
        ButterKnife.bind(this);

        // set up UI and collections
        setupUI(savedInstanceState);

        supportStartPostponedEnterTransition();

        // Display the collections
        getFourSquareLists();
    }

    private void setupUI(Bundle savedInstanceState) {
        toolbar.setTitle("Top Picks");
//        getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_getting_around)));

        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_lists));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_lists_dark));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set the padding to match the Status Bar height
        if (C.HAS_L)
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private void getFourSquareLists() {
        setProgressItems(View.VISIBLE);

        FourSquareApi listsService = FourSquareServiceProvider.get();
        Call<FourSquResult> listsCall = listsService.getBTownLists(BuildConfig.FOURSQUARE_BTOWN_LISTS_ID);

        listsCall.enqueue(new Callback<FourSquResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {
                FourSquResult body = response.body();
                if (Commons.isNotNull(body)) {
                    BtownListsResult lists = body.getResponse().getBtownListsResult();

                    if (lists.getCount() > 0) {
                        Log.d(TAG, "onResponse: ");

                        for (GroupsList grp : lists.getGroupsLists()) {
                            if (grp.getType().equals("created")) {
                                Log.d(TAG, "onResponse: FOUND THE RIGHT ONE");
                                setupRecycler(grp.getItemsUserLists());
                            }
                        }
                    } else
                        showError();
                } else
                    showError();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: CANCELLED");
                } else
                    showError();
            }
        });
    }

    private void showError() {
        progressLayout.setVisibility(View.GONE);
        if (Commons.isNetworkAvailable(FourSquareListsActivity.this))
            Commons.Toast(this, R.string.network_not_available_error, Color.RED, Toast.LENGTH_SHORT);
        else
            Commons.Toast(this, R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
    }

    private void launchVenueList(String title, ListItems venueList) {
        Intent intent = new Intent(FourSquareListsActivity.this, FourSquareListDetailsActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_LISTS, (Parcelable) venueList);
        startActivityForResult(intent, RESULT_SHOW_MAP_ALL);
        setProgressItems(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupRecycler(ArrayList<ItemsUserList> lists) {
        mAdapter = new ListsAdapter(lists);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                PreDrawLayoutManager layoutManager = new PreDrawLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
            }
            recyclerView.setAdapter(mAdapter);
            scrollHider(recyclerView, fab);
            setProgressItems(View.GONE);

            recyclerView.addOnItemTouchListener(new OnItemSelectedListener(this) {

                @Override
                public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                    setProgressItems(View.VISIBLE);

                    final ItemsUserList item = mAdapter.getItem(pos);

                    String venueList = db.getString(item.getId());
                    if (!Commons.isEmpty(venueList)) {
                        ListItems items = new Gson().fromJson(venueList, new TypeToken<ListItems>() {}.getType());
                        launchVenueList(item.getName(), items);
                    } else {
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("venue_lists/" + item.getId());
                        final File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), item.getId());
                        imageRef.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // List exists on Firebase
                                        try {
                                            ListItems venue = new Gson().fromJson(FileUtils.readFile(localFile),
                                                    new TypeToken<ListItems>() {}.getType());

                                            launchVenueList(item.getName(), venue);
                                            db.putString(item.getId(), new Gson().toJson(venue));
                                        } catch (IOException e) {
                                            //You'll need to add proper error handling here
                                            Log.d(TAG, "File read erro");
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        FourSquareApi service = FourSquareServiceProvider.get();
                                        Call<FourSquResult> listsCall = service.getListDetails(item.getId());

                                        listsCall.enqueue(new Callback<FourSquResult>() {
                                            @SuppressWarnings({"ConstantConditions"})
                                            @Override
                                            public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {
                                                FourSquResult body = response.body();
                                                if (Commons.isNotNull(body)) {
                                                    ListResult resp = body.getResponse().getListResult();

                                                    if (Commons.isNotNull(resp) && resp.getListItems().getCount() > 0) {
                                                        if (BuildConfig.USE_CRASHLYTICS)
                                                            Answers.getInstance().logCustom(new CustomEvent("TopPicks " + item.getName()));

                                                        uploadVenueList(resp);
                                                        launchVenueList(item.getName(), resp.getListItems());

                                                    } else
                                                        showError();
                                                } else
                                                    showError();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                                                Log.d(TAG, "onFailure: ");
                                                showError();
                                            }
                                        });
                                    }
                        });
                    }
                }
            });
        }
    }


    private void uploadVenueList(final ListResult venueList) {

        Gson gson = new Gson();
        final String json = gson.toJson(venueList.getListItems());

        final File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), venueList.getId());
        Uri uploadUri = Uri.fromFile(localFile);
        try {
            // write json data to file
            Writer output = new BufferedWriter(new FileWriter(localFile));
            output.write(json);
            output.close();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/json")
                    .setCustomMetadata("listID", venueList.getId())
                    .setCustomMetadata("listName", venueList.getName())
                    .setCustomMetadata("listDescription", venueList.getDescription())
                    .build();
            UploadTask uploadTask = storageRef.child("venue_lists/" + uploadUri.getLastPathSegment()).putFile(uploadUri, metadata);

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
                    db.putString(venueList.getId(), json);
                }
            });

        } catch (Exception ignore) {
        }
    }


    private void setProgressItems(int vis) {
        progressLayout.setVisibility(vis);
    }


    /**
     * Handle Intent returns
     *
     * @param requestCode the request code
     * @param resultCode  result code
     * @param data        the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SHOW_MAP_ALL:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
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