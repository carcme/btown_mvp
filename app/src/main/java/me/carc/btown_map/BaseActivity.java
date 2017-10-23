package me.carc.btown_map;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import me.carc.btown_map.common.C;
import me.carc.btown_map.common.CacheDir;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.common.TinyDB;
import me.carc.btown_map.tours.CatalogueActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private static final String SAVED_HEADER_BACKGROUND = "SAVED_HEADER_BACKGROUND";

    private AlertDialog calibrateDialog;

    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @VisibleForTesting
    private static ProgressDialog mProgressDialog;

    protected TinyDB db;
    private CacheDir cacheDir;

    protected CallbackManager callbackManager;


    /**
     * Initialize the facebook sdk.
     * And then callback manager will handle the login responses.
     */
    protected CallbackManager facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(getApplication());
        return callbackManager;
    }

    public App getApp() {
        return (App) getApplication();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseFunctions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApp().setCurrentActivity(this);
        facebookSDKInitialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
        clearReferences();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(Commons.isNotNull(mDrawerToggle))
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
        hideProgressDialog();
    }

    private void clearReferences() {
        AppCompatActivity currActivity = getApp().getCurrentActivity();
        if (this.equals(currActivity))
            getApp().setCurrentActivity(null);
    }

    public void getBaseFunctions() {
        db = TinyDB.getTinyDB();
        if (db == null)
            db = new TinyDB(this);

        cacheDir = CacheDir.getCacheDir();
        if (cacheDir == null)
            cacheDir = new CacheDir(this);

        calculateImageHeight();

      if (defaultFont == null) getFonts();
    }


    private static Typeface defaultFont;
    private static Typeface fancyFont;
    private static Typeface blockFont;

    public static Typeface getDefaultFont(Context appContext) {
        if (defaultFont == null)
            defaultFont = Typeface.createFromAsset(appContext.getAssets(), "fonts/Roboto-Light.ttf");
        return defaultFont;
    }


    static Typeface getFancyFont(Context appContext) {
        if (fancyFont == null)
            fancyFont = Typeface.createFromAsset(appContext.getAssets(), "fonts/Satisfy-Regular.ttf");
        return fancyFont;
    }


    public static Typeface getBlockFont(Context appContext) {
        if (blockFont == null)
            blockFont = Typeface.createFromAsset(appContext.getAssets(), "fonts/diplomatasc-regular.ttf");
        return blockFont;
    }

    private void getFonts() {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                defaultFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Light.ttf");
                fancyFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Satisfy-Regular.ttf");
                blockFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/diplomatasc-regular.ttf");
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

    public void showProgressDialog(@Nullable String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void showProgressDialog() {
        showProgressDialog(getString(R.string.loading));
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


    /* SIDE BAR */

    public @DrawableRes int getBackground() {
        int bg = R.drawable.background_jewish_memorial;
        try {
            bg = TinyDB.getTinyDB().getInt(SAVED_HEADER_BACKGROUND, bg);
        } catch (NullPointerException e) {
            Log.d(TAG, "getBackground: " + e.getLocalizedMessage());
        }
        return bg;
    }

    public void setBackgroundImage(@DrawableRes int imageRes) {
        ImageView header = (ImageView) findViewById(R.id.nav_header_background_image);
        header.setImageResource(imageRes);
        db.putInt(SAVED_HEADER_BACKGROUND, imageRes);
    }

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mDrawerLayout == null) {
            return;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        configureNavView();
    }

    private void configureNavView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(navigationViewListener);

        View headerLayout = navigationView.getHeaderView(0);
        TextView mHeaderCity = (TextView) headerLayout.findViewById(R.id.nav_header_city);
        mHeaderCity.setText("TODO");

        ImageView header = (ImageView) headerLayout.findViewById(R.id.nav_header_background_image);
        header.setImageResource(getBackground());
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundImageDialog.showInstance(getApplicationContext());
            }
        });
    }

    private NavigationView.OnNavigationItemSelectedListener navigationViewListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.nav_feedback:
                    Intent sendMessage = new Intent(Intent.ACTION_SEND);
                    sendMessage.setType("message/rfc822");
                    sendMessage.putExtra(Intent.EXTRA_EMAIL, new String[]{ getResources().getString(R.string.feedback_email)});
                    try {
                        startActivity(Intent.createChooser(sendMessage, "Send feedback"));
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(BaseActivity.this, "Email app not found", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.nav_debug_clear_json:
                    db.remove(CatalogueActivity.JSON_VERSION);
                    db.remove(CatalogueActivity.SERVER_FILE);
                    break;

            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDraw();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDraw() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}