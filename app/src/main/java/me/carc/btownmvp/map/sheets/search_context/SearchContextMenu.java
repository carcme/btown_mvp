package me.carc.btownmvp.map.sheets.search_context;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import me.carc.btownmvp.App;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.R;
import me.carc.btownmvp.db.favorite.FavoriteEntry;
import me.carc.btownmvp.db.history.HistoryEntry;
import me.carc.btownmvp.map.interfaces.SimpleClickListener;
import me.carc.btownmvp.map.search.SearchDialogFragment;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.sheets.share.ShareMenu;

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

    private  SimpleClickListener onClickListener;


    public enum ContextItem {
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

    public void selected(ContextItem item) {
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
                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    FavoriteEntry entry = App.get().getDB().favoriteDao().findByOsmId(osmId);
                    App.get().getDB().favoriteDao().delete(entry);
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    HistoryEntry entry = App.get().getDB().historyDao().findByOsmId(osmId);
                    App.get().getDB().historyDao().delete(entry);
                }
                updateListAdapters();
            }
        });
    }

    private void deleteAll() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    App.get().getDB().favoriteDao().nukeTable();
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    App.get().getDB().historyDao().nukeTable();
                }
                updateListAdapters();
            }
        });
    }

    private void updateListAdapters() {
        Log.d("DEAD", "updateListAdapters: ");
        onClickListener.OnClick(category);
    }

    public void saveMenu(Bundle bundle) {

    }

}
