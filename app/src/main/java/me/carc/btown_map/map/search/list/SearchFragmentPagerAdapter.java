package me.carc.btown_map.map.search.list;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.carc.btown_map.map.search.fragments.QuickSearchCategoriesListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchFavoriteListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchHistoryListFragment;

/**
 * Created by bamptonm on 20/09/2017.
 */

public class SearchFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private final String[] fragments = new String[]{
            QuickSearchCategoriesListFragment.class.getName()
            ,
            QuickSearchFavoriteListFragment.class.getName()
            ,
            QuickSearchHistoryListFragment.class.getName()

    };
    private final int[] titleIds = new int[]{
            QuickSearchCategoriesListFragment.TITLE
            ,
            QuickSearchFavoriteListFragment.TITLE
            ,
            QuickSearchHistoryListFragment.TITLE

    };
    private final String[] titles;

    public SearchFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        titles = new String[titleIds.length];
        for (int i = 0; i < titleIds.length; i++) {
            titles[i] = mContext.getResources().getString(titleIds[i]);
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(mContext, fragments[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}