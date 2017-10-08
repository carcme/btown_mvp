package me.carc.btownmvp.map.route;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Methods to encode and decode a polyline with Google polyline encoding/decoding scheme.
 * @see <a href="https://developers.google.com/maps/documentation/utilities/polylinealgorithm">Google polyline algorithm</a>
 *
 * From package org.osmdroid.bonuspack.utils;
 */

public class PointsEncoder {

    private static StringBuffer encodeSignedNumber(int num) {
        int sgn_num = num << 1;
        if (num < 0) {
            sgn_num = ~(sgn_num);
        }
        return(encodeNumber(sgn_num));
    }

    private static StringBuffer encodeNumber(int num) {
        StringBuffer encodeString = new StringBuffer();
        while (num >= 0x20) {
            int nextValue = (0x20 | (num & 0x1f)) + 63;
            encodeString.append((char)(nextValue));
            num >>= 5;
        }
        num += 63;
        encodeString.append((char)(num));
        return encodeString;
    }

    /**
     * Encode a polyline with Google polyline encoding method
     * @param polyline the polyline
     * @param precision 1 for a 6 digits encoding, 10 for a 5 digits encoding.
     * @return the encoded polyline, as a String
     */
    public static String encode(ArrayList<GeoPoint> polyline, double  precision) {
        StringBuilder encodedPoints = new StringBuilder();
        double prev_lat = 0, prev_lng = 0;
        for (GeoPoint trackpoint:polyline) {
            double lat = trackpoint.getLatitude() / precision;
            double lng = trackpoint.getLongitude() / precision;
            encodedPoints.append(encodeSignedNumber((int)(lat - prev_lat)));
            encodedPoints.append(encodeSignedNumber((int)(lng - prev_lng)));
            prev_lat = lat;
            prev_lng = lng;
        }
        return encodedPoints.toString();
    }

    /**
     * Decode a "Google-encoded" polyline
     * @param encodedString recieved string to decode
     * @param precision 1 for a 6 digits encoding of lat and lon, 10 for a 5 digits encoding.
     * @param hasAltitude if the polyline also contains altitude (GraphHopper routes, with altitude in cm).
     * @return the polyline.
     */
    public static ArrayList<GeoPoint> decode(String encodedString, int precision, boolean hasAltitude) {
        int index = 0;
        int len = encodedString.length();
        int lat = 0, lng = 0, alt = 0;
        ArrayList<GeoPoint> polyline = new ArrayList<>(len/3);
        //capacity estimate: polyline size is roughly 1/3 of string length for a 5digits encoding, 1/5 for 10digits.

        while (index < len) {
            int b, shift, result;
            shift = result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            if (hasAltitude){
                shift = result = 0;
                do {
                    b = encodedString.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dalt = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                alt += dalt;
            }

            GeoPoint p = new GeoPoint(lat*precision, lng*precision);
            polyline.add(p);
        }
        return polyline;
    }
}
