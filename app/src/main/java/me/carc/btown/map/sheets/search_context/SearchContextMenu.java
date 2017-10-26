package me.carc.btown.map.sheets.search_context;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.common.interfaces.SimpleClickListener;
import me.carc.btown.data.model.OverpassQueryResult;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.db.history.HistoryEntry;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.model.Place;
import me.carc.btown.map.sheets.share.ShareMenu;

/**
 * Display search list options in bottomsheet
 */
public class SearchContextMenu {

    public static final int POI = 1;
    public static final int WIKI = 2;

    private Context mContext;
    private int category;
    private long dbID;
//    private Object object;
    private GeoPoint latLon;
    private String title;
    private String address;

    private SimpleClickListener onClickListener;


    enum ContextItem {
        DELETE_ITEM(CommunityMaterial.Icon.cmd_delete, R.string.shared_string_delete),
        DELETE_All(CommunityMaterial.Icon.cmd_delete_sweep, R.string.shared_string_delete_all),
        ADD_FAVORITE(CommunityMaterial.Icon.cmd_content_save, R.string.favorite_save),
        ADD_BOOKMARK(CommunityMaterial.Icon.cmd_content_save, R.string.bookmark_add),
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


    public static void show(int category, Object obj, Context appContext, SimpleClickListener clickListener) {

        SearchContextMenu menu = new SearchContextMenu(appContext);

        if (obj instanceof OverpassQueryResult.Element) {
            OverpassQueryResult.Element element = (OverpassQueryResult.Element) obj;
            menu.category = POI;
//            menu.object = element;
            menu.dbID = element.id;
            menu.latLon = element.getGeoPoint();
            menu.title = element.tags.name;
            menu.address = element.tags.getAddress();  // share the address

        } else if (obj instanceof WikiQueryPage) {
            WikiQueryPage page = (WikiQueryPage) obj;
            menu.category = WIKI;
//            menu.object = page;
            menu.dbID = page.pageId();
            menu.latLon = new GeoPoint(page.getLat(), page.getLon());
            menu.title = page.title();
            menu.address = page.fullurl();              // share the web link

        } else if (obj instanceof Place) {
            Place place = (Place) obj;
            menu.category = category;
            menu.dbID = place.getOsmId();
            menu.latLon = place.getGeoPoint();
            menu.title = place.getName();
            menu.address = place.getAddress();
        }

        menu.onClickListener = clickListener;
        SearchContextDialogFragment.showInstance(menu);
    }

    private SearchContextMenu(Context applicationContext) {
        mContext = applicationContext;
    }

    public AppCompatActivity getCurrentActivity() {
        return ((App) mContext).getCurrentActivity();
    }

    public List<ContextItem> getItems() {
        List<ContextItem> list = new LinkedList<>();

        if (category == POI)
            list.add(SearchContextMenu.ContextItem.ADD_FAVORITE);

        else if (category == WIKI)
            list.add(SearchContextMenu.ContextItem.ADD_BOOKMARK);

        else if(category == SearchDialogFragment.SEARCH_ITEM_HISTORY || category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
            list.add(ContextItem.DELETE_ITEM);
            list.add(ContextItem.DELETE_All);
        }
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

            case ADD_FAVORITE:
                addToDatabase();
//                addItem();
                break;

            case ADD_BOOKMARK:
                addToDatabase();
                //addItem();
                break;

            case SHARE:
                ShareMenu.show(latLon, title, address, mContext);
                break;
        }
    }
/*
    private void addItem() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App) mContext.getApplicationContext()).getDB();

                if (category == POI) {
                    FavoriteEntry entry = new FavoriteEntry((OverpassQueryResult.Element)object);
                    db.favoriteDao().insert(entry);

                    getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getCurrentActivity(), getCurrentActivity().getText(R.string.favorite_saved), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if(category == WIKI) {
                    BookmarkEntry entry = new BookmarkEntry((WikiQueryPage)object);
                    db.bookmarkDao().insert(entry);

                    getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getCurrentActivity(), getCurrentActivity().getText(R.string.bookmark_addded), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
*/
    private void deleteItem() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = ((App) mContext.getApplicationContext()).getDB();

                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    FavoriteEntry entry = db.favoriteDao().findByOsmId(dbID);
                    db.favoriteDao().delete(entry);
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    HistoryEntry entry = db.historyDao().findByOsmId(dbID);
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
                AppDatabase db = ((App) mContext.getApplicationContext()).getDB();

                if (category == SearchDialogFragment.SEARCH_ITEM_FAVORITE) {
                    db.favoriteDao().nukeTable();
                } else if (category == SearchDialogFragment.SEARCH_ITEM_HISTORY) {
                    db.historyDao().nukeTable();
                }
                updateListAdapters();
            }
        });
    }

    private void addToDatabase() {
        onClickListener.OnClick(category);
    }

    private void updateListAdapters() {
        onClickListener.OnClick(category);
    }

    void saveMenu(Bundle bundle) {

    }

    int getCategory() {
        return category;
    }

}
