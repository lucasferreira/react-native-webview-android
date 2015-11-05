# react-native-webview-android
Simple React Native Android module to use Android's WebView inside your app.
This module will be util until the official RN support aren't released.

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

* Register Module (in MainActivity.java)

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
      .addPackage(new RNWebViewPackage()) // <------ add this line to yout MainActivity class
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

var WebViewAndroidExample = React.createClass({
    onNavigationStateChange: function(event) {
        console.log(event);
    },
    render: function() {
        var SITE_URL = "https://www.google.com";

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
    }
});

var styles = StyleSheet.create({
    containerWebView: {
        flex: 1,
    }
});
```

## License
MIT
