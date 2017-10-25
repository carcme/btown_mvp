package me.carc.btown.tours;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.login.LoginActivity;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;

public class ToursLaunchActivity extends BaseActivity {

    public static final int RESULT_LOGIN = 156;

    public static final int TOURS = 0;
    public static final int TOP10 = 1;
    public static final int LOGIN = 2;
    public static final int EXIT = 3;

    private Animation animation;

    @BindView(R.id.tours_holder)
    RelativeLayout tours_holder;

    @BindView(R.id.top10_holder)
    RelativeLayout top10_holder;

    @BindView(R.id.login_holder)
    RelativeLayout login_holder;

    @BindView(R.id.loginIcon)
    ImageView loginIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_launch_activity);
        ButterKnife.bind(this);

        FirebaseAuth.getInstance().addAuthStateListener(stateListener);
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
//                    new GetFirebaseData();
                    Intent getImagesIntent = new Intent(this, FirebaseImageDownloader.class);
                    startService(getImagesIntent);
                    showLoggedInIcon();
                } else {
                    finish();
                }
                break;
        }
    }

    private void showLoggedInIcon() {

        // TODO: 23/10/2017 add a morph transform to this
        Drawable icon = new IconicsDrawable(ToursLaunchActivity.this, CommunityMaterial.Icon.cmd_account_check).color(0xFF669900).sizeDp(20);
        loginIcon.setImageDrawable(icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flyIn();
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
        showAlertDialog(R.string.top10_coming_soon_title, R.string.top10_coming_soon_msg, -1, R.drawable.ic_timelapse);
//        flyOut(TOP10);
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
    }

    /**
     * Animate leaving the screen
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
            }

            @Override
            public void onAnimationRepeat(Animation a) {
            }

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
                        break;
                    case EXIT:
                        back();
                        break;
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
