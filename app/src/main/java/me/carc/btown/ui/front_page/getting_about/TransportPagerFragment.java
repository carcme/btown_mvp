package me.carc.btown.ui.front_page.getting_about;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
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
import butterknife.Unbinder;
import me.carc.btown.R;
import me.carc.btown.common.interfaces.ToursScrollListener;

public class TransportPagerFragment extends Fragment {

    public static final String TAG_ID = TransportPagerFragment.class.getName();

    private static final int BAHNS_TAB = 0;
    private static final int PLANS_TAB = 1;
    private static final int TAXIS_TAB = 2;
    private static final int AIRPORT_TAB = 3;

    private static final int TAB_COUNT = AIRPORT_TAB + 1;

    private Unbinder unbinder;

    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsing_toolbar;
    @BindView(R.id.headerImage) ImageView headerImage;

    ToursScrollListener scrollListener;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        try {
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement TourListener callbacks");
        }
    }
    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
        try {
            scrollListener = (ToursScrollListener) act;
        } catch (ClassCastException e) {
            throw new ClassCastException(act.toString() + " must implement TourListener callbacks");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front_page_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        // don't recreate the fragments when changing tabs
        viewPager.setOffscreenPageLimit(TAB_COUNT);

        PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /* EMPTY*/ }

            @Override
            public void onPageSelected(int i) {
                if (i == PLANS_TAB)
                    headerImage.setImageResource(R.drawable.ic_station);
                else if (i == AIRPORT_TAB)
                    headerImage.setImageResource(R.drawable.ic_fp_travel);
                else if (i == TAXIS_TAB)
                    headerImage.setImageResource(R.drawable.ic_taxi);
                else if (i == BAHNS_TAB)
                    headerImage.setImageResource(R.drawable.ic_train);

                scrollListener.onScrollView(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) { /* EMPTY*/ }
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

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
