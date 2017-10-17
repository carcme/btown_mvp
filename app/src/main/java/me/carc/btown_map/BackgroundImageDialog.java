package me.carc.btown_map;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
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
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;

/**
 * Show a rotating compass dialog page
 * Created by bamptonm on 6/3/17.
 */

public class BackgroundImageDialog extends DialogFragment {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "BackgroundImageDialog";

    @BindView(R.id.backgrounds)
    RecyclerView mBackgrounds;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_picker_dialog, container, false);
        ButterKnife.bind(this, view);

        if(BackgroundChooserAdapter.BACKGROUNGS.length != BackgroundChooserAdapter.DESCRIPTION_IDS.length)
            throw new RuntimeException("Check array lenghts");
        if(BackgroundChooserAdapter.BACKGROUNGS.length != BackgroundChooserAdapter.DESCRIPTION_NAMES.length)
            throw new RuntimeException("Check array lenghts");

        mBackgrounds.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBackgrounds.setAdapter(new BackgroundChooserAdapter());
        mBackgrounds.setHasFixedSize(true);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }


    private static class BackgroundChooserAdapter extends RecyclerView.Adapter<BackgroundImageHolder> {
        private static final int[] BACKGROUNGS = new int[]{
//                R.drawable.background_btown,
                R.drawable.background_jewish_memorial,
                R.drawable.background_brandenburg_tor,
                R.drawable.background_transportr,
                R.drawable.background_blue,
                R.drawable.background_orange_fractal,
                R.drawable.background_red_swirl
        };

        private static final int[] DESCRIPTION_NAMES = new int[]{
//                R.string.app_name,
                R.string.name_background_jewish,
                R.string.name_background_brandenburg_tor,
                R.string.name_background_transportr,
                R.string.no_name_background,
                R.string.no_name_background,
                R.string.no_name_background,
        };

        private static final int[] DESCRIPTION_IDS = new int[]{
//                R.string.desc_background_btown,
                R.string.desc_background_jewish,
                R.string.desc_background_brandenburg_tor,
                R.string.desc_background_transportr,
                R.string.no_name_background,
                R.string.no_name_background,
                R.string.no_name_background
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
            mImage = (ImageView) itemView.findViewById(R.id.backgroundChooserImage);
            mTitle = (TextView) itemView.findViewById(R.id.backgroundChooserTitle);
            mDescr = (TextView) itemView.findViewById(R.id.backgroundChooserDesc);
        }

        private void bind(int position, @DrawableRes int drawable, @StringRes int name, @StringRes int desc) {
            mImage.setImageResource(drawable);
            index = position;

            if(name == R.string.no_name_background)
                mTitle.setVisibility(View.GONE);
            else
                mTitle.setText(name);

            if(desc == R.string.no_name_background)
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
                            .setBackgroundImage(BackgroundChooserAdapter.BACKGROUNGS[index]);

                    BackgroundImageDialog fragment = (BackgroundImageDialog) currActivity.getSupportFragmentManager()
                            .findFragmentByTag(BackgroundImageDialog.ID_TAG);
                    fragment.dismiss();
                }
            }
        }
    }

    private static MapActivity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof MapActivity)
            return (MapActivity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return null;
    }
}