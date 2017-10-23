package me.carc.btown_map.tours.attractionPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown_map.BaseActivity;
import me.carc.btown_map.R;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.map.markers.MarkersOverlay;
import me.carc.btown_map.tours.model.Attraction;
import me.carc.btown_map.tours.model.POIs;

/**
 * Show map and POIs of tour location
 * Created by bamptonm on 5/11/17.
 */

public class AttractionMapActivity extends BaseActivity {

    private final int PRE_PAN_DURATION = 1000;

    public static final String INDEX = "INDEX";

    @BindView(R.id.attractionsMmap)
    MapView mapView;
    @BindView(R.id.distanceIndicatorTours)
    TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_map_activity);
        ButterKnife.bind(this);

        Attraction attraction = null;;

        Intent intent = getIntent();
        if (intent.hasExtra("ATTRACTION")) {
            attraction = intent.getParcelableExtra("ATTRACTION");
        } else
            finish();

        if(Commons.isNotNull(attraction))
            configureMap(true, attraction);
        else
            finish();
    }


    public MarkersOverlay mPoiMarkers;
    private void initMarkers() {

        //POI markers:
        mPoiMarkers = new MarkersOverlay(this, mapView, "POIs");

        Drawable clusterIconD = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_cluster, null);
        Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();
        mPoiMarkers.setIcon(clusterIcon);
        mPoiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        mPoiMarkers.mTextAnchorU = 0.70f;
        mPoiMarkers.mTextAnchorV = 0.27f;
        mPoiMarkers.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density);
        mapView.getOverlays().add(mPoiMarkers);
    }

    private void configureMap(boolean enableControls, final Attraction data) {

        final GeoPoint geoAttractionPoint = new GeoPoint(data.getLocation().lat, data.getLocation().lon);

        mapView.getController().setZoom(16);
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

                // add pois to map
                ArrayList<POIs> pois = data.getPOIs();
                if(Commons.isNotNull(pois))
                    mPoiMarkers.addTourPOIs(pois, getResources(), getPackageName());

                //tour map point of interest
                Marker poi = new Marker(mapView);
                poi.setPosition(geoAttractionPoint);
                poi.setTitle(data.getStopName());
                poi.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star_yellow, null));
                mPoiMarkers.add(poi);

                mapView.getController().animateTo(geoAttractionPoint);

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(Commons.isNotNull(mPoiMarkers)) {
            mPoiMarkers.closeInfoWidows();
            mPoiMarkers.getItems().clear();
            mPoiMarkers.clear();
            mPoiMarkers = null;
        }
        mapView = null;
        super.onDestroy();
    }
}
