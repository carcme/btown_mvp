package me.carc.btown.tours.externalLinks;


import java.util.List;

import me.carc.btown.ui.base.MvpView;

public interface ExternalLinksMvpView extends MvpView {

    void loadFrontPageMenu(List<ExternalLinkItem> items);
    void showError();

}
