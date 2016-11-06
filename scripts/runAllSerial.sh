#!/bin/bash

xml=$1
logs=$2
indivPerGroup=$3

truncate --size 0 out

groupIni=0
for i in $(seq 1 4); do
    groupEnd=$(($groupIni+$indivPerGroup-1))
    ./scripts/execExperimentSerial.sh $xml $logs $groupIni $groupEnd  >> out &
    groupIni=$(($groupEnd+1))
done
