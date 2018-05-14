package me.carc.btown.tours;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.Holder;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.db.TourViewModel;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.ui.custom.ExploreButton;
import me.carc.btown.ui.custom.GalleryBottomView;

/**
 * Created by Carc.me on 20.04.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class CataloguePreviewActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = C.DEBUG + Commons.getTag();
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

    TourViewModel mTourViewModel;

    @BindView(R.id.root)
    ViewGroup filler;
    @BindView(R.id.catalogueImage)
    ImageView catalogueImage;
    @BindView(R.id.galleryFab)
    FloatingActionButton backFab;
    @BindView(R.id.launch_container)
    ViewGroup launch_container;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.summary)
    TextView summary;
    @BindView(R.id.collection_Title)
    TextView collection_Title;
    @BindView(R.id.collectionDescription)
    TextView collectionDescription;
    @BindView(R.id.collectionCost)
    TextView collectionCost;
    @BindView(R.id.bottom)
    GalleryBottomView bottomView;
    @BindView(R.id.launch_srollview)
    ScrollView launchSrollview;
    @BindView(R.id.launchBtn)
    ExploreButton launchBtn;
    @BindView(R.id.launchEndBtn)
    ExploreButton launchEndBtn;


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

        if (getIntent().hasExtra(CatalogueActivity.CATALOGUE_INDEX)) {
//            card = getIntent().getParcelableExtra(CatalogueActivity.CATALOGUE);
            int tourID = getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, 0);

            mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
            mTourViewModel.getTour(tourID).observe(this, new Observer<TourCatalogueItem>() {
                @Override
                public void onChanged(@Nullable final TourCatalogueItem tour) {
                    // Update the cached copy of the words in the adapter.
                    card = tour;

                    title.setText(card.getCatalogueName(isGermanLanguage));
                    collection_Title.setText(title.getText());
                    collectionDescription.setText(card.getCatalogueBrief(isGermanLanguage));
                    summary.setText(card.getCatalogueDesc(isGermanLanguage));

                    launchBtn.setOnClickListener(CataloguePreviewActivity.this);
                    launchEndBtn.setOnClickListener(CataloguePreviewActivity.this);  // add second button to end of description (does same thing)

                    catalogueImage.setImageDrawable(Holder.get());

                    backFab.setOnClickListener(CataloguePreviewActivity.this);
                    final Animation a = AnimationUtils.loadAnimation(CataloguePreviewActivity.this, R.anim.fade_in);
                    a.setDuration(getResources().getInteger(R.integer.gallery_alpha_duration));
                    backFab.setAnimation(a);
                    launchSrollview.setOnTouchListener(CataloguePreviewActivity.this);
                    catalogueImage.setOnTouchListener(CataloguePreviewActivity.this);

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

    @OnClick(R.id.galleryFab)
    void onClickStar(View v) {
        toggleInfoContainer(v);
    }

    private void toggleInfoContainer(final View view) {
        final boolean show = ViewUtils.isGone(launch_container);
        Animator animator;

        int duration = getResources().getInteger(R.integer.toggle_animation_duration);
        if (C.HAS_L) {
            float radius = Math.max(catalogueImage.getWidth(), catalogueImage.getHeight()) * 2.0f;
            animator = ViewUtils.toggleViewCircular(view, launch_container, show, radius, duration,
                    startRunnable, endRunnable);
        } else {
            animator = ViewUtils.toggleViewFade(launch_container, show, duration,
                    startRunnable, endRunnable);
        }
        animator.start();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.launchBtn:
            case R.id.launchEndBtn:
                launchCollection();
                break;

            case R.id.galleryFab:
                onBackPressed();
                break;
            default:
        }
    }

    private void launchCollection() {
        Intent intent = new Intent(CataloguePreviewActivity.this, AttractionTabsActivity.class);
        intent.putExtra(CATALOGUE_TITLE, card.getCatalogueName(isGermanLanguage));
        intent.putExtra(CatalogueActivity.CATALOGUE_INDEX, getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1));
//        intent.putExtra(ATTRACTIONS_LIST, card.getAttractions());
        startActivity(intent);
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (C.RESULT_COLLECTION == requestCode) {
            // Check if error has been detected - renew DB if so
            if (data != null && data.hasExtra(C.EXTRA_ERROR_DETECTED)) {
                quitError();
            }

            // Are we using network images? Ask to cache images if so
            if (TourLists.usingNetwork()) {
                Intent returnIntent = getIntent();
                returnIntent.putExtra(C.EXTRA_NETWORK_IMAGE, TourLists.usingNetwork());
                setResult(RESULT_OK, returnIntent);

                // reset the network indicator
                TourLists.setUsingNetwork(false);
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            Utils.setHideyBar(getWindow());
        }
    }
*/

    private float downX, downY;

    @Override
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
            animator = ViewUtils.toggleViewFade(launch_container, show, duration,
                    startRunnable, endRunnable);
        }
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ButterKnife.unbind(this);
    }

    private void quitError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Build the dialog box
        builder.setTitle(getString(R.string.serious_error_title))
                .setMessage(getString(R.string.serious_error_body))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int i) {
                        dlg.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int which) {
                        recreate();
                        dlg.dismiss();
                    }
                });
        // Display the dialog
        builder.show();
    }
}
