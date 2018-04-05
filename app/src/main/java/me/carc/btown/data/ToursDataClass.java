package me.carc.btown.data;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import me.carc.btown.tours.model.Attraction;
import me.carc.btown.tours.model.TourCatalogue;
import me.carc.btown.tours.model.TourHolderResult;

/**
 * Created by bamptonm on 26/02/2018.
 */

public class ToursDataClass extends ViewModel {

    private static volatile ToursDataClass sSoleInstance = new ToursDataClass();

    private static volatile TourHolderResult mToursPreLoad;

    //private constructor.
    private ToursDataClass(){}

    public static ToursDataClass getInstance() {
        return sSoleInstance;
    }

    public void setTourResult(TourHolderResult tourResult) {
        mToursPreLoad = tourResult;
    }

    public boolean hasTours() {
        return mToursPreLoad != null;
    }

    public ArrayList<TourCatalogue> getAllTours() {
        return mToursPreLoad.tours;
    }

    public TourCatalogue getTourCatalogue(int index) {
        return mToursPreLoad.tours.get(index);
    }

    public ArrayList<Attraction> getTourAttractions(int index) {
        return mToursPreLoad.tours.get(index).getAttractions();
    }

    /**
     * get specific attraction from the specific cat
     * @param catalogue
     * @param attraction
     * @return
     */
    public Attraction getTourAttraction(int catalogue, int attraction) {
        return mToursPreLoad.tours.get(catalogue).getAttractions().get(attraction);
    }




}