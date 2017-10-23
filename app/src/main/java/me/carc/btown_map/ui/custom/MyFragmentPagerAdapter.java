package me.carc.btown_map.ui.custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carc.me on 25.04.16.
 * <p/>
 * Page adapter for StoryTabsActivity
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final FragmentManager mFragmentManager;
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    public void addFragment(Fragment fragment, String title, @Nullable Bundle bundle) {
        fragment.setArguments(bundle);
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

}
