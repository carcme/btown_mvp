package me.carc.btown.tours;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.tours.adapters.TourDataAdapter;
import me.carc.btown.tours.attractionPager.AttractionPagerActivity;
import me.carc.btown.tours.model.Attraction;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import me.carc.btown.ui.custom.MyRecyclerItemClickListener;

public class AttractionTabsStopsFragment extends Fragment {

    private static final String TAG = AttractionTabsStopsFragment.class.getName();
    public static final int RESULT_ATTRACTION = 1100;

//    public interface AttractionListListener {
//        void onItemSelected();
//    }

//    AttractionListListener cbListener;
    ToursScrollListener scrollListener;

    RecyclerView rv;

    ProgressDialog mProgressBar;

    public AttractionTabsStopsFragment() {
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy > 0)
                scrollListener.onScrollView(true);
            else
                scrollListener.onScrollView(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rv = (RecyclerView)inflater.inflate( R.layout.tour_list_recycler, container, false);
        setupRecyclerView(rv);
        rv.setNestedScrollingEnabled(true);
        if(C.HAS_M)
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
                    if(dy > 0)
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
//            cbListener = (AttractionListListener) ctx;
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement AttractionListListener callbacks");
        }
    }

    @Override
    public void onDetach() {
//        cbListener = null;
        super.onDetach();
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        final Bundle args = getArguments();
        if(Commons.isNotNull(args)) {

            ArrayList<Attraction> list = args.getParcelableArrayList(CataloguePreviewActivity.ATTRACTIONS_LIST);

            recyclerView.setLayoutManager(new MyCustomLayoutManager(recyclerView.getContext()));
            final TourDataAdapter adapter = new TourDataAdapter(list, C.USER_LANGUAGE.equals("de"));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            // Is this needed
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                    recyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // lots of info to process and views to build - show a modal dialog so user doesn't press stuff while loading
                    showLoadingDialog();

                    Intent intent = new Intent(getActivity(), AttractionPagerActivity.class);
                    intent.putExtra(CatalogueActivity.CATALOGUE_INDEX, args.getInt(CatalogueActivity.CATALOGUE_INDEX));
                    intent.putExtra(AttractionPagerActivity.ATTRACTION_INDEX, position);
                    startActivityForResult(intent, RESULT_ATTRACTION);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));

            createProgressBar();
        }
    }

    @Override
    public void onResume() {
        hideLoadingDialog();
        super.onResume();
    }

    private void createProgressBar() {
        mProgressBar = new ProgressDialog(getActivity(), R.style.LoadingDialog);
        mProgressBar.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setCancelable(false);
        mProgressBar.setTitle(R.string.shared_string_initialising);
    }

    private void showLoadingDialog() {
        if (mProgressBar == null) {
            createProgressBar();
        }
        mProgressBar.show();
    }

    private void hideLoadingDialog() {
        if(Commons.isNotNull(mProgressBar)) mProgressBar.dismiss();
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