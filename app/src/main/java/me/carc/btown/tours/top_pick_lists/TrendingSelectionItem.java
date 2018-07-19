package me.carc.btown.tours.top_pick_lists;


import me.carc.btown.R;

/**
 * Set up the front page menu items
 * <p>
 * Created by bamptonm on 18/12/2017.
 */

public enum TrendingSelectionItem {

    topPicks("https://ss3.4sqi.net/img/categories_v2/event/default_", R.string.trending_topPicks, "topPicks"),
    food("https://ss3.4sqi.net/img/categories_v2/food/default_", R.string.trending_food, "food"),
    drinks("https://ss3.4sqi.net/img/categories_v2/nightlife/default_", R.string.trending_drinks, "drinks"),
    coffee("https://ss3.4sqi.net/img/categories_v2/food/coffeeshop_", R.string.trending_coffee, "coffee"),
    shops("https://ss3.4sqi.net/img/categories_v2/shops/default_", R.string.trending_shops, "shops"),
    arts("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/default_", R.string.trending_arts, "arts"),
    outdoors("https://ss3.4sqi.net/img/categories_v2/parks_outdoors/default_", R.string.trending_outdoors, "outdoors"),
    sights("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/museum_", R.string.trending_sights, "sights");

    final int titleResourceId;
    final String sectionalue;
    final String iconUrl;

    TrendingSelectionItem(String icon, int titleResourceId, String value) {
        this.iconUrl = icon;
        this.titleResourceId = titleResourceId;
        this.sectionalue = value;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public String getSectionValue() { return sectionalue; }

    public String getIconUrl() {
        return iconUrl;
    }
}