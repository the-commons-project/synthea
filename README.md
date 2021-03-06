# Synthea<sup>TM</sup> Patient Generator [![Build Status](https://travis-ci.org/synthetichealth/synthea.svg?branch=master)](https://travis-ci.org/synthetichealth/synthea) [![codecov](https://codecov.io/gh/synthetichealth/synthea/branch/master/graph/badge.svg)](https://codecov.io/gh/synthetichealth/synthea)

Synthea<sup>TM</sup> is a Synthetic Patient Population Simulator. The goal is to output synthetic, realistic (but not real), patient data and associated health records in a variety of formats.

Read our [wiki](https://github.com/synthetichealth/synthea/wiki) for more information.

Currently, Synthea<sup>TM</sup> features include:
- Birth to Death Lifecycle
- Configuration-based statistics and demographics (defaults with Massachusetts Census data)
- Modular Rule System
  - Drop in [Generic Modules](https://github.com/synthetichealth/synthea/wiki/Generic-Module-Framework)
  - Custom Java rules modules for additional capabilities
- Primary Care Encounters, Emergency Room Encounters, and Symptom-Driven Encounters
- Conditions, Allergies, Medications, Vaccinations, Observations/Vitals, Labs, Procedures, CarePlans
- Formats
  - HL7 FHIR (STU3 v3.0.1, DSTU2 v1.0.2 and R4)
  - Bulk FHIR in ndjson format (set `exporter.fhir.bulk_data = true` to activate)
  - C-CDA (set `exporter.ccda.export = true` to activate)
  - CSV (set `exporter.csv.export = true` to activate)
  - CPCDS (set `exporter.cpcds.export = true` to activate)
- Rendering Rules and Disease Modules with Graphviz

## Developer Quick Start

These instructions are intended for those wishing to examine the Synthea source code, extend it or build the code locally. Those just wishing to run Synthea should follow the [Basic Setup and Running](https://github.com/synthetichealth/synthea/wiki/Basic-Setup-and-Running) instructions instead.

### Installation

**System Requirements:**
Synthea<sup>TM</sup> requires Java 1.8 or above.

To clone the Synthea<sup>TM</sup> repo, then build and run the test suite:
```
git clone https://github.com/synthetichealth/synthea.git
cd synthea
./gradlew build check test
```

### Changing the default properties 


The default properties file values can be found at `src/main/resources/synthea.properties`.
By default, synthea does not generate CCDA, CPCDA, CSV, or Bulk FHIR (ndjson). You'll need to
adjust this file to activate these features.  See the [wiki](https://github.com/synthetichealth/synthea/wiki)
for more details.



### Generate Synthetic Patients
Generating the population one at a time...
```
./run_synthea
```

Command-line arguments may be provided to specify a state, city, population size, or seed for randomization.

Usage is 
```
run_synthea [-s seed] [-p populationSize] [-m moduleFilter] [state [city]]
```
For example:

 - `run_synthea Massachusetts`
 - `run_synthea Alaska Juneau`
 - `run_synthea -s 12345`
 - `run_synthea -p 1000`
 - `run_synthea -s 987 Washington Seattle`
 - `run_synthea -s 21 -p 100 Utah "Salt Lake City"`
 - `run_synthea -m metabolic*`

Some settings can be changed in `./src/main/resources/synthea.properties`.

Synthea<sup>TM</sup> will output patient records in C-CDA and FHIR formats in `./output`.

### TCP functionality
This project has functionality added specifically for TCT which includes:
- Saving data to local files (now using an argument)
- Sending patients directly to a FHIR server
- Sending files to AWS S3

### Saving files locally
In order to save files locally (json format) you have to add the following argument on the command line
```
-local
```

and that is it

### Sending patients to a FHIR Server
In order to send the generated patients directly to a FHIR Server you need to add the following parameter on the command line
```
-fhir
```

and you have to configure correctly the following properties at synthea.properties file

```
exporter.fhir.fhirServer = <base url for the FHIR server>
```

### Sending patients to AWS S3
In order to send the generated to AWS S3 you need to add the following parameter on the command line
```
-s3
```

and you have to configure correctly the following properties at synthea.properties file

```
exporter.aws.role = <IAM role to assume>
exporter.aws.bucket = <bucket name>
```

The S3 client is taking the authentication data from the config and credentials files, so in order for this to work you 
need to follow the steps to configure your programmatic access to AWS.
For the Role to assume you'll need to user the entire ARN and that role has to be able to see the configured
bucket.

### Adding Person generated percentage
If we want to link more than one patient to a single person (empi) we can include
```
-per <percentage>
```
this parameter tells synthea to generate a percentage of persons based on the total population parameter (-p), for example
if we are sending 100 patients, and we want to generate 50% of persona, this will link 2 patients per person, the usage 
would be as follows
```
run_synthea -p 100 -fhir -per 50 
```
### Synthea<sup>TM</sup> GraphViz
Generate graphical visualizations of Synthea<sup>TM</sup> rules and modules.
```
./gradlew graphviz
```

### Concepts and Attributes
Generate a list of concepts (used in the records) or attributes (variables on each patient).
```
./gradlew concepts
./gradlew attributes
```

# License

Copyright 2017-2020 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
