package me.carc.btown.tours;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.ui.custom.MyFragmentPagerAdapter;

/**
 * Created by Carc.me on 25.04.16.
 */
public class AttractionTabsActivity extends BaseActivity implements ToursScrollListener {

    private static final String TAG = AttractionTabsActivity.class.getName();

    private MyFragmentPagerAdapter adapter;
    private String tourTitle;

    @SuppressFBWarnings("MS_CANNOT_BE_FINAL")
    public static SparseArray<GalleryItem> galleryItems;
    private List<Attraction> attractions;

    @BindView(R.id.tabsToolbar) Toolbar tabsToolbar;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.tour_tab_fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_tabs_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(CatalogueActivity.CATALOGUE_INDEX)) {
            tourTitle = intent.getStringExtra(CataloguePreviewActivity.CATALOGUE_TITLE);
                adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
                int tourID = intent.getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1);

                TourViewModel mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
                mTourViewModel.getTour(tourID).observe(this, new Observer<TourCatalogueItem>() {
                    @Override
                    public void onChanged(@Nullable final TourCatalogueItem tour) {
                        if (tour != null) {
                            attractions = tour.getAttractions();
                            // populate the various lists
                            if (Commons.isNotNull(attractions)) {
                                getImageURLs(attractions);
                                setupViewPager(attractions);
                            } else {
                                new AlertDialog.Builder(AttractionTabsActivity.this)
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
                });
        }

        // Add colour selection for this??
        ViewUtils.changeFabColour(AttractionTabsActivity.this, fab, R.color.toursBackButtonBackgroundColor);
        setupUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CataloguePreviewActivity.ATTRACTIONS_LIST, new ArrayList<Parcelable>(attractions));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void getImageURLs(List<Attraction> attractions) {
        Log.d(TAG, "getImageURLs: ");

        if (galleryItems == null) {
            int index = 0;
            galleryItems = new SparseArray<>(1);

            for (Attraction attraction : attractions) {
                GalleryItem gallery = new GalleryItem();

                gallery.setFilename(attraction.getImage());
                gallery.setCachedFilePath(CacheDir.getInstance().getCachePath() + attraction.getImage());
                gallery.setTitle(attraction.getStopName());
                gallery.setDesc(attraction.getAttractionStopInfo(isGermanLanguage()).getTeaser()[0]);

                galleryItems.put(index++, gallery);
            }
        }
    }

    private void setupUI() {
        setSupportActionBar(tabsToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tourTitle);  // reveived in start intent

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
    }

    private void setupViewPager(List<Attraction> attractions) {
        Log.d(TAG, "setupViewPager: ");

        Bundle bundle = null;
        if (Commons.isNotNull(attractions)) {
            bundle = new Bundle();
//            bundle.putParcelableArrayList(CataloguePreviewActivity.ATTRACTIONS_LIST, new ArrayList<>(attractions));
            bundle.putInt(CatalogueActivity.CATALOGUE_INDEX, getIntent().getIntExtra(CatalogueActivity.CATALOGUE_INDEX, -1));
        }

        adapter.addFragment(new AttractionTabsStopsFragment(), getString(R.string.attractions), bundle);
        adapter.addFragment(new AttractionTabsGalleryFragment(), getString(R.string.gallery), bundle);
        adapter.addFragment(new AttractionTabsNotesFragment(), getString(R.string.notes), null);
        viewPager.setAdapter(adapter);

        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                AndroidUtils.hideSoftKeyboard(AttractionTabsActivity.this, viewPager);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);

        // Clear up gallery cache
        if(Commons.isNotNull(galleryItems)) {  // Crashlytics #165
            galleryItems.clear();
            galleryItems = null;
        }

        ViewUtils.hideView(fab, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Skip the preview screen and return to tour catalogue screen
                Intent goToCatalogueActivity = new Intent(AttractionTabsActivity.this, CatalogueActivity.class);
                goToCatalogueActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToCatalogueActivity);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }, duration * 2);
    }

    /**
     * Find story data from its ID
     *
     * @param id the ID of the list item
     * @return the position of the item
     */
    private int findPositionById(int id) {
/*
        ArrayList<TourData> allStories = TourLists.getAttractions();
        for (int position = 0; position < allStories.size(); position++) {
            if (allStories.get(position).getId() == id)
                return position;
        }
*/
        return -1;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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