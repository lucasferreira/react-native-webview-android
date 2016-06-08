/**
 * @providesModule WebViewAndroid
 */
'use strict';

try {
  var React = require('react');
} catch(ex) {
  var React = require('react-native');
}

var { StyleSheet } = require('react-native');

var UnimplementedView = React.createClass({
  setNativeProps: function() {
    // Do nothing.
  },
  render: function() {
    // Workaround require cycle from requireNativeComponent
    var { View } = require('react-native');

    return (
      <View style={[styles.unimplementedView, this.props.style]}>
        {this.props.children}
      </View>
    );
  },
});

var styles = StyleSheet.create({
  unimplementedView: {
    borderWidth: 1,
    borderColor: 'red',
    alignSelf: 'flex-start',
  }
});

module.exports = UnimplementedView;
