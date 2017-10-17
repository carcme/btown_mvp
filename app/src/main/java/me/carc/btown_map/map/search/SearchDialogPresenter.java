package me.carc.btown_map.map.search;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import me.carc.btown_map.App;
import me.carc.btown_map.BuildConfig;
import me.carc.btown_map.MapActivity;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.MapUtils;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.data.autocomplete.AutoCompleteApi;
import me.carc.btown_map.data.autocomplete.AutoCompleteServiceProvider;
import me.carc.btown_map.data.model.AutoCompleteResult;
import me.carc.btown_map.data.model.OverpassQueryResult;
import me.carc.btown_map.data.model.PlaceToOverpass;
import me.carc.btown_map.db.AppDatabase;
import me.carc.btown_map.db.bookmark.BookmarkEntry;
import me.carc.btown_map.db.favorite.FavoriteEntry;
import me.carc.btown_map.db.history.HistoryEntry;
import me.carc.btown_map.map.IconManager;
import me.carc.btown_map.map.interfaces.SimpleClickListener;
import me.carc.btown_map.map.search.model.Place;
import me.carc.btown_map.map.sheets.search_context.SearchContextMenu;
import me.carc.btown_map.map.sheets.wiki.WikiReadingListDialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Show the search, favorite and history dialog
 * Created by bamptonm on 20/09/2017.
 */

public class SearchDialogPresenter implements ISearch.Presenter {

    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final int SEARCH_RADIUS = 20000;  // TODO: 5/12/17 make this a user setting
    public static final int AUTOCOMPLETE_THRESHOLD = 3;


    private Context mContext;
    private final ISearch.View view;

    private GeoPoint myLocation = null;
    private GeoPoint centerLatLng;
    private GeoPoint mapCenter;

    private boolean paused;
    private boolean show;
    private boolean hidden;


    public SearchDialogPresenter(Context context, ISearch.View view) {
        this.mContext = context;
        this.view = view;
        view.setPresenter(this);
    }

    private AppDatabase getDatabase() {
        return ((App) mContext.getApplicationContext()).getDB();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void setLocations(GeoPoint center, GeoPoint location) {
        mapCenter = center;
        myLocation = location;
    }

    @Override
    public void show() {
        view.onShowProgressBar(false);
    }

    @Override
    public void hide() {
        view.onShowProgressBar(false);
    }

    @Override
    public void setPaused(boolean set) {
        paused = set;
    }

    @Override
    public void setSearchHint(int len) {

        if (Commons.isNotNull(mapCenter) && Commons.isNotNull(myLocation) && len == 0) {
            double d = MapUtils.getDistance(myLocation, mapCenter.getLatitude(), mapCenter.getLongitude());
            String dist = MapUtils.getFormattedDistance(d);
            view.onSetSearchHint(mContext.getString(R.string.dist_away_from_my_location, dist));
        } else {
            view.onSetSearchHint(mContext.getString(R.string.search_all_hint));
            view.onSetClearButtonIcon(R.drawable.ic_times);
        }
    }

    @Override
    public void showLongPressSelectionDialog(int type, Place place) {
        SearchContextMenu.show(type, place, (MapActivity) mContext, new SimpleClickListener() {
            @Override
            public void OnClick(int type) {
                switch (type) {

                    case SearchDialogFragment.SEARCH_ITEM_FAVORITE:
                        loadFavoriteInternal();

                        break;

                    case SearchDialogFragment.SEARCH_ITEM_HISTORY:
                        loadHistoryInternal();
                        break;

                    default:
                        throw new RuntimeException("showLongPressSelectionDialog::Unhandled case");
                }
            }

            @Override
            public void OnLongClick(int type) {

            }
        });
    }

    @Override
    public void runMainSearch(String query) {
        if (query.length() >= AUTOCOMPLETE_THRESHOLD)
            runAutoCompleteSearch(query);
    }

    private void runAutoCompleteSearch(String text) {

        HashMap<String, String> map = new HashMap<>();
        map.put("q", text);
        map.put("lon", String.valueOf(mapCenter.getLongitude()));
        map.put("lat", String.valueOf(mapCenter.getLatitude()));
        map.put("lang", Locale.getDefault().getLanguage());

        AutoCompleteApi service = AutoCompleteServiceProvider.get();
        Call<AutoCompleteResult> call = service.autoComplete(map);
        call.enqueue(new Callback<AutoCompleteResult>() {
            @Override
            public void onResponse(@NonNull Call<AutoCompleteResult> call, @NonNull Response<AutoCompleteResult> response) {

                ArrayList<Place> places = new ArrayList<>();
                List<AutoCompleteResult.Features> features;

                try {
                    features = response.body().features;

                    IconManager iconManager = new IconManager(mContext);

                    for (AutoCompleteResult.Features feature : features) {

                        OverpassQueryResult.Element element = new OverpassQueryResult.Element();

                        element.lat = feature.geometry.coordinates[1];
                        element.lon = feature.geometry.coordinates[0];
                        element.extent = feature.properties.extent;
                        element.distance = MapUtils.getDistance(mapCenter, element.lat, element.lon);

                        if (element.distance < SEARCH_RADIUS) {
                            element.tags.name = feature.properties.name;
                            element.id = feature.properties.osm_id;

                            String[] value = new String[1];     // REMOVE THIS
                            Field field = Commons.extractFieldByString(OverpassQueryResult.Element.Tags.class, feature.properties.osm_key);
                            try {
                                if (Commons.isNotNull(field)) {
                                    value[0] = feature.properties.osm_value;  // REMOVE THIS
                                    field.set(element.tags, feature.properties.osm_value);
                                    Log.d(TAG, "onResponse: ");
                                } else
                                    Log.d(TAG, "Reflection : Not found in Structure::" + feature.properties.osm_key);
                            } catch (IllegalAccessException e) {
                                Log.d(TAG, "Reflection : Not found in Structure::" + feature.properties.osm_key);
                                e.printStackTrace();
                            }

                            element.tags.addressHouseNumber = feature.properties.housenumber;
                            element.tags.addressStreet = feature.properties.street;
                            element.tags.addressPostCode = feature.properties.postcode;
                            element.tags.addressCity = feature.properties.city;

                            Place place = new Place.Builder()
                                    .name(element.tags.name)
                                    .address(element.tags.getAddress())
                                    .distance(element.distance)
                                    .lat(element.lat)
                                    .lng(element.lon)
                                    .osmId(element.id)
                                    .osmKey(feature.properties.osm_key)
                                    .osmType(feature.properties.osm_value)
                                    .tags(value)
                                    .iconRes(iconManager.getIdentifier(feature.properties.osm_value))
                                    .iconName(feature.properties.osm_key)
                                    .placeId(SearchDialogFragment.SEARCH_ITEM_MAIN)
                                    .build();

                            places.add(place);
                        }
                    }
                } catch (NullPointerException e) {
                    return;
                }

                view.updateListAdapter(SearchDialogFragment.SEARCH_ITEM_MAIN, places);
            }

            @Override
            public void onFailure(@NonNull Call<AutoCompleteResult> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void loadHistory() {
        view.onShowProgressBar(true);
        if (!paused) {
            loadHistoryInternal();
            view.onShowProgressBar(false);
        }
//        addToHistoryDebug();
    }

    @Override
    public void loadFavorite() {
        view.onShowProgressBar(true);
        if (!paused) {
            loadFavoriteInternal();
            view.onShowProgressBar(false);
        }
    }

    @Override
    public void loadCategories() {
        view.onShowProgressBar(true);
        if (!paused) {
            reloadCategoriesInternal();
            view.onShowProgressBar(false);
        }
    }

    private void reloadCategoriesInternal() {
        Resources res = mContext.getResources();

        TypedArray icons = res.obtainTypedArray(R.array.poi_category_icons);
        String[] categories = res.getStringArray(R.array.poi_category_tags);

        List<Place> places = new ArrayList<>();

        for (int i = 0; i < categories.length; i++) {
            String[] line = categories[i].split(":");

            places.add(new Place.Builder()
                    .name(line[0])          // display name
                    .address(line[1])       // display sub title
                    .iconRes(icons.getResourceId(i, 0))
                    .placeId(SearchDialogFragment.SEARCH_ITEM_POI_CATEGORIES)
                    .tags(line)             // categories to lookup
                    .build());
        }
        icons.recycle();

        view.updateListAdapter(SearchDialogFragment.SEARCH_ITEM_POI_CATEGORIES, places);
    }

    private void loadFavoriteInternal() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<FavoriteEntry> list = getDatabase().favoriteDao().getAllFavorites();
                final List<Place> places = new ArrayList<>();

                // Sort the list by time added.
                Collections.sort(list, new FavoriteEntry.TimeStampComparator());

                for (FavoriteEntry entry : list) {
                    places.add(new Place.Builder()
                            .name(entry.getName())          // display name
                            .address(entry.getAddress())       // display sub title
                            .lat(entry.getLat())
                            .lng(entry.getLon())
                            .osmId(entry.getOsmId())
                            .osmKey(entry.getOsmPojo().tags.getPrimaryType())
                            .iconRes(entry.getIconInt() != 0 ? entry.getIconInt() : R.drawable.ic_heart)
                            .iconName(entry.getIconStr())
                            .userComment(entry.getComment() != null ? entry.getComment() : entry.getIconStr())
                            .placeId(SearchDialogFragment.SEARCH_ITEM_FAVORITE)
                            .timestamp(entry.getTimestamp())
                            .build());
                }

                ((MapActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.updateListAdapter(SearchDialogFragment.SEARCH_ITEM_FAVORITE, places);
                    }
                });
//                }
            }
        });
    }

    private void loadHistoryInternal() {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<HistoryEntry> list = getDatabase().historyDao().getAllHistories();
                final List<Place> places = new ArrayList<>();

                // Sort the list by time added
                Collections.sort(list, new HistoryEntry.TimeStampComparator());

                for (HistoryEntry entry : list) {
                    places.add(new Place.Builder()
                            .name(entry.getName())          // display name
                            .address(entry.getAddress())       // display sub title
                            .lat(entry.getLat())
                            .lng(entry.getLon())
                            .osmId(entry.getOsmId())
                            .osmKey(entry.getOsmPojo().tags.getPrimaryType())
                            .iconRes(entry.getIconInt() != 0 ? entry.getIconInt() : R.drawable.ic_history)
                            .iconName(entry.getIconStr())
                            .userComment(entry.getComment() != null ? entry.getComment() : entry.getIconStr())
                            .placeId(SearchDialogFragment.SEARCH_ITEM_HISTORY)
                            .timestamp(entry.getTimestamp())
                            .build());
                }

                ((MapActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.updateListAdapter(SearchDialogFragment.SEARCH_ITEM_HISTORY, places);
                    }
                });
//                }
            }
        });
    }

    @Override
    public void addToHistory(final Place place) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                HistoryEntry entry = new HistoryEntry(new PlaceToOverpass(null).convertPlace(place));
                getDatabase().historyDao().insert(entry);

                ((MapActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, mContext.getText(R.string.history_saved), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onShowWikiReadingList() {
        view.onShowProgressBar(true);

        final AppCompatActivity activity = ((App) mContext.getApplicationContext()).getCurrentActivity();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                final List<BookmarkEntry> list = getDatabase().bookmarkDao().getAllBookmarks();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<BookmarkEntry> arrayList = new ArrayList<BookmarkEntry>(list);

                        WikiReadingListDialogFragment.showInstance(mContext.getApplicationContext(), myLocation, arrayList);
                        view.onShowProgressBar(false);
                    }
                });
            }
        });
    }

    @SuppressWarnings("unused")
    private void addToHistoryDebug() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                final List<Place> places = new ArrayList<>();
                if (BuildConfig.DEBUG) {

                    long timestamp = Calendar.getInstance().getTimeInMillis();

                    for (int i = 0; i < 11; i++) {
                        places.add(new Place.Builder()
                                .name("test " + i)          // display name
                                .address("test " + i)       // display sub title
                                .lat(0)
                                .lng(0)
                                .osmId(i + 10000)
                                .osmKey("amentiy")
                                .osmType("pub")
                                .iconRes(0)
                                .iconName("")
                                .placeId(SearchDialogFragment.SEARCH_ITEM_HISTORY)
                                .timestamp(timestamp - (i * C.TIME_ONE_WEEK))
                                .build());
                    }

                    for (Place place : places) {
                        HistoryEntry entry = new HistoryEntry(new PlaceToOverpass(null).convertPlace(place));
                        getDatabase().historyDao().insert(entry);

                    }
                }


                ((MapActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.updateListAdapter(SearchDialogFragment.SEARCH_ITEM_HISTORY, places);
                        Toast.makeText(mContext, mContext.getText(R.string.history_saved), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
