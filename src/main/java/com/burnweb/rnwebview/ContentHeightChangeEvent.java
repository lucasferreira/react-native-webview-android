package com.burnweb.rnwebview;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/* package */ class ContentHeightChangeEvent extends Event<ContentHeightChangeEvent> {

    public static final String EVENT_NAME = "contentHeightChange";

    private final int mContentHeight;

    protected ContentHeightChangeEvent(int viewTag, long timestampMs, int contentHeight) {
        super(viewTag, timestampMs);

        mContentHeight = contentHeight;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private WritableMap serializeEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("contentHeight", mContentHeight);

        return eventData;
    }

}
