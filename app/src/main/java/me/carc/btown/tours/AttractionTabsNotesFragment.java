package me.carc.btown.tours;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.R;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.interfaces.ToursScrollListener;


public class AttractionTabsNotesFragment extends Fragment {
    private static final String TAG = AttractionTabsNotesFragment.class.getName();
    public static final String PREFKEY_SAVED_TOUR_NOTES = "PREFKEY_SAVED_TOUR_NOTES";

    ToursScrollListener scrollListener;

    @BindView(R.id.notesNestedScrollView) NestedScrollView notesNestedScrollView;
    @BindView(R.id.notesText) EditText notesText;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attraction_notes_fragment_layout, container, false);
        ButterKnife.bind(this, v);

        notesNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        notesText.setText(TinyDB.getTinyDB().getString(PREFKEY_SAVED_TOUR_NOTES));
    }

    @Override
    public void onPause() {
        super.onPause();
        TinyDB.getTinyDB().putString(PREFKEY_SAVED_TOUR_NOTES, notesText.getText().toString());
    }

    public void onDestroy() {
        super.onDestroy();
    }
}