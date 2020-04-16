import React, {Component, useState, useEffect} from 'react';
import { ScrollView, StyleSheet, TouchableOpacity, View, Text, TextInput, StatusBar, Animated, Easing } from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';

import 'react-native-gesture-handler';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

export default class Home extends Component {
  constructor(props) {
    super(props)
    this.props.navigation.setOptions({
      headerRight: () => (
        <TouchableOpacity
          style={styles.searchButton}
          onPress={() => this.toggleSearchbar(this.state.searching)}>
          <Icon
            name="search"
            size={24}
            color="#fff"
          />
        </TouchableOpacity>
      ),
    })
    this.state = {
      searching: false,
      searchAnim: new Animated.Value(1),
      scaleAnim: new Animated.Value(1)
    }
  }

  toggleSearchbar(search) {
    // this.setState({searching: !search})
    if(search) {
      //hide
      this.setState({searchAnim: new Animated.Value(1)}, () => {
          Animated.timing(
            this.state.searchAnim,
            {
              toValue: 0,
              duration: 500,
              useNativeDriver: true,
              easing: Easing.ease
            }
          ).start(({finished}) => {
            this.setState({searching: !search})
          })
        }
      )
    } else {
      //show
      this.setState({searching: !search, searchAnim: new Animated.Value(0)}, () => {
        Animated.timing(
          this.state.searchAnim,
          {
            toValue: 1,
            duration: 500,
            useNativeDriver: true,
            easing: Easing.ease
          }
        ).start()
      }
    )
    }
  }

  renderSearchbar() {
    let {searchAnim} = this.state

    return <Animated.View style={{
        transform: [
          {
            translateY: searchAnim.interpolate({
              inputRange: [0, 1],
              outputRange: [-100, 0]
            }),
          },
          // {
          //   scale: searchAnim.interpolate({
          //     inputRange: [0, 1],
          //     outputRange: [0, 1]
          //   })
          // }
        ],
      }}>
        <TextInput 
          style={styles.searchbar}
          placeholder="Search for Patient ID..."
          onChangeText={(id) => console.log(id)}
          // value={this.state.filterText}
        />
      </Animated.View>
  }

  render() {
    return (
      <ScrollView style={{flex: 1, backgroundColor: '#ededed'}}>
        <StatusBar backgroundColor="#4d89e8" />
        {this.state.searching ? this.renderSearchbar() : null}
        <View style={styles.cards}>
          <View style={styles.info}>
            <Text style={styles.infoText}>Personal Information</Text>
            <View style={styles.patientInfo}>
              <View>
                <Text style={styles.patientHeaderText}>Name</Text>
                <Text style={styles.patientText}>Bob Smith</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Patient ID</Text>
                <Text style={styles.patientText}>1233421</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Ethnicity</Text>
                <Text style={styles.patientText}>Asian</Text>
              </View>
            </View>
          </View>
          <View style={styles.details}>
            <View style={styles.patientInfo}>
              <View>
                <Text style={styles.patientHeaderText}>Age</Text>
                <Text style={styles.patientText}>42</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Gender</Text>
                <Text style={styles.patientText}>Male</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Insurance</Text>
                <Text style={styles.patientText}>Private</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>PreX Cond</Text>
                <Text style={styles.patientText}>Yes</Text>
              </View>
            </View>
          </View>
          <View style={styles.los}>
            <View style={styles.losInfo}>
              <Text style={styles.losHeader}>Length of Stay Prediction</Text>
              <View style={styles.losValue}>
                <Text style={styles.losText}>7.1</Text>
                <Text style={styles.losText}>Days</Text>
              </View>
            </View>
          </View>
          <View style={styles.los}>
            <Text style={styles.infoText}>Model Predictions</Text>
            <View style={styles.modelInfo}>
              <View>
                <Text style={styles.modelHeader}>Model 1</Text>
                <View style={styles.modelValue}>
                  <Text style={styles.modelText}>7.1</Text>
                  <Text style={styles.modelText}>Days</Text>
                </View>
              </View>
              <View>
                <Text style={styles.modelHeader}>Model 2</Text>
                <View style={styles.modelValue}>
                  <Text style={styles.modelText}>7.1</Text>
                  <Text style={styles.modelText}>Days</Text>
                </View>
              </View>
              <View>
                <Text style={styles.modelHeader}>Model 3</Text>
                <View style={styles.modelValue}>
                  <Text style={styles.modelText}>7.1</Text>
                  <Text style={styles.modelText}>Days</Text>
                </View>
              </View>
            </View>
          </View>
        </View>
      </ScrollView>
    );
  }
};

const styles = StyleSheet.create({
  searchButton: {
    marginEnd: 8,
    marginStart: 8,
    padding: 8
  },
  searchbar: {
    marginBottom: 10,
    paddingStart: 10,
    paddingEnd: 10,
    backgroundColor: '#eff2f7',
    elevation: 2,
    borderRadius: 4
  },
  cards: {
    margin: 12,
  },
  los: {
    padding: 8,
    marginTop: 10,
    marginBottom: 10,
    backgroundColor: '#FFF',
    elevation: 4,
    borderRadius: 4,
    elevation: 4
  },
  info: {
    padding: 8,
    backgroundColor: '#FFF',
    elevation: 4,
    borderTopStartRadius: 4, 
    borderTopEndRadius: 4
  },
  details: {
    padding: 8,
    backgroundColor: '#eff2f7',
    elevation: 4,
    marginBottom: 10,
    borderBottomStartRadius: 4, 
    borderBottomEndRadius: 4
  },
  modelInfo: {
    padding: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  modelHeader: {
    fontSize: 18,
    color: '#5e5e5e'
  },
  modelValue: {
    marginTop: 8,
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center'
  },
  modelText: {
    fontSize: 24,
  },
  losInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  losHeader: {
    flex: 2,
    fontSize: 24,
    margin: 12
  },
  losValue: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center'
  },
  losText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: "#4d89e8"
  },
  infoText: {
    paddingStart:2,
    fontSize: 18,
    fontWeight: 'bold'
  },
  patientInfo: {
    padding: 12,
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  patientHeaderText: {
    fontSize: 16,
  },
  patientText: {
    color: '#5e5e5e'
  }
});
