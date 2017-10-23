package me.carc.btown.tours;

import android.app.Activity;
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

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.tours.attractionPager.AttractionPagerActivity;
import me.carc.btown.tours.model.Attraction;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import me.carc.btown.ui.custom.MyRecyclerItemClickListener;

import static me.carc.btown.BaseActivity.hideProgressDialog;


public class AttractionTabsStopsFragment extends Fragment {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final int RESULT_ATTRACTION = 1100;

    public interface AttractionListListener {
        void onItemSelected();
    }

    AttractionListListener cbListener;
    ToursScrollListener scrollListener;

    RecyclerView rv;


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
            rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
            cbListener = (AttractionListListener) ctx;
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement AttractionListListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        cbListener = null;
        super.onDetach();
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        Log.d(TAG, "setupRecyclerView: ");

        Bundle args = getArguments();

        if(Commons.isNotNull(args)) {

            ArrayList<Attraction> list = args.getParcelableArrayList("ATTRACTIONS_LIST");

            recyclerView.setLayoutManager(new MyCustomLayoutManager(recyclerView.getContext()));
            final TourDataAdapter adapter = new TourDataAdapter(list);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            // Is this needed
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);

            recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                    recyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(TAG, "onItemClick: ");

                    ArrayList<Attraction> attractions = getArguments().getParcelableArrayList("ATTRACTIONS_LIST");

                    Intent intent = new Intent(getActivity(), AttractionPagerActivity.class);
                    intent.putParcelableArrayListExtra("ATTRACTIONS_LIST", attractions);
                    intent.putExtra("INDEX", position);

                    startActivityForResult(intent, RESULT_ATTRACTION);
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_ATTRACTION:
                hideProgressDialog();
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: ");

                    int storyIndex = data.getIntExtra(AttractionPagerActivity.SCROLL_TO_NEW_INDEX, -1);
                    if (-1 != storyIndex)
                        rv.smoothScrollToPosition(storyIndex);
                }
                break;
        }
    }
}