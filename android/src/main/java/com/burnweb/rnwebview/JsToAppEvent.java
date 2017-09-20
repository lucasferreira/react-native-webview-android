package com.burnweb.rnwebview;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by qicheng on 2017/9/12.
 */

public class JsToAppEvent extends Event<JsToAppEvent> {

    public static final String JS_EVENT_NAME = "jsToApp";

    private final String mTitle;

    public JsToAppEvent(int viewId, String title) {
        super();

        mTitle = title;
    }

    @Override
    public String getEventName() {
        return JS_EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private WritableMap serializeEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putString("title", mTitle);

        return eventData;
    }

}
