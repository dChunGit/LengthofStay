import React, {Component} from 'react';
import { Dimensions, StyleSheet, TouchableOpacity, View, Text, TextInput, StatusBar, Image } from 'react-native';

import 'react-native-gesture-handler';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

export default class Details extends Component {
  constructor(props) {
    super(props)
  }

  render() {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
        <Text>Details</Text>
      </View>
    );
  }
};

const styles = StyleSheet.create({
  
});
