#!/bin/bash

#SBATCH --time=0:40:00
<<<<<<< HEAD
=======
#SBATCH --mem=8192
>>>>>>> bug-algs
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

if [ `whoami` == "martin" ]; then
  export PATH=/work/R-3.3.3/bin:$PATH
  export R_LIBS=/work/R-3.3.3/library/
else
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
  module add apps/jre/1.8.0_121.x86 
  module unload apps/jre/1.7.0_80.x64
fi
java -cp "./platform/src/:./multiscalemodel/src/:./bin:./deps/*" edu.usf.experiment.CalibrationExperiment $logPath $individual

