forked from [react-native-webview-android](https://github.com/lucasferreira/react-native-webview-android)

背景
- 官方的 WebView 不支持 `<input type="file"/>`。这不是官方的错，选择相册要Android去实现。
- `react-native-web-android` 支持了 RN 和 webView 通信，但使用方法和官方有区别。比如在网页内官方是 window.postMessage，它是 window.webView.postMessage。比如在RN内 e.nativeEvent.data，它是 e.message。
- `react-native-web-android` 不支持定位，也没的配置。
- `react-native-web-android` 的文档对 `android/setting.gradle` 的代码添加是错误的，坑爹。

估造了个轮子，支持
- input type file
- RN和Web通信
- 定位

### Installation

```bash
npm install react-native-gm-webview
```

### Add it to your android project

* In `android/setting.gradle`

```gradle
...
include ':RNWebView'
project(':RNWebView').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-gm-webview/android')
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
