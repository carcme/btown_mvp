package me.carc.btown.tours.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Classes to hold the tour data
 * Created by bamptonm on 16/10/2017.
 */

public class TourHolderResult {

    @SuppressWarnings("unused")
    @SerializedName("filename")
    public String filename;
    @SuppressWarnings("unused")
    @SerializedName("version")
    public int version;

    @SuppressWarnings("unused")
    @SerializedName("tours")
    public ArrayList<TourCatalogue> tours = new ArrayList<>();



}
