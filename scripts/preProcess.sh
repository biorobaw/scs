#!/bin/bash

#SBATCH -p development,circe

experimentFile=$1
logPath=$2

mkdir -p $logPath

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PreExperiment $experimentFile $logPath > $logPath/preProc.txt 2>&1
