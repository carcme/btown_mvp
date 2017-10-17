package me.carc.btown_map.map.sheets;

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

import com.bumptech.glide.Glide;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btown_map.App;
import me.carc.btown_map.MapActivity;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.MapUtils;
import me.carc.btown_map.Utils.ViewUtils;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.common.TinyDB;
import me.carc.btown_map.data.model.OverpassQueryResult;
import me.carc.btown_map.data.wiki.WikiQueryPage;
import me.carc.btown_map.map.IconManager;
import me.carc.btown_map.map.interfaces.MyClickListener;
import me.carc.btown_map.ui.custom.DividerItemDecoration;

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
    private TinyDB db;

    private Unbinder unbinder;

    private static ArrayList<Marker> markersArray;

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



    public static boolean showInstance(final Context appContext, final GeoPoint currLocation, ArrayList<Marker> markersList) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if (currLocation != null) {
                bundle.putDouble(MY_LAT, currLocation.getLatitude());
                bundle.putDouble(MY_LNG, currLocation.getLongitude());
            }

            if (Commons.isNotNull(markersList)) {
                bundle.putSerializable(MARKER_LIST, markersList);
            }

            MarkerListDialogFragment fragment = new MarkerListDialogFragment();
            fragment.setArguments(bundle);
            markersArray = markersList;
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);

        unbinder = ButterKnife.bind(this, view);

        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.almostWhite));

        db = TinyDB.getTinyDB();

        Bundle args = getArguments();
        if (args != null) {

            double lat = args.getDouble(MY_LAT, Double.NaN);
            double lng = args.getDouble(MY_LNG, Double.NaN);
            GeoPoint myLocation = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                myLocation = new GeoPoint(lat, lng);


            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            recyclerView.addItemDecoration(itemDecoration);

            adapter = new MarkerListAdapter(myLocation, buildAdapterList(myLocation), new MyClickListener() {
                @Override
                public void OnClick(View v, int position) {
                    hide();
                    ((MapActivity) getActivity()).showPoiDlg(markersArray.get(position).getRelatedObject());
                }

                @Override
                public void OnLongClick(View v, int position) {
                }
            });

//            new buildList(myLocation).run();


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
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

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

        for (Marker marker : markersArray) {
            MarkerListAdapter.PoiAdpaterItem item = new MarkerListAdapter.PoiAdpaterItem();

            Object obj = marker.getRelatedObject();

            if (obj instanceof OverpassQueryResult.Element) {
                OverpassQueryResult.Element node = (OverpassQueryResult.Element) obj;
                item.name = node.tags.name;
                item.poiType = node.tags.getPrimaryType();
                item.icon = im.getRoundedIcon(item.poiType);
                item.lat = node.lat;
                item.lon = node.lon;
                item.distance = getdistance(node, location);
                item.image = node.tags.image;
                array.add(item);

                if (!Commons.isEmpty(node.tags.image)) {
                    imageList.add(node.tags.image);
                }

                if(doTitle)
                    toolbar.setTitle(getActivity().getString(R.string.search_tab_category));
                doTitle = false;

            } else if (obj instanceof WikiQueryPage) {
                WikiQueryPage wiki = (WikiQueryPage) obj;
                item.name = wiki.title();
                item.poiType = wiki.description();
                item.icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_wiki_map_marker);
                item.lat = wiki.getLat();
                item.lon = wiki.getLon();
                item.distance = getdistance(wiki, location);
                item.image = wiki.thumbUrl();
                array.add(item);

                if (!Commons.isEmpty(wiki.thumbUrl())) {
                    imageList.add(wiki.thumbUrl());
                }

                if(doTitle)
                    toolbar.setTitle(getActivity().getString(R.string.wikipedia));
                doTitle = false;
            }
        }

        if(imageList.size() == 0) {
            imageBackDrop.setImageResource(R.drawable.checkered_background);
            imageBackDrop.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            String randomImageUrl = imageList.get(new Random().nextInt(imageList.size()));

            Glide.with(getActivity())
                    .load(randomImageUrl)
                    .placeholder(R.drawable.checkered_background)
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


    private class buildList implements Runnable {

        GeoPoint location;

        buildList(GeoPoint location) {
            this.location = location;
        }

        @Override
        public void run() {
            buildIt();
        }

        private void buildIt() {


            ArrayList<MarkerListAdapter.PoiAdpaterItem> array = new ArrayList<>();
            IconManager im = new IconManager(getActivity());

            ArrayList<String> randomImageList = new ArrayList<>();

            for (Marker marker : markersArray) {
                MarkerListAdapter.PoiAdpaterItem item = new MarkerListAdapter.PoiAdpaterItem();

                Object obj = marker.getRelatedObject();

                if (obj instanceof OverpassQueryResult.Element) {
                    OverpassQueryResult.Element node = (OverpassQueryResult.Element) obj;
                    item.name = node.tags.name;
                    item.poiType = node.tags.getPrimaryType();
                    item.icon = im.getRoundedIcon(item.poiType);
                    item.lat = node.lat;
                    item.lon = node.lon;
                    item.distance = getdistance(node, location);
                    item.image = node.tags.image;
                    array.add(item);

                    if (!Commons.isEmpty(node.tags.image)) {
                        randomImageList.add(node.tags.image);
                    }

                } else if (obj instanceof WikiQueryPage) {
                    WikiQueryPage wiki = (WikiQueryPage) obj;
                    item.name = wiki.title();
                    item.poiType = wiki.description();
                    item.icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_wiki_map_marker);
                    item.lat = wiki.getLat();
                    item.lon = wiki.getLon();
                    item.distance = getdistance(wiki, location);
                    item.image = wiki.thumbUrl();
                    array.add(item);

                    if (!Commons.isEmpty(wiki.thumbUrl())) {
                        randomImageList.add(wiki.thumbUrl());
                    }
                }
            }

            String randomImageUrl = randomImageList.get(new Random().nextInt(randomImageList.size()));

            if (Commons.isNotNull(randomImageUrl)) {
                Glide.with(getActivity())
                        .load(randomImageUrl)
                        .placeholder(R.drawable.checkered_background)
                        .into(imageBackDrop);
            } else {
                imageBackDrop.setImageResource(R.drawable.checkered_background);
                imageBackDrop.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            adapter.updateItems(array);
        }
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
