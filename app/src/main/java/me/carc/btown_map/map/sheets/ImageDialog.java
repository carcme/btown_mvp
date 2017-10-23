package me.carc.btown_map.map.sheets;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btown_map.App;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.FileUtils;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.ui.TouchImageView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Show a rotating compass dialog page
 * Created by bamptonm on 6/3/17.
 */

public class ImageDialog extends DialogFragment {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "ImageDialog";
    public static final int ACTIVITY_CROP  = 101;
    public static final int ACTIVITY_SHARE = 102;

    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String PAGE_URL = "PAGE_URL";
    private static final String TITLE = "TITLE";
    private static final String SUBTITLE = "SUBTITLE";

    private Unbinder unbinder;

    @BindView(R.id.imageDialogImage)
    TouchImageView image;

    @BindView(R.id.imageLoadProgress)
    ProgressBar imageLoadProgress;

    @BindView(R.id.imageTitle)
    TextView imageTitle;

    @BindView(R.id.imageSubTitle)
    TextView imageSubTitle;

    @BindView(R.id.imageMoreBtn)
    ImageButton imageMoreBtn;

    @BindView(R.id.helpContainer)
    LinearLayout helpContainer;


    public static boolean showInstance(final Context appContext, final String imageUrl, final String pageUrl, final String title, final String subTitle) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if(Commons.isNotNull(imageUrl)) bundle.putString(IMAGE_URL, imageUrl);
            if(Commons.isNotNull(pageUrl))  bundle.putString(PAGE_URL, pageUrl);
            if(Commons.isNotNull(title))    bundle.putString(TITLE, title);
            if(Commons.isNotNull(subTitle)) bundle.putString(SUBTITLE, subTitle);

            ImageDialog fragment = new ImageDialog();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_dialog_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if(args != null) {

            String imageUrl = args.getString(IMAGE_URL);
            imageTitle.setText(args.getString(TITLE));
            imageSubTitle.setText(args.getString(SUBTITLE));
            if(Commons.isEmpty(args.getString(SUBTITLE)))
                imageSubTitle.setVisibility(View.GONE);

            Glide.with(getActivity())
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(R.drawable.background_glide_placeholder)  // required otherwise load doesn't happen!!
                    .error(R.drawable.no_image)
                    .into(new BitmapImageViewTarget(image) {
                        @Override
                        public void onResourceReady(final Bitmap bitmap, @Nullable GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);

                            // make sure user hasn't exited while waiting for the image to load!!
                            if(Commons.isNotNull(imageLoadProgress))
                                imageLoadProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);

                            // make sure user hasn't exited while waiting for the image to load!!
                            if(Commons.isNotNull(image))
                                image.setScaleType(ImageView.ScaleType.CENTER);
                            if(Commons.isNotNull(imageLoadProgress))
                                imageLoadProgress.setVisibility(View.GONE);
                        }
                    });
        } else
            dismiss();

        return view;
    }

    @OnClick(R.id.textContainer)
    void onTextContainerClick() {
        dismiss();
    }

    @OnClick(R.id.imageDialogImage)
    void singleImageTouch() {
        if(image.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }


    // TODO: 09/10/2017 - this is copied from SearchDialogFragment, needs updating for ImageDialog
    @OnClick(R.id.imageMoreBtn)
    void onSettingOverflow() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), imageMoreBtn);
        popupMenu.inflate(R.menu.menu_image);
        popupMenu.setOnMenuItemClickListener(menuListener);
        popupMenu.show();
    }

    private PopupMenu.OnMenuItemClickListener menuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_image_homescreen:
                    downloadAndSetOrShareImage(true);
                    return true;

                case R.id.menu_image_share:
                    downloadAndSetOrShareImage(false);
                    return true;

                case R.id.menu_image_help:
                    showHideHelp();
                    return true;

                default:
                    return false;
            }
        }
    };

    @OnClick(R.id.helpContainer)
    void showHideHelp() {
        if(helpContainer.getVisibility() == View.GONE)
            helpContainer.setVisibility(View.VISIBLE);
        else
            helpContainer.setVisibility(View.GONE);
    }

    private void downloadAndSetOrShareImage(final boolean set) {
        final String imageUrl = getArguments().getString(IMAGE_URL);

        if (Commons.isNotNull(imageUrl)) {

            imageLoadProgress.setVisibility(View.VISIBLE);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    imageLoadProgress.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Could not download the image", Toast.LENGTH_SHORT).show();
                }

                @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if(response.body().byteStream() != null) {

                        try {
                            //create a temporary directory within the cache folder
                            File dir = new File(getActivity().getCacheDir() + "/images");
                            if (!dir.exists()) dir.mkdirs();

                            //create the file
                            File file = new File(dir, "btownTemp.jpg");
                            if (!file.exists()) file.createNewFile();

                            FileUtils.copyInputStreamToFile(response.body().byteStream(), file);

                            //get the contentUri for this file and start the intent
                            Uri contentUri = FileProvider.getUriForFile(getActivity(), "me.carc.btownmvp.fileprovider", file);

                            if (set) {
                                //Home screen it
                                Intent intent = WallpaperManager.getInstance(getActivity()).getCropAndSetWallpaperIntent(contentUri);
                                //startActivityForResult to stop the progress bar
                                startActivityForResult(intent, ACTIVITY_CROP);
                            } else {
                                //Share it
                                final String pageUrl = imageTitle.getText().toString() + "\n\n" + getArguments().getString(PAGE_URL);

                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setData(contentUri);
                                shareIntent.setType("image/jpg");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, pageUrl);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                //startActivityForResult to stop the progress bar
                                startActivityForResult(Intent.createChooser(shareIntent, "Share via..."), ACTIVITY_SHARE);
                            }

                        } catch (Exception ex) {
                            Log.e("BTOWN::", ex.toString());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageLoadProgress.setVisibility(View.GONE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_FilledFullscreen);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}