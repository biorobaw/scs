#!/bin/bash

xml=$1
logs=$2

truncate --size 0 out

./scripts/execExperimentSerial.sh $xml $logs 0 31 >> out &
./scripts/execExperimentSerial.sh $xml $logs 32 63 >> out & 
./scripts/execExperimentSerial.sh $xml $logs 64 95 >> out &
./scripts/execExperimentSerial.sh $xml $logs 96 127 >> out &
