package me.carc.btownmvp.map;

import me.carc.btownmvp.BasePresenter;
import me.carc.btownmvp.BaseView;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.sheets.model.RouteInfo;

/**
 * Created by nawin on 6/14/17.
 */

public interface IMap {

    interface View extends BaseView<Presenter> {
        void onLoadStart();
        void onLoadFinish();
        void onLoadFailed();
        void showUserMsg(int msg);
        void showSearching(boolean show);
        void setTrackingMode(boolean trackingMode);
        void setSearchgMode(boolean searchComplete);
    }

    interface Presenter extends BasePresenter {
        void initMap();
        void zoomIn();
        void zoomOut();
        void zoomToMyLocation();
        void showOrClearSearchDialog();
        void onSearchItemSelected(Place poi);
        void onShowPlaceItem(Place poi);
        void onShowFavoriteItem(Place poi);
        void onWikiLookup();
        void showPoiDialog(Object obj);
        void showCameraOrPoiMarkerListDialog();
        void routeToPoi(RouteInfo routeInfo);
    }
}
