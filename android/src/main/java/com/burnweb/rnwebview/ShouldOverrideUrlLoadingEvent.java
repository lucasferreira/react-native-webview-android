package com.burnweb.rnwebview;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class ShouldOverrideUrlLoadingEvent extends Event<ShouldOverrideUrlLoadingEvent> {

  public static final String EVENT_NAME = "shouldOverrideUrlLoading";

  private final String mUrl;
  private final int mNavigationType;

  public ShouldOverrideUrlLoadingEvent(int viewId, long timestampMs, String url, int navigationType) {
    super(viewId);

    mUrl = url;
    mNavigationType = navigationType;
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
    eventData.putString("url", mUrl);
    eventData.putInt("navigationType", mNavigationType);

    return eventData;
  }
}
