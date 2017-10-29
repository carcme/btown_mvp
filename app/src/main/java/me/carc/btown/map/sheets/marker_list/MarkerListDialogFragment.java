package me.carc.btown.map.sheets.marker_list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btown.App;
import me.carc.btown.MapActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.MarkerListListener;
import me.carc.btown.common.interfaces.SimpleClickListener;
import me.carc.btown.data.results.OverpassQueryResult;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.db.favorite.FavoriteEntry;
import me.carc.btown.map.IconManager;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.map.sheets.search_context.SearchContextMenu;
import me.carc.btown.ui.CompassDialog;
import me.carc.btown.ui.custom.DividerItemDecoration;

/**
 * Show the favorites list
 * Created by bamptonm on 31/08/2017.
 */
public class MarkerListDialogFragment extends DialogFragment {

    public static final String ID_TAG = "MarkerListDialogFragment";
    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LNG";
    private static final String MARKER_LIST = "MARKER_LIST";
    private MarkerListAdapter adapter;

    private GeoPoint myLocation;
    private Unbinder unbinder;

    private ArrayList<Object> relatedObjects;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.bookmarksToolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop)
    ImageView imageBackDrop;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

/*
    @BindView(R.id.readingListHeaderImageContainer)
    LinearLayout readingListHeaderImageContainer;

    @BindViews({R.id.reading_list_header_image_0,
            R.id.reading_list_header_image_1,
            R.id.reading_list_header_image_2,
            R.id.reading_list_header_image_3,
            R.id.reading_list_header_image_4,
            R.id.reading_list_header_image_5})
    List<ImageView> imageViews;
*/

    public static boolean showInstance(final Context appContext, final GeoPoint currLocation, ArrayList<Object> objects) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if (currLocation != null) {
                bundle.putDouble(MY_LAT, currLocation.getLatitude());
                bundle.putDouble(MY_LNG, currLocation.getLongitude());
            }

            if (Commons.isNotNull(objects)) {
                bundle.putSerializable(MARKER_LIST, objects);
            }

            MarkerListDialogFragment fragment = new MarkerListDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_list_recyclerview_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.almostWhite));

        Bundle args = getArguments();
        if (args != null) {

            relatedObjects = (ArrayList<Object>) args.getSerializable(MARKER_LIST);

            double lat = args.getDouble(MY_LAT, Double.NaN);
            double lng = args.getDouble(MY_LNG, Double.NaN);
            myLocation = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                myLocation = new GeoPoint(lat, lng);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            recyclerView.addItemDecoration(itemDecoration);
            adapter = new MarkerListAdapter(myLocation, buildAdapterList(myLocation), new MarkerListListener() {
                @Override
                public void onClick(View v, int position) {
                    hide();
                    ((MapActivity) getActivity()).showPoiDlg(relatedObjects.get(position));
                }

                @Override
                public void onClickImage(View v, int position) {
                    showImage(relatedObjects.get(position));
                }

                @Override
                public void onClickOverflow(View v, int position) {
                    showOptions(relatedObjects.get(position));
                }

                @Override
                public void onClickCompass(View v, int position) {
                    showCompass(adapter.getNode(position));
                }
            });

            if (recyclerView != null) {
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            assert collapsingToolbar != null;

            Drawable drawable = ViewUtils.changeIconColor(getContext(), R.drawable.ic_arrow_back, R.color.white);
            toolbar.setNavigationIcon(drawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            collapsingToolbar.setTitleEnabled(true);
            collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
        return view;
    }

    public void hide() {
        getDialog().hide();
    }

    public void show() {
        getDialog().show();
    }

    public void closeFavorites() {
        dismiss();
    }

    private void showCompass(final MarkerListAdapter.PoiAdpaterItem node) {
        String title = node.name;
        String subTitle = node.poiType;
        GeoPoint start = new GeoPoint(node.lat, node.lon);
        GeoPoint end = myLocation;
        String dist = node.distance;
        CompassDialog.showInstance(getActivity().getApplicationContext(), title, subTitle, start, end, dist);
    }

    private void showOptions(final Object obj) {

        SearchContextMenu.show(-1, obj, getActivity().getApplicationContext(), new SimpleClickListener() {

            @Override
            public void OnClick(final int type) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = ((App) getActivity().getApplicationContext()).getDB();

                        if (type == SearchContextMenu.POI) {
                            FavoriteEntry entry = new FavoriteEntry((OverpassQueryResult.Element) obj);
                            db.favoriteDao().insert(entry);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getActivity().getText(R.string.favorite_saved), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (type == SearchContextMenu.WIKI) {
                            BookmarkEntry entry = new BookmarkEntry((WikiQueryPage) obj);
                            db.bookmarkDao().insert(entry);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getActivity().getText(R.string.bookmark_addded), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void OnLongClick(int type) {

            }
        });
    }

    private void showImage(Object obj) {
        if (obj instanceof OverpassQueryResult.Element) {
            OverpassQueryResult.Element element = (OverpassQueryResult.Element) obj;
            if (Commons.isNotNull(element.tags.image)) {
                final String httpUrl = "https://www.openstreetmap.org/#map=16/" + element.lat + "/" + ((float) element.lon);
                ImageDialog.showInstance(getActivity().getApplicationContext(), element.tags.image, httpUrl, element.tags.name, element.tags.getPrimaryType());
            }
        } else if (obj instanceof WikiQueryPage) {
            WikiQueryPage page = (WikiQueryPage) obj;
            if (Commons.isNotNull(page.thumbUrl())) {
                ImageDialog.showInstance(getActivity().getApplicationContext(), page.thumbUrl(), page.fullurl(), page.title(), page.extract());
            }
        }
    }

    /**
     * Load favorites from shared preferences
     *
     * @return the list of favorites
     */
    private ArrayList<MarkerListAdapter.PoiAdpaterItem> buildAdapterList(GeoPoint location) {

        ArrayList<MarkerListAdapter.PoiAdpaterItem> array = new ArrayList<>();
        IconManager im = new IconManager(getActivity());

        ArrayList<String> imageList = new ArrayList<>();

        boolean doTitle = true;

        for (Object obj : relatedObjects) {
            MarkerListAdapter.PoiAdpaterItem item = new MarkerListAdapter.PoiAdpaterItem();

            if (obj instanceof OverpassQueryResult.Element) {
                OverpassQueryResult.Element node = (OverpassQueryResult.Element) obj;
                item.name = node.tags.name;
                item.poiType = node.tags.getPrimaryType();
                item.icon = im.getRoundedIcon(item.poiType);
                item.lat = node.lat;
                item.lon = node.lon;
                item.distance = getdistance(node, location);
                item.thumb = node.tags.thumbnail;
                array.add(item);

                if (!Commons.isEmpty(node.tags.image)) {
                    imageList.add(node.tags.image);
                }

                if (doTitle)
                    collapsingToolbar.setTitle(getActivity().getString(R.string.search_tab_category));
                doTitle = false;

            } else if (obj instanceof WikiQueryPage) {
                WikiQueryPage wiki = (WikiQueryPage) obj;
                item.name = wiki.title();
                item.poiType = wiki.description();
                item.icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_wiki_map_marker);
                item.lat = wiki.getLat();
                item.lon = wiki.getLon();
                item.distance = getdistance(wiki, location);
                item.thumb = wiki.thumbUrl();
                array.add(item);

                if (!Commons.isEmpty(wiki.thumbUrl())) {
                    imageList.add(wiki.thumbUrl());
                }

                if (doTitle)
                    collapsingToolbar.setTitle(getActivity().getString(R.string.wikipedia));
                doTitle = false;
            }
        }

        if (imageList.size() == 0) {
            imageBackDrop.setImageResource(R.drawable.checkered_background);
            imageBackDrop.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            String randomImageUrl = imageList.get(new Random().nextInt(imageList.size()));

            Glide.with(getActivity())
                    .load(randomImageUrl)
                    .placeholder(R.drawable.checkered_background)
                    .error(R.drawable.no_image)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageBackDrop);
            imageBackDrop.setVisibility(View.VISIBLE);
/*
        } else {
            readingListHeaderImageContainer.setVisibility(View.VISIBLE);

            for (int i = 0; i < imageList.size() && i < imageViews.size(); ++i) {
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(getActivity())
                            .load(imageList.get(i))
                            .into(imageViews.get(i));
                    view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
*/
        }
        return array;
    }

    private String getdistance(OverpassQueryResult.Element node, GeoPoint location) {
        double d = MapUtils.getDistance(location, node.lat, node.lon);
        return MapUtils.getFormattedDistance(d);
    }

    private String getdistance(WikiQueryPage page, GeoPoint location) {
        double d = MapUtils.getDistance(location, page.getLat(), page.getLon());
        return MapUtils.getFormattedDistance(d);
    }

    public void updatePoiDirection(GeoPoint myLocation, float dir) {
        if (adapter != null) {
            if (adapter.canUpdate())
                adapter.rotationUpdate(myLocation, dir);
        }
    }
}
