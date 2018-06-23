package me.carc.btown.ui.front_page.externalLinks;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import me.carc.btown.ui.base.MvpBasePresenter;

public class ExternalLinksPresenter extends MvpBasePresenter<ExternalLinksMvpView> {

    private static final String TAG = ExternalLinksPresenter.class.getName();

    @Inject
    public ExternalLinksPresenter() {}

    @Override
    public void attachView(ExternalLinksMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void buildMenuItems() {
        List<ExternalLinkItem> items = new LinkedList<>();
        items.add(ExternalLinkItem.VISIT_BERLIN);
        items.add(ExternalLinkItem.ABANDONED_BERLIN);
        items.add(ExternalLinkItem.NOW_THEN);
        items.add(ExternalLinkItem.BERLIN_LOVE);
        items.add(ExternalLinkItem.YEAR_IN_BERLIN);
        items.add(ExternalLinkItem.SLOW_TRAVEL);
        items.add(ExternalLinkItem.COSMO);

        getMvpView().loadFrontPageMenu(items);
    }
}