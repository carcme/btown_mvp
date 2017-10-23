package me.carc.btown.map;

import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

import me.carc.btown.BasePresenter;
import me.carc.btown.BaseView;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.search.model.Place;

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

        void requestGpsEnable();
        void onCameraLaunch();

        void showNavigationPopup(final GeoPoint point);
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

        void onFoodLookupFromLongPress(GeoPoint p);
        void onTouristLookupFromLongPress(GeoPoint p);
        void onWikiLookup();
        void onShowWikiOnMap(BookmarkEntry entry);
        void showPoiDialog(Object obj);
        void onShowCameraOrPoiMarkerListDialog();


        Bundle getBundle(Bundle outstate);
    }
}
