package me.carc.btown.data;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.db.tours.model.ToursResponse;

/**
 * Created by bamptonm on 26/02/2018.
 */

public class ToursDataClass extends ViewModel {

    private static volatile ToursDataClass sSoleInstance = new ToursDataClass();
    private static volatile ToursResponse mToursPreLoad;

    //private constructor.
    private ToursDataClass(){}

    public static ToursDataClass getInstance() {
        return sSoleInstance;
    }

    public void setTourResult(ToursResponse tourResult) {
        mToursPreLoad = tourResult;
    }

    public boolean hasTours() {
        return mToursPreLoad != null;
    }

    public ArrayList<TourCatalogueItem> getAllTours() {
        return mToursPreLoad.tours;
    }

    public TourCatalogueItem getTourCatalogue(int index) {
        return mToursPreLoad.tours.get(index);
    }

    public ArrayList<Attraction> getTourAttractions(int index) {
        return new ArrayList<Attraction>(mToursPreLoad.tours.get(index).getAttractions());
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