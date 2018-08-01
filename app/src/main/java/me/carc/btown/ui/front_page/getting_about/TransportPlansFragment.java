package me.carc.btown.ui.front_page.getting_about;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btown.R;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.extras.PublicTransportPlan;
import me.carc.btown.extras.adapters.TransportPlanSelectionAdapter;
import me.carc.btown.extras.bahns.Bahns;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.ui.custom.DividerItemDecoration;
import me.carc.btown.ui.custom.PreDrawLayoutManager;

import static android.app.Activity.RESULT_OK;

public class TransportPlansFragment extends Fragment {

    ToursScrollListener scrollListener;

    public static final String MAP_ID = "MAP_ID";
    public static final String MAP_DESC = "MAP_DESC";
    private static final String LIST_POSITION = "LIST_POSITION";
    private static final int RESULT_OPENED = 789;

    private TransportPlanSelectionAdapter mAdapter;
    private Unbinder unbinder;

    @BindView(R.id.catalogue_recycler) RecyclerView recyclerView;
    @BindView(R.id.toursToolbar) Toolbar toolbar;
    @BindView(R.id.tourProgressBar) ContentLoadingProgressBar progressCenter;
    @BindView(R.id.appBarProgressBar) ProgressBar appBarProgressBar;
    @BindView(R.id.fabExit) FloatingActionButton fabExit;

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
    public void onAttach(Activity act) {
        super.onAttach(act);
        try {
            scrollListener = (ToursScrollListener) act;
        } catch (ClassCastException e) {
            throw new ClassCastException(act.toString() + " must implement TourListener callbacks");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tours_catalogue_activity, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupUI();

        return view;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void setupUI() {
        toolbar.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
        fabExit.setVisibility(View.GONE);
        setupRecycler();
    }

    private void setupRecycler() {
        mAdapter = new TransportPlanSelectionAdapter();

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                PreDrawLayoutManager layoutManager = new PreDrawLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
                recyclerView.addItemDecoration(itemDecoration);
            }
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnScrollListener(onScrollListener);

            recyclerView.addOnItemTouchListener(new OnItemSelectedListener(getActivity()) {

                @Override
                public void onItemSelected(RecyclerView.ViewHolder holder, int pos) {
                    Bahns.BahnItem item = mAdapter.getItem(pos);
                    Intent intent = new Intent(getActivity(), PublicTransportPlan.class);
                    intent.putExtra(MAP_ID, item.getFileName());
                    intent.putExtra(MAP_DESC, item.getDescriptionResourceId());
                    intent.putExtra(LIST_POSITION, pos);
                    startActivityForResult(intent, RESULT_OPENED);
                }
            });

        }

        progressCenter.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_OPENED:
                if (resultCode == RESULT_OK && data.hasExtra(LIST_POSITION))
                    mAdapter.notifyItemChanged(data.getIntExtra(LIST_POSITION, 0));
                break;

            default:
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                new SendFeedback(getActivity(), SendFeedback.TYPE_FEEDBACK);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}