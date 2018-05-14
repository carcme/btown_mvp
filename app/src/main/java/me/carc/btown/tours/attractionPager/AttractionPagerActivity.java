package me.carc.btown.tours.attractionPager;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.camera.CameraActivity;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.db.TourViewModel;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.extras.messaging.CommentsActivity;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;
import me.carc.btown.ui.custom.LockableViewPager;

public class AttractionPagerActivity extends BaseActivity implements PlaceholderFragment.TourListener {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String SCROLL_TO_NEW_INDEX = "SCROLL_TO_NEW_INDEX";
    public static final String ATTRACTION = "ATTRACTION";
    public static final String ATTRACTION_INDEX = "ATTRACTION_INDEX";

    private static final int RESULT_DETAIL = 10;
    private static final int RESULT_CAMERA_PREVIEW = 101;

//    private String mOnCameraLocation;
    private TourPagerAdapter tourAdapter;
//    private ArrayList<Attraction> attractions;
    private CallbackManager callbackManager;

//    @BindView(R.id.fabMap)
//    FloatingActionButton fabShowMap;
//    @BindView(R.id.fabCamera)
//    FloatingActionButton fabCamera;

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
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_attraction_pager);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(CatalogueActivity.CATALOGUE_INDEX)) {

            int CATALOGUE_INDEX = intent.getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1);
            final int selectedAttraction = intent.getIntExtra(ATTRACTION_INDEX, 0);

            TourViewModel mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
            mTourViewModel.getTour(CATALOGUE_INDEX).observe(this, new Observer<TourCatalogueItem>() {
                @Override
                public void onChanged(@Nullable final TourCatalogueItem tour) {
                    List<Attraction> attractions = tour.getAttractions();

                    tourAdapter = new TourPagerAdapter(getSupportFragmentManager(), attractions, isGermanLanguage());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager.setOffscreenPageLimit(2);
                    mViewPager.setAdapter(tourAdapter);
                    mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    mViewPager.addOnPageChangeListener(onPagerChangedListener);
                    mViewPager.setCurrentItem(selectedAttraction, true);


                    // Android Team solution to status bar overlay problem
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


            });
/*
        fabShowMap.getDrawable().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
        ViewUtils.changeFabColour(this, fabShowMap, R.color.colorPrimary);
        fabShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.getCurrentItem();
                onShowMap(ToursDataClass.getInstance()
                        .getTourAttraction(getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1), mViewPager.getCurrentItem()));
//                        attractions.get(mViewPager.getCurrentItem()));
            }
        });

        fabCamera.getDrawable().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
        ViewUtils.changeFabColour(this, fabCamera, R.color.colorPrimary);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCamera(tourAdapter.getPageTitle(mViewPager.getCurrentItem()).toString());
            }
        });
*/
        }
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

//        ViewUtils.createAlphaAnimator(fabShowMap, false, getResources()
//                .getInteger(R.integer.gallery_alpha_duration) * 2).start();
//        ViewUtils.createAlphaAnimator(fabCamera, false, getResources()
//                .getInteger(R.integer.gallery_alpha_duration) * 2)
//                .withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent returnIntent = getIntent();
//                        returnIntent.putExtra(SCROLL_TO_NEW_INDEX, mViewPager.getCurrentItem());
//                        setResult(RESULT_OK, returnIntent);
//                        finish();
//                    }
//                }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // facebook stuff
        if (Commons.isNotNull(callbackManager))
            callbackManager.onActivityResult(requestCode, resultCode, data);

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onSendPostCard(Bitmap bitmap, String title) {
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
    public void onCheckin(final String id, final double lat, final double lng, final String title) {
//        Toast.makeText(AttractionPagerActivity.this, "Check In", Toast.LENGTH_SHORT).show();
        if (Commons.isNetworkAvailable(AttractionPagerActivity.this)) {
            showProgressDialog(getString(R.string.checking_in));

            if (AccessToken.getCurrentAccessToken() == null) {
                // Need to log in to facebook first
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        checkinFacebook(id, lat, lng, title);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });

                LoginManager.getInstance().logInWithReadPermissions(AttractionPagerActivity.this,
                        Collections.singletonList(C.FACEBOOK_PERMISSIONS) /*Arrays.asList(C.FACEBOOK_PERMISSIONS)*/);
            } else {
                checkinFacebook(id, lat, lng, title);
            }
        }
    }


    @Override
    public void onAddComment(String stopName) {
        Log.d(TAG, "onAddComment: ");

        Intent iComment = new Intent(AttractionPagerActivity.this, CommentsActivity.class);
        iComment.putExtra(CommentsActivity.EXTRA_MESSAGE_BOARD_CAT, CommentsActivity.MSG_BOARD_CAT_COMMENTS);
        iComment.putExtra(CommentsActivity.EXTRA_MESSAGE_BOARD_ID, stopName);
        startActivity(iComment);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCamera(String location) {
        if (C.HAS_M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, C.PERMISSION_CAMERA);
            return;
        }
        startActivity(new Intent(this, CameraActivity.class));
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
        Intent mapIntent = new Intent(AttractionPagerActivity.this, AttractionMapActivity.class);
        mapIntent.putExtra(ATTRACTION, (Parcelable) attractionData);
        startActivity(mapIntent);
    }


    @Override
    public void onImageTouch(View v) {

        String image = v.getTag(R.string.key_image_url).toString();
        String title = v.getTag(R.string.key_image_title).toString();

        ImageDialog.showInstance(getApplicationContext(), image, null, title, getString(R.string.wikipedia));

//        Bitmap bm = v.getDrawingCache();
//        Holder.setBitmap(bm);

/*
        Intent detailIntent = new Intent(AttractionPagerActivity.this, ActivityImageDetail.class);
        detailIntent.putExtra(C.EXTRA_HOLDER_IMAGE, true);
        detailIntent.putExtra(C.EXTRA_IMAGE_TITLE, v.getTag(R.string.key_image_title).toString());
        detailIntent.putExtra(C.EXTRA_IMAGE_URL, v.getTag(R.string.key_image_url).toString());
        detailIntent.putExtra(C.EXTRA_HAS_TRANSITION, false);

        Bundle options = null;
        if (C.HAS_L)
            options = ActivityOptions.makeSceneTransitionAnimation(AttractionPagerActivity.this, v, "imageCover").toBundle();
        startActivityForResult(detailIntent, RESULT_DETAIL, options);
*/
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

    /**
     * @param pageId facebook id if provided
     */
    private void checkinFacebookWithID(String pageId) {
        Bundle params = new Bundle();
        String postLocation = "me/feed";
        params.putString("caption", "Post just using lat and lng coords then get first from the list of nearby attractions");
        params.putString("place", pageId);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                postLocation,
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        // TODO: if successful, add check in visited marker to main map?
                        hideProgressDialog();
                    }
                }
        ).executeAsync();
    }


    private void checkinFacebook(final String id, final double lat, final double lng, final String title) {

        if (!id.isEmpty()) {
            checkinFacebookWithID(id);
        } else {

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "search?type=place&center=" + lat + "," + lng + "&distance=100",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse res) {
                            if (res.getError() == null) {
                                JSONObject obj = res.getJSONObject();

                                try {
                                    JSONArray arr = obj.getJSONArray("data");
                                    Log.d(TAG, "onCompleted: " + arr.toString());
                                    String pageId = null;

                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject item = arr.getJSONObject(i);
                                        String name = item.getString("name");
                                        if (title.contains(name)) {
                                            pageId = item.getString("id");
                                            break;
                                        }
                                    }
                                    checkinFacebookWithID(pageId);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            ).executeAsync();
        }
    }
}