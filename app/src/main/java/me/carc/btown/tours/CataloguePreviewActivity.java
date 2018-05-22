package me.carc.btown.tours;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.Holder;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.ui.custom.ExploreButton;
import me.carc.btown.ui.custom.GalleryBottomView;

/**
 * Created by Carc.me on 20.04.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class CataloguePreviewActivity extends BaseActivity {

    private static final String TAG = CataloguePreviewActivity.class.getName();
    public static final String CATALOGUE_TITLE = "CATALOGUE_TITLE";
    public static final String ATTRACTIONS_LIST = "ATTRACTIONS_LIST";

    private static final int MIN_DISTANCE = 50;
    private final Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
//            backFab.setEnabled(false);
        }
    };
    private final Runnable endRunnable = new Runnable() {
        @Override
        public void run() {
//            backFab.setEnabled(true);
        }
    };
    private TourCatalogueItem card;
    private boolean isGermanLanguage;

    private float downX, downY;

    @BindView(R.id.root) ViewGroup filler;
    @BindView(R.id.catalogueImage) ImageView catalogueImage;
    @BindView(R.id.galleryFab) FloatingActionButton backFab;
    @BindView(R.id.launch_container) ViewGroup launch_container;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.summary) TextView summary;
    @BindView(R.id.collection_Title) TextView collection_Title;
    @BindView(R.id.collectionDescription) TextView collectionDescription;
    @BindView(R.id.collectionCost) TextView collectionCost;
    @BindView(R.id.bottom) GalleryBottomView bottomView;
    @BindView(R.id.launch_srollview) ScrollView launchSrollview;
    @BindView(R.id.launchBtn) ExploreButton launchBtn;
    @BindView(R.id.launchEndBtn) ExploreButton launchBtnBottom;

    @SuppressLint("ClickableViewAccessibility")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_catalogue_preview_activity);
        ButterKnife.bind(this);

        isGermanLanguage = isGermanLanguage();

        calculateImageHeight();
        initializeViews();

        if (getIntent().hasExtra(CatalogueActivity.CATALOGUE_INDEX) && savedInstanceState == null) {
            int tourID = getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, 0);

            TourViewModel mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
            mTourViewModel.getTour(tourID).observe(this, new Observer<TourCatalogueItem>() {
                @Override
                public void onChanged(@Nullable final TourCatalogueItem tour) {
                    card = tour;

                    title.setText(card.getCatalogueName(isGermanLanguage));
                    collection_Title.setText(title.getText());
                    collectionDescription.setText(card.getCatalogueBrief(isGermanLanguage));
                    summary.setText(card.getCatalogueDesc(isGermanLanguage));
                    catalogueImage.setImageDrawable(Holder.get());

                    final Animation a = AnimationUtils.loadAnimation(CataloguePreviewActivity.this, R.anim.fade_in);
                    a.setDuration(getResources().getInteger(R.integer.gallery_alpha_duration));
                    backFab.setAnimation(a);
                    ViewUtils.changeFabColour(CataloguePreviewActivity.this, backFab, R.color.toursBackButtonBackgroundColor);
                }
            });
        } else {
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initializeViews() {
        repositionFab();
        ViewUtils.setViewHeight(launch_container, C.IMAGE_HEIGHT, true);
        ViewUtils.setViewHeight(launchSrollview, C.IMAGE_HEIGHT, true);
        ViewUtils.setViewHeight(catalogueImage, C.IMAGE_HEIGHT, false);
    }

    private void repositionFab() {
        int fabSize = getResources().getDimensionPixelSize(R.dimen.fab_size);
        backFab.setTranslationY(C.IMAGE_HEIGHT - (fabSize / 2.0f));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        ViewUtils.createAlphaAnimator(backFab, false, getResources().getInteger(R.integer.gallery_alpha_duration) * 2)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        finishAfterTransition();
                    }
                }).start();
    }

    @OnClick(R.id.galleryFab)
    void onClickStar() {
        onBackPressed();
    }

    @OnClick({R.id.launchBtn, R.id.launchEndBtn})
    public void launchCollection() {
        Intent intent = new Intent(CataloguePreviewActivity.this, AttractionTabsActivity.class);
        intent.putExtra(CATALOGUE_TITLE, card.getCatalogueName(isGermanLanguage));
        intent.putExtra(CatalogueActivity.CATALOGUE_INDEX, getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1));
        startActivity(intent);
    }

    @OnTouch({R.id.launch_srollview, R.id.catalogueImage})
    public boolean onTouch(View v, MotionEvent event) {
        final boolean show = ViewUtils.isGone(launch_container);

        switch (v.getId()) {
            case R.id.launch_srollview:

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        return false;

                    case MotionEvent.ACTION_UP:
//                        float upX = event.getX();
                        float upY = event.getY();

//                        float deltaX = downX - upX;
                        float deltaY = downY - upY;

                        // swipe vertical?
                        if (Math.abs(deltaY) > MIN_DISTANCE) {
                            return true;
                        } else {
                            showHideLauncher(show, v, event);
                            return true;
                        }
                }
                return false;

            case R.id.catalogueImage:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showHideLauncher(show, v, event);
                    return true;
                }
        }
        return false;
    }

    private void showHideLauncher(boolean show, View v, MotionEvent event) {
        Animator animator;
        int duration = getResources().getInteger(R.integer.toggle_animation_duration);
        if (C.HAS_L) {
            float radius = Math.max(catalogueImage.getWidth(), catalogueImage.getHeight()) * 2.0f;
            animator = ViewUtils.toggleViewCircularTouch(v, (int) event.getX(), (int) event.getY(),
                    launch_container, show, radius, duration, startRunnable, endRunnable);
        } else {
            animator = ViewUtils.toggleViewFade(launch_container, show, duration, startRunnable, endRunnable);
        }
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
