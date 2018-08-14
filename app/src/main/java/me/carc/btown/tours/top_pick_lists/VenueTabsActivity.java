package me.carc.btown.tours.top_pick_lists;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.flaviofaria.kenburnsview.KenBurnsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.ImageUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.data.all4squ.entities.Photo;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.map.MapActivity;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.tours.top_pick_lists.fragments.VenueInfoFragment;
import me.carc.btown.ui.custom.MyFragmentPagerAdapter;

/**
 * Created by Carc.me on 25.04.16.
 */
public class VenueTabsActivity extends BaseActivity implements ToursScrollListener {
    private static final String TAG = VenueTabsActivity.class.getName();

    public static final String EXTRA_VENUE      = "EXTRA_VENUE";
    public static final String EXTRA_VENUE_URL  = "EXTRA_VENUE_URL";
    public static final String EXTRA_TIPS       = "EXTRA_TIPS";
    public static final String EXTRA_VENUE_ID   = "EXTRA_VENUE_ID";
    public static final String EXTRA_PHOTOS     = "EXTRA_PHOTOS";

    private VenueResult mVenueResult;

    @BindView(R.id.venueAppbar) AppBarLayout venueAppbar;
    @BindView(R.id.venueCollapsingToolbar) CollapsingToolbarLayout venueCollapsingToolbar;
    @BindView(R.id.venueToolbar) Toolbar tabsToolbar;
    @BindView(R.id.venueViewpager) ViewPager viewPager;
    @BindView(R.id.venueTabs) TabLayout tabs;
    @BindView(R.id.venueHeaderImage) KenBurnsView venueHeaderImage;
    @BindView(R.id.venueFab) FloatingActionButton fab;


    @OnClick(R.id.venueHeaderImage)
    public void imageClick() {
        Photo photo = mVenueResult.getBestPhoto();
        String image = photo.getPrefix() + "original" + photo.getSuffix();

        ImageDialog.showInstance(getApplicationContext(),
                image,
                getString(R.string.playStoreLink),
                mVenueResult.getName(),
                Commons.readableDate(photo.getCreatedAt() * 1000L));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_tabs);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_VENUE)) {
            mVenueResult = intent.getParcelableExtra(EXTRA_VENUE);

            // populate the various lists
            if (Commons.isNotNull(mVenueResult)) {
                // Add colour selection for this??
                ViewUtils.changeFabColour(this, fab, R.color.toursBackButtonBackgroundColor);

                setupUI();
                setupViewPager();

            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Thats Unexpected!!")
                        .setMessage("Looks like the tours didn't download... ")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    private void setupUI() {
        setSupportActionBar(tabsToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mVenueResult.getName());  // reveived in start intent

        setStatusBarColor(false, R.color.colorPrimaryDark);

        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        final Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.setDuration(getResources().getInteger(R.integer.gallery_alpha_duration) * 2);
        fab.setAnimation(a);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            ViewUtils.setViewHeight(venueAppbar, C.IMAGE_HEIGHT - actionBarHeight, true);
            ViewUtils.setViewHeight(venueHeaderImage, C.IMAGE_HEIGHT - actionBarHeight, false);
        } else {
            ViewUtils.setViewHeight(venueAppbar, C.IMAGE_HEIGHT, true);
            ViewUtils.setViewHeight(venueHeaderImage, C.IMAGE_HEIGHT, false);
        }

        try {
            String size = ((int) ViewUtils.dpFromPx(this, C.IMAGE_WIDTH)) + "x" + ((int) ViewUtils.dpFromPx(this, C.IMAGE_HEIGHT));
            String photo = mVenueResult.getBestPhoto().getPrefix() + size + mVenueResult.getBestPhoto().getSuffix();

            Glide.with(this)
                    .load(photo)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(new DrawableImageViewTarget(venueHeaderImage) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            super.setResource(resource);
                            if(resource != null) {
                                final Bitmap bitmap = ImageUtils.drawableToBitmap(resource);
                                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        if (!bitmap.isRecycled()) {
                                            if (palette != null) {
                                                Palette.Swatch s = palette.getVibrantSwatch();
                                                if (s == null)
                                                    s = palette.getDarkVibrantSwatch();
                                                if (s == null)
                                                    s = palette.getLightVibrantSwatch();
                                                if (s == null)
                                                    s = palette.getMutedSwatch();

                                                setColors(s.getTitleTextColor(), s.getRgb(), s.getBodyTextColor());
                                            }
                                        } else
                                            Log.d(TAG, "onGenerated: BITMAP RECYCLED");
                                    }
                                });
                            }
                        }
                        });

        } catch (Exception e) {
            venueHeaderImage.setImageResource(R.drawable.no_image);
        }
    }

    private void setColors(final int titleTextColor, final int rgb, int body) {

        if (C.HAS_L) {
            //TEXT COLOR
            int colorFromText = ContextCompat.getColor(VenueTabsActivity.this, R.color.white);
            ValueAnimator textAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromText, titleTextColor);
            textAnimation.setDuration(250); // milliseconds
            textAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {

                    Drawable drawableIcon = ContextCompat.getDrawable(VenueTabsActivity.this, R.drawable.ic_back_white);
                    assert drawableIcon.getConstantState() != null;
                    Drawable matchText = drawableIcon.getConstantState().newDrawable();
                    matchText.mutate().setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.MULTIPLY);
                    fab.setImageDrawable(matchText);

//                    drawableIcon = new IconicsDrawable(AttractionShowcaseImageActivity.this, CommunityMaterial.Icon.cmd_folder_image).color((int) animator.getAnimatedValue()).sizeDp(16);
//                    mFabWallpaperButton.setImageDrawable(drawableIcon);

                    venueCollapsingToolbar.setExpandedTitleColor((int) animator.getAnimatedValue());
                    venueCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(VenueTabsActivity.this, R.color.white));
                }
            });

            // BACKGROUND COLOR
            int colorFromRgb = ContextCompat.getColor(VenueTabsActivity.this, R.color.colorPrimary);
            ValueAnimator rgbAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFromRgb, rgb);
            rgbAnimation.setDuration(250); // milliseconds
            rgbAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {

                    ColorStateList stateList = new ColorStateList(
                            new int[][]{new int[]{}},
                            new int[]{(int) animator.getAnimatedValue(), titleTextColor});

                    fab.setBackgroundTintList(stateList);
                    viewPager.setBackgroundTintList(stateList);
                    tabs.setBackgroundColor((int) animator.getAnimatedValue());
                    tabs.setTabTextColors(titleTextColor, ContextCompat.getColor(VenueTabsActivity.this, R.color.white));
                    venueCollapsingToolbar.setContentScrimColor(rgb);
                }
            });

            // START COLOR CHANGE ANIMATIONS
            textAnimation.start();
            rgbAnimation.start();
            setStatusBarColor(true, rgb);

        } else {
            venueCollapsingToolbar.setExpandedTitleColor(titleTextColor);
            venueCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(VenueTabsActivity.this, R.color.white));
            venueCollapsingToolbar.setStatusBarScrimColor(rgb);
            fab.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            fab.getDrawable().setTint(ContextCompat.getColor(this, android.R.color.white));
            viewPager.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            tabs.getBackground().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);
            tabs.setTabTextColors(titleTextColor, ContextCompat.getColor(VenueTabsActivity.this, R.color.white));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupViewPager() {
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        Bundle bundle = null;
        if (Commons.isNotNull(mVenueResult)) {
            bundle = new Bundle();
            bundle.putParcelable(EXTRA_VENUE, mVenueResult);
            bundle.putString(EXTRA_VENUE_URL, getIntent().getStringExtra(EXTRA_VENUE_URL));
        }
        adapter.addFragment(new VenueInfoFragment(), getString(R.string.shared_string_info), bundle);

/*
        bundle = new Bundle();
        bundle.putString(EXTRA_VENUE_ID, mVenueResult.getId());
        bundle.putParcelableArrayList(EXTRA_PHOTOS, mVenueResult.getPhotosVenuePhotos().getGroupsPhotos());
        adapter.addFragment(new VenuePhotosFragment(), "Photos", bundle);


        bundle = new Bundle();
        bundle.putString(EXTRA_VENUE_ID, mVenueResult.getId());
        bundle.putParcelableArrayList(EXTRA_TIPS, mVenueResult.getTips().getGroupsTips());
        adapter.addFragment(new VenueTipsFragment(), "Tips", bundle);
*/

        viewPager.setAdapter(adapter);

//        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /* EMPTY */ }

            @Override
            public void onPageSelected(int position) { onScrollView(false); }

            @Override
            public void onPageScrollStateChanged(int state) { /* EMPTY */ }
        });
    }


    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);

        ViewUtils.hideView(fab, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fsq_venue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.menu_show_on_map:
                getIntent().setClass(this, MapActivity.class);
                setResult(RESULT_OK, getIntent());
                startActivity(getIntent());
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onScrollView(boolean hide) {
        if (hide) {
            fab.hide();
        } else {
            fab.show();
        }
    }
}