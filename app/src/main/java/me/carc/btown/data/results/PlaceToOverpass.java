package me.carc.btown.data.results;

import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.map.search.model.Place;

/**
 * Convert Place to OverpassQueryResult.Element - refactor later to remove Place from code completely
 * Created by bamptonm on 25/09/2017.
 */

public class PlaceToOverpass {
    private static final String TAG = PlaceToOverpass.class.getName();

    private GeoPoint mapCenter;

    public PlaceToOverpass(IGeoPoint mapCenter) {
        this.mapCenter = (GeoPoint)mapCenter;
    }


    public OverpassQueryResult.Element convertPlace(Place place) {
        return convertPlaceToElement(place);
    }

    public List<OverpassQueryResult.Element> convertPlaces(List<Place> places) {
        List<OverpassQueryResult.Element> elements = new ArrayList<>();

        for (Place place : places) {
            elements.add(convertPlaceToElement(place));
        }

        return elements;
    }

    private OverpassQueryResult.Element convertPlaceToElement(Place place) {

        OverpassQueryResult.Element element = new OverpassQueryResult.Element();

        element.lat = place.getLat();
        element.lon = place.getLng();
        element.distance = MapUtils.getDistance(mapCenter, element.lat, element.lon);

        element.tags.name = place.getName();
        element.id = place.getOsmId();

        Field field = Commons.extractFieldByString(OverpassQueryResult.Element.Tags.class, place.getOsmKey());
        try {
            if (Commons.isNotNull(field)) {
                field.set(element.tags, place.getOsmType());
            } else {
                Log.d(TAG, "Reflection : Not found in Structure::" + place.getOsmKey());
            }
        } catch (IllegalAccessException e) {
            Log.d(TAG, "Reflection : Not found in Structure::" + place.getOsmKey());
            e.printStackTrace();
        }

        splitAddress(place.getAddress(), element);

        return element;
    }

    private void splitAddress(String address, OverpassQueryResult.Element element){
        String[] array = address.split(", ");
        try {
            element.tags.addressStreet      = Commons.isNotNull(array[0]) ? array[0] : null;
            element.tags.addressHouseNumber = Commons.isNotNull(array[1]) ? array[1] : null;
            element.tags.addressPostCode    = Commons.isNotNull(array[2]) ? array[2] : null;
            element.tags.addressCity        = Commons.isNotNull(array[3]) ? array[3] : null;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
