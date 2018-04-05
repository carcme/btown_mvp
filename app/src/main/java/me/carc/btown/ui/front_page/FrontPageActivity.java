package me.carc.btown.ui.front_page;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.SplashActivity;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.extras.BackgroundImageDialog;
import me.carc.btown.map.MapActivity;
import me.carc.btown.ui.base.MvpBaseActivity;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by Carc.me on 03.08.16.
 * <p/>
 * Display a menu screen at start up
 */
public class FrontPageActivity extends MvpBaseActivity implements FrontPageMvpView, BackgroundImageDialog.BgImageDialogListener {

    @Override
    public void onImageChanged() {
        mPresenter.setupHeader();
    }

    public interface ClickListener {
        void onClick(MenuItem item);
    }

    private static final String TAG = FrontPageActivity.class.getName();
    public static final int RESULT_NONE = 0;
    public static final int RESULT_SETTINGS = 41;
    public static final int RESULT_FSQ = 42;
    public static final int RESULT_GETTING_ROUND = 43;

    @Inject
    FrontPagePresenter mPresenter;
    @Inject
    FrontPageAdapter mAdapter;

    @BindView(R.id.detailsAppBar)
    AppBarLayout detailsAppBar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.profileImage)
    ImageView profileImage;

    @BindView(R.id.detailsToolbar)
    Toolbar detailsToolbar;

    @BindView(R.id.detailsRecycleView)
    RecyclerView mRecyclerView;

//    @BindView(R.id.profileFab)
//    FloatingActionButton profileFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.front_page);
        ButterKnife.bind(this);

        setSupportActionBar(detailsToolbar);
        if (getSupportActionBar() != null) {
            setSupportActionBar(detailsToolbar);
            detailsToolbar.setNavigationIcon(R.mipmap.ic_launcher_skyline_rnd_blue);
        }

        mRecyclerView.setAdapter(mAdapter);


        int orient = getResources().getConfiguration().orientation;
        switch (orient) {
            case ORIENTATION_LANDSCAPE:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                break;
            case ORIENTATION_PORTRAIT:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                break;
            default:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                break;
        }

        mPresenter.attachView(this);
        mPresenter.setupHeader();
        mPresenter.buildMenuItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("lang")) {
                        restart();
                    }
                }
                break;

            case RESULT_FSQ:
                // TODO: 19/12/2017  dont use this - go straight to displaying fsq places on the map
                if (resultCode == RESULT_OK) {
                    data.setClass(FrontPageActivity.this, MapActivity.class);
                    setResult(RESULT_OK, data);
                    startActivity(data);
                }
                break;
            default:
        }
    }


    private void restart() {
        finish();
        startActivity(new Intent(this, SplashActivity.class));
    }


    /***** MVP View methods implementation *****/

    @Override
    public void setupHeader(@DrawableRes int imageRes) {

        profileImage.setImageResource(imageRes);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundImageDialog.showInstance(getApplicationContext());
            }
        });

        detailsToolbar.setTitle(R.string.app_name);

        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle(getString(R.string.app_name));
        collapsingToolbar.setExpandedTitleTypeface(Typeface.SERIF);

        ViewUtils.setViewHeight(detailsAppBar, C.SCREEN_HEIGHT / 3, true);
        ViewUtils.setViewHeight(profileImage, C.SCREEN_HEIGHT / 3, false);
    }

    @Override
    public void loadFrontPageMenu(List<MenuItem> items) {

        mAdapter.setData(items, new ClickListener() {
            @Override
            public void onClick(MenuItem item) {
                Intent intent = new Intent(FrontPageActivity.this, item.getStartActivityClass());
                if(item.getResultKey() == RESULT_NONE)
                    startActivity(intent);
                else
                    startActivityForResult(intent, item.getResultKey());
            }
        });

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
    }
}
