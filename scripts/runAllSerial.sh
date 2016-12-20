#!/bin/bash

xml=$1
logs=$2
<<<<<<< HEAD
indivPerGroup=$3
=======
groups=$3
indivPerGroup=$4
>>>>>>> multipleTBranch

truncate --size 0 out

groupIni=0
<<<<<<< HEAD
for i in $(seq 1 4); do
=======
for i in $(seq 1 $groups); do
>>>>>>> multipleTBranch
    groupEnd=$(($groupIni+$indivPerGroup-1))
    ./scripts/execExperimentSerial.sh $xml $logs $groupIni $groupEnd  >> out &
    groupIni=$(($groupEnd+1))
done
