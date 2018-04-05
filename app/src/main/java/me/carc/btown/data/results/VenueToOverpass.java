package me.carc.btown.data.results;

import android.support.annotation.Nullable;
import android.util.Log;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.carc.btown.Utils.OpeningHoursParser;
import me.carc.btown.common.Commons;
import me.carc.btown.data.all4squ.entities.Attributes;
import me.carc.btown.data.all4squ.entities.Category;
import me.carc.btown.data.all4squ.entities.GroupsAttribute;
import me.carc.btown.data.all4squ.entities.ItemsAttribute;
import me.carc.btown.data.all4squ.entities.Photo;
import me.carc.btown.data.all4squ.entities.Timeframe;
import me.carc.btown.data.all4squ.entities.VenueResult;

/**
 * Created by bamptonm on 13/11/2017.
 */

public class VenueToOverpass {

    public VenueToOverpass() {
    }

    public OverpassQueryResult.Element convertFsqToElement(VenueResult venue, @Nullable Photo photo) {
        OverpassQueryResult.Element element = new OverpassQueryResult.Element();

        element.id = (venue.getName() + venue.getLocation().getAddress()).hashCode();

        element.lat = venue.getLocation().getLat();
        element.lon = venue.getLocation().getLng();
        element.distance = venue.getLocation().getDistance();

        element.tags.name = venue.getName();
        element.tags.contactEmail = venue.getContact().getEmail();
        element.tags.contactPhone = venue.getContact().getPhone();
        element.tags.facebook = venue.getContact().getFacebook();
        element.tags.instagram = venue.getContact().getInstagram();
        element.tags.twitter = venue.getContact().getTwitter();
        element.tags.contactWebsite = venue.getVenueUrl();

        element.tags.addressCity = venue.getLocation().getCity();
        element.tags.addressPostCode = String.valueOf(venue.getLocation().getPostalCode());
        element.tags.addressStreet = venue.getLocation().getAddress();
        element.tags.addressSuburb = venue.getLocation().getState();

        element.tags.cuisine = "";
        for (Category cat : venue.getCategories()) {
            if (Commons.isNull(element.tags.getPrimaryType()))
                element.tags.amenity = cat.getShortName();
            else
                element.tags.cuisine += cat.getShortName() + ";";

        }
        if (!Commons.isEmpty(element.tags.cuisine))
            element.tags.cuisine = element.tags.cuisine.substring(0, element.tags.cuisine.lastIndexOf(";"));
/*

        if (Commons.isNotNull(venue.getPopular())) {
            ArrayList<Timeframe> timeframes = venue.getPopular().getTimeframes();
            StringBuilder sb;
            for (Timeframe frame : timeframes) {
                if (frame.isIncludesToday()) {
                    sb = new StringBuilder();
                    for (Open open : frame.getOpen()) {
                        sb.append(getOsmFormatDayOfWeek()).append(" ").append(open.getRenderedTime()).append(", ");
                    }
                    element.tags.openingHours = sb.substring(0, sb.lastIndexOf(", "));
                }
            }
        }
*/

        element.tags.openingHours = "";  // show unknown for now - need to build the open times converter

        if (Commons.isNotNull(venue.getAttributes()))
            getAttribs(element, venue.getAttributes());


        if (Commons.isNotNull(photo)) {
            element.tags.thumbnail = photo.getPrefix() + "50x50" + photo.getSuffix();
            element.tags.image = photo.getPrefix() + "original" + photo.getSuffix();
        } else {
            element.tags.thumbnail = buildIcon(venue.getCategories());
            element.tags.isIcon = true;
        }
        return element;
    }

    private String buildIcon(ArrayList<Category> categories) {
        String icon = null;
        try {
            for (Category cat : categories) {
                if (Commons.isNotNull(cat.getIcon()) && cat.isPrimary()) {
                    icon = cat.getIcon().getPrefix() + "64" + cat.getIcon().getSuffix();
                    break;
                }
            }
        } catch (Exception e) {
            icon = null;
        }
        return icon;
    }


    // TODO: 14/11/2017 filter the attribs and put them in the overpass object
    private void getAttribs(OverpassQueryResult.Element element, Attributes attribs) {
        for (GroupsAttribute grp : attribs.getGroupsAttributes()) {

            Log.d("ATTRIB", "GroupsAttribute: " + grp.getName() + " :: " + grp.getSummary());

            for (ItemsAttribute list : grp.getItemsAttributes()) {
                Log.d("ATTRIB", "ItemsAttribute: " + list.getDisplayName() + list.getDisplayValue());
            }
        }
    }

    private String buildOsmOpenTime(ArrayList<Timeframe> timeframes) {
        return "";
    }

    private String getOsmFormatDayOfWeek() {

        DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(Locale.US);
        String[] daysStr = OpeningHoursParser.getTwoLettersStringArray(dateFormatSymbols.getShortWeekdays());;
        Calendar calendar = Calendar.getInstance();
//        int day1 = OpeningHoursParser.getDayIndex(calendar.get(Calendar.DAY_OF_WEEK));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return daysStr[day];
    }

    public BoundingBox findBoundingBoxForGivenLocations(ArrayList<OverpassQueryResult.Element> elements) {
        double west = 0.0;
        double east = 0.0;
        double north = 0.0;
        double south = 0.0;

        for (int lc = 0; lc < elements.size(); lc++) {
            GeoPoint point = elements.get(lc).getGeoPoint();
            if (lc == 0) {
                north = point.getLatitude();
                south = point.getLatitude();
                west = point.getLongitude();
                east = point.getLongitude();
            } else {
                if (point.getLatitude() > north) {
                    north = point.getLatitude();
                } else if (point.getLatitude() < south) {
                    south = point.getLatitude();
                }
                if (point.getLongitude() < west) {
                    west = point.getLongitude();
                } else if (point.getLongitude() > east) {
                    east = point.getLongitude();
                }
            }
        }

        // OPTIONAL - Add some extra "padding" for better map display
        double padding = 0.01;
        north = north + padding;
        south = south - padding;
        west = west - padding;
        east = east + padding;

        return new BoundingBox(north, east, south, west);
    }
}
