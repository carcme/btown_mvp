package me.carc.btown_map.tours.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes to hold the tour data
 * Created by bamptonm on 16/10/2017.
 */

public class AttractionsHolder {

    @SuppressWarnings("unused")
    @SerializedName("attractions")
    public List<Attraction> attractions = new ArrayList<>();

    public AttractionsHolder() {
    }
}
