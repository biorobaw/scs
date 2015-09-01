#!/bin/bash

logPath=$1
experimentFile=$2
calibrationFile=$3
#individual=$4
individual=$SLURM_ARRAY_TASK_ID

export PATH=/work/R-3.1.1/bin:$PATH
export R_LIBS=/work/R-3.1.1/library/

java -cp "../experiment/src/:../experiment/bin/:./target:./target/classes:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationExperiment $logPath $experimentFile $calibrationFile $individual

