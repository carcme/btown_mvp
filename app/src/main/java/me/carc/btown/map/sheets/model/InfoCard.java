package me.carc.btown.map.sheets.model;

import android.support.annotation.NonNull;

import com.mikepenz.iconics.typeface.IIcon;

import java.io.Serializable;

/**
 * Populate the show more for poi lookup
 *
 * Created by bamptonm on 28/09/2017.
 */
public class InfoCard implements Serializable {

    private String dataString;
    private IIcon icon;
    private ItemType type;

    public enum ItemType {
        NONE,
        INFO,
        EMAIL,
        PHONE,
        WEB,
        WIKI,
        FACEBOOK,
        CLIPBOARD
    }

    public InfoCard(String info, ItemType type, @NonNull IIcon icon) {
        this.dataString = info;
        this.type = type;
        this.icon = icon;
    }

    public String getData() { return dataString; }
    public IIcon getIcon() { return icon; }
    public ItemType getType() { return type; }
}
