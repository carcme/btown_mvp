package me.carc.btown.tours.attractionPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.POIs;
import me.carc.btown.map.markers.MarkersOverlay;
import me.carc.btown.ui.custom.MySimplePointOverlayOptions;

/**
 * Show map and POIs of tour location
 * Created by bamptonm on 5/11/17.
 */

public class AttractionMapActivity extends BaseActivity {

    private final int PRE_PAN_DURATION = 1000;

    public static final String TITLE    = "TITLE";
    public static final String GEOPOINT = "GEOPOINT";

    public MarkersOverlay mPoiMarkers;
    private SimpleFastPointOverlay mTourOverlay;

    @BindView(R.id.attractionsMmap)
    MapView mapView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fabMapExit)
    FloatingActionButton fabMapExit;

    @OnClick(R.id.fabMapExit)
    void done() {
        onBackPressed();
    }


    @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_map_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        Intent intent = getIntent();
        if (intent.hasExtra(AttractionPagerActivity.ATTRACTION)) {
            Attraction attraction = intent.getParcelableExtra(AttractionPagerActivity.ATTRACTION);
            getSupportActionBar().setTitle(attraction.getStopName());
            configureMap(true, attraction);
        } else if (intent.hasExtra(TITLE) && intent.hasExtra(GEOPOINT)) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(TITLE));
            configureMap(true, null);  //  Does FindBugs recognise the @Nullable annotation??
        } else
            finish();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void initMarkers() {
        //POI markers:
        mPoiMarkers = new MarkersOverlay(this, mapView, "POIs");
        mPoiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        mPoiMarkers.mTextAnchorU = 0.70f;
        mPoiMarkers.mTextAnchorV = 0.27f;
        mPoiMarkers.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density);
        mapView.getOverlays().add(mPoiMarkers);
    }

    private void configureMap(boolean enableControls, @Nullable final Attraction data) {
        final GeoPoint geoAttractionPoint;
        if(Commons.isNotNull(data))
            geoAttractionPoint = new GeoPoint(data.getLocation().lat, data.getLocation().lon);
        else
            geoAttractionPoint = getIntent().getParcelableExtra(GEOPOINT);

        mapView.getController().setZoom(17f);
        mapView.getController().setCenter(geoAttractionPoint);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(enableControls);
        mapView.setTilesScaledToDpi(true);

        // allow map to be drawn before adding pois to it
        mapView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mapView.getViewTreeObserver().removeOnPreDrawListener(this);

                initMarkers();

                //tour map point of interest
                Marker poi = new Marker(mapView);
                poi.setPosition(geoAttractionPoint);
                poi.setTitle(data != null ? data.getStopName() : getIntent().getStringExtra(TITLE));
                poi.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.btown_marker_icon, null));
                mPoiMarkers.add(poi);

                // add pois to map
                if(Commons.isNotNull(data)) {
                    List<POIs> pois = data.getPOIs();
                    if (Commons.isNotNull(pois)) {

                        List<IGeoPoint> points = new ArrayList<>();     // tour points
                        for (POIs item : pois) {
                            points.add(new LabelledGeoPoint(item.lat, item.lng, item.title));
                        }

                        SimplePointTheme theme = new SimplePointTheme(points, true);
                        MySimplePointOverlayOptions opt = new MySimplePointOverlayOptions()
                                .setRadius(24)
                                .setSelectedRadius(24)
                                .setIsClickable(true)
                                .setCellSize(20);

                        mTourOverlay = new SimpleFastPointOverlay(theme, opt);
                        mTourOverlay.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
                            @Override
                            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                                Toast.makeText(mapView.getContext(), ((LabelledGeoPoint) points.get(point)).getLabel(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        mapView.getOverlays().add(mTourOverlay);
                    }
                }
                mapView.getController().animateTo(geoAttractionPoint);

                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        if(Commons.isNotNull(mPoiMarkers)) {
            mPoiMarkers.closeInfoWidows();
            mPoiMarkers.getItems().clear();
            mPoiMarkers.clear();
            mPoiMarkers = null;
            mapView.getOverlays().remove(mTourOverlay);
            mapView.getOverlays().remove(mPoiMarkers);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);
        ViewUtils.hideView(fabMapExit, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }

    @Override
    protected void onDestroy() {
        mapView.removeAllViews();
        mapView = null;
        super.onDestroy();
    }
}
