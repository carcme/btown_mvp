package me.carc.btown_map.Utils;


import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;

/**
 * Created by Carc.me on 10.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class MapZoom {

    public static void zoomTo(MapView mapView, GeoPoint min, GeoPoint max) {
        MapTileProviderBase tileProvider = mapView.getTileProvider();
        IMapController controller = mapView.getController();
        GeoPoint center = new GeoPoint((max.getLatitude() + min.getLatitude()) / 2, (max.getLongitude() + min.getLongitude()) / 2);

        // diagonale in pixels
        int padding = 100;
        int mapWidth = mapView.getWidth() - padding;
        int mapHeight = mapView.getHeight() - padding;

        double pixels = Math.sqrt((mapWidth * mapWidth) + (mapHeight * mapHeight));
        final double requiredMinimalGroundResolutionInMetersPerPixel = ((double) new GeoPoint(min.getLatitude(), min.getLongitude()).distanceTo(max)) / pixels;
        int zoom = calculateZoom(center.getLatitude(), requiredMinimalGroundResolutionInMetersPerPixel, tileProvider.getMaximumZoomLevel(), tileProvider.getMinimumZoomLevel());
        controller.setZoom(zoom > 17 ? 17 : zoom-1);
        controller.animateTo(center);
    }

    private static int calculateZoom(double latitude, double requiredMinimalGroundResolutionInMetersPerPixel, int maximumZoomLevel, int minimumZoomLevel) {
        for (int zoom = maximumZoomLevel; zoom >= minimumZoomLevel; zoom--) {
            if (TileSystem.GroundResolution(latitude, zoom) > requiredMinimalGroundResolutionInMetersPerPixel)
                return zoom;
        }
        return 0;
    }



}

