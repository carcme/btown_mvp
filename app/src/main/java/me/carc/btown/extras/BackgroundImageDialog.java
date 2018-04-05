package me.carc.btown.extras;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.map.MapActivity;

/**
 * Show a rotating compass dialog page
 * Created by bamptonm on 6/3/17.
 */

public class BackgroundImageDialog extends DialogFragment {

    public interface BgImageDialogListener {
        void onImageChanged();
    }

    public BgImageDialogListener dlgFinishedListener;

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "BackgroundImageDialog";
    public static final String FRONT_PAGE_IMAGE = "FRONT_PAGE_IMAGE";

    @BindView(R.id.backgrounds)
    RecyclerView mBackgrounds;

    @BindView(R.id.headerPickerFab)
    FloatingActionButton fab;


    public static final int[] BACKGROUNGS = new int[]{
            R.drawable.background_jewish_memorial,
            R.drawable.background_brandenburg_tor,
            R.drawable.brandenburger_tor,
            R.drawable.background_transportr,
            R.drawable.background_blue,

            R.drawable.background_orange_fractal,
            R.drawable.background_red_swirl,
            R.drawable.background_urban_blur_crop,
            R.drawable.background_potsdam_sanssouci,
            R.drawable.background_museum_insel
    };

    public int getBACKGROUNG(int index) {
        return BACKGROUNGS[index];
    }


    protected void scrollHider(RecyclerView rv, final FloatingActionButton fab) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    fab.hide();
                else
                    fab.show();
            }
        });
    }

    public static boolean showInstance(final Context appContext) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            BackgroundImageDialog fragment = new BackgroundImageDialog();
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            dlgFinishedListener = (BgImageDialogListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement TourListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        dlgFinishedListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_picker_dialog, container, false);
        ButterKnife.bind(this, view);

        if (BACKGROUNGS.length != BackgroundChooserAdapter.DESCRIPTION_IDS.length)
            throw new RuntimeException("Check array lenghts");
        if (BACKGROUNGS.length != BackgroundChooserAdapter.DESCRIPTION_NAMES.length)
            throw new RuntimeException("Check array lenghts");

        mBackgrounds.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBackgrounds.setAdapter(new BackgroundChooserAdapter());
        mBackgrounds.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int oldImageRes = TinyDB.getTinyDB().getInt(FRONT_PAGE_IMAGE, 0);
        for(int i = 0; i < BACKGROUNGS.length; i++){
            if (oldImageRes == BACKGROUNGS[i]) {
                final int index = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBackgrounds.smoothScrollToPosition(index);
                    }
                }, 500);
                break;
            }
        }

        scrollHider(mBackgrounds, fab);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }

    @OnClick(R.id.headerPickerFab)
    void exit() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);
        ViewUtils.hideView(fab, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, duration * 2);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dlgFinishedListener.onImageChanged();
    }

    private static class BackgroundChooserAdapter extends RecyclerView.Adapter<BackgroundImageHolder> {
        private static final int[] DESCRIPTION_NAMES = new int[]{
                R.string.name_background_jewish,
                R.string.name_background_brandenburg_tor,
                R.string.name_background_brandenburg_tor,
                R.string.name_background_transportr,
                R.string.no_name_background,

                R.string.no_name_background,
                R.string.no_name_background,
                R.string.name_background_ubahn,
                R.string.name_background_sanssouci,
                R.string.name_background_insel
        };

        private static final int[] DESCRIPTION_IDS = new int[]{
                R.string.desc_background_jewish,
                R.string.desc_background_brandenburg_tor,
                R.string.desc_background_brandenburg_tor2,
                R.string.desc_background_transportr,
                R.string.no_name_background,

                R.string.no_name_background,
                R.string.no_name_background,
                R.string.desc_background_ubahn,
                R.string.desc_background_sanssouci,
                R.string.desc_background_insel
        };

        @Override
        public BackgroundImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BackgroundImageHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.nav_header_item_container, parent, false));
        }

        @Override
        public void onBindViewHolder(BackgroundImageHolder holder, int position) {
            holder.bind(position, BACKGROUNGS[position], DESCRIPTION_NAMES[position], DESCRIPTION_IDS[position]);
        }

        @Override
        public int getItemCount() {
            return BACKGROUNGS.length;
        }
    }


    private static class BackgroundImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImage;
        private TextView mTitle;
        private TextView mDescr;
        private int index;

        public BackgroundImageHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.backgroundChooserImage);
            mTitle = itemView.findViewById(R.id.backgroundChooserTitle);
            mDescr = itemView.findViewById(R.id.backgroundChooserDesc);
        }

        private void bind(int position, @DrawableRes int drawable, @StringRes int name, @StringRes int desc) {
            mImage.setImageResource(drawable);
            index = position;

            if (name == R.string.no_name_background)
                mTitle.setVisibility(View.GONE);
            else
                mTitle.setText(name);

            if (desc == R.string.no_name_background)
                mDescr.setVisibility(View.GONE);
            else
                mDescr.setText(desc);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            AppCompatActivity currActivity = ((App) itemView.getContext()
                    .getApplicationContext()).getCurrentActivity();

            if (Commons.isNotNull(currActivity)) {
                if (currActivity instanceof MapActivity) {
                    ((MapActivity) currActivity)
                            .setBackgroundImage(BACKGROUNGS[index]);
                } else {
                    TinyDB.getTinyDB().putInt(BackgroundImageDialog.FRONT_PAGE_IMAGE, BACKGROUNGS[index]);
                }
                BackgroundImageDialog fragment = (BackgroundImageDialog) currActivity.getSupportFragmentManager()
                        .findFragmentByTag(BackgroundImageDialog.ID_TAG);
                fragment.exit();
            }
        }
    }

    private static MapActivity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof MapActivity)
            return (MapActivity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}