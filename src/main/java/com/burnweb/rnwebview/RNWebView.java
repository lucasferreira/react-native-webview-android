package com.burnweb.rnwebview;

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

    protected class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

    protected class EventWebClient extends WebViewClient {
        public void onPageFinished(WebView view, String url) {
            mEventDispatcher.dispatchEvent(
                    new NavigationStateChangeEvent(getId(), SystemClock.uptimeMillis(), false, url, view.canGoBack(), view.canGoForward()));
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mEventDispatcher.dispatchEvent(
                    new NavigationStateChangeEvent(getId(), SystemClock.uptimeMillis(), true, url, view.canGoBack(), view.canGoForward()));
        }
    }

    private final EventDispatcher mEventDispatcher;

    public RNWebView(ReactContext reactContext) {
        super(reactContext);

        mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setGeolocationEnabled(false);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAllowFileAccessFromFileURLs(true);
        this.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.getSettings().setLoadsImagesAutomatically(true);
        this.getSettings().setBlockNetworkImage(false);
        this.getSettings().setBlockNetworkLoads(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(new EventWebClient());
    }

    public GeoWebChromeClient getGeoClient() {
        return new GeoWebChromeClient();
    }

}
