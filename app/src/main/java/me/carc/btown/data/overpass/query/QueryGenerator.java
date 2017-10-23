package me.carc.btown.data.overpass.query;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.Map;

import me.carc.btown.Utils.MapUtils;
import me.carc.btown.data.overpass.output.OutputFormat;

/**
 * Generate the overpass query
 * Created by bamptonm on 21/09/2017.
 */
public class QueryGenerator {

    public OverpassQuery generator(HashMap<String, String> tags, GeoPoint point) {
        return generator(tags, MapUtils.getBoundsFromPoint(point));
    }

    public OverpassQuery generator(HashMap<String, String> tags, BoundingBox box) {
        OverpassQuery query = new OverpassQuery()
                .format(OutputFormat.JSON)
                .timeout(10)
                .boundingBox(box.getLatSouth(), box.getLonWest(), box.getLatNorth(), box.getLonEast());

        OverpassFilterQuery filter = new OverpassFilterQuery(query);

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            filter.node();
            filter.custom(entry.getValue(), entry.getKey());  //  NOTICE: the HashMap key/value is switched from usual use
            filter.tag("name");
//            filter.boundingBox(box.getLatSouth(), box.getLonWest(), box.getLatNorth(), box.getLonEast());
            filter.prepareNext();
        }
        filter.end().output(100);

        return query;
    }
}
