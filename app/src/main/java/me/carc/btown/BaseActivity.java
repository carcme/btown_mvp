package me.carc.btown;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.extras.BackgroundImageDialog;
import me.carc.btown.extras.PublicTransportPlan;
import me.carc.btown.login.LoginActivity;
import me.carc.btown.settings.Preferences;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.settings.SettingsActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private static final String SAVED_HEADER_BACKGROUND = "SAVED_HEADER_BACKGROUND";
    public static final String FIREBASE_MSG_BOARD_FEEDBACK = "FEEDBACK";

    private static final int RESULT_SETTINGS = 71;
    private static final int RESULT_LOGIN = 72;
    private static final int RESULT_DONATE = 73;

    public static final String TRANSPORTR_INTENT = "de.grobox.liberario";
    public static final String TRANSLATE_INTENT  = "com.google.android.apps.translate";


    private static IInAppBillingService mBillingService;
    private static List<PurchaseItem> mPurchaseItems;
    private String[] mDonateItems;

    private GeoPoint currentLocation;

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
        getBaseFunctions();
        useLanguage(this);
        mDonateItems = getResources().getStringArray(R.array.donate_items);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApp().setCurrentActivity(this);
        facebookSDKInitialize();

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
        clearReferences();

        if (mBillingService != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Commons.isNotNull(mDrawerToggle))
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void restart() {
        try {
            if (mBillingService != null) {
                unbindService(mServiceConnection);
            }
        } catch (IllegalArgumentException e) { /* EMPTY */ }

        finish();
        startActivity(new Intent(BaseActivity.this, SplashActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("lang")) {
                        restart();
                    }
                }
                break;

            case RESULT_LOGIN:
                configureNavView();
                break;

            case RESULT_DONATE:
                // comsume the newly purchased item
                consumeAllIAB();

                if (resultCode == RESULT_OK) {
                    new android.app.AlertDialog.Builder(this)
                            .setMessage(getString(R.string.text_donate_thanks))
                            .setPositiveButton(R.string.text_close, null)
                            .show();
                } else {
                    showDonateDialogErrorCallback(null);
                }
                break;
        }
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
    }


    protected GeoPoint getCurrentLocation(GeoPoint point) {
        return currentLocation;
    }

    protected void setCurrentLocation(GeoPoint point) {
        currentLocation = point;
    }


    protected static boolean isGermanLanguage() {
        return C.USER_LANGUAGE.equals("de");
    }

    protected static void useLanguage(Context context) {
        String lang = Preferences.getLanguage(context);
        if (!lang.equals(context.getString(R.string.pref_language_value_default))) {
            Locale locale;
            if (lang.contains("_")) {
                String[] lang_array = lang.split("_");
                locale = new Locale(lang_array[0], lang_array[1]);
            } else {
                locale = new Locale(lang);
            }
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            C.USER_LANGUAGE = config.locale.getLanguage();

        } else {
            // use default language
            context.getResources().updateConfiguration(Resources.getSystem().getConfiguration(), context.getResources().getDisplayMetrics());
            C.USER_LANGUAGE = Resources.getSystem().getConfiguration().locale.getLanguage();
        }
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


    protected void removeToolBarFlags(Toolbar toolbar){
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
    }


    protected void scrollHider(RecyclerView rv, final FloatingActionButton fab){
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0)
                    fab.hide();
                else
                    fab.show();
            }
        });
    }


    /**
     * Prompt to install from Playstore
     *
     * @param title            The dialog title
     * @param content          The dialog text
     * @param resId            The dialog icon
     * @param packageToInstall The id of app on Playstore
     */
    public void installExtApp(final String title, final String content, int resId,  final String packageToInstall) {
        new AlertDialog.Builder(BaseActivity.this)
                .setTitle(title)
                .setIcon(resId)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // download from android market
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(IntentUtils.getUrlWithRef(packageToInstall)));
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

    public void showAlertDialog(@StringRes int title, @StringRes int message, @StringRes int btnText) {
        showAlertDialog(title, message, btnText, -1);
    }

    public void showAlertDialog(@StringRes int title, @StringRes int message, @StringRes int btnText, @DrawableRes int icon) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(title);
        dlg.setMessage(message);

        if (btnText != -1)
            dlg.setPositiveButton(btnText, null);

        if (icon != -1)
            dlg.setIcon(icon);

        dlg.show();
    }

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
        showProgressDialog(getString(R.string.shared_string_loading));
    }

    public void showProgressDialog(CharSequence title, CharSequence message) {
        if (isFinishing())
            return;
        hideProgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }


    /* SIDE BAR */


    public
    @DrawableRes
    int getBackground() {
        return TinyDB.getTinyDB().getInt(SAVED_HEADER_BACKGROUND, R.drawable.background_jewish_memorial);
    }

    public void setBackgroundImage(@DrawableRes int imageRes) {
        ImageView header = findViewById(R.id.nav_header_background_image);
        header.setImageResource(imageRes);
        db.putInt(SAVED_HEADER_BACKGROUND, imageRes);
    }

    private void setupNavDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);

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
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(navigationViewListener);

        if (!Preferences.showTours(this)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_drawer_no_tours);
        }

        View headerLayout = navigationView.getHeaderView(0);

        ImageView header = headerLayout.findViewById(R.id.nav_header_background_image);
        header.setImageResource(getBackground());
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundImageDialog.showInstance(getApplicationContext());
            }
        });

        TextView headeUser = headerLayout.findViewById(R.id.nav_header_email);

        ImageView userImage = headerLayout.findViewById(R.id.nav_header_user_image);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BaseActivity.this, LoginActivity.class), RESULT_LOGIN);
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Profile facebookUser = Profile.getCurrentProfile();
        if (Commons.isNotNull(firebaseUser) || Commons.isNotNull(facebookUser)) {
            headeUser.setText(facebookUser != null ? facebookUser.getName() : firebaseUser.getDisplayName());
            Glide.with(this)
                    .load(facebookUser != null ? facebookUser.getProfilePictureUri(50, 50) : firebaseUser.getPhotoUrl())
                    .into(userImage);
        } else {
            userImage.setImageResource(R.mipmap.ic_launcher_rnd);
            headeUser.setText(R.string.app_name);
        }
    }


    private NavigationView.OnNavigationItemSelectedListener navigationViewListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_transportr:
                    transportr();
                    break;

                case R.id.nav_transport_maps:
                    transportPlans();
                    break;

                case R.id.nav_translate:
                    translate();
                    break;

                case R.id.nav_settings:
                    settings();
                    break;

                case R.id.nav_donate:
                    showDonateDialog();
                    break;

            }

            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };


    public void transportr() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(TRANSPORTR_INTENT);
        if (intent != null) {
            startActivity(intent);
        } else {
            if (Commons.isNetworkAvailable(this))
                installExtApp(getString(R.string.install_transportr), getString(R.string.no_transportr), R.mipmap.ic_transportr, TRANSPORTR_INTENT);
            else
                showAlertDialog(R.string.shared_string_error, R.string.network_not_available_error, -1);
        }
    }

    public void translate() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(TRANSLATE_INTENT);
        if (intent != null) {
            startActivity(intent);
        } else {
            if (Commons.isNetworkAvailable(this))
                installExtApp(getString(R.string.install_translate), getString(R.string.no_translate), R.drawable.ic_translate, TRANSLATE_INTENT);
            else
                showAlertDialog(R.string.shared_string_error, R.string.network_not_available_error, -1);
        }
    }

    public void transportPlans() {
        startActivity(new Intent(BaseActivity.this, PublicTransportPlan.class));
    }

    public void settings() {
        startActivityForResult(new Intent(BaseActivity.this, SettingsActivity.class), RESULT_SETTINGS);
    }

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



    /* PURCHASE STUFF */


    protected void showDonateDialog() {

        if (mPurchaseItems == null)
            showProgressDialog(getString(R.string.text_donate_progress_title), getString(R.string.text_donate_progress_message));

        requestItemsForPurchase(new AsyncCallback<List<PurchaseItem>>(this) {
            @Override
            public void onError(Throwable throwable) {
                if (getContext() instanceof BaseActivity && !((BaseActivity) getContext()).isFinishing()) {
                    BaseActivity context = (BaseActivity) getContext();
                    hideProgressDialog();
                    context.showDonateDialogErrorCallback(throwable);
                }
            }

            @Override
            public void onSuccess(List<PurchaseItem> data) {
                if (getContext() instanceof BaseActivity && !((BaseActivity) getContext()).isFinishing()) {
                    BaseActivity context = (BaseActivity) getContext();
                    hideProgressDialog();
                    context.showDonateDialogSuccessCallback(data);
                }
            }
        });
    }

    private static ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBillingService = null;
        }
    };


    private void onDonate(PurchaseItem item) {
        if (item == null || !item.isValid() || mBillingService == null) {
            return;
        }
        try {
            Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(), item.getId(), "inapp", "");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null)
                startIntentSenderForResult(pendingIntent.getIntentSender(), RESULT_DONATE, new Intent(), 0, 0, 0);
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void requestItemsForPurchase(final AsyncCallback<List<PurchaseItem>> callback) {
        if (mPurchaseItems != null && mPurchaseItems.size() > 0) {
            if (callback != null) {
                callback.onSuccess(mPurchaseItems);
            }
            return;
        }
        new AsyncTask<IInAppBillingService, Void, AsyncResult<List<PurchaseItem>>>() {

            @Override
            protected AsyncResult<List<PurchaseItem>> doInBackground(IInAppBillingService... params) {
                List<PurchaseItem> result = new ArrayList<>();
                Throwable exception = null;
                IInAppBillingService billingService = params[0];

                if (billingService == null) {
                    exception = new Exception("Unknown");
                } else {
                    ArrayList<String> skuList = new ArrayList<>(Arrays.asList(mDonateItems));
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    try {
                        Bundle skuDetails = billingService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                        int response = skuDetails.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                            PurchaseItem purchaseItem;
                            for (String item : responseList) {
                                purchaseItem = new PurchaseItem(new JSONObject(item));
                                if (purchaseItem.isValid()) {
                                    result.add(purchaseItem);
                                }
                            }
                        }
                    } catch (RemoteException | JSONException e) {
                        e.printStackTrace();
                        exception = e;
                    }
                }
                return new AsyncResult<>(result, exception);
            }

            @Override
            protected void onPostExecute(AsyncResult<List<PurchaseItem>> result) {
                if (!isFinishing() && callback != null) {
                    Throwable error = result.getError();
                    if (error == null && (result.getResult() == null || result.getResult().size() == 0)) {
                        error = new Exception("Unknow");
                    }
                    if (error != null) {
                        callback.onError(error);
                    } else {
                        mPurchaseItems = result.getResult();
                        Collections.sort(mPurchaseItems, new Comparator<PurchaseItem>() {
                            @Override
                            public int compare(PurchaseItem lhs, PurchaseItem rhs) {
                                return (int) ((lhs.getPriceAmountMicros() - rhs.getPriceAmountMicros()) / 1000);
                            }
                        });
                        callback.onSuccess(mPurchaseItems);
                    }
                }
            }
        }.execute(mBillingService);
    }

    public void showDonateDialogErrorCallback(Throwable throwable) {
        showAlertDialog(R.string.shared_string_error, R.string.text_donate_error, -1);
    }

    private void showDonateDialogSuccessCallback(final List<PurchaseItem> data) {

        PurchaseAdapter adapter = new PurchaseAdapter(this, data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.text_donate_title)
                .setCancelable(false)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDonate(data.get(which));
                    }
                })
                .setPositiveButton(R.string.text_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(BaseActivity.this)
                                .setMessage(getString(R.string.text_donate_cancel))
                                .setPositiveButton(R.string.shared_string_rate, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new SendFeedback(BaseActivity.this, SendFeedback.TYPE_RATE);
                                    }
                                })
                                .setNegativeButton(R.string.text_close, null)
                                .show();
                    }
                });
        builder.show();
    }


    public void consumeAllIAB() {

        new AsyncTask<IInAppBillingService, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(IInAppBillingService... params) {
                IInAppBillingService billingService = params[0];

                if (billingService != null) {
                    try {
                        Bundle ownedItems = billingService.getPurchases(3, getPackageName(), "inapp", null);
                        int responseCode = ownedItems.getInt("RESPONSE_CODE");
                        if (responseCode == 0) {

                            ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                            if (Commons.isNotNull(purchaseDataList)) {
                                for (String purchaseData : purchaseDataList) {
                                    JSONObject o = new JSONObject(purchaseData);
                                    String purchaseToken = o.optString("token", o.optString("purchaseToken"));

                                    responseCode = billingService.consumePurchase(3, getPackageName(), purchaseToken);
                                    Log.d(TAG, "Consume IAB: " + responseCode);
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean res) {
            }
        }.execute(mBillingService);
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


    public static class PurchaseAdapter extends ArrayAdapter<PurchaseItem> {

        @BindView(R.id.description)
        TextView vDescription;

        @BindView(R.id.title)
        TextView vTitle;

        private List<PurchaseItem> mItems;

        public PurchaseAdapter(Context context, List<PurchaseItem> items) {
            super(context, 0);
            mItems = items == null ? new ArrayList<PurchaseItem>() : items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public PurchaseItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public
        @NonNull
        View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.donate_item, parent, false);
            }
            ButterKnife.bind(this, convertView);
            vTitle.setText(getItem(position).getDescription());
            vDescription.setText(getItem(position).getPrice());
            return convertView;
        }
    }

    public static class PurchaseItem {

        private String mDescription;

        private String mId;

        private String mPrice;

        private long mPriceAmountMicros;

        private String mPriceCurrencyCode;

        private String mTitle;

        private String mType;

        public PurchaseItem(JSONObject item) {
            try {
                if (item != null) {
                    mId = item.getString("productId");
                    mType = item.getString("type");
                    mTitle = item.getString("title");
                    mDescription = item.getString("description");
                    mPrice = item.getString("price");
                    mPriceAmountMicros = item.getLong("price_amount_micros");
                    mPriceCurrencyCode = item.getString("price_currency_code");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getDescription() {
            return mDescription;
        }

        public String getId() {
            return mId;
        }

        public String getPrice() {
            return mPrice;
        }

        public long getPriceAmountMicros() {
            return mPriceAmountMicros;
        }

        public String getPriceCurrencyCode() {
            return mPriceCurrencyCode;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getType() {
            return mType;
        }

        public boolean isValid() {
            return !TextUtils.isEmpty(mId);
        }
    }
}