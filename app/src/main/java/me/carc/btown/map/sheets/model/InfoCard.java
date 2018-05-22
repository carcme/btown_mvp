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

    private String displayString;
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
        INSTAGRAM,
        TWITTER,
        CLIPBOARD
    }

    public InfoCard(String display, String data, ItemType type, @NonNull IIcon icon) {
        this.displayString = display;
        this.dataString = data;
        this.type = type;
        this.icon = icon;
    }

    public InfoCard(String data, ItemType type, @NonNull IIcon icon) {
        this.displayString = data;
        this.dataString = data;
        this.type = type;
        this.icon = icon;
    }

    public String getDisplay() { return displayString; }
    public String getData() { return dataString; }
    public IIcon getIcon() { return icon; }
    public ItemType getType() { return type; }
}
