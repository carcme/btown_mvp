package me.carc.btown.ui.front_page.good_to_know;

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
import me.carc.btown.R;
import me.carc.btown.common.interfaces.ToursScrollListener;


public class GoodToKnowPagerFragment extends Fragment {

    public static final String TAG_ID = "TransportPagerFragment";

    private static final int EMERGENCY_TAB = 0;
    private static final int MONEY_TAB = 1;
    private static final int INTERNET_TAB = 2;
    private static final int TAB_COUNT = INTERNET_TAB + 1;

    PagerAdapter mPagerAdapter;

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
        ButterKnife.bind(this, view);

        viewPager.setOffscreenPageLimit(TAB_COUNT);

        mPagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /*EMPTY*/ }

            @Override
            public void onPageSelected(int i) {
                if (i == EMERGENCY_TAB)
                    headerImage.setImageResource(R.drawable.ic_hospital);
                else if (i == MONEY_TAB)
                    headerImage.setImageResource(R.drawable.ic_atm);
                else if (i == INTERNET_TAB)
                    headerImage.setImageResource(R.drawable.ic_wifi);

                scrollListener.onScrollView(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) { /*EMPTY*/ }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.white));

        int color = ContextCompat.getColor(getActivity(), R.color.color_good_to_know);
        tabLayout.setBackgroundColor(color);
        collapsing_toolbar.setContentScrimColor(color);
        collapsing_toolbar.setBackgroundColor(color);

        headerImage.setImageResource(R.drawable.ic_hospital);

        return view;
    }


    private class PagerAdapter extends FragmentPagerAdapter {
        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == EMERGENCY_TAB)         return new EmergencyFragment();
            else if (i == MONEY_TAB)        return new MoneyFragment();
            else if(i == INTERNET_TAB)      return new InternetFragment();

            return new EmergencyFragment();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            if (i == EMERGENCY_TAB)       return getString(R.string.goodtoknow_tab_health);
            else if (i == MONEY_TAB)      return getString(R.string.goodtoknow_tab_money);
            else if (i == INTERNET_TAB)   return getString(R.string.goodtoknow_tab_internet);

            return getString(R.string.transport_tab_overview);
        }
    }
}
