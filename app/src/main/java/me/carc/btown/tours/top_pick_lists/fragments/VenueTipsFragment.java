package me.carc.btown.tours.top_pick_lists.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.GroupsTip;
import me.carc.btown.data.all4squ.entities.ItemsGroupsTip;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.tours.top_pick_lists.VenueTabsActivity;
import me.carc.btown.tours.top_pick_lists.adapters.VenueTipsAdapter;
import me.carc.btown.ui.GridMarginDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenueTipsFragment extends Fragment {
    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String PREFKEY_SAVED_TOUR_NOTES = "PREFKEY_SAVED_TOUR_NOTES";

    public static final int TIPS_LIMIT = 30;
    private int TIPS_OFFSET = 0;
    private int TIPS_TOTAL_AVAILABLE = 0;

    private VenueTipsAdapter mAdapter;

    @BindView(R.id.venueTipsNestedScrollView)
    NestedScrollView venueTipsNestedScrollView;

    @BindView(R.id.tipsLoadMore)
    Button tipsLoadMore;

    @BindView(R.id.venueTipsRecycleView)
    RecyclerView venueTipsRV;

    @BindView(R.id.tipsProgressBar)
    ProgressBar tipsProgressBar;

    @BindView(R.id.emptyTips)
    TextView emptyTips;


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
        View v = inflater.inflate(R.layout.venue_fragment_tips, container, false);
        ButterKnife.bind(this, v);

        setup();

        venueTipsNestedScrollView.setOnScrollChangeListener(onScrollListener);

        return v;
    }

    private void setup() {
        Bundle args = getArguments();

        if (Commons.isNotNull(args)) {
            setupRecyclerView();
            populateTips(args);
        }
    }

    private void setupRecyclerView() {
        Context ctx = getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
        venueTipsRV.setLayoutManager(layoutManager);
        venueTipsRV.addItemDecoration(new GridMarginDecoration(getResources().getDimensionPixelSize(R.dimen.grid_item_spacing_none)));
        venueTipsRV.setHasFixedSize(false);
        venueTipsRV.setNestedScrollingEnabled(false);
    }

    private void populateTips(Bundle args) {

        ArrayList<GroupsTip> groups = args.getParcelableArrayList(VenueTabsActivity.EXTRA_TIPS);

        if (groups.size() > 1)
            Log.d(TAG, "populateGrid: Check this - have more than one group");

        mAdapter = new VenueTipsAdapter(filterByLanguage(groups));
        venueTipsRV.setAdapter(mAdapter);

        venueTipsRV.addOnItemTouchListener(new OnItemSelectedListener(getActivity()) {

            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {

                ItemsGroupsTip tip = mAdapter.getTip(position);
                String image = tip.getPhotourl();

                if (Commons.isNotNull(image)) {
                    ImageDialog.showInstance(getActivity().getApplicationContext(),
                            image,
                            image,
                            tip.getUser().getFullName(),
                            Commons.readableDate(tip.getCreatedAt() * 1000L));
                }
            }
        });

        if (TIPS_OFFSET >= TIPS_TOTAL_AVAILABLE) {
            tipsLoadMore.setVisibility(View.GONE);
        } else {
            tipsLoadMore.setText(String.format(getString(R.string.load_more_photos), TIPS_TOTAL_AVAILABLE - TIPS_OFFSET));
            tipsLoadMore.setTag(mAdapter.getItemCount());
            emptyTips.setVisibility(View.GONE);
        }

        tipsProgressBar.setVisibility(View.GONE);
    }

    private ArrayList<ItemsGroupsTip> filterByLanguage(ArrayList<GroupsTip> groups) {
        ArrayList<ItemsGroupsTip> tips = new ArrayList<>();
        for (GroupsTip grp : groups) {
            TIPS_TOTAL_AVAILABLE = grp.getCount();
            for (ItemsGroupsTip tip : grp.getItemsGroupsTips()) {
                if (tip.getLang().equals(C.USER_LANGUAGE))
                    tips.add(tip);
                else
                    TIPS_TOTAL_AVAILABLE--;

                TIPS_OFFSET++;
            }
        }

        if (tips.size() == 0)
            emptyTips.setVisibility(View.VISIBLE);

        return tips;
    }

    @OnClick(R.id.tipsLoadMore)
    void loadMorePhotos() {
        tipsProgressBar.setVisibility(View.VISIBLE);
        Bundle args = getArguments();

        if (Commons.isNotNull(args)) {
            String venueId = args.getString(VenueTabsActivity.EXTRA_VENUE_ID);

            FourSquareApi service = FourSquareServiceProvider.get();
            Call<FourSquResult> listsCall = service.getVenueTips(venueId, "recent", TIPS_LIMIT, TIPS_OFFSET);

            listsCall.enqueue(new Callback<FourSquResult>() {
                @SuppressWarnings({"ConstantConditions"})
                @Override
                public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                    FourSquResult body = response.body();

                    try {
                        // TODO: 11/11/2017   Problem

                        GroupsTip grpTips = body.getResponse().getTips();

                        new SetupMore(grpTips).run();


/*
                        mAdapter.updateTipList(grpTips.getItemsGroupsTips());
                        TIPS_OFFSET = mAdapter.getItemCount();

                        if (TIPS_OFFSET >= (int) tipsLoadMore.getTag())
                            tipsLoadMore.setVisibility(View.GONE);
                        else {
                            int remainingImageCount = (int) tipsLoadMore.getTag() - TIPS_OFFSET;
                            tipsLoadMore.setText(String.format(getString(R.string.load_more_photos), remainingImageCount));
                        }
*/


                    } catch (Exception e) {
                        Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
                    }
                    tipsProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                    tipsProgressBar.setVisibility(View.GONE);
                    Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
                }
            });
        }
    }


    private class SetupMore implements Runnable {

        GroupsTip grpTips;

        SetupMore(GroupsTip grpTips) {
            this.grpTips = grpTips;

            ArrayList<GroupsTip> groups = getArguments().getParcelableArrayList(VenueTabsActivity.EXTRA_TIPS);
            for (GroupsTip grp : groups) {
                if(grp.getName().equals("All tips"))
                    grp.getItemsGroupsTips().addAll(grpTips.getItemsGroupsTips());
            }
            // update the list
            getArguments().putParcelableArrayList(VenueTabsActivity.EXTRA_TIPS, groups);
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {
            mAdapter.updateTipList(grpTips.getItemsGroupsTips());
            TIPS_OFFSET = mAdapter.getItemCount();

            if (TIPS_OFFSET >= (int) tipsLoadMore.getTag())
                tipsLoadMore.setVisibility(View.GONE);
            else {
                int remainingImageCount = (int) tipsLoadMore.getTag() - TIPS_OFFSET;
                tipsLoadMore.setText(String.format(getString(R.string.load_more_photos), remainingImageCount));
            }
        }
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