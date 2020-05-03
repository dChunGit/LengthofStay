const patients = {
  patientData: {
    "resourceType": "Patient",
    "id": "231677",
    "meta": {
      "versionId": "1",
      "lastUpdated": "2020-05-02T19:43:18.705-04:00"
    },
    "extension": [
      {
        "url": "http://hl7.org/fhir/StructureDefinition/patient-religion",
        "valueCodeableConcept": {
          "text": "CATHOLIC"
        }
      },
      {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
        "valueCodeableConcept": {
          "text": "WHITE"
        }
      }
    ],
    "identifier": [
      {
        "value": "10019"
      }
    ],
    "gender": "male",
    "birthDate": "2114-06-20",
    "deceasedDateTime": "2163-05-15",
    "maritalStatus": {
      "coding": [
        {
          "system": "http://mimic.physionet.org/fhir/MaritalStatus",
          "code": "DIVORCED"
        }
      ]
    }
  },
  encounterData: {
    "resourceType": "Bundle",
    "id": "5fb32e25-7d2a-430d-a855-50a7ba95dcc1",
    "meta": {
      "lastUpdated": "2020-05-02T19:49:39.132-04:00"
    },
    "type": "searchset",
    "total": 1,
    "link": [
      {
        "relation": "self",
        "url": "https://r3.smarthealthit.org/Encounter?subject=231677"
      }
    ],
    "entry": [
      {
        "fullUrl": "https://r3.smarthealthit.org/Encounter/231679",
        "resource": {
          "resourceType": "Encounter",
          "id": "231679",
          "meta": {
            "versionId": "1",
            "lastUpdated": "2020-05-02T19:45:40.240-04:00"
          },
          "type": [
            {
              "coding": [
                {
                  "system": "http://mimic.physionet.org/fhir/AdmissionType",
                  "code": "EMERGENCY"
                }
              ]
            }
          ],
          "subject": {
            "reference": "Patient/231677"
          },
          "period": {
            "start": "2163-05-14T20:43:00",
            "end": "2163-05-15T12:00:00"
          },
          "diagnosis": [
            {
              "condition": {
                "reference": "Condition/231678"
              },
              "rank": 1
            }
          ],
          "hospitalization": {
            "admitSource": {
              "coding": [
                {
                  "system": "http://mimic.physionet.org/fhir/AdmitSource",
                  "code": "TRANSFER FROM HOSP/EXTRAM"
                }
              ]
            },
            "dischargeDisposition": {
              "coding": [
                {
                  "system": "http://mimic.physionet.org/fhir/DischargeLocation",
                  "code": "DEAD/EXPIRED"
                },
                {
                  "system": "http://terminology.hl7.org/CodeSystem/discharge-disposition",
                  "code": "exp"
                }
              ]
            }
          }
        },
        "search": {
          "mode": "match"
        }
      }
    ]
  },
  conditionData: {
    "resourceType": "Bundle",
    "id": "70490ba4-72fb-4c0d-a1f6-7aeb98228f85",
    "meta": {
      "lastUpdated": "2020-05-02T19:50:09.723-04:00"
    },
    "type": "searchset",
    "total": 1,
    "link": [
      {
        "relation": "self",
        "url": "https://r3.smarthealthit.org/Condition?subject=231677"
      }
    ],
    "entry": [
      {
        "fullUrl": "https://r3.smarthealthit.org/Condition/231678",
        "resource": {
          "resourceType": "Condition",
          "id": "231678",
          "meta": {
            "versionId": "1",
            "lastUpdated": "2020-05-02T19:44:49.904-04:00"
          },
          "code": {
            "coding": [
              {
                "system": "http://hl7.org/fhir/sid/icd-9-cm",
                "code": "5770"
              }
            ]
          },
          "subject": {
            "reference": "Patient/231677"
          }
        },
        "search": {
          "mode": "match"
        }
      }
    ]
  },
  losData: [
    {
      "ID": 0,
      "LOS": 1.14444444444444,
      "blood": 0,
      "circulatory": 1,
      "congenital": 0,
      "digestive": 0,
      "endocrine": 0,
      "genitourinary": 0,
      "infectious": 0,
      "injury": 4,
      "mental": 1,
      "misc": 0,
      "muscular": 0,
      "neoplasms": 0,
      "nervous": 1,
      "pregnancy": 0,
      "prenatal": 0,
      "respiratory": 0,
      "skin": 0,
      "GENDER": 1,
      "ICU": 1,
      "NICU": 0,
      "ADM_ELECTIVE": 0,
      "ADM_EMERGENCY": 1,
      "ADM_NEWBORN": 0,
      "ADM_URGENT": 0,
      "INS_Government": 0,
      "INS_Medicaid": 0,
      "INS_Medicare": 0,
      "INS_Private": 1,
      "INS_Self Pay": 0,
      "REL_NOT SPECIFIED": 0,
      "REL_RELIGIOUS": 0,
      "REL_UNOBTAINABLE": 1,
      "ETH_ASIAN": 0,
      "ETH_BLACK/AFRICAN AMERICAN": 0,
      "ETH_HISPANIC/LATINO": 0,
      "ETH_OTHER/UNKNOWN": 0,
      "ETH_WHITE": 1,
      "AGE_300": 0,
      "AGE_middle_adult": 0,
      "AGE_newborn": 0,
      "AGE_senior": 1,
      "AGE_young_adult": 0,
      "MAR_DIVORCED": 0,
      "MAR_LIFE PARTNER": 0,
      "MAR_MARRIED": 1,
      "MAR_SEPARATED": 0,
      "MAR_SINGLE": 0,
      "MAR_UNKNOWN (DEFAULT)": 0,
      "MAR_WIDOWED": 0
    },
    {
      "ID": 1,
      "LOS": 5.49652777777778,
      "blood": 0,
      "circulatory": 4,
      "congenital": 0,
      "digestive": 0,
      "endocrine": 1,
      "genitourinary": 1,
      "infectious": 0,
      "injury": 1,
      "mental": 0,
      "misc": 0,
      "muscular": 0,
      "neoplasms": 0,
      "nervous": 1,
      "pregnancy": 0,
      "prenatal": 0,
      "respiratory": 0,
      "skin": 0,
      "GENDER": 0,
      "ICU": 1,
      "NICU": 0,
      "ADM_ELECTIVE": 1,
      "ADM_EMERGENCY": 0,
      "ADM_NEWBORN": 0,
      "ADM_URGENT": 0,
      "INS_Government": 0,
      "INS_Medicaid": 0,
      "INS_Medicare": 1,
      "INS_Private": 0,
      "INS_Self Pay": 0,
      "REL_NOT SPECIFIED": 0,
      "REL_RELIGIOUS": 1,
      "REL_UNOBTAINABLE": 0,
      "ETH_ASIAN": 0,
      "ETH_BLACK/AFRICAN AMERICAN": 0,
      "ETH_HISPANIC/LATINO": 0,
      "ETH_OTHER/UNKNOWN": 0,
      "ETH_WHITE": 1,
      "AGE_300": 0,
      "AGE_middle_adult": 0,
      "AGE_newborn": 0,
      "AGE_senior": 1,
      "AGE_young_adult": 0,
      "MAR_DIVORCED": 0,
      "MAR_LIFE PARTNER": 0,
      "MAR_MARRIED": 1,
      "MAR_SEPARATED": 0,
      "MAR_SINGLE": 0,
      "MAR_UNKNOWN (DEFAULT)": 0,
      "MAR_WIDOWED": 0
    },
    {
      "ID": 231677,
      "LOS": 6.76805555555556,
      "blood": 0,
      "circulatory": 2,
      "congenital": 0,
      "digestive": 0,
      "endocrine": 2,
      "genitourinary": 0,
      "infectious": 0,
      "injury": 3,
      "mental": 0,
      "misc": 0,
      "muscular": 0,
      "neoplasms": 1,
      "nervous": 1,
      "pregnancy": 0,
      "prenatal": 1,
      "respiratory": 0,
      "skin": 0,
      "GENDER": 0,
      "ICU": 1,
      "NICU": 0,
      "ADM_ELECTIVE": 0,
      "ADM_EMERGENCY": 1,
      "ADM_NEWBORN": 0,
      "ADM_URGENT": 0,
      "INS_Government": 0,
      "INS_Medicaid": 0,
      "INS_Medicare": 1,
      "INS_Private": 0,
      "INS_Self Pay": 0,
      "REL_NOT SPECIFIED": 0,
      "REL_RELIGIOUS": 1,
      "REL_UNOBTAINABLE": 0,
      "ETH_ASIAN": 0,
      "ETH_BLACK/AFRICAN AMERICAN": 0,
      "ETH_HISPANIC/LATINO": 0,
      "ETH_OTHER/UNKNOWN": 0,
      "ETH_WHITE": 1,
      "AGE_300": 0,
      "AGE_middle_adult": 0,
      "AGE_newborn": 0,
      "AGE_senior": 1,
      "AGE_young_adult": 0,
      "MAR_DIVORCED": 0,
      "MAR_LIFE PARTNER": 0,
      "MAR_MARRIED": 1,
      "MAR_SEPARATED": 0,
      "MAR_SINGLE": 0,
      "MAR_UNKNOWN (DEFAULT)": 0,
      "MAR_WIDOWED": 0
    },
    {
      "ID": 3,
      "LOS": 2.85694444444444,
      "blood": 0,
      "circulatory": 2,
      "congenital": 0,
      "digestive": 1,
      "endocrine": 1,
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
      "ICU": 1,
      "NICU": 0,
      "ADM_ELECTIVE": 0,
      "ADM_EMERGENCY": 1,
      "ADM_NEWBORN": 0,
      "ADM_URGENT": 0,
      "INS_Government": 0,
      "INS_Medicaid": 0,
      "INS_Medicare": 0,
      "INS_Private": 1,
      "INS_Self Pay": 0,
      "REL_NOT SPECIFIED": 0,
      "REL_RELIGIOUS": 1,
      "REL_UNOBTAINABLE": 0,
      "ETH_ASIAN": 0,
      "ETH_BLACK/AFRICAN AMERICAN": 0,
      "ETH_HISPANIC/LATINO": 0,
      "ETH_OTHER/UNKNOWN": 0,
      "ETH_WHITE": 1,
      "AGE_300": 0,
      "AGE_middle_adult": 1,
      "AGE_newborn": 0,
      "AGE_senior": 0,
      "AGE_young_adult": 0,
      "MAR_DIVORCED": 0,
      "MAR_LIFE PARTNER": 0,
      "MAR_MARRIED": 0,
      "MAR_SEPARATED": 0,
      "MAR_SINGLE": 1,
      "MAR_UNKNOWN (DEFAULT)": 0,
      "MAR_WIDOWED": 0
    },
    {
      "ID": 4,
      "LOS": 3.53402777777778,
      "blood": 0,
      "circulatory": 3,
      "congenital": 0,
      "digestive": 0,
      "endocrine": 1,
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
      "ICU": 1,
      "NICU": 0,
      "ADM_ELECTIVE": 0,
      "ADM_EMERGENCY": 1,
      "ADM_NEWBORN": 0,
      "ADM_URGENT": 0,
      "INS_Government": 0,
      "INS_Medicaid": 0,
      "INS_Medicare": 0,
      "INS_Private": 1,
      "INS_Self Pay": 0,
      "REL_NOT SPECIFIED": 0,
      "REL_RELIGIOUS": 0,
      "REL_UNOBTAINABLE": 1,
      "ETH_ASIAN": 0,
      "ETH_BLACK/AFRICAN AMERICAN": 0,
      "ETH_HISPANIC/LATINO": 0,
      "ETH_OTHER/UNKNOWN": 0,
      "ETH_WHITE": 1,
      "AGE_300": 0,
      "AGE_middle_adult": 0,
      "AGE_newborn": 0,
      "AGE_senior": 1,
      "AGE_young_adult": 0,
      "MAR_DIVORCED": 0,
      "MAR_LIFE PARTNER": 0,
      "MAR_MARRIED": 1,
      "MAR_SEPARATED": 0,
      "MAR_SINGLE": 0,
      "MAR_UNKNOWN (DEFAULT)": 0,
      "MAR_WIDOWED": 0
    }
  ]
}

export default patients