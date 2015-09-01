#!/bin/bash

experimentFile=$1
logPath=$2
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$SLURM_ARRAY_TASK_ID
else
  individual=$4
fi

if [ `hostname` == "pinky" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
fi

java -cp "./experiment/src/:./experiment/bin/:./multiscalemodel/target:./multiscalemodel/target/classes:./deps/*:./deps/j3dport/*" edu.usf.experiment.RunIndividualByNumber $experimentFile $logPath $individual

