package me.carc.btownmvp.map.search.model;

import java.io.Serializable;

/**
 * Created by Carc.me on 22.04.16.
 * <p/>
 * Catalogue card view
 */
public class SearchItem implements Serializable {

    private String name;
    private String address;
    private int resIcon;
    private String resUrl;
    private double distance;

    public SearchItem(String title, String addy, String iconUrl, double dist) {
        name = title;
        address = addy;
        resIcon = 0;
        resUrl = iconUrl;
        distance = dist;
    }
    public SearchItem(String title, String addy, int iconRes, double dist) {
        name = title;
        address = addy;
        resIcon = iconRes;
        resUrl = "";
        distance = dist;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getResIcon() {
        return resIcon;
    }

    public String getResUrl() {
        return resUrl;
    }

    public double getDistance() {
        return distance;
    }
}
