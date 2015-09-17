#!/bin/bash

#SBATCH -p development
#SBATCH --time=2:30:00
#SBATCH --mem=4000
#SBATCH --cpus-per-task 2

logPath=$1
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  echo Executing individual from parameters: $3
  individual=$3
else
  echo Executing individual from slurm array: $SLURM_ARRAY_TASK_ID
  individual=$SLURM_ARRAY_TASK_ID
fi

if [ `whoami` == "biorob" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
else
  module add apps/R/3.1.2
  export R_LIBS=/home/m/mllofriualon/R-library/
fi
/usr/bin/java -cp "./platform/src/:./experiment/src/:./bin:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationExperiment $logPath $individual

