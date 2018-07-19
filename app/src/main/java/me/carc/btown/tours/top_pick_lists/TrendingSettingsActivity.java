package me.carc.btown.tours.top_pick_lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.common.TinyDB;
import me.carc.btown.tours.top_pick_lists.adapters.TrendingSearchAdapter;

public class TrendingSettingsActivity extends BaseActivity {
    private static final String TAG = TrendingSettingsActivity.class.getName();

    public static final String TRENDING_QUERY       = "TRENDING_QUERY";
    public static final String TRENDING_SECTION     = "TRENDING_SECTION";
    public static final String TRENDING_RADIUS      = "TRENDING_RADIUS";
    public static final String TRENDING_SORT_DIST   = "TRENDING_SORT_DIST";
    public static final String TRENDING_OPEN_NOW    = "TRENDING_OPEN_NOW";

    int[] mRadiusValues;

    @BindView(R.id.trending_toolbar) Toolbar toolbar;
    @BindView(R.id.nowOpen) CheckBox nowOpen;
    @BindView(R.id.sortDistance) CheckBox sortDistance;
    @BindView(R.id.searchRadius) Spinner searchRadius;

    @BindView(R.id.trendingEditText) EditText trendingEditText;
    @BindView(R.id.trendingClearButton) ImageButton trendingClearButton;
    @BindView(R.id.trendingSearchButton) ImageButton trendingSearchButton;

    @BindView(R.id.trendingRecyclerView) RecyclerView trendingRecyclerView;

    public interface ClickListener {
        void onClick(TrendingSelectionItem item);
    }

    @OnClick(R.id.trendingClearButton)
    void clear(){
        trendingEditText.setText("");
    }

    @OnClick(R.id.trendingSearchButton)
    void querySearch(){
        startSearch(true, trendingEditText.getText().toString());
    }

    @OnTextChanged(value = R.id.trendingEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterEmailInput(Editable editable) {
        if(editable.toString().length() > 0) {
            trendingSearchButton.setVisibility(View.VISIBLE);
            trendingClearButton.setVisibility(View.VISIBLE);
        } else {
            trendingSearchButton.setVisibility(View.GONE);
            trendingClearButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TrendingSearchAdapter adapter = new TrendingSearchAdapter(buildMenuItems(), new ClickListener() {
            @Override
            public void onClick(TrendingSelectionItem item) {
                startSearch(false, item.getSectionValue());
            }
        });

        trendingRecyclerView.setAdapter(adapter);
        trendingRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        mRadiusValues = getResources().getIntArray(R.array.trendingRadiusValuesArray);
        searchRadius.setSelection(TinyDB.getTinyDB().getInt("RADIUS_INDEX", 1));
    }

    private void startSearch(boolean querySearch, String query) {

        TinyDB.getTinyDB().putInt("RADIUS_INDEX", searchRadius.getSelectedItemPosition());

        Intent intent = getIntent();
        if(querySearch)
            intent.putExtra(TRENDING_QUERY, query);
        else
            intent.putExtra(TRENDING_SECTION, query);

        intent.putExtra(TRENDING_RADIUS, mRadiusValues[searchRadius.getSelectedItemPosition()]);
        intent.putExtra(TRENDING_SORT_DIST, sortDistance.isChecked());
        intent.putExtra(TRENDING_OPEN_NOW, nowOpen.isChecked());
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private List<TrendingSelectionItem> buildMenuItems() {
        List<TrendingSelectionItem> items = new LinkedList<>();
        items.add(TrendingSelectionItem.topPicks);
        items.add(TrendingSelectionItem.food);
        items.add(TrendingSelectionItem.drinks);
        items.add(TrendingSelectionItem.coffee);
        items.add(TrendingSelectionItem.shops);
        items.add(TrendingSelectionItem.arts);
        items.add(TrendingSelectionItem.outdoors);
        items.add(TrendingSelectionItem.sights);

        return items;
    }
}
