package me.carc.btown.map.sheets.search_context;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.map.search.SearchDialogFragment;


public class SearchContextDialogFragment extends BottomSheetDialogFragment implements OnItemClickListener {
    private static final String TAG = SearchContextDialogFragment.class.getName();
    public static final String ID_TAG = "ShareMenuDialogFragment";

    private ArrayAdapter<SearchContextMenu.ContextItem> listAdapter;
    private SearchContextMenu menu;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCB = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.d(TAG, "onSlide: " + slideOffset);
        }
    };


    public static void showInstance(SearchContextMenu menu) {

        SearchContextDialogFragment fragment = new SearchContextDialogFragment();
        fragment.menu = menu;
        fragment.show(menu.getCurrentActivity().getSupportFragmentManager(), ID_TAG);
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(menu.getCurrentActivity(), R.layout.share_menu_fragment, null);

        dialog.setContentView(rootView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior)
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCB);

        ListView listView = rootView.findViewById(R.id.list);
        listAdapter = createAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        TextView title = rootView.findViewById(R.id.header_caption);

        switch (menu.getCategory()) {
            case SearchDialogFragment.SEARCH_ITEM_FAVORITE:
                title.setText(R.string.search_tab_favorite);
                break;

            case SearchDialogFragment.SEARCH_ITEM_HISTORY:
                title.setText(R.string.search_tab_history);
                break;

            case SearchContextMenu.POI:
                title.setText("Point of Interest");
                break;

            case SearchContextMenu.WIKI:
                title.setText(R.string.wikipedia);
                break;

            default:
                throw new RuntimeException("showLongPressSelectionDialog::Unhandled case");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        menu.saveMenu(outState);
    }

    private ArrayAdapter<SearchContextMenu.ContextItem> createAdapter() {
        final List<SearchContextMenu.ContextItem> items = menu.getItems();
        final Context ctx = getActivity().getBaseContext();
        return new ArrayAdapter<SearchContextMenu.ContextItem>(menu.getCurrentActivity(), R.layout.share_list_item, items) {

            @SuppressLint("InflateParams")
            @Override
            @NonNull
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = menu.getCurrentActivity().getLayoutInflater().inflate(R.layout.share_list_item, null);
                }
                final SearchContextMenu.ContextItem item = getItem(position);
                ImageView icon = v.findViewById(R.id.icon);
                assert item != null;
                icon.setImageDrawable(new IconicsDrawable(ctx, item.getIconResourceId()).color(ContextCompat.getColor(ctx, R.color.sheet_heading_text_color)).sizeDp(20));
                TextView name = v.findViewById(R.id.name);
                name.setText(getContext().getText(item.getTitleResourceId()));
                return v;
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        menu.selected(listAdapter.getItem(position));
        dismissMenu();
    }

    public void dismissMenu() {
        dismiss();
    }
}
