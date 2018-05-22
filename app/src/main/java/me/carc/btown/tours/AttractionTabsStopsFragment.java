package me.carc.btown.tours;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.tours.adapters.TourDataAdapter;
import me.carc.btown.tours.attractionPager.AttractionPagerActivity;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import me.carc.btown.ui.custom.MyRecyclerItemClickListener;

public class AttractionTabsStopsFragment extends Fragment {

    private static final String TAG = AttractionTabsStopsFragment.class.getName();
    public static final String TAG_ID = "AttractionTabsStopsFragment";

    public static final int RESULT_ATTRACTION = 1100;

    ToursScrollListener scrollListener;
    RecyclerView rv;
    ProgressDialog mProgressBar;

    public AttractionTabsStopsFragment() {
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0)
                scrollListener.onScrollView(true);
            else
                scrollListener.onScrollView(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        rv = (RecyclerView) inflater.inflate(R.layout.tour_list_recycler, container, false);
        setupRecyclerView(rv);
        rv.setNestedScrollingEnabled(true);

        if (C.HAS_M)
            rv.addOnScrollListener(onScrollListener);
        else
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0)
                        scrollListener.onScrollView(true);
                    else
                        scrollListener.onScrollView(false);

                }
            });
        return rv;
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement AttractionListListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        Log.d(TAG, "setupRecyclerView: ");
        final Bundle args = getArguments();
        if (Commons.isNotNull(args)) {
            final TourDataAdapter adapter = new TourDataAdapter(C.USER_LANGUAGE.equals("de"));
            recyclerView.setAdapter(adapter);

            TourViewModel tourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
            tourViewModel.getAllTours().observe(this, new Observer<List<TourCatalogueItem>>() {
                @Override
                public void onChanged(@Nullable final List<TourCatalogueItem> tours) {
                    Log.d(TAG, "onChanged: is good? -> " + (tours != null));
                    if (tours != null)
                        adapter.setItems(tours.get(args.getInt(CatalogueActivity.CATALOGUE_INDEX)).getAttractions());
                }
            });

            recyclerView.setLayoutManager(new MyCustomLayoutManager(recyclerView.getContext()));
            // Is this needed
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                    recyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // lots of info to process and views to build - show a modal dialog so user doesn't press stuff while loading
//                    showLoadingDialog();
                    Intent intent = new Intent(getActivity(), AttractionPagerActivity.class);
                    intent.putExtra(CatalogueActivity.CATALOGUE_INDEX, args.getInt(CatalogueActivity.CATALOGUE_INDEX));
                    intent.putExtra(AttractionPagerActivity.ATTRACTION_INDEX, position);
                    startActivityForResult(intent, RESULT_ATTRACTION);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_ATTRACTION:
                if (resultCode == Activity.RESULT_OK) {
                    int storyIndex = data.getIntExtra(AttractionPagerActivity.SCROLL_TO_NEW_INDEX, -1);
                    if (-1 != storyIndex)
                        rv.smoothScrollToPosition(storyIndex);
                }
                break;
            default:
        }
    }
}