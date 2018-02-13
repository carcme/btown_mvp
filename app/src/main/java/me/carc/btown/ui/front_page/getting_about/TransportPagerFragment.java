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

package me.carc.btown.ui.front_page.getting_about;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;

public class TransportPagerFragment extends Fragment {

    public static final String TAG_ID = TransportPagerFragment.class.getName();

    private static final int BAHNS_TAB = 0;
    private static final int PLANS_TAB = 1;
    private static final int TAXIS_TAB = 2;
    private static final int AIRPORT_TAB = 3;

    private static final int TAB_COUNT = AIRPORT_TAB + 1;

    PagerAdapter mPagerAdapter;

    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.headerImage) ImageView headerImage;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front_page_main, container, false);
        ButterKnife.bind(this, view);

        // don't recreate the fragments when changing tabs
        viewPager.setOffscreenPageLimit(TAB_COUNT);

        mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == PLANS_TAB)
                    headerImage.setImageResource(R.drawable.ic_station);
                else if (i == AIRPORT_TAB)
                    headerImage.setImageResource(R.drawable.ic_travel);
                else if (i == TAXIS_TAB)
                    headerImage.setImageResource(R.drawable.ic_taxi);
                else if (i == BAHNS_TAB)
                    headerImage.setImageResource(R.drawable.ic_train);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.white));

        int color = ContextCompat.getColor(getActivity(), R.color.color_getting_around);
        tabLayout.setBackgroundColor(color);
        collapsing_toolbar.setContentScrimColor(color);
        collapsing_toolbar.setBackgroundColor(color);

        headerImage.setImageResource(R.drawable.ic_train);

        return view;
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == PLANS_TAB)         return new TransportPlansFragment();
            else if (i == AIRPORT_TAB)  return new TransportAirportFragment();
            else if (i == TAXIS_TAB)    return new TransportTaxisFragment();

            return new TransportBahnsFragment();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            if (i == PLANS_TAB)         return getString(R.string.transport_tab_plans);
            else if (i == AIRPORT_TAB)  return getString(R.string.transport_tab_airport);
            else if (i == TAXIS_TAB)    return getString(R.string.transport_tab_taxi);

            return getString(R.string.transport_tab_overview);
        }
    }
}
