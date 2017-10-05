package me.carc.btownmvp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.CacheDir;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.common.TinyDB;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private static final String SAVED_HEADER_BACKGROUND = "SAVED_HEADER_BACKGROUND";

    private AlertDialog calibrateDialog;


    @VisibleForTesting
    private static ProgressDialog mProgressDialog;


    protected TinyDB db;
    private CacheDir cacheDir;


    public App getApp() {
        return (App) getApplication();
    }



    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        getBaseFunctions();
    }

    public void getBaseFunctions() {
        db = TinyDB.getTinyDB();
        if (db == null)
            db = new TinyDB(this);

        cacheDir = CacheDir.getCacheDir();
        if (cacheDir == null)
            cacheDir = new CacheDir(this);

        calculateImageHeight();

//        if (defaultFont == null) getFonts();
    }


    private static Typeface defaultFont;
    private static Typeface fancyFont;
    private static Typeface blockFont;

    public static Typeface getDefaultFont() {
        if (defaultFont == null)
            defaultFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/Roboto-Light.ttf");
        return defaultFont;
    }


    static Typeface getFancyFont() {
        if (fancyFont == null)
            fancyFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/Satisfy-Regular.ttf");
        return fancyFont;
    }


    public static Typeface getBlockFont() {
        if (blockFont == null)
            blockFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/diplomatasc-regular.ttf");
        return blockFont;
    }

    private void getFonts() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                defaultFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/Roboto-Light.ttf");
                fancyFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/Satisfy-Regular.ttf");
                blockFont = Typeface.createFromAsset(App.get().getAssets(), "fonts/diplomatasc-regular.ttf");
                return true;
            }

            @Override
            protected void onPostExecute(Boolean res) {
            }
        }.execute();
    }


    /**
     * Display image on 2/3 of screen
     */
    protected void calculateImageHeight() {
        if (C.IMAGE_WIDTH == null || C.IMAGE_HEIGHT == null) {
            final DisplayMetrics metrics = new DisplayMetrics();
            Display display = getWindowManager().getDefaultDisplay();

            display.getRealMetrics(metrics);

            // SCREEN DIMENSIONS
            C.SCREEN_WIDTH = metrics.widthPixels;
            C.SCREEN_HEIGHT = metrics.heightPixels;

            // IMAGE DIMENSIONS
            C.IMAGE_WIDTH = C.SCREEN_WIDTH;
            if (C.SCREEN_HEIGHT > 1200)
                C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 3 / 4.0f);
            else
                C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 2 / 3.0f);
        }
    }

    /**
     * Set the background color of the status bar
     *
     * @param color status bar background color
     */
    protected void setStatusBarColor(boolean palette, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        final View decorView = getWindow().getDecorView();
        View view = new View(this);
        final int statusBarHeight = getStatusBarHeight();
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        if (palette)
            view.setBackgroundColor(color);
        else
            view.setBackgroundColor(ContextCompat.getColor(this, color));
        ((ViewGroup) decorView).addView(view);
    }

    /**
     * find height of the status bar
     *
     * @return the height
     */
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTranslucentStatusBarLollipop(Window window) {
        setStatusBarColor(false, android.R.color.transparent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    /**
     * Prompt to install from Playstore
     *
     * @param title   The dialog title
     * @param content The dialog text
     * @param id      The id of app on Playstore
     */
    public void installExtApp(final String title, final String content, final String id) {
        new AlertDialog.Builder(BaseActivity.this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // download from android market
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(C.INTENT_MARKET + id));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


/*
    public void showCompassCalibateDialog(int accuracy) {

        if (calibrateDialog != null)
            calibrateDialog.cancel();

        if (accuracy < 3 && db.getBoolean(C.PREF_KEY_ARROW_ANIMATE, true)) {

            AlertDialog.Builder calibrateBuilder = new AlertDialog.Builder(this);
            calibrateBuilder.setTitle(getString(R.string.calibrate_compass));
            calibrateBuilder.setMessage(R.string.compass_calibate_message);

            final ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.compass_calibration);

            calibrateBuilder.setView(image);
            calibrateBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlg, int which) {
                    dlg.dismiss();
                }
            });

            calibrateBuilder.setNeutralButton(getString(R.string.never_ask), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlg, int which) {
                    db.putBoolean(C.PREFKEY_NEVER_ASK_COMPASS, true);
                    dlg.dismiss();
                }
            });

            calibrateDialog = calibrateBuilder.show();
        }
    }
*/

    public static void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showProgressDialog(CharSequence title, CharSequence message) {
        if (isFinishing()) {
            return;
        }
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public static abstract class AsyncCallback<T> {

        public abstract void onError(Throwable throwable);

        public abstract void onSuccess(T data);

        private WeakReference<Context> mContextWeakReference;

        public AsyncCallback(Context context) {
            mContextWeakReference = new WeakReference<>(context);
        }

        public Context getContext() {
            return mContextWeakReference.get();
        }
    }

    public static class AsyncResult<T> {

        private Throwable mError;

        private T mResult;

        public AsyncResult(T result, Throwable error) {
            mResult = result;
            mError = error;
        }

        public Throwable getError() {
            return mError;
        }

        public T getResult() {
            return mResult;
        }
    }

}