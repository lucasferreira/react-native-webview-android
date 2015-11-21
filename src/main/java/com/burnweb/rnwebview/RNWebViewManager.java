package com.burnweb.rnwebview;

import javax.annotation.Nullable;

import android.webkit.WebChromeClient;
import android.webkit.CookieManager;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.common.annotations.VisibleForTesting;

import java.util.Map;

public class RNWebViewManager extends ViewGroupManager<RNWebView> {

    public static final int GO_BACK = 1;
    public static final int GO_FORWARD = 2;
    public static final int RELOAD = 3;

    @VisibleForTesting
    public static final String REACT_CLASS = "RNWebViewAndroid";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public RNWebView createViewInstance(ThemedReactContext context) {
        RNWebView rnwv = new RNWebView(context);

        CookieManager.getInstance().setAcceptCookie(true); // add default cookie support
        CookieManager.getInstance().setAcceptFileSchemeCookies(true); // add default cookie support

        return rnwv;
    }

    @ReactProp(name = "disableCookies", defaultBoolean = false)
    public void setDisableCookies(RNWebView view, boolean disableCookies) {
        if(disableCookies) {
            CookieManager.getInstance().setAcceptCookie(false);
            CookieManager.getInstance().setAcceptFileSchemeCookies(false);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().setAcceptFileSchemeCookies(true);
        }
    }

    @ReactProp(name = "builtInZoomControls", defaultBoolean = false)
    public void setBuiltInZoomControls(RNWebView view, boolean builtInZoomControls) {
        view.getSettings().setBuiltInZoomControls(builtInZoomControls);
    }

    @ReactProp(name = "geolocationEnabled", defaultBoolean = false)
    public void setGeolocationEnabled(RNWebView view, boolean geolocationEnabled) {
        view.getSettings().setGeolocationEnabled(geolocationEnabled);

        if(geolocationEnabled) {
            view.setWebChromeClient(view.getGeoClient());
        }
        else {
            view.setWebChromeClient(new WebChromeClient());
        }
    }

    @ReactProp(name = "javaScriptEnabled", defaultBoolean = true)
    public void setJavaScriptEnabled(RNWebView view, boolean javaScriptEnabled) {
        view.getSettings().setJavaScriptEnabled(javaScriptEnabled);
    }

    @ReactProp(name = "url")
    public void setUrl(RNWebView view, @Nullable String url) {
        view.loadUrl(url);
    }

    @ReactProp(name = "htmlCharset")
    public void setHtmlCharset(RNWebView view, @Nullable String htmlCharset) {
        if(htmlCharset != null) view.setCharset(htmlCharset);
    }

    @ReactProp(name = "html")
    public void setHtml(RNWebView view, @Nullable String html) {
        view.loadData(html, "text/html", view.getCharset());
    }

    @ReactProp(name = "injectedJavaScript")
    public void setInjectedJavaScript(RNWebView view, @Nullable String injectedJavaScript) {
        view.setInjectedJavaScript(injectedJavaScript);
    }

    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
            "goBack", GO_BACK,
            "goForward", GO_FORWARD,
            "reload", RELOAD
        );
    }

    @Override
    public void receiveCommand(RNWebView view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case GO_BACK:
                view.goBack();
                break;
            case GO_FORWARD:
                view.goForward();
                break;
            case RELOAD:
                view.reload();
                break;
        }
    }

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                NavigationStateChangeEvent.EVENT_NAME, MapBuilder.of("registrationName", "onNavigationStateChange")
        );
    }

}
