#!/bin/bash

#SBATCH -p development,circe

calibrationFile=$1
logPath=$2

mkdir -p $logPath

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationPreExperiment $calibrationFile $logPath > $logPath/preProc.txt 2>&1
