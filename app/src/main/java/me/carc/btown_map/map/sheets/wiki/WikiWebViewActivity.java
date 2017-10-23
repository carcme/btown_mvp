package me.carc.btown_map.map.sheets.wiki;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown_map.App;
import me.carc.btown_map.BaseActivity;
import me.carc.btown_map.BuildConfig;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.IntentUtils;
import me.carc.btown_map.Utils.WikiUtils;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.map.sheets.ImageDialog;
import me.carc.btown_map.ui.custom.NonLeakingWebView;

public class WikiWebViewActivity extends BaseActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final String WIKI_EXTRA_PAGE_TITLE = "WIKI_EXTRA_PAGE_TITLE";
    public static final String WIKI_EXTRA_PAGE_SUBHEADING = "WIKI_EXTRA_PAGE_SUBHEADING";
    public static final String WIKI_EXTRA_PAGE_URL = "WIKI_EXTRA_PAGE_URL";


    private static final String HTTPS_COMMONS = "https://upload.wikimedia.org/wikipedia/commons/";
    private static final String WIKIPEDIA = "wikipedia";

    private static final String HTTP   = "HTTP://";
    private static final String JPG    = ".JPG";
    private static final String JPEG   = ".JPEG";
    private static final String PNG    = ".PNG";
    private static final String FILE   = "File:";
    private static final String TEL    = "TEL://";
    private static final String MAILTO = "mailto:";  // NOTE: This is lower case

    private static final String MIME_TYPE_PDF = "application/pdf";
    private static final String URL_ENCODING = "UTF-8";

    private boolean titleRequired = false;
    private String postUrl = null;

    @Nullable
    @BindView(R.id.wikiActivityToolbar)
    Toolbar toolbar;

    @BindView(R.id.nonLeakWebView)
    NonLeakingWebView webView;

    @BindView(R.id.wikiActivityProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.wikiMoreBtn)
    ImageView wikiMoreBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_webview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (Commons.isNotNull(intent) && Commons.isNull(savedInstanceState)) {
            if (intent.hasExtra(WIKI_EXTRA_PAGE_URL)) {
                postUrl = intent.getStringExtra(WIKI_EXTRA_PAGE_URL);
            }

            if (intent.hasExtra(WIKI_EXTRA_PAGE_TITLE)) {
                getSupportActionBar().setTitle(intent.getStringExtra(WIKI_EXTRA_PAGE_TITLE));
            } else
                titleRequired = true;

            if (intent.hasExtra(WIKI_EXTRA_PAGE_SUBHEADING)) {
                getSupportActionBar().setSubtitle(intent.getStringExtra(WIKI_EXTRA_PAGE_SUBHEADING));
            }
        }

        webView.setListener(new NonLeakingWebView.WebViewCallback() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return overrideImageUrls(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return overrideImageUrls(view, request.getUrl().toString());
            }

            @Override
            public boolean loadPdfUrl(final String url) {
                if (!Commons.isEmpty(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), MIME_TYPE_PDF);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

/*
                    progressBar.setVisibility(View.VISIBLE);
                    allowJavaScript(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(url);
                        }
                    }, 300);
*/

                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);

                // skip facebook sub-headings for now - am using page ID which does nothing for the user
/*                if(!getSupportActionBar().getTitle().equals(getString(R.string.facebook))) {
                    String title = url.substring(url.lastIndexOf("/") + 1, url.length()).replace("_", " ");
                    try {
                        getSupportActionBar().setSubtitle(URLDecoder.decode(title, URL_ENCODING));
                    } catch (UnsupportedEncodingException e) {
                        getSupportActionBar().setSubtitle(title);
                    }
                }
*/
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(titleRequired) {
                    getSupportActionBar().setTitle(title);
                    titleRequired = false;
                } else
                    getSupportActionBar().setSubtitle(title);
            }
        });

        if(!Commons.contains(postUrl, WIKIPEDIA)) {
            allowJavaScript(true);
            if(BuildConfig.DEBUG)
                Toast.makeText(this, "JAVA SCRIPT ENABLED", Toast.LENGTH_SHORT).show();
        }

        // Load the URL
        if (Commons.isNotNull(savedInstanceState)) {
            webView.restoreState(savedInstanceState);
            webView.getSettings().setJavaScriptEnabled(savedInstanceState.getBoolean("JAVA_SCRIPT"));
        } else {
            if(!postUrl.toUpperCase().startsWith("HTTP")) // catch JSON errors (missed some http stuff :/ )
                postUrl = HTTP + postUrl;

            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            webView.setHorizontalScrollBarEnabled(false);
            webView.loadUrl(postUrl);
        }
    }

    private void allowJavaScript(boolean allow) {
        webView.getSettings().setSupportZoom(allow);
        webView.getSettings().setJavaScriptEnabled(allow);
    }

    /**
     * TODO: 12/10/2017 get the ideal image size from wikiUtils and show that - download image cam get best resolution
     *
     * @param view the webview
     * @param url  the url
     * @return true if override, false let webView handle link
     */
    private boolean overrideImageUrls(WebView view, String url) {

        int hitTestType = view.getHitTestResult().getType();

        if (isLinkImageType(hitTestType)) {
            WebHistoryItem history = webView.copyBackForwardList().getCurrentItem();

            if (url.startsWith(HTTPS_COMMONS)) {
                if (url.toUpperCase().endsWith(JPG) || url.toUpperCase().endsWith(JPEG)) {
                    String title = history.getTitle().replace(" - Wikipedia", "");
                    ImageDialog.showInstance(getApplication(), url, history.getOriginalUrl(), title, getString(R.string.wikipedia));
                    return true;
                }
            } else if (url.contains(FILE)) {
                String decodedString = Uri.decode(url);
                String deviceThumbnail = WikiUtils.buildWikiCommonsLink(decodedString, C.SCREEN_WIDTH);

                if (!deviceThumbnail.equals(url)) {
                    String title = history.getTitle().replace(" - Wikipedia", "");
                    ImageDialog.showInstance(getApplication(), deviceThumbnail, history.getOriginalUrl(), title, getString(R.string.wikipedia));
                    return true;
                }
            } else if (!url.contains("wikipedia") && isLinkImage(url)) {
                // not a wiki link -  just show the image file in the imageviewer
                ImageDialog.showInstance(getApplication(), url, history.getOriginalUrl(), history.getTitle(), null);
                return true;
            }
        } else if (isLinkEmailType(hitTestType, url)) {
            return true;

        } else if(isLinkTelephoneType(hitTestType, url)) {
            return true;
        }
        return false;
    }

    private String getViewerSubTitle() {
        if(webView.copyBackForwardList().getSize() > 1) {
            return webView.copyBackForwardList().getItemAtIndex(webView.copyBackForwardList().getCurrentIndex() - 1).getTitle();
        }
        return null;
    }

    /**
     * @param type Hit test type
     * @return true if image, false otherwise
     */
    private boolean isLinkImageType(int type) {
        return type == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                type == WebView.HitTestResult.IMAGE_TYPE;
    }

    /**
     * @param type Hit test type
     * @return true if image, false otherwise
     */
    private boolean isLinkEmailType(int type, String url) {
        if(type == WebView.HitTestResult.EMAIL_TYPE) {
            Intent intent = IntentUtils.sendEmail(Commons.replace(url, MAILTO), "", "");
            if (Commons.isNotNull(intent)) startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * @param type Hit test type
     * @return true if image, false otherwise
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isLinkTelephoneType(int type, String url) {
         if(type == WebView.HitTestResult.PHONE_TYPE) {
             if (C.HAS_M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                 requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, C.PERMISSION_CALL_PHONE);
                 return false;
             }
             startActivity(IntentUtils.callPhone(Commons.replace(url, TEL)));
             return true;
         }
        return false;
    }

    private boolean isLinkImage(String url) {
        return url.toUpperCase().endsWith(JPG) || url.toUpperCase().endsWith(JPEG) || url.toUpperCase().endsWith(PNG);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplication()).setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        try {
            if (Commons.isNotNull(webView)) {
                webView.removeAllViews();
                webView.destroyDrawingCache();
                webView.clearHistory();
                webView.destroy();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    @OnClick(R.id.wikiMoreBtn)
    void onSettingOverflow() {
        PopupMenu popupMenu = new PopupMenu(this, wikiMoreBtn);
        popupMenu.inflate(R.menu.menu_wiki_overflow);
        popupMenu.setOnMenuItemClickListener(menuListener);
        popupMenu.show();
    }

    private PopupMenu.OnMenuItemClickListener menuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_wiki_share:
                    WebHistoryItem wikiItem = webView.copyBackForwardList().getCurrentItem();
                    startActivity(IntentUtils.shareText(wikiItem.getTitle(), wikiItem.getUrl()));
                    return true;

                case R.id.menu_wiki_browser:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webView.copyBackForwardList().getCurrentItem().getUrl())));
                    return true;

                default:
                    return false;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Allow back button active in webview
        if (webView.copyBackForwardList().getCurrentIndex() > 0) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (webView != null) {
            webView.saveState(outState);
            outState.putBoolean("JAVA_SCRIPT", webView.getSettings().getJavaScriptEnabled());
        }
        super.onSaveInstanceState(outState);
    }
}