package me.carc.btown.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import me.carc.btown.db.tours.TourRepository;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;


/**
 * Created by bamptonm on 12/05/2018.
 */

public class TourViewModel extends AndroidViewModel {

    private TourRepository mRepository;
    private LiveData<List<TourCatalogueItem>> mAllTours;
    private LiveData<List<Attraction>> mAllAttractions;


    public TourViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TourRepository(application);
        mAllTours = mRepository.getAllTours();
    }

    public LiveData<List<TourCatalogueItem>> getAllTours() {
        return mAllTours;
    }

    public LiveData<TourCatalogueItem> getTour(int id) {
        return mRepository.getTour(id);
    }


    public void insert(TourCatalogueItem tour) { mRepository.insert(tour); }
}
