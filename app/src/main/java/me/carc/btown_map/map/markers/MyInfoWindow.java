package me.carc.btown_map.map.markers;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

/**
 * Created by bamptonm on 11/25/16.
 */

public abstract class MyInfoWindow extends InfoWindow {
    /**
     * @param layoutResId the id of the view resource.
     * @param mapView     the mapview on which is hooked the view
     */
    public MyInfoWindow(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
    }

    /** close all InfoWindows currently opened on this MapView */
    static public boolean closeAllInfoWindows(MapView mapView){
        boolean hasOpenWindow = false;
        ArrayList<InfoWindow> opened = getOpenedInfoWindowsOn(mapView);
        for (InfoWindow infoWindow:opened){
            infoWindow.close();
            hasOpenWindow = true;
        }
        return hasOpenWindow;
    }
}
