package me.carc.btown.map;

import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import me.carc.btown.BasePresenter;
import me.carc.btown.BaseView;
import me.carc.btown.data.all4squ.entities.ExploreItem;
import me.carc.btown.data.all4squ.entities.ListItems;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.db.tours.model.TourCatalogueItem;

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

        void showFsqSearchResults(ArrayList<VenueResult> results);
        void showFsqSExploreResults(String header, ArrayList<ExploreItem> results);

        void addClearDropMenuItem(boolean hasDropPins);

        void resetToursBtn();

//        void showLocationSettings(GeoPoint point, LocationRequest locationRequest, Location location);
    }

    interface Presenter extends BasePresenter {
        void initMap(Bundle savedInstanceState);
        void onUpdateLocation();
        void zoomIn();
        void zoomOut();
        void zoomToMyLocation();
        void showOrClearSearchDialog();
        void clearPoiMarkers();

        void onSearchItemSelected(Place poi);
        void onShowPlaceItem(Place poi);
        void onShowFromDatabase(int dbType, Place poi);

        void onFoodLookupFromLongPress(GeoPoint p);
        void onTouristLookupFromLongPress(GeoPoint p);
        void onWikiLookup();
        void onFsqSearch();
        void onFsqExplore();
        void onShowWikiOnMap(BookmarkEntry entry);
        void showPoiDialog(Object obj);
        void onShowCameraOrPoiMarkerListDialog();

        void onDropPin(GeoPoint p);
        void onClearPins();

        void showFsqVenue(VenueResult venueResult);
        void showFsqVenues(ArrayList<VenueResult> venues);
        void showFsqList(ListItems items);

        void hidePoiDialog();
//        void debugBtn();

        Bundle getBundle(Bundle outstate);

        void showTour(TourCatalogueItem catalogue);
        void clearTourIcons();
    }
}
