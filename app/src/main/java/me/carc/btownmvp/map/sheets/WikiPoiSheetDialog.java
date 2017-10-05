package me.carc.btownmvp.map.sheets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btownmvp.App;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.FragmentUtil;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.data.wiki.WikiQueryPage;
import me.carc.btownmvp.ui.custom.CapitalisedTextView;

/**
 * Bottom Sheet Dialog for points of interest
 * Created by bamptonm on 31/08/2017.
 */
public class WikiPoiSheetDialog extends BottomSheetDialogFragment {
    public interface WikiCallback {
        void onLinkPreviewLoadPage();
        void onLinkPreviewCopyLink();
        void onLinkPreviewAddToList();
        void onLinkPreviewShareLink();
    }

    public static final String TAG = "FavoriteDialogFragment";
    public static final String ITEM = "ITEM";

    private String address;
    private String userDescription;
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

    @BindView(R.id.wikiDialogContentContainer)
    LinearLayout wikiDialogContentContainer;

    @BindView(R.id.wikiDescription)
    CapitalisedTextView wikiDescription;

    @BindView(R.id.wikiExtract)
    TextView wikiExtract;

    @BindView(R.id.wikiProgress)
    ProgressBar wikiProgress;


    public static boolean showInstance(final MapActivity mapActivity, WikiQueryPage element) {

        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM, element);

            WikiPoiSheetDialog fragment = new WikiPoiSheetDialog();
            fragment.setArguments(bundle);
            fragment.show(mapActivity.getSupportFragmentManager(), TAG);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.PoiItemDialog);
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
            this.behavior.setPeekHeight(R.dimen.wikiSheetPeekHeight);
        }

        try {
            WikiQueryPage page = (WikiQueryPage) args.getSerializable(ITEM);

            wikiTitle.setText(page.title());
            if(Commons.isEmpty(page.description()))
                wikiDescription.setVisibility(View.GONE);
            else
                wikiDescription.setText(page.description());
            wikiExtract.setText(page.extract());

            if(C.HAS_L) wikiExtract.setNestedScrollingEnabled(true);

            Glide.with(App.get().getApplicationContext())
                    .load(page.thumbUrl())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.ic_times_red)
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
        Toast.makeText(getActivity(), "Image Touched", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.wikiToolbar)
    void onToolBar() {
        Toast.makeText(getActivity(), "Toolbar Touched", Toast.LENGTH_SHORT).show();
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
                case R.id.menu_link_preview_open_in_new_tab:
                    dismiss();
                    return true;
                case R.id.menu_link_preview_add_to_list:
                    if (callback != null) {
                        callback.onLinkPreviewAddToList();
                    }
                    return true;
                case R.id.menu_link_preview_share_page:
                    if (callback != null) {
                        callback.onLinkPreviewShareLink();
                    }
                    return true;
                case R.id.menu_link_preview_copy_link:
                    if (callback != null) {
                        callback.onLinkPreviewCopyLink();
                    }
                    dismiss();
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



    @Override
    public void onStart() {
        super.onStart();
        this.behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
