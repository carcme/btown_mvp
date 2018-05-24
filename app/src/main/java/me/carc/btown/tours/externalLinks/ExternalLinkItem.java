package me.carc.btown.tours.externalLinks;

import me.carc.btown.R;

/**
 * Set up the front page menu items
 *
 * Created by bamptonm on 18/12/2017.
 */

public enum ExternalLinkItem {

    ABANDONED_BERLIN(R.string.front_menu_abandoned_icon, R.string.front_menu_abandoned, R.string.front_menu_abandoned_desc, R.string.front_menu_abandoned_link, R.string.front_menu_abandoned_owner, 0),
    VISIT_BERLIN(R.string.front_menu_visit_berlin_icon, R.string.front_menu_visit_berlin, R.string.front_menu_visit_berlin_desc, R.string.front_menu_visit_berlin_link, R.string.front_menu_visit_berlin_owner, 2),
    NOW_THEN(R.string.front_menu_nowthen_icon, R.string.front_menu_nowthen, R.string.front_menu_nowthen_desc, R.string.front_menu_nowthen_link, R.string.front_menu_nowthen_owner, 0),
    BERLIN_LOVE(R.string.front_menu_berlin_love_icon, R.string.front_menu_berlin_love, R.string.front_menu_berlin_love_desc, R.string.front_menu_berlin_love_link, R.string.front_menu_berlin_love_owner, 2),
    SLOW_TRAVEL(R.string.front_menu_slowtravel_icon, R.string.front_menu_slowtravel, R.string.front_menu_slowtravel_desc, R.string.front_menu_slowtravel_link, R.string.front_menu_slowtravel_owner, 0),
    YEAR_IN_BERLIN(R.string.front_menu_year_berlin_icon, R.string.front_menu_year_berlin, R.string.front_menu_year_berlin_desc, R.string.front_menu_year_berlin_link, R.string.front_menu_year_berlin_owner, 0),
    COSMO(R.string.front_menu_cosmo_icon, R.string.front_menu_cosmo, R.string.front_menu_cosmo_desc, R.string.front_menu_cosmo_link, R.string.front_menu_cosmo_owner, 0);


    final int LANG_EN = 0;
    final int LANG_DE = 1;
    final int LANG_BOTH = 2;

    final int iconLink;
    final int titleResourceId;
    final int subTitleResourceId;
    final int linkResourceId;
    final int siteOwner;
    final int siteLanguage;


    ExternalLinkItem(int icon, int titleResourceId, int subTitleResourceId, int link, int owner, int lang) {
        this.iconLink = icon;
        this.titleResourceId = titleResourceId;
        this.subTitleResourceId = subTitleResourceId;
        this.linkResourceId = link;
        this.siteOwner = owner;
        this.siteLanguage = lang;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getSubTitleResourceId() {
        return subTitleResourceId;
    }

    public int getLinkResourceId() {
        return linkResourceId;
    }

    public int getIconLink() {
        return iconLink;
    }

    public int getSiteOwner() {
        return siteOwner;
    }

    public int getSiteLanguage() {
        return siteLanguage;
    }
}