package me.carc.btown_map.tours.attractionPager;

import java.io.Serializable;

/**
 * Created by Carc.me on 22.04.16.
 * <p/>
 * Catalogue card view
 */
public class InfoCard implements Serializable {

    private String dataString;
    private int dataRes;

    public InfoCard(String info, int iconRes) {
        this.dataRes = iconRes;
        this.dataString = info;
    }

    public String getData() {
        return dataString;
    }

    public int getDataRes() {
        return dataRes;
    }
}
