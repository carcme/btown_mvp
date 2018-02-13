package me.carc.btown.data.results;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by bamptonm on 27/09/2017.
 */

public class ReverseResult {

    @SerializedName("place_id")
    public String place_id;

    @SerializedName("osm_type")
    public String osm_type;

    @SerializedName("osm_id")
    public String osm_id;

    @SerializedName("lat")
    public double lat;

    @SerializedName("lon")   // changed this to lon from ln for drop pin
    public double lon;

    @SerializedName("display_name")
    public String display_name;

    @SerializedName("name")
    public String name;

    @SerializedName("boundingbox")
    public double[] boundingbox;

    @SerializedName("place_rank")
    public String place_rank;

    @SerializedName("importance")
    public String importance;

    @SerializedName("category")   // category: "leisure",
    public String tagCategory;

    @SerializedName("type")         // type: "playground",
    public String tagType;

    @SerializedName("addresstype")  //addresstype: "leisure",
    public String addresstype;


    @SerializedName("address")
    public Address address = new Address();

    public static class Address implements Serializable {
        @SerializedName("house_number")
        public String house_number;
        @SerializedName("road")
        public String road;
        @SerializedName("suburb")
        public String suburb;
        @SerializedName("city_district")
        public String city_district;
        @SerializedName("city")
        public String city;
        @SerializedName("state")
        public String state;
        @SerializedName("postcode")
        public String postcode;
        @SerializedName("country")
        public String country;
        @SerializedName("country_code")
        public String country_code;

        public String buildAddress() {

            StringBuilder str = new StringBuilder();

            if (road != null)
                str.append(road);

            if (house_number != null)
                str.append(", ").append(house_number);

            if (postcode != null)
                str.append(", ").append(postcode);

            if (city != null)
                str.append(", ").append(city);

            return str.toString();
        }
    }

    @SerializedName("extratags")
    public ExtraTags extratags = new ExtraTags();

    public static class ExtraTags implements Serializable {
        @SerializedName("image")
        public String image;

        @SerializedName("phone")
        public String phone;

        @SerializedName("contact:phone")
        public String contactPhone;

        @SerializedName("contact:email")
        public String contactEmail;

        @SerializedName("website")
        public String website;

        @SerializedName("facebook")
        public String facebook;

        @SerializedName("instagram")
        public String instagram;

        @SerializedName("twitter")
        public String twitter;

        @SerializedName("fax")
        public String fax;

        @SerializedName("contact:website")
        public String contactWebsite;

        @SerializedName("source")
        public String source;

        @SerializedName("source:name")
        public String sourceName;

        @SerializedName("source:ref")
        public String sourceRef;

        @SerializedName("url")
        public String url;

        @SerializedName("wheelchair")
        public String wheelchair;

        @SerializedName("toilets:wheelchair")
        public String wheelchairToilets;

        @SerializedName("wheelchair:description")
        public String wheelchairDescription;

        @SerializedName("opening_hours")
        public String openingHours;

        @SerializedName("internet_access")
        public String internetAccess;

        @SerializedName("fee")
        public String fee;

        @SerializedName("operator")
        public String operator;

        @SerializedName("collection_times")
        public String collectionTimes;

        @SerializedName("takeaway")
        public String takeaway;

        @SerializedName("delivery")
        public String delivery;

        @SerializedName("outdoor_seating")
        public String outdoor_seating;

        @SerializedName("religion")
        public String religion;

        @SerializedName("denomination")
        public String denomination;

        @SerializedName("smoking")
        public String smoking;

        @SerializedName("note")
        public String note;

        @SerializedName("cuisine")
        public String cuisine;

        @SerializedName("wikipedia")
        public String wikipedia;

        @SerializedName("wikidata")
        public String wikidata;

        @SerializedName("nudism")
        public String nudism;

        @SerializedName("cycleway")
        public String cycleway;

        @SerializedName("comment")
        public String comment;



    }

    @SerializedName("namedetails")
    public NameDetails namedetails = new NameDetails();

    public static class NameDetails implements Serializable {
        @SerializedName("name")
        public String name;
    }



/*    {
        place_id: "15337425",
                licence: "Data © OpenStreetMap contributors, ODbL 1.0. http://www.openstreetmap.org/copyright",
            osm_type: "node",
            osm_id: "1367821333",
            lat: "52.5206056",
            lng: "13.2985634",
            display_name: "Friedrich der Große, Luisenplatz, Charlottenburg, Charlottenburg-Wilmersdorf, Berlin, 10585, Germany",
            address: {
                memorial: "Friedrich der Große",
                road: "Luisenplatz",
                suburb: "Charlottenburg",
                city_district: "Charlottenburg-Wilmersdorf",
                city: "Berlin",
                state: "Berlin",
                postcode: "10585",
                country: "Germany",
                country_code: "de"
            },
        extratags: {
            image: "http://commons.wikimedia.org/wiki/File:Charlottenburg_Friedrich_II.jpg"
        },
        namedetails: {
            name: "Friedrich der Große"
        },
        boundingbox: [
        "52.5205056",
                "52.5207056",
                "13.2984634",
                "13.2986634"
],
        svg: "cx="13.298563400000001" cy="-52.520605600000003""
    }
*/
}
