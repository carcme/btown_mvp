package me.carc.btown.tours;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.App;
import me.carc.btown.BaseActivity;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.Holder;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.common.viewHolders.CatalogueViewHolder;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.tours.adapters.ToursAdapter;
import me.carc.btown.ui.custom.MyCustomLayoutManager;

public class CatalogueActivity extends BaseActivity {
    private static final String TAG = CatalogueActivity.class.getName();

    public static final String CATALOGUE = "SELECTED_CATALOGUE";
    public static final String CATALOGUE_INDEX = "SELECTED_CATALOGUE_INDEX";

    public static final String SERVER_FILE = "SERVER_FILE";
    public static final String JSON_VERSION = "JSON_VERSION";
    public static final String LAST_JSON_UPDATE = "LAST_JSON_UPDATE";
    public static final String EXTRA_SHOW_ON_MAP = "EXTRA_SHOW_ON_MAP";

    private ToursAdapter mAdapter;

    @BindView(R.id.toursToolbar) Toolbar toolbar;
    @BindView(R.id.tourProgressBar) ContentLoadingProgressBar progressLayout;
    @BindView(R.id.catalogue_recycler) RecyclerView recyclerView;
    @BindView(R.id.fabExit) FloatingActionButton fabExit;


    @BindView(R.id.downloadLayout) RelativeLayout downloadLayout;

    @OnClick(R.id.fabExit)
    void done() {
        onBackPressed();
    }

    @OnClick(R.id.startDownloadBtn)
    void downloadTours() {
        ((App) getApplication()).getFirebaseTours();
        showDownloadingDialog();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_catalogue_activity);
        ButterKnife.bind(this);

        // set up UI and collections
        setupUI();
        setupRecycler();
    }

    private void setupUI() {
        toolbar.setTitle(R.string.tours_tour_catalogue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabExit.setVisibility(View.VISIBLE);

        // Set the padding to match the Status Bar height
        if (C.HAS_L)
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupRecycler() {
        mAdapter = new ToursAdapter(isGermanLanguage());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
        } else {
            MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }
        recyclerView.setAdapter(mAdapter);
        scrollHider(recyclerView, fabExit);

        TourViewModel tourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
        tourViewModel.getAllTours().observe(this, new Observer<List<TourCatalogueItem>>() {
            @Override
            public void onChanged(@Nullable final List<TourCatalogueItem> tours) {
                if(Commons.isNotNull(tours)) {
                    if (tours.size() == 0)
                        downloadLayout.setVisibility(View.VISIBLE);
                    mAdapter.setTours(tours);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new OnItemSelectedListener(this) {

            public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                CatalogueViewHolder viewHolder = (CatalogueViewHolder) holder;
                if (((App) getApplication()).isUpdatingFirebase()) {
                    showDownloadingDialog();
                    return;
                }

                TourCatalogueItem catalogue = mAdapter.getItem(pos);

                // show tour on map?
                if (getIntent().hasExtra(EXTRA_SHOW_ON_MAP)) {
                    if (BuildConfig.USE_CRASHLYTICS) {
                        Answers.getInstance().logCustom(new CustomEvent("Show Tours on Map"));
                        Crashlytics.log(TAG + " : onItemSelected() <show tour on map>");
                    }

                    getIntent().putExtra(CATALOGUE_INDEX, catalogue.getTourId());
                    setResult(RESULT_OK, getIntent());
                    onBackPressed();
                } else {  // show tour in tabs and pager
                    if (BuildConfig.USE_CRASHLYTICS) {
                        Answers.getInstance().logCustom(new CustomEvent("Show Tours on Map"));
                        Crashlytics.log(TAG + " : onItemSelected() <show tour>");
                    }
                    Holder.set(viewHolder.catalogueImage.getDrawable());
                    Intent intent = new Intent(CatalogueActivity.this, CataloguePreviewActivity.class);
                    intent.putExtra(CATALOGUE_INDEX, catalogue.getTourId());

                    Bundle options = null;
                    if (C.HAS_L)
                        options = ActivityOptions.makeSceneTransitionAnimation(
                                CatalogueActivity.this,
                                viewHolder.card,
                                getString(R.string.image_pop_transition)
                        ).toBundle();
                    startActivity(intent, options);
                }
            }
        });
    }


    private void showDownloadingDialog() {
        int title = R.string.shared_string_offline;
        int msg = R.string.check_network;

        if (BuildConfig.USE_CRASHLYTICS)
            Answers.getInstance().logCustom(new CustomEvent("Dowloading Tours"));

        if(((App)getApplication()).isNetworkAvailable()) {
           title = R.string.getting_tours;
           msg = R.string.getting_tours_desc;
        }

        AlertDialog.Builder dlg = new AlertDialog.Builder(CatalogueActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dlg.show();
    }

    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);
        ViewUtils.hideView(fabExit, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }
}