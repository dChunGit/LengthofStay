import React, {Component} from 'react';
import { View, Text, Button } from 'react-native';
import 'react-native-gesture-handler';
import { NavigationContainer, StackActions } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

import Home from './screens/Home'
import Details from './screens/Details'


function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator 
        screenOptions={{
          headerStyle: {
            backgroundColor: '#4d89e8',
          },
          headerTintColor: '#fff',
        }}
        initialRouteName="Home"
      >
        <Stack.Screen name = "Home" component={Home} options={{ title: 'Patient View' }} />
        <Stack.Screen name = "Details" component={Details} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const Stack = createStackNavigator();
export default App;
