package me.carc.btown.data.results;

import java.util.HashMap;

/**
 * Create some test HashMaps - a selection to choose from :)
 * Created by bamptonm on 23/08/2017.
 */
@SuppressWarnings("unused")
public class OverpassDataTags {

    public static HashMap<String, String> TouristData() {
        HashMap<String, String> dataTourism = new HashMap<>();
        dataTourism.put("arts_centre", "amenity");
        dataTourism.put("planetarium", "amenity");
        dataTourism.put("theatre", "amenity");
        dataTourism.put("fountain", "amenity");
        dataTourism.put("theatre", "amenity");
        dataTourism.put("monastery", "amenity");
        dataTourism.put("citywalls", "amenity");

        dataTourism.put("gallery", "tourism");
        dataTourism.put("museum", "tourism");
        dataTourism.put("viewpoint", "tourism");
        dataTourism.put("artwork", "tourism");
        dataTourism.put("aquarium", "tourism");
        dataTourism.put("zoo", "tourism");
        dataTourism.put("theme_park", "tourism");
        dataTourism.put("attraction", "tourism");

        dataTourism.put("castle", "historic");
        dataTourism.put("building", "historic");
        dataTourism.put("city_gate", "historic");
        dataTourism.put("ruins", "historic");
        dataTourism.put("tomb", "historic");
        dataTourism.put("pillory", "historic");
        dataTourism.put("memorial", "historic");
        dataTourism.put("monument", "historic");
        dataTourism.put("monastery", "historic");

        return dataTourism;
    }

    public static HashMap<String, String> Amenity() {
        HashMap<String, String> dataTest = new HashMap<>();
        dataTest.put("", "amenity");
        return dataTest;
    }

    public static HashMap<String, String> SinglePoi() {
        HashMap<String, String> dataTest = new HashMap<>();
        dataTest.put("", "amenity");
        dataTest.put("", "tourism");
        dataTest.put("", "historic");
//        dataTest.put("", "shop");
        return dataTest;
    }

    public static HashMap<String, String> FoodDrinkData() {
        HashMap<String, String> dataTest = new HashMap<>();
        dataTest.put("bbq", "amenity");
        dataTest.put("cafe", "amenity");
        dataTest.put("fast_food", "amenity");
        dataTest.put("food_court", "amenity");
        dataTest.put("ice_cream", "amenity");
        dataTest.put("restaurant", "amenity");
        dataTest.put("bar", "amenity");
        dataTest.put("biergarten", "amenity");
        dataTest.put("pub", "amenity");
        return dataTest;
    }

    public static HashMap<String, String> RandomData() {

        HashMap<String, String> dataTest = new HashMap<>();
        dataTest.put("cafe", "amenity");
        dataTest.put("restaurant", "amenity");
        dataTest.put("memorial", "historic");
        dataTest.put("museum", "tourism");

        return dataTest;
    }
}
