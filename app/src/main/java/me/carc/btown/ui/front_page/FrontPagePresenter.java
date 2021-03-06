package me.carc.btown.ui.front_page;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import me.carc.btown.common.TinyDB;
import me.carc.btown.extras.BackgroundImageDialog;
import me.carc.btown.ui.base.MvpBasePresenter;

/**
 *
 */
public class FrontPagePresenter extends MvpBasePresenter<FrontPageMvpView> {

//    public static TourHolderResult toursPreLoad;

    @Inject
    public FrontPagePresenter() {}


    @Override
    public void attachView(FrontPageMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void setupHeader() {
        Random rand = new Random();
        int i = rand.nextInt(BackgroundImageDialog.BACKGROUNGS.length);
        getMvpView().setupHeader(TinyDB.getTinyDB().getInt(BackgroundImageDialog.FRONT_PAGE_IMAGE, BackgroundImageDialog.BACKGROUNGS[i]));
    }

    public void buildMenuItems() {
        List<MenuItem> items = new LinkedList<>();
        items.add(MenuItem.MAP);
        items.add(MenuItem.TOURS);
//        items.add(MenuItem.SEARCH);
        items.add(MenuItem.FOURSQUARELISTS);
        items.add(MenuItem.EXTERNAL_LINKS);
        items.add(MenuItem.GET_AROUND);
//        items.add(MenuItem.TRENDING);
//        items.add(MenuItem.FOOD);
//        items.add(MenuItem.WIKI);
        items.add(MenuItem.GOOD_TO_KNOW_AROUND);
        items.add(MenuItem.SETTINGS);

        getMvpView().loadFrontPageMenu(items);
    }
}
