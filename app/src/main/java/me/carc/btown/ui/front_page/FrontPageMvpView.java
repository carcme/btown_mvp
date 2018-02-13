package me.carc.btown.ui.front_page;


import android.support.annotation.DrawableRes;

import java.util.List;

import me.carc.btown.ui.base.MvpView;

public interface FrontPageMvpView extends MvpView {

    void setupHeader(@DrawableRes int imageRes);
    void loadFrontPageMenu(List<MenuItem> items);
    void showError();

}
