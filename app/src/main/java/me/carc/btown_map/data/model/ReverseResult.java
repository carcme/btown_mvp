package me.carc.btown_map.data.model;

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

    @SerializedName("lat")
    public double lat;

    @SerializedName("lon")
    public double lon;

    @SerializedName("display_name")
    public String display_name;

    @SerializedName("boundingbox")
    public double[] boundingbox;

    @SerializedName("address")
    public Address address= new Address();


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

            if(road != null)
                str.append(road);

            if(house_number != null)
                str.append(", ").append(house_number);

            if(postcode != null)
                str.append(", ").append(postcode);

            if(city != null)
                str.append(", ").append(city);

            return str.toString();
        }    }




/*    {
        place_id: "15337425",
                licence: "Data © OpenStreetMap contributors, ODbL 1.0. http://www.openstreetmap.org/copyright",
            osm_type: "node",
            osm_id: "1367821333",
            lat: "52.5206056",
            lon: "13.2985634",
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
