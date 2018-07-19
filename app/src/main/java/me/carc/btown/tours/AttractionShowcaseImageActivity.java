package me.carc.btown.tours;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.BaseActivity;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.tours.data.services.FirebaseImageDownloader;
import me.carc.btown.ui.TouchImageView;
import me.carc.btown.ui.custom.CustomAnimatorListener;
import me.carc.btown.ui.custom.CustomTransitionListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttractionShowcaseImageActivity extends BaseActivity {
    private static final String TAG = AttractionShowcaseImageActivity.class.getName();

    private static final String TEXT_COLOR = "TEXT_COLOR";
    private static final String RGB = "RGB";


    private static final int ACTIVITY_CROP = 13451;
    private static final int ACTIVITY_SHARE = 13452;

    private static final int ANIMATION_DURATION_SHORT = 150;
    private static final int ANIMATION_DURATION_MEDIUM = 300;
    private static final int ANIMATION_DURATION_LONG = 450;
    private static final int ANIMATION_DURATION_EXTRA_LONG = 850;

    private RetainedFragment mRetainedFragment;

    private GalleryItem galleryItem;
    private Animation mProgressFabAnimation;

    private Drawable mDrawablePhoto;
    private Drawable mDrawableClose;
    private Drawable mDrawableSuccess;
    private Drawable mDrawableError;

    @BindView(R.id.showcaseRoot) RelativeLayout showcaseRoot;
    @BindView(R.id.showcaseImage) TouchImageView showcaseImage;
    @BindView(R.id.fabBackButton) ImageView fabBackButton;
    @BindView(R.id.mFabShareButton) ImageButton mFabShareButton;
    @BindView(R.id.mFabWallpaperButton) ImageButton mFabWallpaperButton;

    @BindView(R.id.captionsFrame) View captionsFrame;
    @BindView(R.id.showcaseCaptions) View showcaseCaptions;
    @BindView(R.id.captionTitle) TextView captionTitle;
    @BindView(R.id.captionSubTitle) TextView captionSubTitle;
    @BindView(R.id.mFabProgress) DonutProgress mFabProgress;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_showcase_activity);
        ButterKnife.bind(this);

        if (BuildConfig.USE_CRASHLYTICS) {
            Answers.getInstance().logCustom(new CustomEvent("Showcae Image"));
            Crashlytics.log(TAG + " : Showcae Image");
        }

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(RetainedFragment.ID_TAG);


        // create the fragment and data the first time
        if (mRetainedFragment == null && getIntent().hasExtra("INDEX")) {
            final int pos = getIntent().getIntExtra("INDEX", 0);
            galleryItem = AttractionTabsActivity.galleryItems.get(pos);

            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, RetainedFragment.ID_TAG).commit();
            // load data from a data source or perform any calculation
            mRetainedFragment.setGalleryItem(galleryItem);
        } else
            galleryItem = mRetainedFragment.getGalleryItem();


        //check if we already had the colors during click
        int swatch_title_text_color = galleryItem.getSwatch() != null ? galleryItem.getSwatch().getTitleTextColor() : -1;
        int swatch_rgb = galleryItem.getSwatch() != null ? galleryItem.getSwatch().getRgb() : -1;

        mDrawablePhoto = new IconicsDrawable(this, FontAwesome.Icon.faw_backward).color(Color.WHITE).sizeDp(24);
        mDrawableClose = new IconicsDrawable(this, FontAwesome.Icon.faw_times).color(Color.WHITE).sizeDp(24);
        mDrawableSuccess = new IconicsDrawable(this, FontAwesome.Icon.faw_check).color(Color.WHITE).sizeDp(24);
        mDrawableError = new IconicsDrawable(this, FontAwesome.Icon.faw_exclamation).color(Color.WHITE).sizeDp(24);

        // Fab button
        fabBackButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_back_white)); //mDrawablePhoto
        fabBackButton.setOnClickListener(onFabBackButtonListener);
        //just allow the longClickAction on Devices newer than api level v19
        if (C.HAS_K) {
            fabBackButton.setOnLongClickListener(onFabButtonLongListener);
        }

        // Fab share button
        mFabShareButton.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_share_alt).color(Color.WHITE).sizeDp(16));
        mFabShareButton.setOnClickListener(onFabShareButtonListener);

        // Fab download button
        mFabWallpaperButton.setImageDrawable(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_folder_image).color(Color.WHITE).sizeDp(16));
        mFabWallpaperButton.setOnClickListener(onFabWallpaperButtonListener);
        mFabWallpaperButton.setOnLongClickListener(onFabWallpaperButtonLongListener);

        // Title container
        ViewUtils.configuredHideYView(captionsFrame);

        //get the imageHeader and set the coverImage
        Bitmap imageCoverBitmap = galleryItem.getBitmap();

        //safety check to prevent nullPointer in the palette if the activity was in the background for too long
        if (Commons.isNotNull(imageCoverBitmap)) {
            int padding = 10;
            if (AndroidUtils.isPortrait(this)) {
                ViewUtils.setViewHeight(showcaseImage, C.SCREEN_HEIGHT - captionsFrame.getLayoutParams().height - padding, false);
            } else
                ViewUtils.setViewHeight(showcaseImage, C.SCREEN_WIDTH - captionsFrame.getLayoutParams().height - padding, false);

            showcaseImage.setImageBitmap(imageCoverBitmap);
            showcaseImage.setScrollPosition(0.5f, 0.5f);
            showcaseImage.setTransitionName("GALLERY_IMAGE");
        }

        // start the transition animations
        if (Commons.isNull(savedInstanceState))
            getWindow().getSharedElementEnterTransition().addListener(new CustomTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    animateActivityStart();
                }
            });
        else
            animateActivityStart();

        // set the colors
        if (swatch_rgb != -1 && swatch_title_text_color != -1) {
            setColors(swatch_title_text_color, swatch_rgb);
        } else
            setColors(ContextCompat.getColor(this, R.color.almostWhite), ContextCompat.getColor(this, R.color.colorPrimary));

        // allow back on title bar touch
        showcaseRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private View.OnClickListener onFabShareButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animateStart();
            downloadAndSetOrShareImage(false);

            mFabShareButton.animate().rotation(360).setDuration(ANIMATION_DURATION_LONG).setListener(new CustomAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }
            }).start();
        }
    };

    private View.OnClickListener onFabWallpaperButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animateStart();
            downloadAndSetOrShareImage(true);

            mFabShareButton.animate().rotation(360).setDuration(ANIMATION_DURATION_LONG).setListener(new CustomAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                }
            }).start();
        }
    };

    private View.OnLongClickListener onFabWallpaperButtonLongListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            Log.d(TAG, "onLongClick: onFabWallpaperButtonLongListener");
            return false;
        }
    };


    private View.OnClickListener onFabBackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // reset the zoom level before animating back
            if (showcaseImage.isZoomed())
                showcaseImage.resetZoom();

            // center image if needed
            showcaseImage.setScrollPosition(0.5f, 0.5f);
            // animate back from activity
            onBackPressed();
        }
    };

    private View.OnLongClickListener onFabButtonLongListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onLongClick: onFabButtonLongListener");
            return false;
        }
    };

    private void setError(@Nullable Exception exeption) {
        // something went wrong, show the error icon
        mProgressFabAnimation.cancel();
        fabBackButton.setImageDrawable(mDrawableError);

        ColorStateList stateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        ContextCompat.getColor(AttractionShowcaseImageActivity.this, R.color.showcaseErrorColor)
                });
        fabBackButton.setBackgroundTintList(stateList);
        mFabProgress.setFinishedStrokeColor(Color.RED);
        if (exeption != null)
            Log.d(TAG, "downloadAndSetOrShareImage ERROR: " + exeption.getMessage());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.shared_string_error))
                .setMessage(exeption != null ? exeption.getLocalizedMessage() : getString(R.string.check_network))
                .show();

        if (Commons.isNetworkAvailable(AttractionShowcaseImageActivity.this)) {
            Intent getImagesIntent = new Intent(this, FirebaseImageDownloader.class);
            startService(getImagesIntent);
        }
    }


    private void downloadAndSetOrShareImage(final boolean set) {
        if (Commons.isNotNull(galleryItem.getUrl())) {
            mFabProgress.setVisibility(View.VISIBLE);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(galleryItem.getUrl())
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    setError(e);
                }

                @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if (response.body().byteStream() != null) {

                        try {
                            //create a temporary directory within the cache folder
                            File file = getAuthorityFile();

                            FileUtils.copyInputStreamToFile(response.body().byteStream(), file);

                            setOrShareImage(set, file);
                        } catch (Exception ex) {
                            setError(ex);
                        }
                    }
                }
            });
        } else if (Commons.isNotNull(galleryItem.getCachedFile())) {

            try {
                File file = getAuthorityFile();
                if (Commons.isNotNull(file)) {
                    FileUtils.copy(galleryItem.getCachedFile(), file);
                    setOrShareImage(set, file);
                } else
                    setError(null);
            } catch (Exception ex) {
                setError(ex);
            }
        }
    }

    private File getAuthorityFile() {
        try {
            File dir = new File(AttractionShowcaseImageActivity.this.getCacheDir() + "/images");
            if (!dir.exists()) {
                dir.mkdirs();
                File file = new File(dir, ".nomedia");
                if (!file.exists()) file.createNewFile();
            }
            //create temp file
            File file = new File(dir, "btownTemp.jpg");
            if (!file.exists()) file.createNewFile();
            return file;
        } catch (Exception ex) {
            setError(ex);
        }
        return null;
    }

    private void setOrShareImage(boolean set, File file) {
        //get the contentUri for this file and start the intent
        Uri contentUri = FileProvider.getUriForFile(AttractionShowcaseImageActivity.this, getString(R.string.file_provider), file);

        if (set) {
            try {
                //Home screen it
                Intent intent = WallpaperManager.getInstance(AttractionShowcaseImageActivity.this).getCropAndSetWallpaperIntent(contentUri);
                //startActivityForResult to stop the progress bar
                startActivityForResult(intent, ACTIVITY_CROP);
            }catch (IllegalArgumentException e) {
                // Seems to be an Oreo bug - fall back to using the bitmap instead
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                    WallpaperManager.getInstance(AttractionShowcaseImageActivity.this).setBitmap(bitmap);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                animateCompleteFirst();
            }

        } else {
            //Share it
            // TODO: 20/10/2017 set the message
            final String pageUrl = "B-Town Tours - " + galleryItem.getTitle() + "\n" + IntentUtils.getUrlWithRef(getPackageName());

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setData(contentUri);
            shareIntent.setType("image/jpg");
            shareIntent.putExtra(Intent.EXTRA_TEXT, pageUrl);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            //startActivityForResult to stop the progress bar
            startActivityForResult(Intent.createChooser(shareIntent, "Share via..."), ACTIVITY_SHARE);
        }

        animateComplete(true);
    }

    /**
     * finish the animations of the ui after the download is complete. reset the button to the start
     *
     * @param success was operation successful
     */
    private void animateComplete(boolean success) {
        //hide the progress again :D
        ViewUtils.hideViewByScaleXY(mFabProgress).setDuration(ANIMATION_DURATION_MEDIUM).start();
        mProgressFabAnimation.cancel();

        //show the fab again ;)
        mFabShareButton.animate().translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 58)).setDuration(ANIMATION_DURATION_MEDIUM).start();
        mFabWallpaperButton.animate().translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 108)).setDuration(ANIMATION_DURATION_MEDIUM).start();

        // if we were not successful remove the x again :D
        if (!success) {
            //Utils.animateViewElevation(fabBackButton, 0, mElavationPx);
            fabBackButton.setImageDrawable(mDrawablePhoto);
            fabBackButton.animate().rotation(360).setDuration(ANIMATION_DURATION_MEDIUM).start();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CROP) {
            //animate the first elements
            animateCompleteFirst();
        } else if (requestCode == ACTIVITY_SHARE) {
            //animate the first elements
            animateCompleteFirst();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * animate the start of the activity
     */
    private void animateActivityStart() {
        ViewPropertyAnimator showTitleAnimator = ViewUtils.showViewByScale(captionsFrame);
        showTitleAnimator.setListener(new CustomAnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {

                super.onAnimationEnd(animation);
                showcaseCaptions.startAnimation(AnimationUtils.loadAnimation(AttractionShowcaseImageActivity.this, R.anim.alpha_on));
                showcaseCaptions.setVisibility(View.VISIBLE);

                //animate the fab
                ViewUtils.showViewByScale(fabBackButton).setDuration(ANIMATION_DURATION_MEDIUM).start();

                //animate the share fab
                ViewUtils.showViewByScale(mFabShareButton)
                        .setDuration(ANIMATION_DURATION_MEDIUM * 2)
                        .start();
                mFabShareButton.animate()
                        .translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 58))
                        .setStartDelay(ANIMATION_DURATION_MEDIUM)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .start();

                //animate the download fab
                ViewUtils.showViewByScale(mFabWallpaperButton)
                        .setDuration(ANIMATION_DURATION_MEDIUM * 2)
                        .start();
                mFabWallpaperButton.animate()
                        .translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 108))
                        .setStartDelay(ANIMATION_DURATION_MEDIUM)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .start();
            }
        });

        showTitleAnimator.start();
    }


    /**
     * animate the start of the download
     */
    @SuppressFBWarnings("ICAST_IDIV_CAST_TO_DOUBLE")
    private void animateStart() {
        //reset progress to prevent jumping
        mFabProgress.setProgress(0);

        //hide the share fab
        mFabShareButton.animate().translationX(0).setDuration(ANIMATION_DURATION_SHORT).start();
        mFabWallpaperButton.animate().translationX(0).setDuration(ANIMATION_DURATION_SHORT).start();

        //some nice button animations
        ViewUtils.showViewByScale(mFabProgress).setDuration(ANIMATION_DURATION_MEDIUM).start();
        mFabProgress.setProgress(1);

        mProgressFabAnimation = new RotateAnimation(0.0f, 360.0f, mFabProgress.getWidth() / 2, mFabProgress.getHeight() / 2);
        mProgressFabAnimation.setDuration(ANIMATION_DURATION_EXTRA_LONG * 2);
        mProgressFabAnimation.setInterpolator(new LinearInterpolator());
        mProgressFabAnimation.setRepeatCount(Animation.INFINITE);
        mProgressFabAnimation.setRepeatMode(-1);
        mFabProgress.startAnimation(mProgressFabAnimation);

        fabBackButton.setImageDrawable(mDrawableClose);

        //animate the button back to blue. just do it the first time
        if (fabBackButton.getTag() != null) {
            TransitionDrawable transition = (TransitionDrawable) fabBackButton.getBackground();
            transition.reverseTransition(ANIMATION_DURATION_LONG);
            fabBackButton.setTag(null);
        }

        if (mFabShareButton.getTag() != null) {
            TransitionDrawable transition = (TransitionDrawable) mFabShareButton.getBackground();
            transition.reverseTransition(ANIMATION_DURATION_LONG);
            mFabShareButton.setTag(null);
        }

        if (mFabWallpaperButton.getTag() != null) {
            TransitionDrawable transition = (TransitionDrawable) mFabWallpaperButton.getBackground();
            transition.reverseTransition(ANIMATION_DURATION_LONG);
            mFabWallpaperButton.setTag(null);
        }
    }

    /**
     * animate the reset of the view
     */
    private void animateReset(boolean error) {

        //animating everything back to default :D
        ViewUtils.hideViewByScaleXY(mFabProgress).setDuration(ANIMATION_DURATION_MEDIUM).start();
        mProgressFabAnimation.cancel();
        //Utils.animateViewElevation(fabBackButton, 0, mElavationPx);

        if (error) {
            fabBackButton.setImageDrawable(mDrawableError);
        } else {
            fabBackButton.setImageDrawable(mDrawablePhoto);
        }

        fabBackButton.animate().rotation(360).setDuration(ANIMATION_DURATION_MEDIUM).start();

        mFabShareButton.animate().translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 58)).setDuration(ANIMATION_DURATION_MEDIUM).start();
        mFabWallpaperButton.animate().translationX((-1) * ViewUtils.pxFromDp(AttractionShowcaseImageActivity.this, 108)).setDuration(ANIMATION_DURATION_MEDIUM).start();
    }

    /**
     * animate the first parts of the UI after the download has successfully finished
     */
    private void animateCompleteFirst() {
        //some nice animations so the user knows the wallpaper was set properly
        fabBackButton.animate().rotation(720).setDuration(ANIMATION_DURATION_EXTRA_LONG).start();
        fabBackButton.setImageDrawable(mDrawableSuccess);

        //animate the button to green. just do it the first time
        if (fabBackButton.getTag() == null) {
            TransitionDrawable transition = (TransitionDrawable) fabBackButton.getBackground();
            transition.startTransition(ANIMATION_DURATION_LONG);
            fabBackButton.setTag("");
        }

        if (mFabShareButton.getTag() == null) {
            TransitionDrawable transition = (TransitionDrawable) mFabShareButton.getBackground();
            transition.startTransition(ANIMATION_DURATION_LONG);
            mFabShareButton.setTag("");
        }

        if (mFabWallpaperButton.getTag() == null) {
            TransitionDrawable transition = (TransitionDrawable) mFabWallpaperButton.getBackground();
            transition.startTransition(ANIMATION_DURATION_LONG);
            mFabWallpaperButton.setTag("");
        }
    }

    /**
     * @param titleTextColor text color
     * @param rgb            rgb color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setColors(final int titleTextColor, final int rgb) {
//        captionsFrame.setBackgroundColor(rgb);

        if (C.HAS_L)
            getWindow().setStatusBarColor(titleTextColor);

        ViewUtils.setHideyBar(getWindow());

        ///   setColors(ContextCompat.getColor(this, R.color.almostWhite), ContextCompat.getColor(this, R.color.almostBlack));


//        titleTV.setTextColor(titleTextColor);
        captionTitle.setText(galleryItem.getTitle());

        if (!Commons.isEmpty(galleryItem.getDesc())) {
            captionSubTitle.setText(galleryItem.getDesc());
        }

//        if (C.HAS_M) {
            captionSubTitle.setTextColor(titleTextColor);

            int colorFromText = ContextCompat.getColor(this, R.color.almostWhite);

            ValueAnimator textAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromText, titleTextColor);
            textAnimation.setDuration(250); // milliseconds
            textAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mFabProgress.setTextColor((int) animator.getAnimatedValue());

                    Drawable drawableIcon = ContextCompat.getDrawable(AttractionShowcaseImageActivity.this, R.drawable.ic_back_white);
                    assert drawableIcon.getConstantState() != null;
                    Drawable matchText = drawableIcon.getConstantState().newDrawable();
                    matchText.mutate().setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                    fabBackButton.setImageDrawable(matchText);

                    drawableIcon = new IconicsDrawable(AttractionShowcaseImageActivity.this, FontAwesome.Icon.faw_share_alt).color((int) animator.getAnimatedValue()).sizeDp(16);
                    mFabShareButton.setImageDrawable(drawableIcon);

                    drawableIcon = new IconicsDrawable(AttractionShowcaseImageActivity.this, CommunityMaterial.Icon.cmd_folder_image).color((int) animator.getAnimatedValue()).sizeDp(16);
                    mFabWallpaperButton.setImageDrawable(drawableIcon);

                    captionTitle.setTextColor((int) animator.getAnimatedValue());
                    captionSubTitle.setTextColor((int) animator.getAnimatedValue());
                }
            });

            int colorFromRgb = ContextCompat.getColor(this, R.color.colorPrimary);

            ValueAnimator rgbAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromRgb, rgb);
            rgbAnimation.setDuration(250); // milliseconds
            rgbAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    captionsFrame.setBackgroundColor((int) animator.getAnimatedValue());

                    ColorStateList stateList = new ColorStateList(
                            new int[][]{new int[]{}},
                            new int[]{(int) animator.getAnimatedValue(), titleTextColor});

                    fabBackButton.setBackgroundTintList(stateList);
                    mFabShareButton.setBackgroundTintList(stateList);
                    mFabWallpaperButton.setBackgroundTintList(stateList);
                }

            });
            textAnimation.start();
            rgbAnimation.start();

/*
        } else {
            fabBackButton.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            fabBackButton.getDrawable().setTint(ContextCompat.getColor(this, android.R.color.white));
            mFabShareButton.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            mFabShareButton.getDrawable().setTint(ContextCompat.getColor(this, android.R.color.white));
            mFabWallpaperButton.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            mFabWallpaperButton.getDrawable().setTint(ContextCompat.getColor(this, android.R.color.white));

        }
*/
    }

    @Override
    public void onBackPressed() {
        mFabWallpaperButton.animate()
                .translationX(0)
                .setDuration(ANIMATION_DURATION_MEDIUM)
                .setListener(animationFinishListener1)
                .start();


        //move the share fab below the normal fab (58 because this is the margin top + the half
        mFabShareButton.animate()
                .translationX(0)
                .setDuration(ANIMATION_DURATION_MEDIUM)
                .setListener(animationFinishListener1)
                .start();
    }

    private CustomAnimatorListener animationFinishListener1 = new CustomAnimatorListener() {
        private int animateFinish1 = 0;

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            process();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            process();
        }

        private void process() {
            animateFinish1 = animateFinish1 + 1;
            if (animateFinish1 >= 2) {
                //create the fab animation and hide fabProgress animation, set an delay so those will hide after the shareFab is below the main fab
                ViewUtils.hideViewByScaleXY(mFabWallpaperButton)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .setListener(animationFinishListener2)
                        .start();
                ViewUtils.hideViewByScaleXY(mFabShareButton)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .setListener(animationFinishListener2)
                        .start();
                ViewUtils.hideViewByScaleXY(mFabProgress)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .setListener(animationFinishListener2)
                        .start();
                ViewUtils.hideViewByScaleXY(fabBackButton)
                        .setDuration(ANIMATION_DURATION_MEDIUM)
                        .setListener(animationFinishListener2)
                        .start();
            }
        }
    };

    private CustomAnimatorListener animationFinishListener2 = new CustomAnimatorListener() {
        private int animateFinish2 = 0;

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            process();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            process();
        }

        private void process() {
            animateFinish2 = animateFinish2 + 1;
            if (animateFinish2 >= 4) {
                ViewPropertyAnimator hideFabAnimator = ViewUtils.hideViewByScaleY(captionsFrame);
                hideFabAnimator.setListener(new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        back();
                    }
                });
            }
        }
    };

    /**
     *
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DE_MIGHT_IGNORE")
    private void back() {
        try {
            super.onBackPressed();

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(mRetainedFragment).commit();

        } catch (Exception e) { /*empty*/ }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
