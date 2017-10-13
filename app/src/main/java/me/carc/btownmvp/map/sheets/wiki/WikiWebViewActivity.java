package me.carc.btownmvp.map.sheets.wiki;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btownmvp.App;
import me.carc.btownmvp.R;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.map.sheets.ImageDialog;
import me.carc.btownmvp.ui.custom.NonLeakingWebView;

import static android.webkit.WebView.HitTestResult.IMAGE_TYPE;
import static android.webkit.WebView.HitTestResult.SRC_ANCHOR_TYPE;
import static android.webkit.WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE;

public class WikiWebViewActivity extends AppCompatActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final String WIKI_EXTRA_PAGE_TITLE = "WIKI_EXTRA_PAGE_TITLE";
    public static final String WIKI_EXTRA_PAGE_URL   = "WIKI_EXTRA_PAGE_URL";


    private static final String HTTPS_COMMONS = "https://upload.wikimedia.org/wikipedia/commons/";
    private static final String JPG = ".JPG";
    private static final String JPEG = ".JPEG";
    private static final String FILE = "File:";
    private static final String URL_ENCODING = "UTF-8";

    private String title = null;
    private String postUrl = null;

    @Nullable
    @BindView(R.id.wikiActivityToolbar)
    Toolbar toolbar;

    @BindView(R.id.nonLeakWebView)
    NonLeakingWebView webView;

    @BindView(R.id.wikiActivityProgressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_webview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.wikipedia);

        Intent intent = getIntent();
        if(Commons.isNotNull(intent)) {
            if(intent.hasExtra(WIKI_EXTRA_PAGE_URL))
                postUrl = intent.getStringExtra(WIKI_EXTRA_PAGE_URL);
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
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
//                getSupportActionBar().setTitle(Html.fromHtml(url.substring(url.lastIndexOf("/") + 1, url.length()).replace("_", " ")));
            }
        });

        webView.loadUrl(postUrl);
        webView.setHorizontalScrollBarEnabled(false);
    }

    /**
     * TODO: 12/10/2017 get the ideal image size from wikiUtils and show that - download image cam get best resolution
     * @param view the webview
     * @param url the url
     * @return true if override, false let webView handle link
     */
    private boolean overrideImageUrls(WebView view, String url) {
        if (isLinkImageType(view.getHitTestResult())) {
            WebHistoryItem history = webView.copyBackForwardList().getCurrentItem();

            if (url.startsWith(HTTPS_COMMONS)) {
                if (url.toUpperCase().endsWith(JPG) || url.toUpperCase().endsWith(JPEG)) {
                    ImageDialog.showInstance(getApplication(), url, history.getOriginalUrl(), history.getTitle(), null);
                    return true;
                }
/*
            } else {
                String deviceSpecificUrl = WikiUtils.buildWikiCommonsLink(url, C.SCREEN_WIDTH);
                if(!deviceSpecificUrl.equals(url)) {
                        ImageDialog.showInstance(getApplication(), deviceSpecificUrl, history.getOriginalUrl(), history.getTitle(), null);
                        return true;

                }
*/
            }
        }
        return false;
    }

    /**     *
     * @param result Hit test result
     * @return true if image, false otherwise
     */
    private boolean isLinkImageType(WebView.HitTestResult result) {
        return result.getType() == SRC_ANCHOR_TYPE ||
                result.getType() == SRC_IMAGE_ANCHOR_TYPE ||
                result.getType() == IMAGE_TYPE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App)getApplication()).setCurrentActivity(this);
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
        try { webView.destroy(); }
        catch (Exception ex) { ex.printStackTrace(); }
        super.onDestroy();
    }

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
        super.onSaveInstanceState(outState);
    }
}