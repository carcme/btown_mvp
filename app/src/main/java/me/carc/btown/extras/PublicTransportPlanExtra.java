package me.carc.btown.extras;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.DrawableClickListener;
import me.carc.btown.extras.adapters.TransportPlanSelectionAdapter;
import me.carc.btown.extras.bahns.Bahns;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.ui.custom.DividerItemDecoration;
import me.carc.btown.ui.custom.MyCustomLayoutManager;

public class PublicTransportPlanExtra extends BaseActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private static final int RESULT_OPENED = 789;
    public static final String ANSWERS_PLAN_OPEN= "U_S_BAHN_PLAN_OPENED";

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

        setupRecycler();
    }

    private void setupRecycler() {

        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mAdapter = new TransportPlanSelectionAdapter(new DrawableClickListener() {

            @Override
            public void OnClick(View v, Drawable drawable, int pos) {
                Bahns.BahnItem item = mAdapter.getItem(pos);

                Intent intent = new Intent(PublicTransportPlanExtra.this, PublicTransportPlan.class);
                intent.putExtra("MAP_ID", item.getFileName());
                intent.putExtra("MAP_DESC", item.getDescriptionResourceId());
                startActivityForResult(intent, RESULT_OPENED);
            }

            @Override
            public void OnLongClick(View v, int pos) {
            }
        });

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
                recyclerView.addItemDecoration(itemDecoration);
            }
            recyclerView.setAdapter(mAdapter);
        }

        progressCenter.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
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
                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // update the entry. Could do it by index but why bother
        mAdapter.notifyDataSetChanged();
    }
}
