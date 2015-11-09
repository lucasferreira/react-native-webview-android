/**
 * @providesModule WebViewAndroid
 */
'use strict';

var React = require('react-native');
var { requireNativeComponent, PropTypes } = React;

var WEBVIEW_REF = 'androidWebView';

var WebViewAndroid = React.createClass({
  propTypes: {
    url: PropTypes.string,
    html: PropTypes.string,
    htmlCharset: PropTypes.string,
    disableCookies: PropTypes.bool,
    javaScriptEnabled: PropTypes.bool,
    geolocationEnabled: PropTypes.bool,
    builtInZoomControls: PropTypes.bool,
    onNavigationStateChange: PropTypes.func
  },
  _onNavigationStateChange: function(event) {
    if (this.props.onNavigationStateChange) {
      this.props.onNavigationStateChange(event.nativeEvent);
    }
  },
  render: function() {
    return <RNWebViewAndroid ref={WEBVIEW_REF} {...this.props} onNavigationStateChange={this._onNavigationStateChange} />;
  }
});

var RNWebViewAndroid = requireNativeComponent('RNWebViewAndroid', null);

module.exports = WebViewAndroid;
