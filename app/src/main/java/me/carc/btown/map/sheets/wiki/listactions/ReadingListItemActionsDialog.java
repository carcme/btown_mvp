package me.carc.btown.map.sheets.wiki.listactions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.FragmentUtil;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.sheets.wiki.WikiReadingListDialogFragment;

public class ReadingListItemActionsDialog extends BottomSheetDialogFragment {
    public interface Callback {
        void onShareItem(BookmarkEntry entry);

        void onShowOnMap(BookmarkEntry entry);

        void onDeleteItem(BookmarkEntry entry);
    }

    public static final String ID_TAG = "ReadingListItemActionsDialog";
    public static final String TITLE = "TITLE";
    public static final String BOOKMARK_ENTRY = "BOOKMARK_ENTRY";

    private Callback callback;

    private int pageIndex;
    private ReadingListItemActionsView actionsView;
    private ItemActionsCallback itemActionsCallback = new ItemActionsCallback();

    @NonNull
    public static ReadingListItemActionsDialog newInstance(Context appContext, @NonNull BookmarkEntry entry) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        ReadingListItemActionsDialog instance = new ReadingListItemActionsDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, entry.getTitle());
        bundle.putParcelable(BOOKMARK_ENTRY, entry);
        instance.setArguments(bundle);
        instance.show(activity.getSupportFragmentManager(), ID_TAG);

        return instance;
    }

    public void setListener(Callback callback) {
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        actionsView = new ReadingListItemActionsView(getContext());
        actionsView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.paper_color));
        actionsView.setCallback(itemActionsCallback);
        pageIndex = getArguments().getInt(TITLE);
        actionsView.setState(getArguments().getString(TITLE, getString(R.string.shared_string_options)));
        return actionsView;
    }

    @Override
    public void onDestroyView() {
        actionsView.setCallback(null);
        actionsView = null;
        super.onDestroyView();
    }

    private class ItemActionsCallback implements ReadingListItemActionsView.Callback {
        @Override
        public void onShare() {
            dismiss();
            callback.onShareItem(getEntry());
        }

        @Override
        public void onShowOnMap() {
            dismiss();
            callback.onShowOnMap(getEntry());
        }

        @Override
        public void onRemove() {
            dismiss();
            callback.onDeleteItem(getEntry());
        }
    }

    private BookmarkEntry getEntry() {
        return getArguments().getParcelable(BOOKMARK_ENTRY);
    }

    @Nullable
    private WikiReadingListDialogFragment.BookmarksCallback callback() {
        return FragmentUtil.getCallback(this, WikiReadingListDialogFragment.BookmarksCallback.class);
    }
}
