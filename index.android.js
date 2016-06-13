/**
 * @providesModule WebViewAndroid
 */
'use strict';

var React = require('react');
var { UIManager, requireNativeComponent } = require('react-native');
var RCTUIManager = UIManager;

var WEBVIEW_REF = 'androidWebView';

var WebViewAndroid = React.createClass({
  propTypes: {
    url: React.PropTypes.string,
    baseUrl: React.PropTypes.string,
    html: React.PropTypes.string,
    htmlCharset: React.PropTypes.string,
    userAgent: React.PropTypes.string,
    injectedJavaScript: React.PropTypes.string,
    disablePlugins: React.PropTypes.bool,
    disableCookies: React.PropTypes.bool,
    javaScriptEnabled: React.PropTypes.bool,
    geolocationEnabled: React.PropTypes.bool,
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
