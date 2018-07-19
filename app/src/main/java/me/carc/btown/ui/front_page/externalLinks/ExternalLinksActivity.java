package me.carc.btown.ui.front_page.externalLinks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.interfaces.RecyclerClickListener;
import me.carc.btown.common.interfaces.RecyclerTouchListener;
import me.carc.btown.map.sheets.share.ShareMenu;
import me.carc.btown.ui.base.MvpBaseActivity;

public class ExternalLinksActivity extends MvpBaseActivity implements ExternalLinksMvpView {
    private static final String TAG = ExternalLinksActivity.class.getName();

    @Inject
    ExternalLinksPresenter mPresenter;
    @Inject
    ExternalLinksAdapter mAdapter;

    @BindView(R.id.toursToolbar) Toolbar toolbar;
    @BindView(R.id.tourProgressBar) ContentLoadingProgressBar progressLayout;
    @BindView(R.id.catalogue_recycler) RecyclerView mRecyclerView;
    @BindView(R.id.fabExit) FloatingActionButton fabExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.tours_catalogue_activity);

        ButterKnife.bind(this);
        setupUI();

        HeaderDecoration.Builder  builder = new HeaderDecoration.Builder(this)
                .inflate(R.layout.external_items_header)
                .columns(1)
                .parallax(.5f)
                .dropShadowDp(40);
        mRecyclerView.addItemDecoration(builder.build());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scrollHider(mRecyclerView, fabExit);

        mPresenter.attachView(this);
        mPresenter.buildMenuItems();
    }

    private void setupUI() {
        toolbar.setVisibility(View.GONE);
/*
        toolbar.setTitle(R.string.external_links);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/

        fabExit.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            default:
        }
    }

    @OnClick(R.id.fabExit)
    public void exit() {
        onBackPressed();
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

    /***** MVP View methods implementation *****/

    @Override
    public void loadFrontPageMenu(final List<ExternalLinkItem> items) {
        mAdapter.setData(items);
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, final int pos) {
                ExternalLinkItem item = mAdapter.getItem(pos);
                if (BuildConfig.USE_CRASHLYTICS)
                    Answers.getInstance().logCustom(new CustomEvent("ExternalLink: " + item.name()));

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(item.getLinkResourceId())));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int pos) {
                ExternalLinkItem item = mAdapter.getItem(pos);

                String title = getString(item.getTitleResourceId());
                String address = getString(item.getLinkResourceId());
                ShareMenu.show(null, title, address, getApplicationContext());
            }
        }));


/*
        mRecyclerView.addOnItemTouchListener(new OnItemSelectedListener(this) {


            @Override
            public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                ExternalLinkItem item = mAdapter.getItem(pos);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(item.getLinkResourceId())));
                startActivity(i);
            }
        });
*/
    }

    @Override
    public void showError() {
    }
}