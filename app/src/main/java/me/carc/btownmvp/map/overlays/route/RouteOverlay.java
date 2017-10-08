package me.carc.btownmvp.map.overlays.route;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import me.carc.btownmvp.R;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.data.model.RouteResult;
import me.carc.btownmvp.map.route.PointsEncoder;
import me.carc.btownmvp.map.route.RoadManager;
import me.carc.btownmvp.map.sheets.model.RouteInfo;

/**
 * Decode the points from route lookup and draw teh route on the map
 * Created by bamptonm on 06/10/2017.
 */

public class RouteOverlay extends FolderOverlay {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ROUTE_OVERLAY = "ROUTE_OVERLAY";
    public static final String ROUTE_MARKERS = "ROUTE_MARKERS";
    public static final String ROUTE_LINES   = "ROUTE_LINES";
    public static int SEARCH_INDEX = -3, START_INDEX = -2, DEST_INDEX = -1;

    private Context mContext;
    private MapView mMap;

    private FolderOverlay mRouteMarkers;
    private Polyline[] mRoadOverlays;

    GeoPoint startPoint;
    GeoPoint destPoint;

    final OnItineraryMarkerDragListener mItineraryListener = new OnItineraryMarkerDragListener();



    public RouteOverlay(Context context, MapView map, RouteInfo routeInfo, RouteResult route) {
        super();
        this.mContext = context;
        this.mMap = map;

        init(route, routeInfo);
    }

    private void init(RouteResult route, RouteInfo routeInfo) {
        setName(ROUTE_OVERLAY);
        mMap.getOverlays().add(this);

        mRouteMarkers = new FolderOverlay();
        mRouteMarkers.setName(ROUTE_MARKERS);
        mMap.getOverlays().add(mRouteMarkers);

        double[] box = route.getPath().bbox;

        mMap.zoomToBoundingBox(new BoundingBox(box[1], box[0], box[3], box[2]), true);

        updateUIWithRoads(route, routeInfo);
    }

    public void setStartPoint(GeoPoint point) {
        startPoint = point;
        addMarker(point, START_INDEX, getIcon(R.drawable.ic_marker_departure));
    }

    public void setDestinationPoint(GeoPoint point) {
        destPoint = point;
        addMarker(point, DEST_INDEX, getIcon(R.drawable.ic_marker_destination));
    }

    private Drawable getIcon(@DrawableRes int resID) {
        return ResourcesCompat.getDrawable(mContext.getResources(), resID, null);
    }


    private Marker addMarker(GeoPoint p, int index, Drawable icon) {

        Marker marker = new Marker(mMap);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setDraggable(true);
        marker.setOnMarkerDragListener(mItineraryListener);
        marker.setPosition(p);
        marker.setIcon(icon);
        marker.setRelatedObject(index);
        mRouteMarkers.add(marker);
        mMap.invalidate();

        return marker;
    }

    public OverlayManager clearRoute() {
        mRouteMarkers.closeAllInfoWindows();
        mRouteMarkers.getItems().clear();

        getItems().clear();
        OverlayManager mngr = mMap.getOverlayManager();

        mngr.remove(mRouteMarkers);
        for (Polyline roadOverlay : mRoadOverlays) mngr.remove(roadOverlay);
        mngr.remove(mRouteMarkers);
        mMap.invalidate();

        return mngr;
    }



    private void updateUIWithRoads(RouteResult route, RouteInfo routeInfo) {

        List<Overlay> mapOverlays = mMap.getOverlays();

        if (mRoadOverlays != null) {
            for (Polyline mRoadOverlay : mRoadOverlays) mapOverlays.remove(mRoadOverlay);
            mRoadOverlays = null;
        }
        if (Commons.isNull(route.getPath())) {
            return;
        }

        final ArrayList<GeoPoint> routePoints = PointsEncoder.decode(route.getPath().points, 10, false);

        route.getPath().setRoutePoints(routePoints);

        mRoadOverlays = new Polyline[route.paths.size()];

        setStartPoint(routePoints.get(0));
        setDestinationPoint(routePoints.get(routePoints.size() - 1));

        for (int i = 0; i < route.paths.size(); i++) {
            int color = ContextCompat.getColor(mMap.getContext(), R.color.roadOverlayBlue);

            Polyline roadPolyline = RoadManager.buildRoadOverlay(route.getPath(), color, 14f);

            mRoadOverlays[i] = roadPolyline;

            int vehicle = routeInfo.getOrdinal();
            if(vehicle == RouteInfo.Vehicle.WALK.ordinal() || vehicle == RouteInfo.Vehicle.BIKE.ordinal()) {
                Paint p = roadPolyline.getPaint();
                p.setPathEffect(new DashPathEffect(new float[]{40, 20}, 0));
            }


            roadPolyline.setRelatedObject(i);
            roadPolyline.setOnClickListener(new RoadOnClickListener());

            mapOverlays.add(1, roadPolyline);
        }
        selectRoad(0);
    }

    /**
     * Primary and Alternative route colors - alternative routes only available with premium account
     * @param roadIndex primary or other alternative route
     */
    private void selectRoad(int roadIndex) {

        for (int i = 0; i < mRoadOverlays.length; i++) {
            Paint p = mRoadOverlays[i].getPaint();
            if (i == roadIndex)
                p.setColor(ContextCompat.getColor(mMap.getContext(), R.color.roadOverlayBlue));
            else
                p.setColor(ContextCompat.getColor(mMap.getContext(), R.color.roadOverlayGray));
        }

        mMap.invalidate();
    }

    private class RoadOnClickListener implements Polyline.OnClickListener {
        @Override
        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
            int selectedRoad = (Integer) polyline.getRelatedObject();
            polyline.showInfoWindow(eventPos);
            selectRoad(selectedRoad);
            return true;
        }
    }

    class OnItineraryMarkerDragListener implements Marker.OnMarkerDragListener {
        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            int index = (Integer) marker.getRelatedObject();
/*
            if (index == START_INDEX)
                startPoint = marker.getPosition();
            else if (index == DEST_INDEX)
                destPoint = marker.getPosition();
*/
        }

        @Override
        public void onMarkerDragStart(Marker marker) {
        }
    }



}
