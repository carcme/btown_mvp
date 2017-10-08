package me.carc.btownmvp.Utils;


import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.text.MessageFormat;
import java.util.Comparator;

import me.carc.btownmvp.map.search.model.Place;


/**
 * Created by Carc.me on 10.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class MapUtils {

    private static final double BOUNDS_FROM_POINT_LIMIT = 0.00012;
    public static final double MIN_LATITUDE = -85.0511;
    public static final double MAX_LATITUDE = 85.0511;
    public static final double LATITUDE_TURN = 180.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;
    public static final double LONGITUDE_TURN = 360.0;

    // TODO change the hostname back to osm.org once HTTPS works for it
    // https://github.com/openstreetmap/operations/issues/2
    private static final String BASE_SHORT_OSM_URL = "https://openstreetmap.org/go/";


    public static float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }


    public static String getFormattedDistance(double meters) {
        double mainUnitInMeters = 1000;
        String mainUnitStr = "km";
        if (meters >= 100 * mainUnitInMeters) {
            return (int) (meters / mainUnitInMeters + 0.5) + " " + mainUnitStr;
        } else if (meters > 9.99f * mainUnitInMeters) {
            return MessageFormat.format("{0,number,#.#} " + mainUnitStr, ((float) meters) / mainUnitInMeters).replace('\n', ' ');
        } else if (meters > 0.999f * mainUnitInMeters) {
            return MessageFormat.format("{0,number,#.##} " + mainUnitStr, ((float) meters) / mainUnitInMeters).replace('\n', ' ');
        } else {
            return ((int) (meters + 0.5)) + " m";
        }
    }

    public static String getFormattedAlt(double alt) {
        return ((int) (alt + 0.5)) + " m";
    }

    public static String getFormattedDuration(int seconds) {
        int hours = seconds / (60 * 60);
        int minutes = (seconds / 60) % 60;
        if (hours > 0) {
            return hours + " "
                    + "h"
                    + (minutes > 0 ? " " + minutes + " "
                    + "min" : "");
        } else {
            return minutes + " min";
        }
    }


    public static double bearingBetweenLocations(GeoPoint a, GeoPoint b) {
        if(a == null || b == null) return 0;

        double PI = 3.14159;
        double lat1 = a.getLatitude() * PI / 180;
        double long1 = a.getLongitude() * PI / 180;
        double lat2 = b.getLatitude() * PI / 180;
        double long2 = b.getLongitude() * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    public static double getDistance(GeoPoint p, double latitude, double longitude) {
        if(p == null) return 0;
        return getDistance(p.getLatitude(), p.getLongitude(), latitude, longitude);
    }


    private static double toRadians(double angdeg) {
//		return Math.toRadians(angdeg);
        return angdeg / 180.0 * Math.PI;
    }

    public static String buildGeoUrl(double latitude, double longitude, int zoom) {
        return "geo:" + ((float) latitude) + "," + ((float) longitude) + "?z=" + zoom;
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372.8; // for haversine use R = 6372.8 km instead of 6371 km
        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        //double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //return R * c * 1000;
        // simplyfy haversine:
        return (2 * R * 1000 * Math.asin(Math.sqrt(a)));
    }

    public static double getLongitudeFromTile(double zoom, double x) {
        return x / getPowZoom(zoom) * 360.0 - 180.0;
    }

    public static double getPowZoom(double zoom) {
        if (zoom >= 0 && zoom - Math.floor(zoom) < 0.001f) {
            return 1 << ((int) zoom);
        } else {
            return Math.pow(2, zoom);
        }
    }
    public static double getLatitudeFromTile(float zoom, double y) {
        int sign = y < 0 ? -1 : 1;
        return Math.atan(sign * Math.sinh(Math.PI * (1 - 2 * y / getPowZoom(zoom)))) * 180d / Math.PI;
    }

    public static BoundingBox getBoundsFromPoint(GeoPoint p) {
        return new BoundingBox(
                p.getLatitude()  + BOUNDS_FROM_POINT_LIMIT,
                p.getLongitude() + BOUNDS_FROM_POINT_LIMIT,
                p.getLatitude()  - BOUNDS_FROM_POINT_LIMIT,
                p.getLongitude() - BOUNDS_FROM_POINT_LIMIT);
    }

    public static class DistanceComparator implements Comparator<Place> {
        boolean closestFirst;
        public DistanceComparator(boolean closestFirst) {
            this.closestFirst = closestFirst;
        }
        @Override
        public int compare(Place lhs, Place rhs) {
            Double d1 = lhs.getDistance();
            Double d2 = rhs.getDistance();
            if(closestFirst) {
                if (d1.compareTo(d2) < 0) {
                    return -1;
                } else if (d1.compareTo(d2) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (d1.compareTo(d2) > 0) {
                    return -1;
                } else if (d1.compareTo(d2) < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    public static class TimeStampComparator implements Comparator<Place> {
        boolean newstFirst;
        public TimeStampComparator(boolean newstFirst) {
            this.newstFirst = newstFirst;
        }

        @Override
        public int compare(Place lhs, Place rhs) {
            Long ts1 = lhs.getTimestamp();
            Long ts2 = rhs.getTimestamp();
            if(newstFirst) {
                if (ts1.compareTo(ts2) > 0) {
                    return -1;
                } else if (ts1.compareTo(ts2) < 0) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (ts1.compareTo(ts2) < 0) {
                    return -1;
                } else if (ts1.compareTo(ts2) > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}

