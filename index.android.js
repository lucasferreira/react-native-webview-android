/**
 * @providesModule WebViewAndroid
 */
'use strict';

try {
  var React = require('react');
} catch(ex) {
  var React = require('react-native');
}

var { requireNativeComponent, NativeModules } = require('react-native');
var RCTUIManager = NativeModules.UIManager;

var WEBVIEW_REF = 'androidWebView';

var WebViewAndroid = React.createClass({
  propTypes: {
    url: React.PropTypes.string,
    source: React.PropTypes.object,
    baseUrl: React.PropTypes.string,
    html: React.PropTypes.string,
    htmlCharset: React.PropTypes.string,
    userAgent: React.PropTypes.string,
    injectedJavaScript: React.PropTypes.string,
    disablePlugins: React.PropTypes.bool,
    disableCookies: React.PropTypes.bool,
    javaScriptEnabled: React.PropTypes.bool,
    geolocationEnabled: React.PropTypes.bool,
    allowUrlRedirect: React.PropTypes.bool,
    builtInZoomControls: React.PropTypes.bool,
    onNavigationStateChange: React.PropTypes.func
  },
  _onNavigationStateChange: function(event) {
    if (this.props.onNavigationStateChange) {
      this.props.onNavigationStateChange(event.nativeEvent);
    }
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
  render: function() {
    return <RNWebViewAndroid ref={WEBVIEW_REF} {...this.props} onNavigationStateChange={this._onNavigationStateChange} />;
  },
  _getWebViewHandle: function() {
    return React.findNodeHandle(this.refs[WEBVIEW_REF]);
  },
});

var RNWebViewAndroid = requireNativeComponent('RNWebViewAndroid', null);

module.exports = WebViewAndroid;
