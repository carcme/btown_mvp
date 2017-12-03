package me.carc.btown.tours.top_pick_lists.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.R;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.Attributes;
import me.carc.btown.data.all4squ.entities.Category;
import me.carc.btown.data.all4squ.entities.GroupsAttribute;
import me.carc.btown.data.all4squ.entities.GroupsTip;
import me.carc.btown.data.all4squ.entities.ItemsAttribute;
import me.carc.btown.data.all4squ.entities.ItemsGroupsTip;
import me.carc.btown.data.all4squ.entities.Location;
import me.carc.btown.data.all4squ.entities.Menu;
import me.carc.btown.data.all4squ.entities.Open;
import me.carc.btown.data.all4squ.entities.Reason;
import me.carc.btown.data.all4squ.entities.Timeframe;
import me.carc.btown.data.all4squ.entities.VenueResult;
import me.carc.btown.map.sheets.share.ShareMenu;
import me.carc.btown.map.sheets.wiki.WikiWebViewActivity;
import me.carc.btown.tours.attractionPager.AttractionMapActivity;
import me.carc.btown.tours.top_pick_lists.VenueTabsActivity;
import me.carc.btown.tours.top_pick_lists.adapters.VenueFeaturesAdapter;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenueInfoFragment extends Fragment {
    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String PREFKEY_SAVED_TOUR_NOTES = "PREFKEY_SAVED_TOUR_NOTES";


    @BindView(R.id.venueInfoNestedScrollView)
    NestedScrollView venueInfoNestedScrollView;

    @BindView(R.id.venueInfoProgressBar)
    ProgressBar venueInfoProgressBar;


    @BindView(R.id.venueAddressLayout)
    LinearLayout venueAddressLayout;

    @BindView(R.id.descLayout)
    LinearLayout descLayout;

    @BindView(R.id.venueDescription)
    TextView venueDescription;

    @BindView(R.id.venueAddressMap)
    ImageView venueAddressMap;
    @BindView(R.id.venueAddress)
    TextView venueAddress;
    @BindView(R.id.venueHours)
    TextView venueHours;
    @BindView(R.id.venueOpeningTimes)
    TextView venueOpeningTimes;
    @BindView(R.id.venueCategory)
    TextView venueCategory;
    @BindView(R.id.addressDivider)
    View addressDivider;
    @BindView(R.id.hoursDivider)
    View hoursDivider;

    // CONTACT
    @BindView(R.id.venuePhoneCall)
    View venuePhoneCall;
    @BindView(R.id.venuePhoneDiv)
    View venuePhoneDiv;
    @BindView(R.id.venueWebsite)
    View venueWebsite;
    @BindView(R.id.venueWebDiv)
    View venueWebDiv;

    // FEATURES
    @BindView(R.id.venueFeaturesContainer)
    View venueFeaturesContainer;
    @BindView(R.id.venueFeaturesList)
    RecyclerView venueFeaturesList;

    //MENU
    @BindView(R.id.venueFeaturesMenuContainer)
    View venueFeaturesMenuContainer;
    @BindView(R.id.venueFeaturesMenuItemFood)
    TextView venueFeaturesMenuItemFood;
    @BindView(R.id.venueFeaturesMenuItemDrinks)
    TextView venueFeaturesMenuItemDrinks;
    @BindView(R.id.venueFeaturesMenuBtn)
    Button venueFeaturesMenuBtn;
    @BindView(R.id.venueFeaturesReserveBtn)
    Button venueFeaturesReserveBtn;


    // POPULAR
    @BindView(R.id.venuePopularBarChart)
    DonutProgress venuePopularBarChart;
    @BindView(R.id.venuePopularReasonRating)
    TextView venuePopularReasonRating;
    @BindView(R.id.venuePopularPeopleHere)
    TextView venuePopularPeopleHere;
    @BindView(R.id.venuePopularReasonSummary)
    TextView venuePopularReasonSummary;
    @BindView(R.id.venuePopularLikes)
    TextView venuePopularLikes;


    ToursScrollListener scrollListener;

    private NestedScrollView.OnScrollChangeListener onScrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY - oldScrollY > 0)
                scrollListener.onScrollView(true);
            else if (scrollY - oldScrollY < 0)
                scrollListener.onScrollView(false);
        }
    };

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement TourListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        scrollListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View v = inflater.inflate(R.layout.venue_fragment_info, container, false);
        ButterKnife.bind(this, v);

        setup();

        venueInfoNestedScrollView.setOnScrollChangeListener(onScrollListener);

        return v;
    }

    private void setup() {
        Bundle args = getArguments();

        if (Commons.isNotNull(args)) {

            String venueUrl = args.getString(VenueTabsActivity.EXTRA_VENUE_URL);
            if (Commons.isNull(venueUrl)) {
                venueWebsite.setVisibility(View.GONE);
                venueWebDiv.setVisibility(View.GONE);
            }

            VenueResult venue = args.getParcelable(VenueTabsActivity.EXTRA_VENUE);

            if (Commons.isNotNull(venue)) {

                if(C.DEBUG_ENABLED)
                    try {
                        if (!Commons.isEmpty(venue.getPage().getPageInfo().getDescription())) {
                            Log.d(TAG, "setup: ");
                        }
                    } catch (Exception e) {}


                new SetupMore(venue).run();
            }
        }
    }

    private class SetupMore implements Runnable {

        VenueResult venue;

        SetupMore(VenueResult venue) {
            this.venue = venue;
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {

            // todo check for both en and de lang options in the B-Town tips
            String btownComment = "";
            try {
                if(venue.getTips().getGroupsTips().size() > 0) {
                    for (GroupsTip grpTip : venue.getTips().getGroupsTips()) {
                        for (ItemsGroupsTip tip : grpTip.getItemsGroupsTips()) {
                            if(tip.getUser().getFirstName().equals(getString(R.string.app_name))){
                                btownComment = tip.getText();
                                btownComment = btownComment + " - " + getString(R.string.app_name);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) { /*IGNORE*/  }

            if(Commons.isEmpty(btownComment))
                venueDescription.setText(venue.getDescription());
            else if(!Commons.isEmpty(venue.getDescription()))
                venueDescription.setText(btownComment);
            else
                descLayout.setVisibility(View.GONE);

            populateContact(venue);
            populateFeatures(venue.getAttributes());
            populatePopular(venue);
        }
    }

    private void populateContact(VenueResult venue) {
        if (Commons.isNull(venue.getContact().getPhone())) {
            venuePhoneCall.setVisibility(View.GONE);
            venuePhoneDiv.setVisibility(View.GONE);
        }

        final Context ctx = getActivity();

        ViewUtils.setViewWidth(addressDivider, C.SCREEN_WIDTH / 2, false);
        ViewUtils.setViewWidth(hoursDivider, (C.SCREEN_WIDTH / 4) * 3, false);


        StringBuilder sb = new StringBuilder();
        for (String str : venue.getLocation().getFormattedAddress()) {
            sb.append(str).append("\n");
        }

        String temp = sb.substring(0, sb.lastIndexOf("\n"));
        venueAddress.setText(temp);


        sb = new StringBuilder();
        for (Category cat : venue.getCategories()) {
            sb.append(cat.getName()).append(", ");
        }
        if(!Commons.isEmpty(sb.toString())) {
            temp = sb.substring(0, sb.lastIndexOf(", "));
            venueCategory.setText(temp);
        }

        final double dLat = venue.getLocation().getLat();
        final double dLon = venue.getLocation().getLng();

        ViewTreeObserver viewTreeObserver = venueAddressLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    venueAddressLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = (int) ViewUtils.dpFromPx(ctx, venueAddressLayout.getWidth());
                    int viewHeight = (int) ViewUtils.dpFromPx(ctx, venueAddressLayout.getHeight());

                    String size = viewWidth + "x" + viewHeight;

                    String url = MapUtils.buildStaticOsmMapImageMarkerRight(dLat, dLon, size, 16);

                    // EG: http://staticmap.openstreetmap.de/staticmap.php?center=52.523121068264295,13.297143505009352&zoom=16&size=360x230&maptype=mapnik&markers=52.523121068264295,13.299143505009352,red-pushpin
                    Glide.with(getActivity())
                            .load(Uri.parse(url))
                            .into(venueAddressMap);
                }
            });
        }

        // Show if open or closed
        if (Commons.isNotNull(venue.getPopular())) {

            ArrayList<Timeframe> timeframes = venue.getPopular().getTimeframes();
            String openTimes;

            for (Timeframe frame : timeframes) {
                if (frame.isIncludesToday()) {
                    sb = new StringBuilder();
                    for (Open open : frame.getOpen()) {
                        sb.append(open.getRenderedTime()).append(", ");
                    }
                    openTimes = sb.substring(0, sb.lastIndexOf(", "));
                    venueOpeningTimes.setText(openTimes);
                }
            }

            if (venue.getPopular().isIsOpen()) {
                venueHours.setText(R.string.poi_string_open);
                venueHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.poiOpenTimesOpenColor));
            } else {
                venueHours.setText(R.string.poi_string_closed);
                venueHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.poiOpenTimesClosedColor));
            }
        } else {
            venueHours.setText(R.string.unknown);
            venueHours.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_text_color));
        }

        if (venue.isHasMenu()) {
            venueFeaturesMenuBtn.setVisibility(View.VISIBLE);
            venueFeaturesReserveBtn.setVisibility(View.VISIBLE);
        }
    }



    private void populatePopular(VenueResult venue) {

        try {
            int color = R.color.tintLight;
            if(Commons.isNotNull(venue.getRatingColor()))
                color = Color.parseColor("#" + venue.getRatingColor());

            venuePopularReasonRating.setText("Rating based on " + venue.getStats().getVisitsCount() + " visitors");

            venuePopularBarChart.setTextColor(color);
            if(venue.getRating() == 0.0)
                venuePopularBarChart.setText(getString(R.string.shared_string_none));
            else
                venuePopularBarChart.setText(String.valueOf(venue.getRating()));
            venuePopularBarChart.setMax(100);
            venuePopularBarChart.setProgress((int) (venue.getRating() * 10));
            venuePopularBarChart.setFinishedStrokeColor(color);

            venuePopularPeopleHere.setText(venue.getHereNow().getSummary()); //One other person is here
            venuePopularLikes.setText(venue.getLikes().getSummary());   //"144 Likes"
            venue.getStats().getCheckinsCount();
            venue.getStats().getVisitsCount();

            for (Reason reason : venue.getReasons().getItemsReasons()) {
                if (reason.getType().equals("general"))
                    venuePopularReasonSummary.setText(reason.getSummary());
                else
                    Log.d(TAG, "populatePopular: REASON:: " + reason.getSummary());
            }
        } catch (Exception e) { /* EMPTY - catch null values */ }

        if (Commons.isNull(venuePopularReasonSummary.getText()) || venuePopularReasonSummary.getText().length() == 0)
            venuePopularReasonSummary.setVisibility(View.GONE);
    }


    private void populateFeatures(Attributes attribs) {

        ArrayList<String> featureItems = new ArrayList<>();
        ArrayList<String> menuItems = new ArrayList<>();

        for (GroupsAttribute grp : attribs.getGroupsAttributes()) {

            if (grp.getName().equals("Menus")) {
                venueFeaturesMenuContainer.setVisibility(View.VISIBLE);
                venueFeaturesMenuItemFood.setText(grp.getSummary());
            } else if (grp.getName().equals("Drinks")) {
                venueFeaturesMenuContainer.setVisibility(View.VISIBLE);
                venueFeaturesMenuItemDrinks.setText(grp.getSummary());
            } else {
                for (ItemsAttribute item : grp.getItemsAttributes()) {

                    if (item.getDisplayName().equals("Price")) {
                        String price;
                        switch (item.getPriceTier()) {
                            case 1:
                                price = "0-10 ";
                                break;

                            case 2:
                                price = "11-20 ";
                                break;

                            case 3:
                                price = "21-35 ";
                                break;

                            case 4:
                                price = "36+ ";
                                break;
                            default:
                                price = "??? ";
                        }
                        featureItems.add(item.getDisplayName() + " - " + price + item.getDisplayValue());
                    } else
                        featureItems.add(item.getDisplayName() + " - " + item.getDisplayValue());
                }
            }
        }

        if (featureItems.size() > 0) {
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            venueFeaturesList.setLayoutManager(layoutManager);

            VenueFeaturesAdapter adapter = new VenueFeaturesAdapter(featureItems);
            venueFeaturesList.setAdapter(adapter);
            venueFeaturesList.setHasFixedSize(true);

        } else
            venueFeaturesContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.venueAddressMap)
    void showMap() {
        VenueResult venue = getParcel();

        Intent mapIntent = new Intent(getActivity(), AttractionMapActivity.class);
        mapIntent.putExtra(AttractionMapActivity.TITLE,  venue.getName());
        mapIntent.putExtra(AttractionMapActivity.GEOPOINT, (Parcelable) new GeoPoint(venue.getLocation().getLat(), venue.getLocation().getLng()));
        startActivity(mapIntent);
    }

    @OnClick(R.id.venueFeaturesMenuBtn)
    void menu() {
        venueInfoProgressBar.setVisibility(View.VISIBLE);
        VenueResult venue = getParcel();
        final String venueName = venue.getName();

        FourSquareApi service = FourSquareServiceProvider.get();
        Call<FourSquResult> listsCall = service.getVenueMenu(venue.getId());

        listsCall.enqueue(new Callback<FourSquResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                FourSquResult body = response.body();

                if (Commons.isNotNull(body) && Commons.isNotNull(body.getResponse())) {
                    Menu menu = body.getResponse().getMenu();

                    Intent intent = IntentUtils.launchWeb(menu.getProvider().getAttributionLink());
/*
Use tne internal browser when I work out how to make it work wtih JS selection pop up inside the webview
Mite be nice to have the menu in a separate browser anyway so user can reference it easily

                    Intent intent = new Intent(getActivity(), WikiWebViewActivity.class);
                    intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, menu.getProvider().getAttributionLink());
                    intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, venueName);
                    intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_SUBHEADING, "Provided by " + menu.getProvider().getName());
                    intent.putExtra(WikiWebViewActivity.DISABLE_JS, true);
*/
                    startActivity(intent);
                }
                venueInfoProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                venueInfoProgressBar.setVisibility(View.GONE);
                Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * EG: https://m.opentable.de/search?latitude=52.519927782678884&longitude=13.299450538326427&address=Luisenpl.%201%2C%2010585%20Berlin%2C%20Germany
     */
    @OnClick(R.id.venueFeaturesReserveBtn)
    void reserve() {

        final VenueResult venue = getParcel();
        if (Commons.isNotNull(venue)) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("This is an expeimental feature and may not ")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Location loc = venue.getLocation();

                            String base = "https://m.opentable.de/search?";
                            String paramLoc = "latitude=" + loc.getLat() + "&longitude=" + loc.getLng();
                            String paramAdd = "&address=" + loc.getAddress() + ", " + loc.getPostalCode() + ", " + loc.getCity() + ", " + loc.getCountry();
                            String url = base + paramLoc + paramAdd;

/*
                            Intent intent = new Intent(getActivity(), WikiWebViewActivity.class);
                            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, url);
                            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, venue.getName());
                            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_SUBHEADING, "Provided by OpenTable");
//                            intent.putExtra(WikiWebViewActivity.DISABLE_JS, true);
*/

                            Intent intent = IntentUtils.launchWeb(url);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    }

    @OnClick(R.id.venuePhoneCall)
    void phone() {
        VenueResult venue = getParcel();

        if (Commons.isNotNull(venue)) {
            if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CALL_PHONE)) {
                startActivity(IntentUtils.callPhone(venue.getContact().getPhone()));
            }
        }
    }

    @OnClick(R.id.venueWebsite)
    void web() {
        String venueUrl = getArguments().getString(VenueTabsActivity.EXTRA_VENUE_URL);

        if (Commons.isNotNull(venueUrl)) {
            Intent intent = new Intent(getActivity(), WikiWebViewActivity.class);
            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, venueUrl);
            startActivity(intent);
        }
    }

    @OnClick(R.id.venueDirections)
    void route() {
        VenueResult venue = getParcel();

        if (Commons.isNotNull(venue)) {
            try {
                Intent intent = IntentUtils.sendGeoIntent(venue.getLocation().getLat(), venue.getLocation().getLng(), venue.getName());
                getActivity().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.error_no_maps_app, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.venueShare)
    void share() {
        try {
            VenueResult venue = getParcel();
            if (Commons.isNotNull(venue))
                ShareMenu.show(venue.getLocation().getGeoPoint(), venue.getName(), venue.getLocation().getAddress(), getActivity().getApplicationContext());
        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
    }


    private VenueResult getParcel() {
        return getArguments().getParcelable(VenueTabsActivity.EXTRA_VENUE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}