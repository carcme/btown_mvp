package me.carc.btownmvp.map.search;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import me.carc.btownmvp.BasePresenter;
import me.carc.btownmvp.BaseView;
import me.carc.btownmvp.map.search.model.Place;

/**
 * Created by nawin on 6/14/17.
 */

public interface ISearch {

    interface View extends BaseView<Presenter> {
        void onShowProgressBar(boolean show);
        void updateListAdapter(int type, List<Place> places);
        void onSetSearchHint(String hint);
        void onSetClearButtonIcon(int id);
        void updateClearButtonVisibility(boolean show);
        void updateTabbarVisibility(boolean show);
    }

    interface Presenter extends BasePresenter {
        void setLocations(GeoPoint mapCenter, GeoPoint myLocation);
        void setSearchHint(int len);
        void showLongPressSelectionDialog(int type, Place place);
        void show();
        void hide();
        void setPaused(boolean set);
        void runMainSearch(String query);
        void loadCategories();
        void loadHistory();
        void loadFavorite();
        void addToHistory(Place place);
        void onShowWikiReadingList();
    }
}
