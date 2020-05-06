import React, {Component, useState, useEffect} from 'react';
import { ScrollView, StyleSheet, TouchableOpacity, View, Text, TextInput, StatusBar, Animated, Easing } from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';

import 'react-native-gesture-handler';

import Global from '../Global'
import patients from '../Data'
import mockData from '../MockData'

const demoFlag = true
var processedData = {
  "ID": 0,
  "LOS": 0,
  "blood": 0,
  "circulatory": 0,
  "congenital": 0,
  "digestive": 0,
  "endocrine": 0,
  "genitourinary": 0,
  "infectious": 0,
  "injury": 0,
  "mental": 0,
  "misc": 0,
  "muscular": 0,
  "neoplasms": 0,
  "nervous": 0,
  "pregnancy": 0,
  "prenatal": 0,
  "respiratory": 0,
  "skin": 0,
  "GENDER": 0,
  "ICU": 0,
  "NICU": 0,
  "ADM_ELECTIVE": 0,
  "ADM_EMERGENCY": 0,
  "ADM_NEWBORN": 0,
  "ADM_URGENT": 0,
  "INS_Government": 0,
  "INS_Medicaid": 0,
  "INS_Medicare": 0,
  "INS_Private": 0,
  "INS_Self Pay": 0,
  "REL_NOT SPECIFIED": 0,
  "REL_RELIGIOUS": 0,
  "REL_UNOBTAINABLE": 0,
  "ETH_ASIAN": 0,
  "ETH_BLACK/AFRICAN AMERICAN": 0,
  "ETH_HISPANIC/LATINO": 0,
  "ETH_OTHER/UNKNOWN": 0,
  "ETH_WHITE": 0,
  "AGE_300": 0,
  "AGE_middle_adult": 0,
  "AGE_newborn": 0,
  "AGE_senior": 0,
  "AGE_young_adult": 0,
  "MAR_DIVORCED": 0,
  "MAR_LIFE PARTNER": 0,
  "MAR_MARRIED": 0,
  "MAR_SEPARATED": 0,
  "MAR_SINGLE": 0,
  "MAR_UNKNOWN (DEFAULT)": 0,
  "MAR_WIDOWED": 0,
}

export default class Home extends Component {
  constructor(props) {
    super(props)
    this.props.navigation.setOptions({
      headerRight: () => (
        <TouchableOpacity
          style={styles.searchButton}
          onPress={() => this.toggleSearchbar()}>
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
      scaleAnim: new Animated.Value(1),
      loading: true,
      patientId: 231677,
      patientData: null,
      conditionData: null,
      encounterData: null,
      losPrediction: 0.0,
      metadata: {}
    }
  }

  componentDidMount() {
    this.fetchData()
  }

  processData() {
    const {patientId, patientData, encounterData, conditionData} = this.state

    var metadata = {}

    //PATIENT DATA
    processedData.ID = patientId
    if(patientData.gender.toLowerCase() == "female") {
      processedData.GENDER = 1
    }
    var age = (new Date().getFullYear()) - (new Date(patientData.birthDate)).getFullYear()
    metadata.age = age
    switch(true) {
      case (age < 14): processedData.ADM_NEWBORN = 1; break;
      case (age < 37): processedData.AGE_young_adult = 1; break;
      case (age < 57): processedData.AGE_middle_adult = 1; break;
      case (age < 101): processedData.AGE_senior = 1; break;
    }
    var maritalStatus = patientData.maritalStatus.coding[0].code.toLowerCase()
    metadata.maritalStatus = maritalStatus
    switch(maritalStatus) {
      case "married": processedData.MAR_MARRIED = 1; break;
      case "divorced": processedData.MAR_DIVORCED = 1; break;
      case "single": processedData.MAR_SINGLE = 1; break;
    }
    var religion = patientData.extension.find((o) => o.url.includes("patient-religion"))
    metadata.religion = religion.valueCodeableConcept.text.toLowerCase()
    if(religion) {
      processedData.REL_RELIGIOUS = 1
    } else {
      processedData["REL_NOT SPECIFIED"] = 1
    }
    var ethnicity = patientData.extension.find((o) => o.url.includes("us-core-ethnicity"))
    metadata.ethnicity = ethnicity.valueCodeableConcept.text.toLowerCase()
    switch(ethnicity.valueCodeableConcept.text.toLowerCase()) {
      case "white": processedData.ETH_WHITE = 1; break;
      default: processedData["ETH_OTHER/UNKNOWN"] = 1
    }

    // ENCOUNTER DATA
    var admission = encounterData[0].resource.type[0].coding[0].code.toLowerCase()
    metadata.admission = admission
    switch(admission) {
      case "emergency": processedData.ADM_EMERGENCY = 1; break;
      case "elective": processedData.ADM_ELECTIVE = 1; break;
      case "newborn": processedData.ADM_NEWBORN = 1; break;
      case "urgent": processedData.ADM_URGENT = 1; break;
    }
    var insurance = encounterData[0].resource.hospitalization.extension.find((o) => o.url.includes("insurance"))
    metadata.insurance = insurance.valueCodeableConcept.text.toLowerCase()
    switch(insurance.valueCodeableConcept.text.toLowerCase()) {
      case "private": processedData.INS_Private = 1; break;
      case "government": processedData.INS_Government = 1; break;
      case "medicaid": processedData.INS_Medicaid = 1; break;
      case "medicare": processedData.INS_Medicare = 1; break;
      case "selfpay": processedData["INS_Self Pay"] = 1; break;
    }
    var icu = encounterData[0].resource.hospitalization.extension.find((o) => o.url.includes("first_careunit"))
    switch(icu.valueCodeableConcept.text.toLowerCase()) {
      case "icu": processedData.ICU = 1; break;
      case "nicu": processedData.NICU = 1; break;
    }

    // CONDITION DATA
    console.log(conditionData[0])

    var icd = parseInt(conditionData[0].resource.code.coding[0].code, 10)
    switch(true) {
      case (icd.isBetween(1, 140)): processedData.infectious = 1; break;
      case (icd.isBetween(140, 240)): processedData.neoplasms = 1; break;
      case (icd.isBetween(240, 280)): processedData.endocrine = 1; break;
      case (icd.isBetween(280, 290)): processedData.blood = 1; break;
      case (icd.isBetween(290, 320)): processedData.mental = 1; break;
      case (icd.isBetween(320, 390)): processedData.nervous = 1; break;
      case (icd.isBetween(390, 460)): processedData.circulatory = 1; break;
      case (icd.isBetween(460, 520)): processedData.respiratory = 1; break;
      case (icd.isBetween(520, 580)): processedData.digestive = 1; break;
      case (icd.isBetween(580, 630)): processedData.genitourinary = 1; break;
      case (icd.isBetween(630, 680)): processedData.pregnancy = 1; break;
      case (icd.isBetween(680, 710)): processedData.skin = 1; break;
      case (icd.isBetween(710, 740)): processedData.muscular = 1; break;
      case (icd.isBetween(740, 760)): processedData.congenital = 1; break;
      case (icd.isBetween(760, 780)): processedData.prenatal = 1; break;
      case (icd.isBetween(780, 800)): processedData.misc = 1; break;
      case (icd.isBetween(800, 1000)): processedData.injury = 1; break;
      case (icd.isBetween(1000, 2000)): processedData.misc = 1; break;
    }

    return metadata
  }

  async fetchData() {
    const {patientId} = this.state

    try {
      this.setState({loading: true})
      const patientApiData = await sendRequest('Patient', patientId);
      const conditionApiData = await processRequest('Condition', patientId)
      const encounterApiData = await processRequest("Encounter", patientId)
      console.log("Got Data")


      this.setState({patientData: patientApiData, conditionData: conditionApiData, 
        encounterData: encounterApiData}, () => {
          var savedMetadata = this.processData()
          console.log(processedData)
          getLOS(patientId).then((losData) => {
            console.log(losData.los)
            this.setState({losPrediction: losData.los, loading: false, metadata: savedMetadata})
            console.log("Got LOS")
          })
      });


    } catch (error) {
      console.log("Error fetching data", error)
    }
  }

  toggleSearchbar() {
    const {searching} = this.state

    if(searching) {
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
            this.setState({searching: !searching})
          })
        }
      )
    } else {
      //show
      this.setState({searching: !searching, searchAnim: new Animated.Value(0)}, () => {
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
        ],
      }}>
        <TextInput 
          style={styles.searchbar}
          placeholder="Search for Patient ID..."
          onSubmitEditing={(event) => {
            console.log(event.nativeEvent.text)
            this.setState({patientId: event.nativeEvent.text}, () => {
              this.fetchData()
              this.toggleSearchbar()
            })
          }}
        />
      </Animated.View>
  }

  render() {
    const {loading, patientId, patientData, conditionData, encounterData, metadata} = this.state

    if(loading) {
      return (
        <Text style={styles.loadingText}>Loading</Text>
      )
    }

    var preX = "No"
    if(conditionData != null) {
      preX = "Yes"
    }

    return (
      <ScrollView style={{flex: 1, backgroundColor: '#ededed'}}>
        <StatusBar backgroundColor="#4d89e8" />
        {this.state.searching ? this.renderSearchbar() : null}
        <View style={styles.cards}>
          <View style={styles.info}>
            <Text style={styles.infoText}>Personal Information</Text>
            <View style={styles.patientInfo}>
              <View>
                <Text style={styles.patientHeaderText}>Patient ID</Text>
                <Text style={styles.patientText}>{patientId}</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Age</Text>
                <Text style={styles.patientText}>{metadata.age}</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Gender</Text>
                <Text style={styles.patientText}>{patientData.gender.toProperCase()}</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Ethnicity</Text>
                <Text style={styles.patientText}>{metadata.ethnicity.toProperCase()}</Text>
              </View>
            </View>
          </View>
          <View style={styles.details}>
            <View style={styles.patientInfo}>
              <View>
                <Text style={styles.patientHeaderText}>Insurance</Text>
                <Text style={styles.patientText}>{metadata.insurance.toProperCase()}</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>Marital Status</Text>
                <Text style={styles.patientText}>{metadata.maritalStatus.toProperCase()}</Text>
              </View>
              <View>
                <Text style={styles.patientHeaderText}>PreX Cond</Text>
                <Text style={styles.patientText}>{preX}</Text>
              </View>
            </View>
          </View>
          <View style={styles.los}>
            <View style={styles.losInfo}>
              <Text style={styles.losHeader}>Length of Stay Prediction</Text>
              <View style={styles.losValue}>
                <Text style={styles.losText}>{this.state.losPrediction}</Text>
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
                  <Text style={styles.modelText}>-.0</Text>
                  <Text style={styles.modelText}>Days</Text>
                </View>
              </View>
              <View>
                <Text style={styles.modelHeader}>Model 2</Text>
                <View style={styles.modelValue}>
                  <Text style={styles.modelText}>-.0</Text>
                  <Text style={styles.modelText}>Days</Text>
                </View>
              </View>
              <View>
                <Text style={styles.modelHeader}>Model 3</Text>
                <View style={styles.modelValue}>
                  <Text style={styles.modelText}>-.0</Text>
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

async function sendRequest(endpoint, patientId) {
  if(demoFlag) {
    var data = mockData.patientData.find((o) => o.id == patientId)
    // console.log(data)
    return data
  }
  try {
    let response = await fetch(Global.appURL +'/' + endpoint + '/' + patientId);
    let responseJson = await response.json();
    console.log(responseJson)

    return responseJson;
  } catch (error) {
    console.error(error)
  }
}

async function processRequest(endpoint, patientId) {
  if(demoFlag) {
    switch (endpoint) {
      case 'Encounter':
        var data = mockData.encounterData.find((o) => o.entry[0].resource.id == patientId)
        // console.log(data)
        return data.entry
      case 'Condition':
        var data = mockData.conditionData.find((o) => o.entry[0].resource.id == patientId)
        // console.log(data)
        return data.entry
    }
  } 

  try {
    let response = await fetch(Global.appURL +'/' + endpoint + '?subject=' + patientId);
    let responseJson = await response.json();
    console.log(responseJson.entry)

    return responseJson.entry;
  } catch (error) {
    console.error(error)
  }
}

async function getLOS(patientId) {
  console.log("Getting LOS")
  var data = {}
  if (demoFlag) {
    data = getSampleData(patientId)
  } else {
    //parse from stuff
    data = processedData
  }

  var packet = JSON.stringify(data)
  console.log("My packet")
  console.log(packet)
  // return 2.1
  try {
    let response = await fetch(Global.flaskURL, {
        method: 'POST',
        headers: new Headers({
          'Content-Type': 'application/json',
        }),
        body: packet
    })
    var los = await response.json()
    console.log(los)
    return los
  } catch (error) {
    console.error(error)
    return "-.-"
  }
}

function getSampleData(patientId) {
  var samples = patients.losData.find((o) => o.ID == patientId)
  console.log(samples.ID)
  return samples
}

String.prototype.toProperCase = function() {
  return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};

Number.prototype.isBetween = function(start, end) {
  return ((this-start)*(this-end) <= 0)
}


const styles = StyleSheet.create({
  loadingText: {
    textAlign: 'center',
    fontWeight: 'bold',
    fontSize: 18,
  },
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
  },
  losText: {
    fontSize: 24,
    fontWeight: 'bold',
    alignSelf: 'stretch',
    textAlign: 'center',
    color: "#4d89e8",
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
