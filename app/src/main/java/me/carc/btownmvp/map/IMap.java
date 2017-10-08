package me.carc.btownmvp.map;

import android.os.Bundle;

import me.carc.btownmvp.BasePresenter;
import me.carc.btownmvp.BaseView;
import me.carc.btownmvp.data.model.RouteResult;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.sheets.model.RouteInfo;

/** Interface between the mapActivity adn the mapPresenter
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
        void setListMode(boolean listMode);

        void enableLocationDependantFab(boolean enable);
        void showRouteBottomSheet(RouteInfo info, RouteResult gHopperRes);

        void requestGpsEnable();

    }

    interface Presenter extends BasePresenter {
        void initMap(Bundle savedInstanceState);
        void onUpdateLocation();
        void zoomIn();
        void zoomOut();
        void zoomToMyLocation();
        void showOrClearSearchDialog();

        void onSearchItemSelected(Place poi);
        void onShowPlaceItem(Place poi);
        void onShowFromDatabase(int dbType, Place poi);

        void onWikiLookup();
        void showPoiDialog(Object obj);
        void onShowCameraOrPoiMarkerListDialog();
        void routeToPoi(boolean newRoute, RouteInfo routeInfo);

        Bundle getBundle(Bundle outstate);
    }
}
