package me.carc.btown.extras;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BaseActivity;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.ui.custom.NonLeakingWebView;

/**
 * Created by bamptonm on 10/11/2017.
 */

public class WebBrowser extends BaseActivity {
    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final String WEB_EXTRA_PAGE_TITLE = "WIKI_EXTRA_PAGE_TITLE";
    public static final String WEB_EXTRA_PAGE_SUBHEADING = "WIKI_EXTRA_PAGE_SUBHEADING";
    public static final String WEB_EXTRA_PAGE_URL = "WIKI_EXTRA_PAGE_URL";
    public static final String WEB_DISABLE_JS = "DISABLE_JS";


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
        setContentView(R.layout.activity_web_browser);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        boolean disableJS = false;
        Intent intent = getIntent();
        if (Commons.isNotNull(intent) && Commons.isNull(savedInstanceState)) {
            if (intent.hasExtra(WEB_EXTRA_PAGE_URL)) {
                postUrl = intent.getStringExtra(WEB_EXTRA_PAGE_URL);
            }

            if (intent.hasExtra(WEB_EXTRA_PAGE_TITLE)) {
                getSupportActionBar().setTitle(intent.getStringExtra(WEB_EXTRA_PAGE_TITLE));
            } else
                titleRequired = true;

            if (intent.hasExtra(WEB_EXTRA_PAGE_SUBHEADING)) {
                getSupportActionBar().setSubtitle(intent.getStringExtra(WEB_EXTRA_PAGE_SUBHEADING));
            }

//            if (intent.hasExtra(WEB_DISABLE_JS)) {
//                disableJS = intent.getBooleanExtra(WEB_DISABLE_JS, false);
//            }
        }


        if(!Commons.contains(postUrl, WIKIPEDIA)) {
            if(BuildConfig.DEBUG)
                Toast.makeText(this, "JAVA SCRIPT ENABLED", Toast.LENGTH_SHORT).show();
        }

//        if(disableJS)
//            allowJavaScript(false);

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




}
