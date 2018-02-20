#!/bin/bash

#SBATCH --time=0:40:00
#SBATCH --mem=8192
#SBATCH --cpus-per-task 2
#SBATCH --qos=preempt

logPath=$1
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  echo Executing individual from parameters: $3
  individual=$3
else
  echo Executing individual from slurm array: $SLURM_ARRAY_TASK_ID
  individual=$SLURM_ARRAY_TASK_ID
fi

# Check for usf cluster
if ! $(hostname | grep --quiet "usf.edu"); then
  export PATH=/work/R-3.3.3/bin:$PATH
  export R_LIBS=/work/R-3.3.3/library/
else
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
  module add apps/jre/1.8.0_121.x86 
  module unload apps/jre/1.7.0_80.x64
fi
java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar edu.usf.experiment.CalibrationExperiment $logPath $individual

