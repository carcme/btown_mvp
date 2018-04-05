package me.carc.btown.map.sheets;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.ui.TouchImageView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Show a rotating compass dialog page
 * Created by bamptonm on 6/3/17.
 */

@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
public class ImageDialog extends DialogFragment {

    public static final String ID_TAG = "ImageDialog";
    public static final int ACTIVITY_CROP = 101;
    public static final int ACTIVITY_SHARE = 102;

    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String PAGE_URL = "PAGE_URL";
    private static final String TITLE = "TITLE";
    private static final String SUBTITLE = "SUBTITLE";

    private static final String SAVED_SCALE_TYPE = "SAVED_SCALE_TYPE";

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

            if (Commons.isNotNull(imageUrl)) bundle.putString(IMAGE_URL, imageUrl);
            if (Commons.isNotNull(pageUrl))  bundle.putString(PAGE_URL, pageUrl);
            if (Commons.isNotNull(title))    bundle.putString(TITLE, title);
            if (Commons.isNotNull(subTitle)) bundle.putString(SUBTITLE, subTitle);

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
        if (args != null) {
            String imageUrl = args.getString(IMAGE_URL);
            imageTitle.setText(args.getString(TITLE));
            imageSubTitle.setText(args.getString(SUBTITLE));
            if (Commons.isEmpty(args.getString(SUBTITLE)))
                imageSubTitle.setVisibility(View.GONE);

            Glide.with(getActivity())
                    .load(imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            retryNetworkImage();
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (Commons.isNotNull(imageLoadProgress))
                                imageLoadProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade(500)
                    .placeholder(R.drawable.checkered_background)  // required otherwise load doesn't happen!!
                    .error(R.drawable.no_image)
                    .into(image);

            int scale = TinyDB.getTinyDB().getInt(SAVED_SCALE_TYPE, ImageView.ScaleType.CENTER_CROP.ordinal());
            switch (scale) {
                case 3:
                    image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;

                case 6:
                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                default:
            }
        } else
            dismiss();

        return view;
    }

    private void retryNetworkImage() {
        String loadString = getArguments().getString(IMAGE_URL);
        // Crashlytics  #138 - check user hasn't got bored and left
        if(getActivity() != null)
            Glide.with(getActivity())
                    .load(loadString)
                    .listener(glideListener)
                    .crossFade(500)
                    .into(image);
    }

    private RequestListener<String, GlideDrawable> glideListener = new RequestListener<String, GlideDrawable>() {
        int retryCount = 2;

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            // Attempt a retry
            if (retryCount != 0) {
                retryNetworkImage();
                retryCount--;
                return true;
            }

            // make sure user hasn't exited while waiting for the image to load!!
            if (Commons.isNotNull(image)) {
                image.setImageResource(R.drawable.no_image);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            if (Commons.isNotNull(imageLoadProgress))
                imageLoadProgress.setVisibility(View.GONE);

            // show load error notification
            if (getActivity() != null) {
                Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
            }
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            image.setImageDrawable(resource);
            if (Commons.isNotNull(imageLoadProgress))
                imageLoadProgress.setVisibility(View.GONE);
            return false;
        }
    };

    @OnClick(R.id.textContainer)
    void onTextContainerClick() {
        dismiss();
    }

    @OnClick(R.id.imageDialogImage)
    void singleImageTouch() {
        if (image.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        TinyDB.getTinyDB().putInt(SAVED_SCALE_TYPE, image.getScaleType().ordinal());
    }

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
        if (helpContainer.getVisibility() == View.GONE)
            helpContainer.setVisibility(View.VISIBLE);
        else
            helpContainer.setVisibility(View.GONE);
    }

    @SuppressFBWarnings("REC_CATCH_EXCEPTION")  // using catch Exception which is a catch all (FindBugs doesn't like this)
    private void downloadAndSetOrShareImage(final boolean set) {
        String imageUrl = getArguments().getString(IMAGE_URL);

        if (Commons.isNotNull(imageUrl)) {
            imageLoadProgress.setVisibility(View.VISIBLE);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showDownloadError();
                }

                @SuppressFBWarnings("REC_CATCH_EXCEPTION")
                @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if (response.body().byteStream() != null) {

                        try {
                            //create a temporary directory within the cache folder
                            File dir = new File(getActivity().getCacheDir() + "/images");
                            if (!dir.exists()) dir.mkdirs();

                            //create the file
                            String filename = "btownTemp.jpg";
                            File file = new File(dir, filename);
                            if (!file.exists()) file.createNewFile();

                            FileUtils.copyInputStreamToFile(response.body().byteStream(), file);

                            //get the contentUri for this file and start the intent
                            Uri contentUri = FileProvider.getUriForFile(getActivity(), getString(R.string.file_provider), file);

                            if (set) {
                                try {
                                    Intent intent = WallpaperManager.getInstance(getActivity()).getCropAndSetWallpaperIntent(contentUri);
                                    //startActivityForResult to stop the progress bar
                                    startActivityForResult(intent, ACTIVITY_CROP);
                                } catch (IllegalArgumentException e) {
                                    // Seems to be an Oreo bug - fall back to using the bitmap instead
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
                                    WallpaperManager.getInstance(getActivity()).setBitmap(bitmap);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageLoadProgress.setVisibility(View.GONE);
                                            Commons.Toast(getActivity(), R.string.shared_string_success, Color.GREEN, Toast.LENGTH_SHORT);
                                        }
                                    });
                                }

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
                            showDownloadError();
                        }
                    }
                }
            });
        }
    }

    private void showDownloadError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Commons.isNotNull(imageLoadProgress))
                    imageLoadProgress.setVisibility(View.GONE);
                Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
            }
        });
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CROP || requestCode == ACTIVITY_SHARE) {
            imageLoadProgress.setVisibility(View.GONE);
            Commons.Toast(getActivity(), R.string.shared_string_success, Color.GREEN, Toast.LENGTH_SHORT);
        }
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