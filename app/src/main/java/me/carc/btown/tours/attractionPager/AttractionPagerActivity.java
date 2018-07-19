package me.carc.btown.tours.attractionPager;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BaseActivity;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.camera.CameraActivity;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.extras.messaging.CommentsActivity;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.RetainedFragment;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;
import me.carc.btown.ui.custom.LockableViewPager;

public class AttractionPagerActivity extends BaseActivity implements PlaceholderFragment.TourListener {

    private static final String TAG = AttractionPagerActivity.class.getName();

    public static final String SCROLL_TO_NEW_INDEX = "SCROLL_TO_NEW_INDEX";
    public static final String ATTRACTION = "ATTRACTION";
    public static final String ATTRACTION_INDEX = "ATTRACTION_INDEX";

    private static final int RESULT_DETAIL = 10;
    private static final int RESULT_CAMERA_PREVIEW = 101;

    //    private String mOnCameraLocation;
    private TourPagerAdapter tourAdapter;
    private int selectedAttraction;
//    private ArrayList<Attraction> attractions;


    @BindView(R.id.attractionViewPager)
    LockableViewPager mViewPager;


    private ViewPager.SimpleOnPageChangeListener onPagerChangedListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            int pos = mViewPager.getCurrentItem();
            if (pos == position && positionOffset == 0)
                mViewPager.setSwipeLocked(false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == ViewPager.SCROLL_STATE_SETTLING)
                mViewPager.setSwipeLocked(true);
        }

        public void onPageSelected(int pos) {
            selectedAttraction = pos;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ATTRACTION_INDEX, selectedAttraction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_attraction_pager);
        ButterKnife.bind(this);

        Log.d(TAG, "onCreate: ");

        Intent intent = getIntent();
        if (intent.hasExtra(CatalogueActivity.CATALOGUE_INDEX)) {
            final FragmentManager fm = getSupportFragmentManager();

            if (savedInstanceState != null) {
                selectedAttraction = savedInstanceState.getInt(ATTRACTION_INDEX);
                // find the retained fragment on activity restarts
                RetainedFragment retainedFragment = (RetainedFragment) fm.findFragmentByTag(RetainedFragment.ID_TAG);
                if(Commons.isNotNull(retainedFragment))
                    setupPager(retainedFragment.getAttractionList());

            } else {
                int CATALOGUE_INDEX = intent.getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1);
                selectedAttraction = intent.getIntExtra(ATTRACTION_INDEX, 0);

                TourViewModel mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
                mTourViewModel.getTour(CATALOGUE_INDEX).observe(this, new Observer<TourCatalogueItem>() {
                    @Override
                    public void onChanged(@Nullable final TourCatalogueItem tour) {
                        if(Commons.isNotNull(tour)) {
                            // Create the retained fragment to store the tour data
                            RetainedFragment retainedFragment = new RetainedFragment();
                            retainedFragment.setAttractionList(tour.getAttractions());
                            fm.beginTransaction().add(retainedFragment, RetainedFragment.ID_TAG).commit();

                            setupPager(tour.getAttractions());
                        }
                    }
                });
            }
            setStatusBarColor(false, R.color.colorPrimary);
        }
    }

    private void setupPager(List<Attraction> attractions) {
        tourAdapter = new TourPagerAdapter(getSupportFragmentManager(), attractions, isGermanLanguage());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(tourAdapter);
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mViewPager.addOnPageChangeListener(onPagerChangedListener);
        mViewPager.setCurrentItem(selectedAttraction, true);

        // Android Team solution to status bar overlay problem
        statusBarOverlaySolution();
    }

    /**
     * Android Team solution to status bar overlay problem
     */
    private void statusBarOverlaySolution() {
        ViewCompat.setOnApplyWindowInsetsListener(mViewPager, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                insets = ViewCompat.onApplyWindowInsets(v, insets);
                if (insets.isConsumed()) {
                    return insets;
                }

                boolean consumed = false;
                for (int i = 0, count = mViewPager.getChildCount(); i < count; i++) {
                    ViewCompat.dispatchApplyWindowInsets(mViewPager.getChildAt(i), insets);
                    if (insets.isConsumed()) {
                        consumed = true;
                    }
                }
                return consumed ? insets.consumeSystemWindowInsets() : insets;
            }
        });
    }


    @Override
    public void onBackPressed() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent returnIntent = getIntent();
                returnIntent.putExtra(SCROLL_TO_NEW_INDEX, mViewPager.getCurrentItem());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }, getResources().getInteger(R.integer.gallery_alpha_duration) * 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CAMERA_PREVIEW:
                Log.d(TAG, "onActivityResult: RESULT_CAMERA_PREVIEW");
                break;

            case RESULT_DETAIL:
                Log.d(TAG, "onActivityResult: RESULT_DETAIL");
                break;
            default:
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSendPostCard(Bitmap bitmap, String title) {
        if (BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Attr:SendPostCard"));
            Crashlytics.log(TAG + " : onSendPostCard()");
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        // Add the descriptions
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.strShareBody));
        share.putExtra(Intent.EXTRA_TEXT, IntentUtils.getUrlWithRef(getPackageName())/*getString(R.string.strShareStore*/);

        // Add the image
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        share.putExtra(Intent.EXTRA_STREAM, bitmapUri);

        startActivity(Intent.createChooser(share, getString(R.string.send_postcard)));
    }

    @Override
    public void onAddComment(String stopName) {
        if (BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Attr:AddComment"));
            Crashlytics.log(TAG + " : onAddComment()");
        }

        Intent iComment = new Intent(AttractionPagerActivity.this, CommentsActivity.class);
        iComment.putExtra(CommentsActivity.EXTRA_MESSAGE_BOARD_CAT, CommentsActivity.MSG_BOARD_CAT_COMMENTS);
        iComment.putExtra(CommentsActivity.EXTRA_MESSAGE_BOARD_ID, stopName);
        startActivity(iComment);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCamera(String location) {
        if (C.HAS_M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, C.PERMISSION_CAMERA);
            return;
        }
        startActivity(new Intent(this, CameraActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == C.PERMISSION_CAMERA) {
            if (C.HAS_M && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, CameraActivity.class));
            } else
                Commons.Toast(this, R.string.rationale_camera, Color.RED, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDonate() {
        showDonateDialog();
    }

    @Override
    public void onColorFab(ColorStateList stateList, ColorFilter filter) {
/*
        if (C.HAS_L) {
            fabShowMap.setBackgroundTintList(stateList);
            fabCamera.setBackgroundTintList(stateList);
        } else {
            fabShowMap.getBackground().setColorFilter(filter);
            fabCamera.getBackground().setColorFilter(filter);
        }
*/
    }

    @Override
    public void onShowMap(Attraction attractionData) {
        if (BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Attr:ShowMap"));
            Crashlytics.log(TAG + " : onShowMap()");
        }

        Intent mapIntent = new Intent(AttractionPagerActivity.this, AttractionMapActivity.class);
        mapIntent.putExtra(ATTRACTION, (Parcelable) attractionData);
        startActivity(mapIntent);
    }


    @Override
    public void onImageTouch(View v) {
        String image = v.getTag(R.string.key_image_url).toString();
        String title = v.getTag(R.string.key_image_title).toString();
        ImageDialog.showInstance(getApplicationContext(), image, null, title, getString(R.string.wikipedia));
    }


    @Override
    public void onUnlockPager() {
        if (mViewPager != null)
            mViewPager.setSwipeLocked(false);
    }

    @Override
    public void onScrollView(boolean hide) {
        // TODO: 11/01/2018 if you keep these, move to collapsing toolbar
/*
        if (hide) {
            fabShowMap.hide();
            fabCamera.hide();
        } else {
            fabShowMap.show();
            fabCamera.show();
        }
*/
    }

    @Override
    public void firebaseUpdateRequired() {
        if (Commons.isNetworkAvailable(this)) {
            Intent getImagesIntent = new Intent(this, FirebaseImageDownloader.class);
            getImagesIntent.putExtra("FORCE_UPDATE", true);
            startService(getImagesIntent);
        } else
            showAlertDialog(R.string.no_image_no_connection_title, R.string.no_image_no_connection_desc, -1, R.drawable.ic_menu_share);
    }
}