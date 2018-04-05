package me.carc.btown.tours;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.login.LoginActivity;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;
import me.carc.btown.tours.model.TourHolderResult;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;

public class ToursLaunchActivity extends BaseActivity {

    public static final int RESULT_LOGIN = 156;
    public static final int RESULT_FSQ = 157;

    public static final int TOURS = 0;
    public static final int TOP10 = 1;
    public static final int LOGIN = 2;
    public static final int EXIT = 3;

    public static TourHolderResult jsonPreLoad;
    private Animation animation;

    @BindView(R.id.backgroundHolder)
    RelativeLayout backgroundHolder;

    @BindView(R.id.tours_holder)
    RelativeLayout tours_holder;
    @BindView(R.id.tours_icon)
    ImageView tours_icon;

    @BindView(R.id.top10_holder)
    RelativeLayout top10_holder;
    @BindView(R.id.top10_icon)
    ImageView top10_icon;

    @BindView(R.id.login_holder)
    RelativeLayout login_holder;

    @BindView(R.id.loginIcon)
    ImageView loginIcon;

    @BindView(R.id.preLoadProgress)
    ProgressBar preLoadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_launch_activity);
        ButterKnife.bind(this);

        Drawable icon = new IconicsDrawable(ToursLaunchActivity.this, FontAwesome.Icon.faw_map_signs).color(Color.WHITE).sizeDp(60);
        tours_icon.setImageDrawable(icon);

        icon = new IconicsDrawable(ToursLaunchActivity.this, CommunityMaterial.Icon.cmd_lightbulb_on).color(Color.WHITE).sizeDp(60);
        top10_icon.setImageDrawable(icon);


        FirebaseAuth.getInstance().addAuthStateListener(stateListener);
        preloadTourCatalogues();
    }

    private FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            Profile facebookUser = Profile.getCurrentProfile();

            // bug fix - gets called twice for some reason
            if (Commons.isNull(firebaseUser) && Commons.isNull(facebookUser)) {
                startActivityForResult(new Intent(ToursLaunchActivity.this, LoginActivity.class), RESULT_LOGIN);
            } else {
                showLoggedInIcon();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOGIN:
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Profile facebookUser = Profile.getCurrentProfile();
                if (Commons.isNotNull(firebaseUser) || Commons.isNotNull(facebookUser)) {
                    Intent getImagesIntent = new Intent(this, FirebaseImageDownloader.class);
                    startService(getImagesIntent);
                    showLoggedInIcon();
                } else {
                    finish();
                }
                break;

            case RESULT_FSQ:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            default:
        }
    }

    private void showLoggedInIcon() {
        Drawable icon = new IconicsDrawable(ToursLaunchActivity.this, CommunityMaterial.Icon.cmd_account_check).color(0xFF669900).sizeDp(20);
        loginIcon.setImageDrawable(icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flyIn();
    }

    Handler handler = new Handler();

    private final Runnable preLoadRunner = new Runnable() {
        int progrss = 1;

        public void run() {
            try {
                preLoadProgress.setProgress(progrss++);
                handler.postDelayed(this, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void preloadTourCatalogues() {
        handler.postDelayed(preLoadRunner, 10);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final Gson gson = new Gson();
                String json = TinyDB.getTinyDB().getString(CatalogueActivity.SERVER_FILE, null);
                if (Commons.isNotNull(json))
                    jsonPreLoad = gson.fromJson(json, TourHolderResult.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        preLoadProgress.setProgress(100);
                        handler.removeCallbacks(preLoadRunner);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuth.getInstance().removeAuthStateListener(stateListener);
    }

    @OnClick(R.id.tours_holder)
    void startTours() {
        flyOut(TOURS);
    }

    @OnClick(R.id.top10_holder)
    void startTop10() {
        flyOut(TOP10);
    }

    @OnClick(R.id.login_holder)
    void startLogin() {
        flyOut(LOGIN);
    }

    @Override
    public void onBackPressed() {
        flyOut(EXIT);
    }

    /**
     * Animate entering the screen
     */
    private void flyIn() {
        animation = AnimationUtils.loadAnimation(this, R.anim.holder_top);
        tours_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom);
        top10_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.step_number);
        login_holder.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                preLoadProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Animate leaving the screen
     *
     * @param method_name activity to start
     */
    public void flyOut(final int method_name) {
        animation = AnimationUtils.loadAnimation(this, R.anim.step_number_back);
        login_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.holder_top_back);
        tours_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_back);
        top10_holder.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation a) {
                switch (method_name) {
                    case TOURS:
                    case LOGIN:
                    case TOP10:
                        backgroundHolder.setBackgroundColor(ContextCompat.getColor(ToursLaunchActivity.this, R.color.colorPrimary));
                        break;
                    case EXIT:
                        backgroundHolder.setBackgroundColor(ContextCompat.getColor(ToursLaunchActivity.this, android.R.color.transparent));
                        break;
                    default:
                }

                preLoadProgress.setVisibility(View.GONE);  // hide in case user doesn't wait for the preload
            }

            @Override
            public void onAnimationRepeat(Animation a) { /* EMPTY*/ }

            @Override
            public void onAnimationEnd(Animation a) {

                switch (method_name) {
                    case LOGIN:
                        startActivity(new Intent(ToursLaunchActivity.this, LoginActivity.class));
                        break;
                    case TOURS:
                        startActivity(new Intent(ToursLaunchActivity.this, CatalogueActivity.class));
                        break;
                    case TOP10:
                        startActivityForResult(new Intent(ToursLaunchActivity.this, FourSquareListsActivity.class), RESULT_FSQ);
                        break;
                    case EXIT:
                        back();
                        break;
                    default:
                }
            }
        });
    }

    private void back() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            finish();
        }
    }
}
