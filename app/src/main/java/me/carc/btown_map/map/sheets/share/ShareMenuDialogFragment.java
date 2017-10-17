package me.carc.btown_map.map.sheets.share;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import me.carc.btown_map.R;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;


public class ShareMenuDialogFragment extends BottomSheetDialogFragment implements OnItemClickListener {
    public static final String ID_TAG = "ShareMenuDialogFragment";
    private static final String TAG = C.DEBUG + Commons.getTag();

    private ArrayAdapter<ShareMenu.ShareItem> listAdapter;
    private ShareMenu menu;

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


    public static void showInstance(ShareMenu menu) {


        ShareMenuDialogFragment fragment = new ShareMenuDialogFragment();
        fragment.menu = menu;
        fragment.show(menu.getActivity().getSupportFragmentManager(), ID_TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(menu.getActivity(), R.layout.share_menu_fragment, null);

        dialog.setContentView(rootView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior)
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCB);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listAdapter = createAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        menu.saveMenu(outState);
    }

    private ArrayAdapter<ShareMenu.ShareItem> createAdapter() {
        final List<ShareMenu.ShareItem> items = menu.getItems();
        final Context ctx = getActivity().getBaseContext();
        return new ArrayAdapter<ShareMenu.ShareItem>(menu.getActivity(), R.layout.share_list_item, items) {

            @SuppressLint("InflateParams")
            @Override
            @NonNull
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    v = menu.getActivity().getLayoutInflater().inflate(R.layout.share_list_item, null);
                }
                final ShareMenu.ShareItem item = getItem(position);
                ImageView icon = (ImageView) v.findViewById(R.id.icon);
                assert item != null;
                icon.setImageDrawable(new IconicsDrawable(ctx, item.getIconResourceId()).color(ContextCompat.getColor(ctx, R.color.sheet_heading_text_color)).sizeDp(20));
                TextView name = (TextView) v.findViewById(R.id.name);
                name.setText(getContext().getText(item.getTitleResourceId()));
                return v;
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        menu.share(listAdapter.getItem(position));
        dismissMenu();
    }

    public void dismissMenu() {
        dismiss();
    }
}
