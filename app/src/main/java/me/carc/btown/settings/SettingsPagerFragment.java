/*    Transportr
 *    Copyright (C) 2013 - 2016 Torsten Grote
 *
 *    This program is Free Software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.carc.btown.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.aboutlibraries.LibsBuilder;

import me.carc.btown.R;


public class SettingsPagerFragment extends Fragment {

    public static final String TAG_ID = "SettingsPagerFragment";

    private static final int TAB_COUNT = 3;

    private static final int SETTING_TAB = 0;
    private static final int ABOUT_TAB = 1;
    private static final int LIBS_TAB = 2;

    AboutPagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_main, container, false);

        final ViewPager viewPager = view.findViewById(R.id.pager);

        // don't recreate the fragments when changing tabs
        viewPager.setOffscreenPageLimit(TAB_COUNT);

        mPagerAdapter = new AboutPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        final TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        return view;
    }

    private class AboutPagerAdapter extends FragmentPagerAdapter {
        private AboutPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == ABOUT_TAB) {
                return new AboutFragment();
            } else if (i == LIBS_TAB) {
                return new LibsBuilder().withFields(R.string.class.getFields()).fragment();
            } else if (i == SETTING_TAB) {
               return new SettingsTabFragment();
            }
            return new SettingsTabFragment();
//            return new AboutDevelopersFragment();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            if (i == ABOUT_TAB) {
                return getString(R.string.shared_string_about);
            } else if (i == LIBS_TAB) {
                return getString(R.string.tab_libraries);
            }
            return getString(R.string.shared_string_settings);
        }
    }
}
