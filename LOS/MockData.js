const mockData = {
  patientData: [
    {
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
      "gender": "female",
      "birthDate": "1977-06-20",
      "maritalStatus": {
        "coding": [
          {
            "system": "http://mimic.physionet.org/fhir/MaritalStatus",
            "code": "MARRIED"
          }
        ]
      }
    },
    {
      "resourceType": "Patient",
      "id": "231678",
      "meta": {
        "versionId": "1",
        "lastUpdated": "2020-05-02T19:43:18.705-04:00"
      },
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/patient-religion",
          "valueCodeableConcept": {
            "text": "PROTESTANT"
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
      "birthDate": "1969-02-13",
      "maritalStatus": {
        "coding": [
          {
            "system": "http://mimic.physionet.org/fhir/MaritalStatus",
            "code": "MARRIED"
          }
        ]
      }
    },
    {
      "resourceType": "Patient",
      "id": "231679",
      "meta": {
        "versionId": "1",
        "lastUpdated": "2020-05-02T19:43:18.705-04:00"
      },
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/patient-religion",
          "valueCodeableConcept": {
            "text": "JEWISH"
          }
        },
        {
          "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
          "valueCodeableConcept": {
            "text": "OTHER"
          }
        }
      ],
      "identifier": [
        {
          "value": "10019"
        }
      ],
      "gender": "male",
      "birthDate": "1995-02-21",
      "maritalStatus": {
        "coding": [
          {
            "system": "http://mimic.physionet.org/fhir/MaritalStatus",
            "code": "MARRIED"
          }
        ]
      }
    },
  ],
  encounterData: [
    {
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
            "id": "231677",
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
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/first_careunit",
                  "valueCodeableConcept": {
                    "text": "ICU"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/insurance",
                  "valueCodeableConcept": {
                    "text": "PRIVATE"
                  }
                }
              ],
            }
          },
          "search": {
            "mode": "match"
          }
        }
      ]
    },
    {
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
            "id": "231678",
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
              "reference": "Patient/231678"
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
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/first_careunit",
                  "valueCodeableConcept": {
                    "text": "ICU"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/insurance",
                  "valueCodeableConcept": {
                    "text": "PRIVATE"
                  }
                }
              ],
            }
          },
          "search": {
            "mode": "match"
          }
        }
      ]
    },
    {
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
                    "code": "ELECTIVE"
                  }
                ]
              }
            ],
            "subject": {
              "reference": "Patient/231679"
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
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/first_careunit",
                  "valueCodeableConcept": {
                    "text": "ICU"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/insurance",
                  "valueCodeableConcept": {
                    "text": "PRIVATE"
                  }
                }
              ],
            }
          },
          "search": {
            "mode": "match"
          }
        }
      ]
    },
  ],
  conditionData: [
    {
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
            "id": "231677",
            "meta": {
              "versionId": "1",
              "lastUpdated": "2020-05-02T19:44:49.904-04:00"
            },
            "code": {
              "coding": [
                {
                  "system": "http://hl7.org/fhir/sid/icd-9-cm",
                  "code": "180"
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
    {
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
                  "code": "751"
                }
              ]
            },
            "subject": {
              "reference": "Patient/231678"
            }
          },
          "search": {
            "mode": "match"
          }
        }
      ]
    },
    {
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
            "id": "231679",
            "meta": {
              "versionId": "1",
              "lastUpdated": "2020-05-02T19:44:49.904-04:00"
            },
            "code": {
              "coding": [
                {
                  "system": "http://hl7.org/fhir/sid/icd-9-cm",
                  "code": "421"
                }
              ]
            },
            "subject": {
              "reference": "Patient/231679"
            }
          },
          "search": {
            "mode": "match"
          }
        }
      ]
    },
  ],
}

export default mockData