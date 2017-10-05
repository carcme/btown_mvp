package me.carc.btownmvp.map.search.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import me.carc.btownmvp.R;
import me.carc.btownmvp.map.search.SearchDialogFragment;

/**
 * Created by bamptonm on 20/09/2017.
 */

public class QuickSearchHistoryListFragment extends QuickSearchListFragment {
    public static final int TITLE = R.string.search_tab_history;

    @Override
    public SearchListFragmentType getType() {
        return SearchListFragmentType.HISTORY;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getDialogFragment().itemLongPress(SearchDialogFragment.SEARCH_ITEM_HISTORY, position - getListView().getHeaderViewsCount());
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
    }
}