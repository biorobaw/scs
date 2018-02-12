#!/bin/bash

# Example exec
#  ./scripts/visualExec.sh "./multiscalemodel/src/edu/usf/ratsim/experiment/xml/tspSimpleRuns.xml" logs/Experiment/bat3 Control 0
experimentFile=$1
logPath=$2
group=$3
indiv=$4


java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar edu.usf.experiment.Experiment -display $experimentFile $logPath $group $indiv
