package me.carc.btown.ui.front_page;

import me.carc.btown.R;
import me.carc.btown.map.MapActivity;
import me.carc.btown.settings.SettingsActivity;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.externalLinks.ExternalLinksActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;
import me.carc.btown.ui.front_page.getting_about.TransportActivity;
import me.carc.btown.ui.front_page.good_to_know.GoodToKnowActivity;

/**
 * Set up the front page menu items
 *
 * Created by bamptonm on 18/12/2017.
 */

public enum MenuItem {
    MAP(R.drawable.ic_fp_map_main, R.color.color_map_dark, R.string.front_menu_map, R.string.front_menu_map_desc, MapActivity.class, FrontPageActivity.RESULT_NONE),
    TOURS(R.drawable.ic_fp_brochure, R.color.color_tours_dark, R.string.front_menu_tours, R.string.front_menu_tours_desc, CatalogueActivity.class, FrontPageActivity.RESULT_NONE),
    FOURSQUARELISTS(R.drawable.ic_fp_notepad, R.color.color_lists, R.string.front_menu_lists, R.string.front_menu_lists_desc, FourSquareListsActivity.class, FrontPageActivity.RESULT_FSQ),
    EXTERNAL_LINKS(R.drawable.ic_fp_network, R.color.color_lists, R.string.front_menu_external, R.string.front_menu_external_desc, ExternalLinksActivity.class, FrontPageActivity.RESULT_NONE),
    GET_AROUND(R.drawable.ic_fp_travel, R.color.color_getting_around_dark, R.string.front_menu_getting_around, R.string.front_menu_getting_around_desc, TransportActivity.class, FrontPageActivity.RESULT_GETTING_ROUND),
    GOOD_TO_KNOW_AROUND(R.drawable.ic_fp_goodtoknow, R.color.color_good_to_know_dark, R.string.front_menu_good_to_know, R.string.front_menu_good_to_know_desc, GoodToKnowActivity.class, FrontPageActivity.RESULT_NONE),
    SETTINGS(R.drawable.ic_fp_settings, R.color.color_settings, R.string.front_menu_settings, R.string.front_menu_settings_desc, SettingsActivity.class, FrontPageActivity.RESULT_SETTINGS);

    final int iconDrawable;
    final int iconColorId;
    final int titleResourceId;
    final int subTitleResourceId;
    final Class<?> cls;
    final int resultKey;

    MenuItem(int drawable, int iconColorId, int titleResourceId, int subTitleResourceId, Class<?> cls, int resultKey) {
        this.iconDrawable = drawable;
        this.iconColorId = iconColorId;
        this.titleResourceId = titleResourceId;
        this.subTitleResourceId = subTitleResourceId;
        this.cls = cls;
        this.resultKey = resultKey;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getSubTitleResourceId() {
        return subTitleResourceId;
    }

    public int geticonColorId() { return iconColorId; }

    public Class<?> getStartActivityClass() { return cls; }

    public int getResultKey() { return resultKey; }

    public int getIconDrawable() {
        return iconDrawable;
    }
}