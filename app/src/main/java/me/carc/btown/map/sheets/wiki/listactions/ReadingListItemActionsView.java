package me.carc.btown.map.sheets.wiki.listactions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.R;

public class ReadingListItemActionsView extends LinearLayout {
    public interface Callback {
        void onShare();
        void onShowOnMap();
        void onRemove();
    }

    @BindView(R.id.reading_list_item_title) TextView titleView;

    @Nullable
    private Callback callback;

    public ReadingListItemActionsView(Context context) {
        super(context);
        init();
    }

    public ReadingListItemActionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadingListItemActionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReadingListItemActionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_reading_list_page_actions, this);
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
    }

    public void setState(@NonNull String pageTitle) {
        titleView.setText(pageTitle);
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @OnClick(R.id.reading_list_item_share) void onShareClick(View view) {
        if (callback != null) {
            callback.onShare();
        }
    }

    @OnClick(R.id.reading_list_item_showMap) void onShowOnMap(View view) {
        if (callback != null) {
            callback.onShowOnMap();
        }
    }

    @OnClick(R.id.reading_list_item_remove) void onRemove(View view) {
        if (callback != null) {
            callback.onRemove();
        }
    }
}
