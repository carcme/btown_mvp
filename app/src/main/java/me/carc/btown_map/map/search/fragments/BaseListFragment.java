package me.carc.btown_map.map.search.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;

import me.carc.btown_map.App;
import me.carc.btown_map.R;


public abstract class BaseListFragment extends ListFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
	}
	
	public abstract ArrayAdapter<?> getAdapter();

	public App getMyApplication() {
		return (App)getActivity().getApplication();
	}
	
}
