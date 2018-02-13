package me.carc.btown.ui.front_page;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import me.carc.btown.R;
import me.carc.btown.map.MapActivity;
import me.carc.btown.settings.SettingsActivity;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.top_pick_lists.FourSquareListsActivity;
import me.carc.btown.ui.front_page.getting_about.TransportActivity;
import me.carc.btown.ui.front_page.good_to_know.GoodToKnowActivity;

/**
 * Set up the front page menu items
 *
 * Created by bamptonm on 18/12/2017.
 */

public enum MenuItem {
    MAP(FontAwesome.Icon.faw_map_marker, R.drawable.ic_map_main, R.color.color_map_dark, R.string.front_menu_map, R.string.front_menu_map_desc, MapActivity.class, FrontPageActivity.RESULT_NONE),
    TOURS(FontAwesome.Icon.faw_map_signs, R.drawable.ic_brochure, R.color.color_tours_dark, R.string.front_menu_tours, R.string.front_menu_tours_desc, CatalogueActivity.class, FrontPageActivity.RESULT_NONE),
//    SEARCH(FontAwesome.Icon.faw_search, R.color.sheet_icon_color, R.string.shared_string_search, R.string.front_menu_subtitle, MapActivity.class, FrontPageActivity.RESULT_NONE),
    LISTS(FontAwesome.Icon.faw_list_alt, R.drawable.ic_notepad, R.color.color_lists, R.string.front_menu_lists, R.string.front_menu_lists_desc, FourSquareListsActivity.class, FrontPageActivity.RESULT_FSQ),
    GET_AROUND(FontAwesome.Icon.faw_train, R.drawable.ic_travel, R.color.color_getting_around_dark, R.string.front_menu_getting_around, R.string.front_menu_getting_around_desc, TransportActivity.class, FrontPageActivity.RESULT_GETTING_ROUND),
    GOOD_TO_KNOW_AROUND(FontAwesome.Icon.faw_info, R.drawable.ic_goodtoknow, R.color.color_good_to_know_dark, R.string.front_menu_good_to_know, R.string.front_menu_good_to_know_desc, GoodToKnowActivity.class, FrontPageActivity.RESULT_NONE),
//    TRENDING(FontAwesome.Icon.faw_foursquare, R.color.sheet_icon_color, R.string.explore_trending, R.string.front_menu_subtitle, MapActivity.class, FrontPageActivity.RESULT_NONE),
//    FOOD(FontAwesome.Icon.faw_bars, R.color.sheet_icon_color, R.string.nearby_food_and_drink, R.string.front_menu_subtitle, MapActivity.class, FrontPageActivity.RESULT_NONE),
//    WIKI(FontAwesome.Icon.faw_wikipedia_w, R.color.sheet_icon_color, R.string.wikipedia, R.string.front_menu_subtitle, MapActivity.class, FrontPageActivity.RESULT_NONE),
    SETTINGS(FontAwesome.Icon.faw_cog, R.drawable.ic_smartphone_settings, R.color.color_settings, R.string.front_menu_settings, R.string.front_menu_settings_desc, SettingsActivity.class, FrontPageActivity.RESULT_SETTINGS);

    final FontAwesome.Icon iconResourceId;
    final int iconDrawable;
    final int iconColorId;
    final int titleResourceId;
    final int subTitleResourceId;
    final Class<?> cls;
    final int resultKey;

    MenuItem(FontAwesome.Icon iconResourceId, int drawable, int iconColorId, int titleResourceId, int subTitleResourceId, Class<?> cls, int resultKey) {
        this.iconResourceId = iconResourceId;
        this.iconDrawable = drawable;
        this.iconColorId = iconColorId;
        this.titleResourceId = titleResourceId;
        this.subTitleResourceId = subTitleResourceId;
        this.cls = cls;
        this.resultKey = resultKey;
    }

    public FontAwesome.Icon getIconResourceId() {
        return iconResourceId;
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