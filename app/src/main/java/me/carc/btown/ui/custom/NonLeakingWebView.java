package me.carc.btown.ui.custom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

/**
 * A non memory leaking WebView
 * https://blog.sonnguyen.org/2017/02/03/Avoid-memory-leak-when-using-Android-WebView/
 * Created by bamptonm on 10/10/2017.
 */

public class NonLeakingWebView extends WebView {

    public interface WebViewCallback {
        void onPageStarted(WebView view, String url, Bitmap favicon);
        boolean shouldOverrideUrlLoading(WebView view, String url);
        boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);  // api 24 ver
        boolean loadPdfUrl(String url);
        void onPageFinished(WebView view, String url);
    }

    private PdfWebViewClient client;


/*
    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) { */
/*EMPTY*//*
 }
    }
*/

    public NonLeakingWebView(Context context) {
        super(context.getApplicationContext());
        client = new PdfWebViewClient((Activity) context);
        setWebViewClient(client);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        client = new PdfWebViewClient((Activity) context);
        setWebViewClient(client);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
        client = new PdfWebViewClient((Activity) context);
        setWebViewClient(client);
    }

    public void setListener(WebViewCallback cb) {
        client.setCallback(cb);
    }

    @Override
    public void destroy() {
        super.destroy();
/*
        try {
            if (sConfigCallback != null) sConfigCallback.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
*/
    }

    private static class PdfWebViewClient extends WebViewClient {

        private static final String PDF = ".pdf";

        WeakReference<Activity> activityRef;
        WebViewCallback callback;

        PdfWebViewClient(Activity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        private boolean activityValid() {
            try {
                final Activity activity = activityRef.get();
                if (activity != null)
                    return true;
            }
            catch (RuntimeException ignored) { /*EMPTY*/ }
            return false;
        }

        public void setCallback(WebViewCallback callback) {
            this.callback = callback;
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (callback != null && activityValid())
                callback.onPageStarted(view, url, favicon);
            else
                throw new RuntimeException("You should set a listener");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (callback != null && activityValid()) {
                if(isPdfUrl(url)) {
                    view.stopLoading();
                    return callback.loadPdfUrl(url);
                } else {
                    return callback.shouldOverrideUrlLoading(view, url);
                }
            } else
                throw new RuntimeException("You should set a listener");
        }


        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (callback != null && activityValid())
                if(isPdfUrl(request.getUrl().toString())) {
                    view.stopLoading();
                    return callback.loadPdfUrl(request.getUrl().toString());
                } else {
                    return callback.shouldOverrideUrlLoading(view, request);
                }
            else
                throw new RuntimeException("You should set a listener");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (callback != null && activityValid())
                callback.onPageFinished(view, url);
            else
                throw new RuntimeException("You should set a listener");
        }

        private boolean isPdfUrl(String url) {
            if (!TextUtils.isEmpty(url)) {
                url = url.trim();
                int lastIndex = url.toLowerCase().lastIndexOf(PDF);
                if (lastIndex != -1) {
                    return url.substring(lastIndex).equalsIgnoreCase(PDF);
                }
            }
            return false;
        }
    }
}
