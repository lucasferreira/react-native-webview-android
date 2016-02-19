# react-native-webview-android
Simple React Native Android module to use Android's WebView inside your app.

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
project(':RNWebView').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-webview-android')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
  ...
  compile project(':RNWebView')
}
```

* Register Module - RN >= 0.18 (in MainActivity.java)

```java
import com.burnweb.rnwebview.RNWebViewPackage;  // <--- import

public class MainActivity extends ReactActivity {
  ......

  @Override
  protected List<ReactPackage> getPackages() {
    return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new RNWebViewPackage()); // <------ add this line to your MainActivity class
  }

  ......

}
```

* Register Module - RN <= 0.17 (in MainActivity.java)

```java
import com.burnweb.rnwebview.RNWebViewPackage;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new RNWebViewPackage()) // <------ add this line to your MainActivity class
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "AndroidRNSample", null);

    setContentView(mReactRootView);
  }

  ......

}
```

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
        status: 'No Page Loaded',
        backButtonEnabled: false,
        forwardButtonEnabled: false,
        loading: true,
      };
    },
    goBack: function() {
      this.refs.webViewAndroidSample.goBack(); // you can use this callbacks to control webview
    },
    goForward: function() {
      this.refs.webViewAndroidSample.goForward();
    },
    reload: function() {
      this.refs.webViewAndroidSample.reload();
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
    render: function() {
      return (
        <WebViewAndroid
          ref="webViewAndroidSample"
          javaScriptEnabled={true}
          geolocationEnabled={false}
          builtInZoomControls={false}
          onNavigationStateChange={this.onNavigationStateChange}
          url={SITE_URL}
          style={styles.containerWebView} />
      );

      // other attributes: html(string), htmlCharset(string), baseUrl(string), injectedJavaScript(string), disableCookies(bool), disablePlugins(bool), userAgent(string)
    }
});

var styles = StyleSheet.create({
  containerWebView: {
    flex: 1,
  }
});
```

## Tips for Video (HTML5) inside WebView

To work with some html5 video player inside your Webview, I recommend you to set the android:hardwareAccelerated="true" in your AndroidManifest.xml file.

More info here: http://stackoverflow.com/questions/17259636/enabling-html5-video-playback-in-android-webview

## License
MIT
