/*    Transportr
 *    Copyright (C) 2013 - 2016 Torsten Grote
 *
 *    This program is Free Software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.carc.btown.ui.front_page.getting_about;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.common.interfaces.DrawableClickListener;
import me.carc.btown.extras.PublicTransportPlan;
import me.carc.btown.extras.adapters.TransportPlanSelectionAdapter;
import me.carc.btown.extras.bahns.Bahns;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.ui.custom.DividerItemDecoration;
import me.carc.btown.ui.custom.MyCustomLayoutManager;

public class TransportPlansFragment extends Fragment {

    private static final int RESULT_OPENED = 789;

    private TransportPlanSelectionAdapter mAdapter;

    @BindView(R.id.catalogue_recycler)
    RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.transportMapView)
    SubsamplingScaleImageView imageView;

    @BindView(R.id.toursApplicationToolBar)
    View toursApplicationToolBar;

    @BindView(R.id.toursToolbar)
    Toolbar toolbar;

    @BindView(R.id.inventoryProgressBar)
    ContentLoadingProgressBar progressCenter;

    @BindView(R.id.appBarProgressBar)
    ProgressBar appBarProgressBar;

    @BindView(R.id.fabExit)
    FloatingActionButton fabExit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tours_catalogue_activity, container, false);
        ButterKnife.bind(this, view);

        setupUI();

        return view;
    }

    private void setupUI() {
        toolbar.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
        fabExit.setVisibility(View.GONE);
        setupRecycler();
    }

    private void setupRecycler() {
        recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mAdapter = new TransportPlanSelectionAdapter(new DrawableClickListener() {

            @Override
            public void OnClick(View v, Drawable drawable, int pos) {
                Bahns.BahnItem item = mAdapter.getItem(pos);

                Intent intent = new Intent(getActivity(), PublicTransportPlan.class);
                intent.putExtra("MAP_ID", item.getFileName());
                intent.putExtra("MAP_DESC", item.getDescriptionResourceId());
                startActivityForResult(intent, RESULT_OPENED);
            }

            @Override
            public void OnLongClick(View v, int pos) {
            }
        });

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
                recyclerView.addItemDecoration(itemDecoration);
            }
            recyclerView.setAdapter(mAdapter);
//            recyclerView.addOnScrollListener(onScrollListener);
        }

        progressCenter.setVisibility(View.GONE);
        appBarProgressBar.setVisibility(View.GONE);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy > 0)
                fabExit.hide();
            else
                fabExit.show();
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