package me.carc.btown.extras;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.extras.adapters.TransportPlanSelectionAdapter;
import me.carc.btown.extras.bahns.Bahns;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.ui.custom.DividerItemDecoration;
import me.carc.btown.ui.custom.PreDrawLayoutManager;

public class PublicTransportPlanExtra extends BaseActivity {

    private static final String TAG = PublicTransportPlanExtra.class.getName();
    private static final int RESULT_OPENED = 789;
    public static final String ANSWERS_PLAN_OPEN = "U_S_BAHN_PLAN_OPENED";

    public static final String FIREBASE_DIR = "resource";

    private TransportPlanSelectionAdapter mAdapter;

    @BindView(R.id.catalogue_recycler)
    RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.transportMapView)
    SubsamplingScaleImageView imageView;

    @BindView(R.id.toursToolbar)
    Toolbar toolbar;

    @BindView(R.id.inventoryProgressBar)
    ContentLoadingProgressBar progressCenter;

    @BindView(R.id.appBarProgressBar)
    ProgressBar appBarProgressBar;

    @BindView(R.id.fabExit)
    FloatingActionButton fabExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_catalogue_activity);
        ButterKnife.bind(this);

//        new Bahns().removeCachedFiles();

        setupUI(savedInstanceState);
    }

    private void setupUI(Bundle savedInstanceState) {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.menu_train_route_map));
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabExit.setVisibility(View.VISIBLE);

        setupRecycler();
    }

    private void setupRecycler() {
        mAdapter = new TransportPlanSelectionAdapter();

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                PreDrawLayoutManager layoutManager = new PreDrawLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
                recyclerView.addItemDecoration(itemDecoration);
            }
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnScrollListener(onScrollListener);

            recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            recyclerView.addOnItemTouchListener(new OnItemSelectedListener(this) {

                @Override
                public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                    Bahns.BahnItem item = mAdapter.getItem(pos);

                    Intent intent = new Intent(PublicTransportPlanExtra.this, PublicTransportPlan.class);
                    intent.putExtra("MAP_ID", item.getFileName());
                    intent.putExtra("MAP_DESC", item.getDescriptionResourceId());
                    startActivityForResult(intent, RESULT_OPENED);
                }
            });
        }

        progressCenter.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0)
                fabExit.hide();
            else
                fabExit.show();
            ;
        }
    };

    @OnClick(R.id.fabExit)
    void fabBack() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_feedback:
                new SendFeedback(this, SendFeedback.TYPE_FEEDBACK);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // update the entry. Could do it by index but why bother
        mAdapter.notifyDataSetChanged();
    }
}
