package me.carc.btown_map.map.sheets.search_context;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import me.carc.btown_map.App;
import me.carc.btown_map.MapActivity;
import me.carc.btown_map.R;
import me.carc.btown_map.db.AppDatabase;
import me.carc.btown_map.db.favorite.FavoriteEntry;
import me.carc.btown_map.db.history.HistoryEntry;
import me.carc.btown_map.map.interfaces.SimpleClickListener;
import me.carc.btown_map.map.search.SearchDialogFragment;
import me.carc.btown_map.map.search.model.Place;
import me.carc.btown_map.map.sheets.share.ShareMenu;

/**
 * Display search list options in bottomsheet
 */
public class SearchContextMenu {

    private Context mContext;
    private int category;
    private long osmId;
    private GeoPoint latLon;
    private String title;
    private String address;

    private SimpleClickListener onClickListener;


    enum ContextItem {
        DELETE_ITEM(CommunityMaterial.Icon.cmd_delete, R.string.shared_string_delete),
        DELETE_All(CommunityMaterial.Icon.cmd_delete_sweep, R.string.shared_string_delete_all),
        SHARE(CommunityMaterial.Icon.cmd_share_variant, R.string.shared_string_share);

        final CommunityMaterial.Icon iconResourceId;
        final int titleResourceId;

        ContextItem(CommunityMaterial.Icon iconResourceId, int titleResourceId) {
            this.iconResourceId = iconResourceId;
            this.titleResourceId = titleResourceId;
        }

        public CommunityMaterial.Icon getIconResourceId() {
            return iconResourceId;
        }

        public int getTitleResourceId() {
            return titleResourceId;
        }
    }


    public static void show(int category, Place place, MapActivity mapActivity, SimpleClickListener clickListener) {

        SearchContextMenu menu = new SearchContextMenu(mapActivity);

        menu.category = category;
        menu.osmId = place.getOsmId();
        menu.latLon = place.getGeoPoint();
        menu.title = place.getName();
        menu.address = place.getAddress();
        menu.onClickListener = clickListener;

        SearchContextDialogFragment.showInstance(menu);
    }


    private SearchContextMenu(MapActivity mapActivity) {
        mContext = mapActivity;
    }

    public MapActivity getMapActivity() {
        return (MapActivity) mContext;
    }

    public List<ContextItem> getItems() {
        List<ContextItem> list = new LinkedList<>();
        list.add(ContextItem.DELETE_ITEM);
        list.add(ContextItem.DELETE_All);
        list.add(ContextItem.SHARE);
        return list;
    }

    public String getTitle() {
        return title;
    }

    void selected(ContextItem item) {
        switch (item) {
            case DELETE_ITEM:
                deleteItem();
                break;

            case DELETE_All:
                deleteAll();
                break;

            case SHARE:
                ShareMenu.show(latLon, title, address, (MapActivity) mContext);
                break;
        }
    }

    private void deleteItem() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App)mContext.getApplicationContext()).getDB();

                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    FavoriteEntry entry = db.favoriteDao().findByOsmId(osmId);
                    db.favoriteDao().delete(entry);
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    HistoryEntry entry = db.historyDao().findByOsmId(osmId);
                    db.historyDao().delete(entry);
                }
                updateListAdapters();
            }
        });
    }

    private void deleteAll() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase db = ((App)mContext.getApplicationContext()).getDB();

                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    db.favoriteDao().nukeTable();
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    db.historyDao().nukeTable();
                }
                updateListAdapters();
            }
        });
    }

    private void updateListAdapters() {
        Log.d("DEAD", "updateListAdapters: ");
        onClickListener.OnClick(category);
    }

    void saveMenu(Bundle bundle) {

    }

    int getCategory() {
        return category;
    }

}
