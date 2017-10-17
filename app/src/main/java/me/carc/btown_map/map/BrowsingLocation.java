package me.carc.btown_map.map;

import org.osmdroid.util.GeoPoint;

/**
 * Allow return to previous location after entering tracking mode
 * Created by bamptonm on 09/08/2017.
 */

public class BrowsingLocation {

    private int zoomLvl;
    private GeoPoint browsingLocation;

    public BrowsingLocation(GeoPoint browsingLocation, int zoom) {
        this.browsingLocation = browsingLocation;
        this.zoomLvl = zoom;
    }

    public int getZoomLvl() { return zoomLvl; }

    public GeoPoint getBrowsingLocation() { return browsingLocation; }
}