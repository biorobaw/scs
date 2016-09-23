#!/bin/bash

xml=$1
logs=$2

truncate --size 0 out

./scripts/execExperimentSerial.sh $xml $logs 0 15 >> out &
./scripts/execExperimentSerial.sh $xml $logs 16 31 >> out & 
./scripts/execExperimentSerial.sh $xml $logs 32 47 >> out &
./scripts/execExperimentSerial.sh $xml $logs 48 63 >> out &
