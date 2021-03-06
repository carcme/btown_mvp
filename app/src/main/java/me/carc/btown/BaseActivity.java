package me.carc.btown;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.extras.PublicTransportPlanExtra;
import me.carc.btown.settings.Preferences;
import me.carc.btown.settings.SendFeedback;
import me.carc.btown.settings.SettingsActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    private static final int RESULT_SETTINGS = 71;
    private static final int RESULT_LOGIN = 72;
    private static final int RESULT_DONATE = 73;
    private static final int RESULT_PLAY_SERVICES_RESOLUTION = 74;

    public static final String TRANSPORTR_INTENT = "de.grobox.liberario";
    public static final String TRANSLATE_INTENT = "com.google.android.apps.translate";

    private static List<PurchaseItem> mPurchaseItems;
    private String[] mDonateItems;

    private GeoPoint currentLocation;

    private AlertDialog calibrateDialog;

    @VisibleForTesting
    private static volatile ProgressDialog mProgressDialog;
    private static final Object lock = new Object();

    protected TinyDB db;

    public App getApp() {
        return (App) getApplication();
    }

    public IInAppBillingService getBillingService() {
        return getApp().getmBillingService();
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
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DE_MIGHT_IGNORE")
    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
        clearReferences();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        setupNavDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (Commons.isNotNull(mDrawerToggle))
//            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void restart() {
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

            case RESULT_PLAY_SERVICES_RESOLUTION:
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
        calculateImageHeight();
    }


    @SuppressWarnings("unused")
    protected GeoPoint getCurrentLocation(GeoPoint point) {
        return currentLocation;
    }

    @SuppressWarnings("unused")
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
        synchronized (lock) {
            if (C.IMAGE_WIDTH == null || C.IMAGE_HEIGHT == null) {
                final DisplayMetrics metrics = new DisplayMetrics();
                Display display = getWindowManager().getDefaultDisplay();

                display.getRealMetrics(metrics);

                // SCREEN DIMENSIONS
                C.SCREEN_WIDTH = metrics.widthPixels;
                C.SCREEN_HEIGHT = metrics.heightPixels;

                // IMAGE DIMENSIONS
                C.IMAGE_WIDTH = C.SCREEN_WIDTH;
//                if (C.SCREEN_HEIGHT > 1200)
//                    C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 3 / 4.0f);
//                else
                C.IMAGE_HEIGHT = (int) (C.SCREEN_HEIGHT * 2 / 3.0f);
            }
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
    @SuppressWarnings("unused")
    public void setTranslucentStatusBarLollipop(Window window) {
        setStatusBarColor(false, android.R.color.transparent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("unused")
    private void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    protected void removeToolBarFlags(Toolbar toolbar) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);
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


    /**
     * Prompt to install from Playstore
     *
     * @param title            The dialog title
     * @param content          The dialog text
     * @param resId            The dialog icon
     * @param packageToInstall The id of app on Playstore
     */
    public void installExtApp(final String title, final String content, int resId, final String packageToInstall) {
        new AlertDialog.Builder(BaseActivity.this)
                .setTitle(title)
                .setIcon(resId)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                        int result = googleAPI.isGooglePlayServicesAvailable(BaseActivity.this);
                        if (result == ConnectionResult.SUCCESS) {
                            // download from android market
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse(IntentUtils.getUrlWithRef(packageToInstall)));
                            startActivity(intent);
                        } else {
                            if (googleAPI.isUserResolvableError(result)) {
                                googleAPI.getErrorDialog(BaseActivity.this, result, RESULT_PLAY_SERVICES_RESOLUTION).show();
                            }
                        }
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
        synchronized (lock) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    public void showProgressDialog(@Nullable String msg) {
        synchronized (lock) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(true);
            }
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

        synchronized (lock) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
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

    public void transportPlansExtra() {
        startActivity(new Intent(BaseActivity.this, PublicTransportPlanExtra.class));
    }

    public void settings() {
        startActivityForResult(new Intent(BaseActivity.this, SettingsActivity.class), RESULT_SETTINGS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* PURCHASE STUFF */


    protected void showDonateDialog() {

        if (BuildConfig.USE_CRASHLYTICS)
            Answers.getInstance().logCustom(new CustomEvent("Show Donate"));

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

    private void onDonate(PurchaseItem item) {
        if (item == null || !item.isValid() || getBillingService() == null) {
            return;
        }
        try {
            Bundle buyIntentBundle = getBillingService().getBuyIntent(3, getPackageName(), item.getId(), "inapp", "");
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
        } else
            new GetSkuDetails(callback, mDonateItems, getPackageName()).execute(getBillingService());
    }

    private static class GetSkuDetails extends AsyncTask<IInAppBillingService, Void, AsyncResult<List<PurchaseItem>>> {
        AsyncCallback<List<PurchaseItem>> mCallback;
        String[] mItems;
        String mPackage;

        private GetSkuDetails(final AsyncCallback<List<PurchaseItem>> callback, String[] items, String packName) {
            mCallback = callback;
            mItems = items;
            mPackage = packName;
        }

        @Override
        protected AsyncResult<List<PurchaseItem>> doInBackground(IInAppBillingService... params) {
            List<PurchaseItem> result = new ArrayList<>();
            Throwable exception = null;
            IInAppBillingService billingService = params[0];

            if (billingService == null) {
                exception = new Exception("Unknown");
            } else {
                ArrayList<String> skuList = new ArrayList<>(Arrays.asList(mItems));
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                try {
                    Bundle skuDetails = billingService.getSkuDetails(3, mPackage, "inapp", querySkus);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        PurchaseItem purchaseItem;
                        if (Commons.isNotNull(responseList)) {
                            for (String item : responseList) {
                                purchaseItem = new PurchaseItem(new JSONObject(item));
                                if (purchaseItem.isValid()) {
                                    result.add(purchaseItem);
                                }
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
            if (mCallback != null) {
                Throwable error = result.getError();
                if (error == null && (result.getResult() == null || result.getResult().size() == 0)) {
                    error = new Exception("Unknow");
                }
                if (error != null) {
                    mCallback.onError(error);
                } else {
                    mPurchaseItems = result.getResult();
                    Collections.sort(mPurchaseItems, new Comparator<PurchaseItem>() {
                        @Override
                        public int compare(PurchaseItem lhs, PurchaseItem rhs) {
                            return (int) ((lhs.getPriceAmountMicros() - rhs.getPriceAmountMicros()) / 1000);
                        }
                    });
                    mCallback.onSuccess(mPurchaseItems);
                }
            }
        }
    }

    public void showDonateDialogErrorCallback(Throwable t) {
        if (t != null)
            Log.d(TAG, "showDonateDialogErrorCallback: " + t.getLocalizedMessage());
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
        new consumeAllIAB(getPackageName()).execute(getBillingService());
    }

    private static class consumeAllIAB extends AsyncTask<IInAppBillingService, Void, Boolean> {
        String mPackage;

        private consumeAllIAB(String packName) {
            mPackage = packName;
        }

        @Override
        protected Boolean doInBackground(IInAppBillingService... params) {
            IInAppBillingService billingService = params[0];

            if (billingService != null) {
                try {
                    Bundle ownedItems = billingService.getPurchases(3, mPackage, "inapp", null);
                    int responseCode = ownedItems.getInt("RESPONSE_CODE");
                    if (responseCode == 0) {

                        ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        if (Commons.isNotNull(purchaseDataList)) {
                            for (String purchaseData : purchaseDataList) {
                                JSONObject o = new JSONObject(purchaseData);
                                String purchaseToken = o.optString("token", o.optString("purchaseToken"));

                                responseCode = billingService.consumePurchase(3, mPackage, purchaseToken);
                                Log.d(TAG, "Consume IAB: " + responseCode);
                            }
                        }
                    }
                } catch (RemoteException | JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean res) {
        }
    }

    public static abstract class AsyncCallback<T> {

        public abstract void onError(Throwable throwable);

        public abstract void onSuccess(T data);

        private WeakReference<Context> mContextWeakReference;

        AsyncCallback(Context context) {
            mContextWeakReference = new WeakReference<>(context);
        }

        public Context getContext() {
            return mContextWeakReference.get();
        }
    }

    public static class AsyncResult<T> {

        private Throwable mError;

        private T mResult;

        AsyncResult(T result, Throwable error) {
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

        PurchaseAdapter(Context context, List<PurchaseItem> items) {
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

    static class PurchaseItem {
        private String mDescription;
        private String mId;
        private String mPrice;
        private long mPriceAmountMicros;
        private String mPriceCurrencyCode;
        private String mTitle;
        private String mType;

        PurchaseItem(JSONObject item) {
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

        String getDescription() {
            return mDescription;
        }

        String getId() {
            return mId;
        }

        String getPrice() {
            return mPrice;
        }

        long getPriceAmountMicros() {
            return mPriceAmountMicros;
        }

        String getPriceCurrencyCode() {
            return mPriceCurrencyCode;
        }

        String getTitle() {
            return mTitle;
        }

        String getType() {
            return mType;
        }

        boolean isValid() {
            return !TextUtils.isEmpty(mId);
        }
    }
}