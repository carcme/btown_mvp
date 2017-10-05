package me.carc.btownmvp.map.sheets;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.TinyDB;
import me.carc.btownmvp.data.model.OverpassQueryResult;
import me.carc.btownmvp.data.wiki.WikiQueryPage;
import me.carc.btownmvp.map.IconManager;
import me.carc.btownmvp.ui.custom.DividerItemDecoration;

/**
 * Show the favorites list
 * Created by bamptonm on 31/08/2017.
 */
public class MarkerListDialogFragment extends DialogFragment {

    public static final String ID_TAG = "FavoriteListDlgFrag";
    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LNG";
    private static final String MARKER_LIST = "MARKER_LIST";
    private MarkerListAdapter adapter;
    private TinyDB db;

    @BindView(R.id.emptyFavoriteList)
    TextView emptyList;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private Unbinder unbinder;

    private static ArrayList<Marker> markersArray;


    public static boolean showInstance(final MapActivity mapActivity, final GeoPoint currLocation, ArrayList<Marker> markersList) {

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
            fragment.show(mapActivity.getSupportFragmentManager(), ID_TAG);

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
        View view = inflater.inflate(R.layout.favorites_recyclerview, container, false);

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
                    ((MapActivity)getActivity()).showPoiDlg(markersArray.get(position).getRelatedObject());
                }

                @Override
                public void OnLongClick(View v, int position) {

                }
            });

            if (recyclerView != null) {
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(adapter);
            }

//            emptyList = (TextView) view.findViewById(R.id.emptyFavoriteList);

            if (adapter.getItemCount() > 0) {
                emptyList.setVisibility(View.GONE);
            }
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

        for (Marker marker : markersArray) {
            MarkerListAdapter.PoiAdpaterItem item = new MarkerListAdapter.PoiAdpaterItem();

            Object obj = marker.getRelatedObject();

            if (obj instanceof OverpassQueryResult.Element) {
                OverpassQueryResult.Element node = (OverpassQueryResult.Element)obj;
                item.name = node.tags.name;
                item.poiType = node.tags.getPrimaryType();
                item.icon = im.getRoundedIcon(item.poiType);
                item.lat = node.lat;
                item.lon = node.lon;
                item.distance = getdistance(node, location);
                array.add(item);
            } else if(obj instanceof WikiQueryPage) {
                WikiQueryPage wiki = (WikiQueryPage)obj;
                item.name = wiki.title();
                item.poiType = wiki.description();
                item.icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_wiki_map_marker);
                item.lat = wiki.getLat();
                item.lon = wiki.getLon();
                item.distance = getdistance(wiki, location);
                array.add(item);
            }
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
