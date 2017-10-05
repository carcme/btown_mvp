package me.carc.btownmvp.map.sheets;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.carc.btownmvp.App;
import me.carc.btownmvp.BuildConfig;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.AndroidUtils;
import me.carc.btownmvp.Utils.FragmentUtil;
import me.carc.btownmvp.Utils.IntentUtils;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.Utils.OpeningHoursParser;
import me.carc.btownmvp.Utils.WikiUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.CompassSensor;
import me.carc.btownmvp.common.TinyDB;
import me.carc.btownmvp.data.model.OverpassQueryResult;
import me.carc.btownmvp.data.model.ReverseResult;
import me.carc.btownmvp.data.reverse.ReverseApi;
import me.carc.btownmvp.data.reverse.ReverseServiceProvider;
import me.carc.btownmvp.db.favorite.FavoriteEntry;
import me.carc.btownmvp.map.search.SearchDialogFragment;
import me.carc.btownmvp.map.search.model.Place;
import me.carc.btownmvp.map.search.model.SavedFavoriteItem;
import me.carc.btownmvp.map.sheets.model.InfoCard;
import me.carc.btownmvp.map.sheets.model.RouteInfo;
import me.carc.btownmvp.map.sheets.model.adpater.PoiMoreRecyclerAdapter;
import me.carc.btownmvp.map.sheets.share.ShareMenu;
import me.carc.btownmvp.ui.CompassDialog;
import me.carc.btownmvp.ui.CompassView;
import me.carc.btownmvp.ui.custom.CapitalisedTextView;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Bottom Sheet Dialog for points of interest
 * Created by bamptonm on 31/08/2017.
 */
public class SinglePoiOptionsDialog extends BottomSheetDialogFragment {
    public interface SinglePoiCallback {
        void onRouteTo(RouteInfo info);
    }

    public static final String ID_TAG = "SinglePoiOptionsDialog";
    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final String ITEM = "ITEM";

    private String address;
    private String userDescription;
    private GeoPoint poiPosition;
    private BottomSheetBehavior behavior;
    private CompassSensor.Callback cbCompass;
    private RouteInfo routeInfo;
    private boolean hidden;
    private Unbinder unbinder;

    @BindView(R.id.userDesc)
    EditText userDesc;

    @BindView(R.id.moreRecyclerView)
    RecyclerView moreRecyclerView;

    @BindView(R.id.featureTitle)
    TextView featureTitle;

    @BindView(R.id.featureIcon)
    ImageView featureIcon;

    @BindView(R.id.featureType)
    CapitalisedTextView featureType;

    @BindView(R.id.featureSubtitle)
    TextView featureSubtitle;

    @BindView(R.id.featureOpeningHours)
    TextView featureOpeningHours;

    @BindView(R.id.poiNavigationIcon)
    CompassView poiNavigationIcon;

    @BindView(R.id.featureDistance)
    TextView featureDistance;

    @BindView(R.id.featureSave)
    Button featureSave;

    @BindView(R.id.featureMore)
    Button featureMore;


    public static boolean showInstance(final MapActivity mapActivity, OverpassQueryResult.Element element) {

        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM, element);

            SinglePoiOptionsDialog fragment = new SinglePoiOptionsDialog();
            fragment.setArguments(bundle);
            fragment.show(mapActivity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCB = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                dismiss();
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                more();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.d(TAG, "onSlide: " + slideOffset);
            if (Float.isNaN(slideOffset)) {
                more();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.PoiItemDialog);
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(getContext(), R.layout.sheet_poi_base_layout, null);
        unbinder = ButterKnife.bind(this, rootView);

        dialog.setContentView(rootView);

        Bundle args = getArguments();
        if (args == null)
            dismiss();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            this.behavior = (BottomSheetBehavior) behavior;
            this.behavior.setBottomSheetCallback(mBottomSheetCB);
        }

        try {
            OverpassQueryResult.Element node = (OverpassQueryResult.Element) args.getSerializable(ITEM);

            featureTitle.setText(node.tags.name);

            if (!Commons.isEmpty(node.userDescription))
                userDesc.setText(Commons.capitalizeFirstLetter(node.userDescription));

            if (!Commons.isEmpty(node.tags.cuisine)) {
                String type = node.tags.getPrimaryType();
                type += " • " + Commons.capitalizeFirstLetter(node.tags.cuisine);
                featureType.setText(type.replace(";", ", "));
            } else
                featureType.setText(node.tags.getPrimaryType());

            address = node.tags.getAddress();

            checkFavorite(node.id);

            if (Commons.isEmpty(address)) {
                featureSubtitle.setText(getActivity().getResources().getText(R.string.seaching));
                reverseAddressLookup(node.lat, node.lon);
            } else
                featureSubtitle.setText(address);

            featureDistance.setText(node.distance > 0 ? MapUtils.getFormattedDistance(node.distance) : "--");

            // used to update the location arrow
            poiPosition = new GeoPoint(node.lat, node.lon);

            String iconStr = node.tags.getPrimaryType();
            node.iconId = 0;
            if (iconStr != null) {
                node.iconId = getResources().getIdentifier(iconStr, "raw", getActivity().getPackageName());
                if (node.iconId == 0) {
                    featureIcon.setImageResource(R.drawable.ic_plus_red);
                    featureType.setText(iconStr.isEmpty() ? "Unknown" : iconStr);
                } else {
                    featureIcon.setImageResource(node.iconId);
                }
            }

            Glide.with(App.get().getApplicationContext())
                    .load(node.tags.image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.background_image_blank)
                    .error(node.iconId)
                    .into(featureIcon);

            new SetupMore(node).run();

//            populateMore(node);


        } catch (NullPointerException e) {
            e.printStackTrace();

            // something went wrong, close the dialog
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 200);
        }

        poiNavigationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OverpassQueryResult.Element node = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);

                String type = node.tags.getPrimaryType();

                if (!Commons.isEmpty(node.tags.cuisine))
                    type += " • " + Commons.capitalizeFirstLetter(node.tags.cuisine);


                CompassDialog.showInstance((MapActivity) getActivity(),
                        node.tags.name,
                        featureType.getText().toString(),
                        new GeoPoint(node.lat, node.lon),
                        node.getGeoPoint());
            }
        });
    }

    public void show() {
        getDialog().show();
        hidden = false;
    }

    public void hide() {
        hidden = true;
        getDialog().hide();
    }

    public void close() {
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDestroy() {
//        if(Commons.isNotNull(unbinder))
        unbinder.unbind();
        super.onDestroy();
    }

    @Nullable
    private SinglePoiCallback callback() {
        return FragmentUtil.getCallback(this, SinglePoiCallback.class);
    }


    private class SetupMore implements Runnable {

        OverpassQueryResult.Element node;

        SetupMore(OverpassQueryResult.Element node) {
            this.node = node;
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {

            final ArrayList<InfoCard> items = new ArrayList<>();

            // Is the place open at right now
            OpeningHoursParser.OpeningHours hours = OpeningHoursParser.parseOpenedHours(node.tags.openingHours);
            if (Commons.isEmpty(node.tags.openingHours)) {
                featureOpeningHours.setText(R.string.unknown);
                featureOpeningHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text_color));
            } else {
                if (hours.isOpenedForTime(Calendar.getInstance())) {
                    featureOpeningHours.setText(R.string.open);
                    featureOpeningHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.poiOpenTimesOpenColor));
                } else {
                    featureOpeningHours.setText(R.string.closed);
                    featureOpeningHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.poiOpenTimesClosedColor));
                }
                items.add(new InfoCard(hours.getOriginalFormatted(), InfoCard.ItemType.NONE, FontAwesome.Icon.faw_clock_o));
            }

            if (Commons.isNotNull(node.tags.cuisine))
                items.add(new InfoCard(node.tags.cuisine, InfoCard.ItemType.NONE, CommunityMaterial.Icon.cmd_food_fork_drink));

            if (Commons.isNotNull(node.tags.contactPhone))
                items.add(new InfoCard(node.tags.contactPhone, InfoCard.ItemType.PHONE, CommunityMaterial.Icon.cmd_phone));

            if (Commons.isNotNull(node.tags.contactWebsite))
                items.add(new InfoCard(node.tags.contactWebsite, InfoCard.ItemType.WEB, CommunityMaterial.Icon.cmd_web));

            if (Commons.isNotNull(node.tags.contactEmail))
                items.add(new InfoCard(node.tags.contactEmail, InfoCard.ItemType.EMAIL, CommunityMaterial.Icon.cmd_email));

            if (Commons.isNotNull(node.tags.smoking))
                items.add(new InfoCard("Smoking: " + node.tags.smoking, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_smoking));

            if (Commons.isNotNull(node.tags.wheelchair))
                items.add(new InfoCard("Wheelchair: " + node.tags.wheelchair, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_wheelchair_accessibility));

            if (Commons.isNotNull(node.tags.wheelchairToilets))
                items.add(new InfoCard("Wheelchair Toilet Access: " + node.tags.wheelchairToilets, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_wheelchair_accessibility));

            if (Commons.isNotNull(node.tags.takeaway))
                items.add(new InfoCard("Takeaway: " + node.tags.takeaway, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_information));

            if (Commons.isNotNull(node.tags.delivery))
                items.add(new InfoCard("Delivery: " + node.tags.delivery, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_information));

            if (Commons.isNotNull(node.tags.internetAccess))
                items.add(new InfoCard("Internet Access: " + node.tags.internetAccess, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_wifi));

            if (Commons.isNotNull(node.tags.operator))
                items.add(new InfoCard("Operator: " + node.tags.operator, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_human));

            if (Commons.isNotNull(node.tags.note))
                items.add(new InfoCard("Note: " + node.tags.note, InfoCard.ItemType.INFO, CommunityMaterial.Icon.cmd_note));

            if (Commons.isNotNull(node.tags.wikipedia))
                items.add(new InfoCard(node.tags.wikipedia, InfoCard.ItemType.WIKI, CommunityMaterial.Icon.cmd_wikipedia));

            items.add(new InfoCard("Lat " + node.lat + ", Lon " + node.lon, InfoCard.ItemType.CLIPBOARD, CommunityMaterial.Icon.cmd_crosshairs_gps));


            if (items.isEmpty()) {
                featureMore.setVisibility(View.INVISIBLE);
            } else {

                final PoiMoreRecyclerAdapter mAdapter = new PoiMoreRecyclerAdapter(items, new MyClickListener() {
                    @Override
                    public void OnClick(View v, int pos) {
                        InfoCard item = items.get(pos);
                        Intent i;

                        switch (item.getType()) {
                            case EMAIL:
                                i = IntentUtils.sendEmail(item.getData(), "", "");
                                if (Commons.isNotNull(i)) startActivity(i);
                                break;

                            case WEB:
                            case WIKI:
                                i = IntentUtils.openLink(WikiUtils.createWikiLink(item.getData()));
                                if (Commons.isNotNull(i)) startActivity(i);
                                break;

                            case PHONE:
                                Toast.makeText(getActivity(), "Phone Call::" + item.getData(), Toast.LENGTH_SHORT).show();
                                if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CALL_PHONE)) {
                                    startActivity(IntentUtils.callPhone(item.getData()));
                                }
                                break;
                            case FACEBOOK:
                                Toast.makeText(getActivity(), "TODO FACEBOOK::" + item.getData(), Toast.LENGTH_SHORT).show();
                                break;

                            case CLIPBOARD:
                                AndroidUtils.copyToClipboard(getActivity(), item.getData());
                                Toast.makeText(getActivity(), "Copied to clipboard: " + item.getData(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void OnLongClick(View v, int position) {

                    }
                });

                moreRecyclerView.setNestedScrollingEnabled(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                moreRecyclerView.setLayoutManager(layoutManager);
                moreRecyclerView.setAdapter(mAdapter);
            }
        }
    }


    private void reverseAddressLookup(double lat, double lon) {
        ReverseApi service = ReverseServiceProvider.get();
        Call<ReverseResult> reverseCall = service.reverse(lat, lon);
        reverseCall.enqueue(new Callback<ReverseResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<ReverseResult> call, @NonNull Response<ReverseResult> response) {

                try {
                    if (Commons.isNotNull(response.body().address)) {
                        ReverseResult.Address add = response.body().address;

                        OverpassQueryResult.Element node = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);
                        node.tags.addressStreet = add.road;
                        node.tags.addressHouseNumber = add.house_number;
                        node.tags.addressSuburb = add.suburb;
                        node.tags.addressPostCode = add.postcode;
                        node.tags.addressCity = add.city;

                        featureSubtitle.setText(node.tags.getAddress());
                    } else {
                        featureSubtitle.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                featureSubtitle.setVisibility(View.GONE);
            }
        });
    }


    private Timer timer;

    /**
     * Add user comments for POI
     */
    @OnTextChanged(value = R.id.userDesc, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void afterEditTextChanged(final Editable editable) {

        if (Commons.isNotNull(timer))
            timer.cancel();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                userDescription = editable.toString();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveFavoriteFromEditText();
                    }
                });
            }
        }, 600);  // high ish delay so we don't spam the shared preferences
    }


    private void saveFavoriteFromEditText() {
        OverpassQueryResult.Element element = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(element)) {

            // Do not update on first open (when comment has been saved previously)
            if (!userDescription.equalsIgnoreCase(element.userDescription)) {
                addToFavoritesDb(element);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @OnClick(R.id.featureSave)
    void save() {
        AndroidUtils.hideSoftKeyboard(getActivity(), userDesc);
        OverpassQueryResult.Element element = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(element)) {
            Drawable icon;
            if (featureSave.getTag().equals(R.drawable.ic_star_yellow)) {
                removeFavorites(element.id);
            } else {
                addToFavoritesDb(element);
            }
        }
    }

    @OnClick(R.id.featurePin)
    void pin() {
        Toast.makeText(getActivity(), "Pin", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.featureShare)
    void share() {
        try {
            OverpassQueryResult.Element node = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);
            ShareMenu.show(node.getGeoPoint(), node.tags.name, node.tags.getAddress(), (MapActivity) getActivity());
        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
    }

    @OnClick(R.id.featureRoute)
    void route() {
        OverpassQueryResult.Element element = (OverpassQueryResult.Element) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(element)) {
            SinglePoiCallback callback = callback();

            if (Commons.isNull(routeInfo))
                routeInfo = new RouteInfo(RouteInfo.Vehicle.CAR);
            routeInfo.setTo(element.getGeoPoint());
            routeInfo.setAddressTo(element.tags.getAddress());
            callback.onRouteTo(routeInfo);
        }
/*
        try {
            OverpassQueryResult.Element node = getArguments().getParcelable(ITEM);
//            MapActivity.getMapActivity().routeToPoi(node.getGeoPoint());
            dismiss();
        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
*/
    }

    @OnClick(R.id.featureMore)
    void more() {
        if (moreRecyclerView.getVisibility() == View.GONE)
            moreRecyclerView.setVisibility(View.VISIBLE);
        else
            moreRecyclerView.setVisibility(View.GONE);
    }


    public void addToFavoritesDb(final OverpassQueryResult.Element element) {
        element.userDescription = userDescription;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FavoriteEntry entry = new FavoriteEntry(element);
                App.get().getDB().favoriteDao().insert(entry);

                checkFavorite(element.id);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.favorite_saved), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Check if the selected poi is in the favorite list
     *
     * @param nodeId
     * @return
     */
    private void checkFavorite(final long nodeId) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
               FavoriteEntry entry = App.get().getDB().favoriteDao().findByOsmId(nodeId);
                final Drawable icon;
                if(Commons.isNull(entry)){
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star);
                    featureSave.setTag(R.drawable.ic_star);
                } else {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_star_yellow);
                    featureSave.setTag(R.drawable.ic_star_yellow);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        featureSave.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
                    }
                });
            }
        });
    }

    private void removeFavorites(final long nodeId) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FavoriteEntry entry = App.get().getDB().favoriteDao().findByOsmId(nodeId);
                if(Commons.isNotNull(entry)){
                    App.get().getDB().favoriteDao().delete(entry);
                    checkFavorite(entry.getOsmId());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Add the favorite to the shared preferences
     *
     * @param element the OsmNode to save
     */
    public void addToFavorites(OverpassQueryResult.Element element) {

        OverpassQueryResult.Element.Tags tags = element.tags;

        element.userDescription = userDescription;

        Place place = new Place.Builder()
                .name(tags.name)          // display name
                .address(tags.getAddress())       // display sub title
                .lat(element.lat)
                .lng(element.lon)
                .iconRes(element.iconId)
                .iconName(tags.getPrimaryType())
                .userComment(userDescription != null ? userDescription : tags.getPrimaryType())
                .placeId(SearchDialogFragment.SEARCH_ITEM_FAVORITE)
                .build();


        if (element == null)
            return;

        ArrayList<SavedFavoriteItem> favoritesList = new ArrayList<>();
        Gson gson = new Gson();

        TinyDB db = TinyDB.getTinyDB();

        ArrayList<Long> idList = db.getListLong(C.SAVED_FAVORITES_ID_LIST);
        String json = db.getString(C.SAVED_FAVORITES_LIST, null);
        if (json != null) {
            Type collectionType = new TypeToken<Collection<SavedFavoriteItem>>() {
            }.getType();
            favoritesList = gson.fromJson(json, collectionType);
        }

        // create new history item
        SavedFavoriteItem savedFavorite = new SavedFavoriteItem();
        Calendar c = Calendar.getInstance();
        savedFavorite.setPlace(place);
        savedFavorite.setElement(element);
        savedFavorite.setDate(c.getTimeInMillis());

        // reverse search for dups and remove old item based on GeoPoint
        for (int i = favoritesList.size() - 1; i >= 0; i--) {
            GeoPoint listItemPoint = favoritesList.get(i).getPlace().getGeoPoint();
            if (listItemPoint.equals(element.getGeoPoint())) {
                favoritesList.remove(i);
            }
        }
        idList.add(0, element.id);
        favoritesList.add(0, savedFavorite);
        json = gson.toJson(favoritesList);

        // add elements to al, including duplicates
        Set<Long> hs = new HashSet<>();
        hs.addAll(idList);
        idList.clear();
        idList.addAll(hs);

        db.putListLong(C.SAVED_FAVORITES_ID_LIST, idList);
        db.putString(C.SAVED_FAVORITES_LIST, json);

        if (BuildConfig.DEBUG) {
            if (favoritesList.size() != idList.size()) {
                Toast.makeText(getActivity(), "Favorite poi list and favorite ids list DO NOT match", Toast.LENGTH_SHORT).show();
//                throw new RuntimeException("Favorite poi list and favorite ids list DO NOT match");
            }
            for (SavedFavoriteItem fav : favoritesList) {
                if (!idList.contains(fav.getElement().id)) {
                    Toast.makeText(getActivity(), "Favorite ids NOT found in favorite list", Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException("Favorite ids NOT found in favorite list");
                }
            }
        }

    }

    public CompassDialog getCompassFragment() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(CompassDialog.ID_TAG);
        return fragment != null && !fragment.isDetached() && !fragment.isRemoving() ? (CompassDialog) fragment : null;
    }

    public void onNewAngleCalculation(float deg, GeoPoint currPos) {
        if (!hidden && Commons.isNotNull(poiNavigationIcon) && Commons.isNotNull(poiPosition)) {
            double d = MapUtils.getDistance(currPos, poiPosition.getLatitude(), poiPosition.getLongitude());
            featureDistance.setText(MapUtils.getFormattedDistance(d));
            float angle = (float) MapUtils.bearingBetweenLocations(currPos, poiPosition);

            poiNavigationIcon.rotationUpdate(angle - deg, true);

            CompassDialog compassDlg = getCompassFragment();
            if (compassDlg != null)
                compassDlg.updatePoiDirection(currPos, angle - deg);
        }
    }
}
