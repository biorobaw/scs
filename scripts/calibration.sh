#!/bin/bash

calibrationFile=$1
logPath=$2
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$3
else
  individual=$SLURM_ARRAY_TASK_ID
fi

if [ `hostname` == "pinky" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
fi

java -cp "./platform/src/:./experiment/src/:./bin:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationExperiment $calibrationFile $logPath $individual

