package me.carc.btown.tours.attractionPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import me.carc.btown.tours.model.Attraction;


/**
 * Created by bamptonm on 5/8/17.
 */

class TourPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Attraction> attractionsData;
    private boolean isGermanLanguage;

    TourPagerAdapter(FragmentManager fm, ArrayList<Attraction> data, boolean isGermanLanguage) {
        super(fm);
        attractionsData = data;
        this.isGermanLanguage = isGermanLanguage;
    }

    @Override
    public Fragment getItem(int position) {
        return PlaceholderFragment.newInstance(position, attractionsData.get(position));
    }

    @Override
    public int getCount() {
        return attractionsData.size();
    }

    @Override
    public CharSequence getPageTitle(int index) {
        return attractionsData.get(index).getAttractionStopInfo(isGermanLanguage).getStopTitle();
    }

    public Attraction getTourData(int index){
        return attractionsData.get(index);
    }

}