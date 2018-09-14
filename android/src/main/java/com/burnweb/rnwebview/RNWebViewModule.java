package com.burnweb.rnwebview;

import android.annotation.SuppressLint;
import com.facebook.react.common.annotations.VisibleForTesting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.NativeViewHierarchyManager;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.StringReader;

public class RNWebViewModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    @VisibleForTesting
    public static final String REACT_CLASS = "RNWebViewAndroidModule";

    private RNWebViewPackage aPackage;

    /* FOR UPLOAD DIALOG */
    private final static int REQUEST_SELECT_FILE = 1001;
    private final static int REQUEST_SELECT_FILE_LEGACY = 1002;

    private ValueCallback<Uri> mUploadMessage = null;
    private ValueCallback<Uri[]> mUploadMessageArr = null;

    public RNWebViewModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public void setPackage(RNWebViewPackage aPackage) {
        this.aPackage = aPackage;
    }

    public RNWebViewPackage getPackage() {
        return this.aPackage;
    }

    @SuppressWarnings("unused")
    public Activity getActivity() {
        return getCurrentActivity();
    }

    public void showAlert(String url, String message, final JsResult result) {
        AlertDialog ad = new AlertDialog.Builder(getCurrentActivity())
                                .setMessage(message)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                                .create();

        ad.show();
    }

    // For Android 4.1+
    @SuppressWarnings("unused")
    public boolean startFileChooserIntent(ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.d(REACT_CLASS, "Open old file dialog");

        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }

        mUploadMessage = uploadMsg;

        if(acceptType == null || acceptType.isEmpty()) {
            acceptType = "*/*";
        }

        Intent intentChoose = new Intent(Intent.ACTION_GET_CONTENT);
        intentChoose.addCategory(Intent.CATEGORY_OPENABLE);
        intentChoose.setType(acceptType);

        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            Log.w(REACT_CLASS, "No context available");
            return false;
        }

        try {
            currentActivity.startActivityForResult(intentChoose, REQUEST_SELECT_FILE_LEGACY, new Bundle());
        } catch (ActivityNotFoundException e) {
            Log.e(REACT_CLASS, "No context available");
            e.printStackTrace();

            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            return false;
        }

        return true;
    }

    // For Android 5.0+
    @SuppressLint("NewApi")
    public boolean startFileChooserIntent(ValueCallback<Uri[]> filePathCallback, Intent intentChoose) {
        Log.d(REACT_CLASS, "Open new file dialog");

        if (mUploadMessageArr != null) {
            mUploadMessageArr.onReceiveValue(null);
            mUploadMessageArr = null;
        }

        mUploadMessageArr = filePathCallback;

        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            Log.w(REACT_CLASS, "No context available");
            return false;
        }

        try {
            currentActivity.startActivityForResult(intentChoose, REQUEST_SELECT_FILE, new Bundle());
        } catch (ActivityNotFoundException e) {
            Log.e(REACT_CLASS, "No context available");
            e.printStackTrace();

            if (mUploadMessageArr != null) {
                mUploadMessageArr.onReceiveValue(null);
                mUploadMessageArr = null;
            }
            return false;
        }

        return true;
    }

    @SuppressLint({"NewApi", "Deprecated"})
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_FILE_LEGACY) {
            if (mUploadMessage == null) return;

            Uri result = ((data == null || resultCode != Activity.RESULT_OK) ? null : data.getData());

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == REQUEST_SELECT_FILE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadMessageArr == null) return;

            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            mUploadMessageArr = null;
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        this.onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent) {}

    public interface RNWebViewHandler {
        void handle(RNWebView result);
    }

    @ReactMethod
    public void evaluateJavascript(final String data, final int viewId, final Promise promise) {
        withRNWebView(viewId, promise, new RNWebViewHandler() {
            @Override
            public void handle(RNWebView view) {
                view.evaluateJavascript(data, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        JsonReader reader = new JsonReader(new StringReader(value));

                        reader.setLenient(true);

                        try {
                            if(reader.peek() != JsonToken.NULL && reader.peek() == JsonToken.STRING) {
                                String msg = reader.nextString();
                                promise.resolve(msg);
                            } else {
                                promise.resolve(value);
                            }
                        } catch (Exception e) {
                            Log.e(REACT_CLASS, "Unparsable evaluate javascript result");
                            promise.reject(e.toString());
                        } finally {
                            try {
                                reader.close();
                            } catch (Exception e) {
                                // NOOP
                                promise.reject(e.toString());
                            }
                        }
                    }
                });
            }
        });
    }

    private void withRNWebView(final int viewId, final Promise promise, final RNWebViewHandler handler) {
        UIManagerModule uiManager = getReactApplicationContext().getNativeModule(UIManagerModule.class);
        uiManager.addUIBlock(new UIBlock() {
            @Override
            public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {
                View view = nativeViewHierarchyManager.resolveView(viewId);
                if (view instanceof RNWebView) {
                    RNWebView myView = (RNWebView) view;
                    handler.handle(myView);
                }
                else {
                    promise.reject("RNWebView", "Unexpected view type");
                }
            }
        });
    }

}
