package me.carc.btown.ui.front_page.getting_about;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.common.interfaces.ToursScrollListener;

public class TransportAirportFragment extends Fragment {

	ToursScrollListener scrollListener;

	@BindView(R.id.nestedScroll) NestedScrollView nestedScroll;

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
		View v = inflater.inflate(R.layout.fragment_transport_airport, container, false);
		ButterKnife.bind(this, v);
		nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
			@Override
			public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
				if(scrollY > oldScrollY)
					scrollListener.onScrollView(true);
				else
					scrollListener.onScrollView(false);
			}
		});
		return v;
	}
}