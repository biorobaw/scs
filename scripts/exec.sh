#!/bin/bash

experimentFile=$1
logPath=$2
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$3
else
  individual=$SLURM_ARRAY_TASK_ID
fi

echo "Individual " $individual

if [ `hostname` == "pinky" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
fi

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.RunIndividualByNumber $experimentFile $logPath $individual

