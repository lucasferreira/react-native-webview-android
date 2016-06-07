package com.burnweb.rnwebview;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import android.webkit.WebSettings;
import android.webkit.CookieManager;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.common.annotations.VisibleForTesting;

public class RNWebViewManager extends SimpleViewManager<RNWebView> {

    public static final int GO_BACK = 1;
    public static final int GO_FORWARD = 2;
    public static final int RELOAD = 3;

    private static final String HTML_MIME_TYPE = "text/html";

    private HashMap<String, String> headerMap = new HashMap<>();
    private RNWebViewPackage aPackage;

    @VisibleForTesting
    public static final String REACT_CLASS = "RNWebViewAndroid";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public RNWebView createViewInstance(ThemedReactContext context) {
        RNWebView rnwv = new RNWebView(this, context);

        CookieManager.getInstance().setAcceptCookie(true); // add default cookie support
        CookieManager.getInstance().setAcceptFileSchemeCookies(true); // add default cookie support

        return rnwv;
    }

    public void setPackage(RNWebViewPackage aPackage) {
        this.aPackage = aPackage;
    }

    public RNWebViewPackage getPackage() {
        return this.aPackage;
    }

    @ReactProp(name = "allowUrlRedirect", defaultBoolean = false)
    public void setAllowUrlRedirect(RNWebView view, boolean allowUrlRedirect) {
        view.setAllowUrlRedirect(allowUrlRedirect);
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

    @ReactProp(name = "disablePlugins", defaultBoolean = false)
    public void setDisablePlugins(RNWebView view, boolean disablePlugins) {
        if(disablePlugins) {
            view.getSettings().setPluginState(WebSettings.PluginState.OFF);
        } else {
            view.getSettings().setPluginState(WebSettings.PluginState.ON);
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
            view.setWebChromeClient(view.getCustomClient());
        }
    }

    @ReactProp(name = "javaScriptEnabled", defaultBoolean = true)
    public void setJavaScriptEnabled(RNWebView view, boolean javaScriptEnabled) {
        view.getSettings().setJavaScriptEnabled(javaScriptEnabled);
    }

    @ReactProp(name = "userAgent")
    public void setUserAgent(RNWebView view, @Nullable String userAgent) {
        if(userAgent != null) view.getSettings().setUserAgentString(userAgent);
    }

    @ReactProp(name = "url")
    public void setUrl(RNWebView view, @Nullable String url) {
        view.loadUrl(url, headerMap);
    }

    @ReactProp(name = "headers")
    public void setHeaders(RNWebView view, @Nullable ReadableMap headers) {
        headerMap = new HashMap<>();

        ReadableMapKeySetIterator iter = headers.keySetIterator();
        while (iter.hasNextKey()) {
            String key = iter.nextKey();
            headerMap.put(key, headers.getString(key));
        }
    }

    @ReactProp(name = "source")
    public void setSource(RNWebView view, @Nullable ReadableMap source) {
        if (source != null) {
            if (source.hasKey("baseUrl")) {
                setBaseUrl(view, source.getString("baseUrl"));
            }
            if (source.hasKey("html")) {
                setHtml(view, source.getString("html"));
                return;
            }
            if (source.hasKey("uri")) {
                if (source.hasKey("headers")) {
                    setHeaders(view, source.getMap("headers"));
                }
                setUrl(view, source.getString("uri"));
                return;
            }
        }
    }

    @ReactProp(name = "baseUrl")
    public void setBaseUrl(RNWebView view, @Nullable String baseUrl) {
        view.setBaseUrl(baseUrl);
    }

    @ReactProp(name = "htmlCharset")
    public void setHtmlCharset(RNWebView view, @Nullable String htmlCharset) {
        if(htmlCharset != null) view.setCharset(htmlCharset);
    }

    @ReactProp(name = "html")
    public void setHtml(RNWebView view, @Nullable String html) {
        view.loadDataWithBaseURL(view.getBaseUrl(), html, HTML_MIME_TYPE, view.getCharset(), null);
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

    @Override
    public void onDropViewInstance(RNWebView webView) {
        super.onDropViewInstance(webView);

        ((ThemedReactContext) webView.getContext()).removeLifecycleEventListener(webView);
    }

}
