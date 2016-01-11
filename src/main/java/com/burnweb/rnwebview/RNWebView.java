package com.burnweb.rnwebview;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;

/*package*/ class RNWebView extends WebView {

    @Override
    public void invalidate() {
        super.invalidate();
        int contentHeight = getContentHeight();
        if (contentHeight != mOldContentHeight) {
            mOldContentHeight = contentHeight;
            mEventDispatcher.dispatchEvent(
                    new ContentHeightChangeEvent(getId(), SystemClock.uptimeMillis(), contentHeight));
        }
    }

    protected class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    protected class EventWebClient extends WebViewClient {
        private boolean openLinksInBrowser = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (openLinksInBrowser) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(browserIntent);
                return true;
            } else {
                return false;
            }
        }

        private String injectedJavaScript = null;

        public void setInjectedJavaScript(String injectedJavaScript) {
            this.injectedJavaScript = injectedJavaScript;
        }

        public String getInjectedJavaScript() {
            return this.injectedJavaScript;
        }

        public void onPageFinished(WebView view, String url) {
            mEventDispatcher.dispatchEvent(
                    new NavigationStateChangeEvent(getId(), SystemClock.uptimeMillis(), view.getTitle(), false, url, view.canGoBack(), view.canGoForward()));

            if(getInjectedJavaScript() != null) {
                view.loadUrl("javascript:(function() { " + getInjectedJavaScript() + "})()");
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mEventDispatcher.dispatchEvent(
                    new NavigationStateChangeEvent(getId(), SystemClock.uptimeMillis(), view.getTitle(), true, url, view.canGoBack(), view.canGoForward()));
        }
    }

    private final EventDispatcher mEventDispatcher;
    private final EventWebClient mWebViewClient;
    private int mOldContentHeight = 0;
    private String charset = "UTF-8";
    private String baseUrl = "file:///";

    public RNWebView(ReactContext reactContext) {
        super(reactContext);
        mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        mWebViewClient = new EventWebClient();

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setGeolocationEnabled(false);
        this.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAllowFileAccessFromFileURLs(true);
        this.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setBlockNetworkImage(false);
        this.getSettings().setBlockNetworkLoads(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.setWebViewClient(mWebViewClient);
        this.setWebChromeClient(new WebChromeClient());
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setInjectedJavaScript(String injectedJavaScript) {
        mWebViewClient.setInjectedJavaScript(injectedJavaScript);
    }

    public void setOpenLinksInBrowser(boolean openInBrowser) {
        mWebViewClient.openLinksInBrowser = openInBrowser;
    }

    public String getInjectedJavaScript() {
        return mWebViewClient.getInjectedJavaScript();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public GeoWebChromeClient getGeoClient() {
        return new GeoWebChromeClient();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.loadDataWithBaseURL(this.getBaseUrl(), "<html></html>", "text/html", this.getCharset(), null);
    }
}
