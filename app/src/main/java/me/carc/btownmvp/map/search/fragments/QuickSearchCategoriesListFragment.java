package me.carc.btownmvp.map.search.fragments;

import me.carc.btownmvp.R;

/**
 * Created by bamptonm on 20/09/2017.
 */

public class QuickSearchCategoriesListFragment extends QuickSearchListFragment {
    public static final int TITLE = R.string.search_tab_category;

    @Override
    public SearchListFragmentType getType() {
        return SearchListFragmentType.CATEGORIES;
    }
}
