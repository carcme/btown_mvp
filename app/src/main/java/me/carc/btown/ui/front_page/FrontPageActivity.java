package me.carc.btown.ui.front_page;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.SplashActivity;
import me.carc.btown.Utils.ForceUpdateChecker;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.extras.BackgroundImageDialog;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.ui.RatingDialog;
import me.carc.btown.ui.base.MvpBaseActivity;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by Carc.me on 03.08.16.
 * <p/>
 * Display a menu screen at start up
 */
public class FrontPageActivity extends MvpBaseActivity implements
        FrontPageMvpView,
        BackgroundImageDialog.BgImageDialogListener,
        ForceUpdateChecker.OnUpdateNeededListener{

    @Override
    public void onImageChanged() {
        mPresenter.setupHeader();
    }

    private static final String TAG = FrontPageActivity.class.getName();
    public static final int RESULT_NONE = 0;
    public static final int RESULT_SETTINGS = 41;
    public static final int RESULT_GETTING_ROUND = 43;

    private static final String LAST_UPDATE_TIME = "UPDATE_ASK_TIME";
    private static final String NEVER_UPDATE = "NEVER_UPDATE";

    @Inject FrontPagePresenter mPresenter;
    @Inject FrontPageAdapter mAdapter;

    @BindView(R.id.detailsAppBar) AppBarLayout detailsAppBar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.profileImage) ImageView profileImage;
    @BindView(R.id.detailsToolbar) Toolbar detailsToolbar;
    @BindView(R.id.detailsRecycleView) RecyclerView mRecyclerView;

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
    protected void onResume() {
        super.onResume();

        boolean neverUpdate = TinyDB.getTinyDB().getBoolean(NEVER_UPDATE);
        long updateTime = TinyDB.getTinyDB().getLong(LAST_UPDATE_TIME, 0);

        if(!neverUpdate && System.currentTimeMillis() > updateTime) {
            TinyDB.getTinyDB().putLong(LAST_UPDATE_TIME, System.currentTimeMillis() + C.TIME_ONE_DAY);
            ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        } else
            showRatingDialog();
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

            default:
        }
    }


    private void restart() {
        finish();
        startActivity(new Intent(this, SplashActivity.class));
    }


    private void showRatingDialog() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .icon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher_rnd))
                .session(7)
                .threshold(3)
                .title(getString(R.string.rating_dialog_experience))
                .titleTextColor(R.color.black)
                .positiveButtonText("Not Now")
                .negativeButtonText("Never")
                .positiveButtonTextColor(R.color.negativeBtnTextColor)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint(getString(R.string.rating_dialog_suggestions))
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.rating_star)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        new SendFeedback(FrontPageActivity.this, SendFeedback.TYPE_FEEDBACK);
                        ratingDialog.dismiss();
                    }
                })
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
    }


    /***** MVP View methods implementation *****/

    @Override
    public void setupHeader(@DrawableRes int imageRes) {

        try { // Crashlytics #183 - resouce not found exception thrown :/ Moved the relevent res images to nodpi folder
            profileImage.setImageResource(imageRes);
        } catch ( Resources.NotFoundException e) {
            profileImage.setImageResource(R.drawable.background_museum_insel);
        }
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
    public void loadFrontPageMenu(final List<MenuItem> items) {
        mAdapter.setData(items);
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new OnItemSelectedListener(this) {
            @Override
            public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                MenuItem item = mAdapter.getItem(pos);
                if (BuildConfig.USE_CRASHLYTICS)
                    Answers.getInstance().logCustom(new CustomEvent("Front Page -> " + item.name()));

                Intent intent = new Intent(FrontPageActivity.this, item.getStartActivityClass());
                if (item.getResultKey() == RESULT_NONE)
                    startActivity(intent);
                else
                    startActivityForResult(intent, item.getResultKey());
            }
        });
    }

    @Override
    public void showError() {
    }

    /**
     * Split string on reg exp ( ¬ ) and show the dialog notification
     * @param updateDesc string to split
     */
    @Override
    public void onUpdateNeeded(String updateDesc) {

        StringBuilder desc = new StringBuilder(getString(R.string.shared_string_new_features));
        String[] split = updateDesc.split("¬");
        for(String str : split)
            desc.append(str).append("\n");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.update_title)
                .setMessage(desc)
                .setIcon(R.drawable.ic_system_update)
                .setPositiveButton(R.string.shared_string_update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                redirectStore();
                            }
                        })
                .setNegativeButton(R.string.rating_dialog_maybe_later, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setNeutralButton(R.string.shared_string_never_ask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TinyDB.getTinyDB().putBoolean(NEVER_UPDATE, true);
                        dialog.dismiss();
                    }
                })
                .create();


        dialog.show();
    }

    private void redirectStore() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playStoreLink)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
