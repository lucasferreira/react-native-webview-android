# react-native-webview-android
Simple React Native Android module to use Android's WebView inside your app (with experimental html file input support to handle file uploads in forms).

[![npm version](http://img.shields.io/npm/v/react-native-webview-android.svg?style=flat-square)](https://npmjs.org/package/react-native-webview-android "View this project on npm")
[![npm downloads](http://img.shields.io/npm/dm/react-native-webview-android.svg?style=flat-square)](https://npmjs.org/package/react-native-webview-android "View this project on npm")
[![npm licence](http://img.shields.io/npm/l/react-native-webview-android.svg?style=flat-square)](https://npmjs.org/package/react-native-webview-android "View this project on npm")

### Installation

```bash
npm install react-native-webview-android --save
```

### Add it to your android project

* In `android/setting.gradle`

```gradle
...
include ':RNWebView', ':app'
project(':RNWebView').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-webview-android/android')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
  ...
  compile project(':RNWebView')
}
```

* Register Module - RN >= 0.29 (in MainApplication.java)

```java
import com.burnweb.rnwebview.RNWebViewPackage;  // <--- import

public class MainApplication extends Application implements ReactApplication {
  ......

  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new RNWebViewPackage()); // <------ add this line to your MainApplication class
  }

  ......

}
```

If you need to see the install instructions for older React Native versions [look here](https://github.com/lucasferreira/react-native-webview-android/blob/react-native-older/README.md).


## Example
```javascript
var React = require('react-native');
var { StyleSheet } = React;

var WebViewAndroid = require('react-native-webview-android');

var SITE_URL = "https://www.google.com";

var WebViewAndroidExample = React.createClass({
    getInitialState: function() {
      return {
        url: SITE_URL,
        // OR
        // you can use a source object like React Native Webview.
        // source {uri: string, method: string, headers: object, body: string}, {html: string, baseUrl: string}
        // Loads static html or a uri (with optional headers) in the WebView. <Just like React Native's version>
        // source: {
        //   uri: SITE_URL,
        //   headers: {
        //     ...
        //   },
        // },
        status: 'No Page Loaded',
        backButtonEnabled: false,
        forwardButtonEnabled: false,
        loading: true,
        messageFromWebView: null
      };
    },
    goBack: function() {
      // you can use this callback to control web view
      this.refs.webViewAndroidSample.goBack();
    },
    goForward: function() {
      this.refs.webViewAndroidSample.goForward();
    },
    reload: function() {
      this.refs.webViewAndroidSample.reload();
    },
    stopLoading: function() {
      // stops the current load
      this.refs.webViewAndroidSample.stopLoading();
    },
    postMessage: function(data) {
      // posts a message to web view
      this.refs.webViewAndroidSample.postMessage(data);
    },
    evaluateJavascript: function(data) {
      // evaluates javascript directly on the webview instance
      this.refs.webViewAndroidSample.evaluateJavascript(data);
    },
    injectJavaScript: function(script) {
      // executes JavaScript immediately in web view
      this.refs.webViewAndroidSample.injectJavaScript(script);
    },
    onShouldStartLoadWithRequest: function(event) {
      // currently only url & navigationState are returned in the event.
      console.log(event.url);
      console.log(event.navigationState);

      if (event.url === 'https://www.mywebsiteexample.com/') {
        return true;
      } else {
        return false;
      }
    },
    onNavigationStateChange: function(event) {
      console.log(event);

      this.setState({
        backButtonEnabled: event.canGoBack,
        forwardButtonEnabled: event.canGoForward,
        url: event.url,
        status: event.title,
        loading: event.loading
      });
    },
    onMessage: function(event) {
      this.setState({
        messageFromWebView: event.message
      });
    },
    javascriptToInject: function () {
      return `
        $(document).ready(function() {
          $('a').click(function(event) {
            if ($(this).attr('href')) {
              var href = $(this).attr('href');
              window.webView.postMessage('Link tapped: ' + href);
            }
          })
        })
      `
    },
    render: function() {
      return (
        <WebViewAndroid
          ref="webViewAndroidSample"
          javaScriptEnabled={true}
          geolocationEnabled={false}
          builtInZoomControls={false}
          useWideViewPort={false}
          injectedJavaScript={this.javascriptToInject()}
          onShouldStartLoadWithRequest={this.onShouldStartLoadWithRequest}
          onNavigationStateChange={this.onNavigationStateChange}
          onMessage={this.onMessage}
          url={SITE_URL} // or use the source(object) attribute...
          style={styles.containerWebView} />
      );

      // other attributes: source(object), html(string), htmlCharset(string), baseUrl(string), injectedJavaScript(string), disableCookies(bool), disablePlugins(bool), userAgent(string)
    }
});

var styles = StyleSheet.create({
  containerWebView: {
    flex: 1,
  }
});
```

## Note about onShouldStartLoadWithRequest

This module has a working implementation of onShouldStartLoadWithRequest. However, the event it returns currently only includes `url` and `navigationState`.

## Note about HTML file input (files upload)

This module implements an experimental support to handle file input in HTML forms for upload files. It was tested in some Android versions 4.1+, but it's possible that some device won't work OK with that *new* feature.

![File input demo](http://i.imgur.com/5Fbaxfn.gif)

## Tips for Video (HTML5) inside WebView

To work with some html5 video player inside your Webview, I recommend you to set the android:hardwareAccelerated="true" in your AndroidManifest.xml file.

More info here: http://stackoverflow.com/questions/17259636/enabling-html5-video-playback-in-android-webview

## License
MIT
