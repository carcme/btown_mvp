package me.carc.btown.map;

import org.osmdroid.util.GeoPoint;

/**
 * Allow return to previous location after entering tracking mode
 * Created by bamptonm on 09/08/2017.
 */

class BrowsingLocation {

    private int zoomLvl;
    private GeoPoint browsingLocation;

    BrowsingLocation(GeoPoint browsingLocation, int zoom) {
        this.browsingLocation = browsingLocation;
        this.zoomLvl = zoom;
    }

    int getZoomLvl() { return zoomLvl; }

    GeoPoint getBrowsingLocation() { return browsingLocation; }
}