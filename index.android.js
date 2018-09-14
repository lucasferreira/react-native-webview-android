/**
 * @providesModule WebViewAndroid
 */
"use strict";

var React = require("react");
var RN = require("react-native");
var createClass = require("create-react-class");
var PropTypes = require("prop-types");

var { requireNativeComponent, NativeModules } = require("react-native");
var RCTUIManager = NativeModules.UIManager;

var WEBVIEW_REF = "androidWebView";

var WebViewAndroid = createClass({
  propTypes: {
    url: PropTypes.string,
    source: PropTypes.object,
    baseUrl: PropTypes.string,
    html: PropTypes.string,
    htmlCharset: PropTypes.string,
    userAgent: PropTypes.string,
    injectedJavaScript: PropTypes.string,
    disablePlugins: PropTypes.bool,
    disableCookies: PropTypes.bool,
    javaScriptEnabled: PropTypes.bool,
    geolocationEnabled: PropTypes.bool,
    allowUrlRedirect: PropTypes.bool,
    builtInZoomControls: PropTypes.bool,
    onNavigationStateChange: PropTypes.func,
    onMessage: PropTypes.func,
    onShouldStartLoadWithRequest: PropTypes.func,
    evaluateJavascript: PropTypes.string
  },
  _onNavigationStateChange: function(event) {
    if (this.props.onNavigationStateChange) {
      this.props.onNavigationStateChange(event.nativeEvent);
    }
  },
  _onMessage: function(event) {
    if (this.props.onMessage) {
      this.props.onMessage(event.nativeEvent);
    }
  },
  _onShouldOverrideUrlLoading: function(event) {
    let shouldOverride = false;

    if (this.props.onShouldStartLoadWithRequest) {
      shouldOverride = !this.props.onShouldStartLoadWithRequest(event.nativeEvent);
    }

    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.shouldOverrideWithResult,
      [shouldOverride]
    );
  },
  goBack: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.goBack,
      null
    );
  },
  goForward: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.goForward,
      null
    );
  },
  reload: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.reload,
      null
    );
  },
  stopLoading: function() {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.stopLoading,
      null
    );
  },
  postMessage: function(data) {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.postMessage,
      [String(data)]
    );
  },
  injectJavaScript: function(data) {
    RCTUIManager.dispatchViewManagerCommand(
      this._getWebViewHandle(),
      RCTUIManager.RNWebViewAndroid.Commands.injectJavaScript,
      [data]
    );
  },
  evaluateJavascript: function(data) {
    return NativeModules.RNWebViewAndroidModule.evaluateJavascript(data, this._getWebViewHandle());
  },
  render: function() {
    return (
      <RNWebViewAndroid
        ref={WEBVIEW_REF}
        {...this.props}
        onNavigationStateChange={this._onNavigationStateChange}
        onMessageEvent={this._onMessage}
        onShouldOverrideUrlLoading={this._onShouldOverrideUrlLoading}
      />
    );
  },
  _getWebViewHandle: function() {
    return RN.findNodeHandle(this.refs[WEBVIEW_REF]);
  },
});

var RNWebViewAndroid = requireNativeComponent("RNWebViewAndroid", null);

module.exports = WebViewAndroid;
