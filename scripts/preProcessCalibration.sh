#!/bin/bash

#SBATCH -p development,circe

calibrationFile=$1
logPath=$2

mkdir -p $logPath

java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar edu.usf.experiment.CalibrationPreExperiment $calibrationFile $logPath > $logPath/preProc.txt 2>&1
