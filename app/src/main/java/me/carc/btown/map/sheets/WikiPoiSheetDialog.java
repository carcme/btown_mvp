package me.carc.btown.map.sheets;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.osmdroid.util.GeoPoint;

import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.FragmentUtil;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.sheets.share.ShareMenu;
import me.carc.btown.map.sheets.wiki.WikiWebViewActivity;
import me.carc.btown.ui.FeedbackDialog;
import me.carc.btown.ui.custom.CapitalisedTextView;

/**
 * Bottom Sheet Dialog for points of interest
 * Created by bamptonm on 31/08/2017.
 */
public class WikiPoiSheetDialog extends BottomSheetDialogFragment {
    public interface WikiCallback {
        void todo();
    }

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "WikiPoiSheetDialog";
    public static final String ITEM = "ITEM";

    private String address;
    private String userComment;
    private BottomSheetBehavior behavior;
    private Unbinder unbinder;

    @BindView(R.id.wikiDialogContainer)
    LinearLayout wikiDialogContainer;

    @BindView(R.id.wikiToolbar)
    LinearLayout wikiToolbar;

    @BindView(R.id.wikiThumbnail)
    ImageView wikiThumbnail;

    @BindView(R.id.wikiTitle)
    TextView wikiTitle;

    @BindView(R.id.wikiOverflowBtn)
    ImageView wikiOverflowBtn;

    @BindView(R.id.featureSave)
    Button featureSave;

    @BindView(R.id.featureMore)
    Button featureMore;

    @BindView(R.id.featureRead)
    Button featureRead;


    @BindView(R.id.wikiDialogContentContainer)
    LinearLayout wikiDialogContentContainer;

    @BindView(R.id.wikiDescription)
    CapitalisedTextView wikiDescription;

    @BindView(R.id.wikiExtract)
    TextView wikiExtract;

    @BindView(R.id.wikiProgress)
    ProgressBar wikiProgress;


    public static boolean showInstance(final Context appContext, WikiQueryPage element) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM, element);

            WikiPoiSheetDialog fragment = new WikiPoiSheetDialog();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCB = new BottomSheetBehavior.BottomSheetCallback() {

       private boolean sliding;

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                sliding = false;
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                if(sliding)
                    sliding = false;
                else
                    onToolBar();
            }
        }


        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (Float.isNaN(slideOffset) && !sliding) {
                sliding = true;
                onToolBar();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.PoiItemDialog);
    }

    private AppDatabase getDatabase() {
        return ((App) getActivity().getApplicationContext()).getDB();
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(getContext(), R.layout.sheet_wiki_dialog, null);
        unbinder = ButterKnife.bind(this, rootView);

        dialog.setContentView(rootView);

        Bundle args = getArguments();
        if (args == null)
            dismiss();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            this.behavior = (BottomSheetBehavior) behavior;
            this.behavior.setBottomSheetCallback(mBottomSheetCB);
        }

        try {
            WikiQueryPage page = (WikiQueryPage) args.getSerializable(ITEM);

            featureMore.setVisibility(View.GONE);
            featureRead.setVisibility(View.VISIBLE);

            checkReadingList(page.pageId());

            if (Commons.isEmpty(page.description())) {
                if(page.title().contains("(") && page.title().contains(")")) {
                    wikiDescription.setText(page.title().substring(page.title().indexOf("(") + 1, page.title().indexOf(")")));
                } else {
                    wikiDescription.setVisibility(View.GONE);
                }
            } else {
                wikiDescription.setText(page.description());
            }

            wikiTitle.setText(page.title());
            wikiExtract.setText(page.extract());

            if (C.HAS_L) wikiExtract.setNestedScrollingEnabled(true);


            Glide.with(getActivity())
                    .load(page.thumbUrl())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.checkered_background)
                    .error(R.drawable.ic_wiki)
                    .into(wikiThumbnail);

        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());

            // something went wrong, close the dialog
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 200);
        }
        wikiProgress.setVisibility(View.GONE);
    }

    @OnClick(R.id.wikiThumbnail)
    void onThumbNail() {
        try {
            WikiQueryPage page = (WikiQueryPage) getArguments().getSerializable(ITEM);
            if (Commons.isNotNull(page)) {
                // todo check page.fullurl()
                ImageDialog.showInstance(getActivity().getApplicationContext(), page.thumbUrl(), page.fullurl(), page.title(), page.extract());
            }

        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
    }

    @OnClick(R.id.wikiToolbar)
    void onToolBar() {
        if (wikiDialogContentContainer.getVisibility() == View.GONE)
            wikiDialogContentContainer.setVisibility(View.VISIBLE);
        else
            wikiDialogContentContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.wikiOverflowBtn)
    void onOverflow() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), wikiOverflowBtn);
        popupMenu.inflate(R.menu.menu_wiki_overflow);
        popupMenu.setOnMenuItemClickListener(menuListener);
        popupMenu.show();
    }

    private PopupMenu.OnMenuItemClickListener menuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            WikiCallback callback = callback();
            switch (item.getItemId()) {
                case R.id.menu_wiki_share:
                    if (callback != null) {
                        callback.todo();
                    }
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    @Nullable
    private WikiCallback callback() {
        return FragmentUtil.getCallback(this, WikiCallback.class);
    }


    /**
     * Check if the selected poi is in the favorite list
     *
     * @param id the id of the database
     * @return true = item is in the database, false otherwise
     */
    private void checkReadingList(final long id) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                BookmarkEntry entry = getDatabase().bookmarkDao().findByPageId(id);
                final Drawable icon;
                if (Commons.isNull(entry)) {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_empty);
                    featureSave.setTag(R.drawable.ic_bookmark_empty);
                } else {
                    icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark);
                    featureSave.setTag(R.drawable.ic_bookmark);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        featureSave.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
                    }
                });
            }
        });
    }

    public void addReadingList(final WikiQueryPage page) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                BookmarkEntry entry = new BookmarkEntry(page);
                getDatabase().bookmarkDao().insert(entry);

                checkReadingList(page.pageId());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getText(R.string.bookmark_addded), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void removeReadingList(final long id) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                BookmarkEntry entry = getDatabase().bookmarkDao().findByPageId((id));
                if (Commons.isNotNull(entry)) {
                    getDatabase().bookmarkDao().delete(entry);
                    checkReadingList(entry.getPageId());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @OnClick(R.id.featureSave)
    void save() {
        final WikiQueryPage page = (WikiQueryPage) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(page)) {
            // Title and appearance
            FeedbackDialog.Builder builder = new FeedbackDialog.Builder(getActivity());
            builder.titleTextColor(R.color.black);

            builder.formTitle(page.title());
            if(Commons.isEmpty(page.extract())) {
                builder.formHint(getString(R.string.add_your_comment));
            } else
                builder.formHint(page.extract().length() < 100 ? page.extract() : page.extract().substring(0, 100) + "...");

            builder.formText(page.userComment());

            // Allow empty comments
            builder.allowEmpty(true);

            // Positive button
            builder.submitBtnText(getString(R.string.shared_string_save));
            builder.positiveBtnTextColor(R.color.positiveBtnTextColor);
            builder.positiveBtnBgColor(R.drawable.button_selector_positive);

            // Negative button - if tag is ic_bookmark, item is already in the Favorites DB
            if (featureSave.getTag().equals(R.drawable.ic_bookmark_empty))
                builder.cancelBtnText(getString(android.R.string.cancel));
            else
                builder.cancelBtnText(getString(R.string.shared_string_remove));
            builder.negativeBtnTextColor(R.color.negativeBtnTextColor);
            builder.negativeBtnBgColor(R.drawable.button_selector_negative);

            builder.onSumbitClick(
                    new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) {
                            if (!feedback.equalsIgnoreCase(page.userComment())) {
                                page.userComment(feedback);
                                addReadingList(page);
                            }
                        }

                        @Override
                        public void onFormCancel() {
                            // if tag is ic_bookmark, item is already in the Favorites DB
                            if (featureSave.getTag().equals(R.drawable.ic_bookmark))
                                removeReadingList(page.pageId());

                        }
                    });
            builder.build().show();

        }
    }


    @OnClick(R.id.featureShare)
    void share() {
        try {
            WikiQueryPage page = (WikiQueryPage) getArguments().getSerializable(ITEM);
            GeoPoint point = new GeoPoint(page.getLat(), page.getLon());
            ShareMenu.show(point, page.title(), page.fullurl(), getActivity().getApplicationContext());

        } catch (NullPointerException e) {
            Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
        }
    }

    @OnClick(R.id.featureGoogle)
    void googleRoute() {
        WikiQueryPage page = (WikiQueryPage) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(page)) {
            try {
                Intent intent = IntentUtils.sendGeoIntent(page.getLat(), page.getLon(), page.title());
                getActivity().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.error_no_maps_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.featureRead)
    void readWiki() {
        WikiQueryPage page = (WikiQueryPage) getArguments().getSerializable(ITEM);
        if (Commons.isNotNull(page)) {
            Intent intent = new Intent(getActivity(), WikiWebViewActivity.class);
            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, page.title());
            intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, page.fullurl());
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
