#!/bin/bash

experimentFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PreExperiment $experimentFile $logPath 
sbatch -a $fromIndiv=$toIndiv ./scripts/execOne.sh $logPath
/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PostExperiment $experimentFile $logPath 

