package me.carc.btown.map.search.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.map.search.SearchDialogFragment;
import me.carc.btown.map.search.SearchListAdapter;
import me.carc.btown.map.search.model.Place;

public abstract class QuickSearchListFragment extends BaseListFragment {

    private SearchDialogFragment dialogFragment;
    private SearchListAdapter mylistAdapter;
    private boolean touching;
    private boolean scrolling;

    public enum SearchListFragmentType {
        HISTORY,
        CATEGORIES,
        MAIN,
        FAVORITE
    }

    public abstract SearchListFragmentType getType();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_dialog_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView)view.findViewById(android.R.id.list);
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);

        SearchListFragmentType type = getType();

        switch (type){
            case FAVORITE:
                emptyText.setText(R.string.favorites_list_empty);
                emptyText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, ContextCompat.getDrawable(getContext(), R.drawable.ic_building), null, null);
                break;

            case HISTORY:
                emptyText.setText(R.string.history_list_empty);
                emptyText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, ContextCompat.getDrawable(getContext(), R.drawable.ic_history_large), null, null);
                break;
        }

        if (listView != null) {
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }

                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    scrolling = (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                    if (scrolling) {
                        dialogFragment.hideKeyboard();
                    }
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        Place item = mylistAdapter.getItem(position - l.getHeaderViewsCount());
        if (item != null) {
            dialogFragment.completeQueryWithObject(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialogFragment = (SearchDialogFragment) getParentFragment();
        mylistAdapter = new SearchListAdapter(getActivity().getApplicationContext());

        setListAdapter(mylistAdapter);

        ListView listView = getListView();
        listView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.almostBlack));
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touching = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touching = false;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public SearchListAdapter getListAdapter() {
        return mylistAdapter;
    }

    public ArrayAdapter<?> getAdapter() {
        return mylistAdapter;
    }

    public SearchDialogFragment getDialogFragment() {
        return dialogFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialogFragment.onSearchListFragmentResume(this);
    }

    public void updateLocation(GeoPoint latLon, Float heading) {
        if (mylistAdapter != null && !touching && !scrolling) {
            mylistAdapter.setLocation(latLon);
            mylistAdapter.setHeading(heading);
            mylistAdapter.notifyDataSetChanged();
        }
    }

    public void updateListAdapter(List<Place> listItems) {
        if (mylistAdapter != null) {
            mylistAdapter.addItems(listItems);
        }
        bugFixShowHide();
    }


    public void sortList(int sortBy) {
        if (mylistAdapter != null) {
            mylistAdapter.sortList(sortBy);
        }
//        bugFixShowHide();
    }

    private void bugFixShowHide(){
        // Bug fix - without this, the last element repeats after deleting items!!
        getListView().setVisibility(View.GONE);
        getListView().setVisibility(View.VISIBLE);
    }

    public void addListItem(Place listItem) {
        if (listItem != null) {
            mylistAdapter.addListItem(listItem);
        }
    }
}