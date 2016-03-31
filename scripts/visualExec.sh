#!/bin/bash

# Example exec
#  ./scripts/visualExec.sh "./multiscalemodel/src/edu/usf/ratsim/experiment/xml/tspSimpleRuns.xml" logs/Experiment/bat3 Control 0
experimentFile=$1
logPath=$2
group=$3
indiv=$4


java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.Experiment $experimentFile $logPath $group $indiv
