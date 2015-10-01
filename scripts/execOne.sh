#!/bin/bash

#SBATCH -p development,circe
#SBATCH --time=2:00:00
#SBATCH --mem=5000
#SBATCH --cpus-per-task 2

logPath=$1
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$2
else
  individual=$SLURM_ARRAY_TASK_ID
fi

echo "Individual " $individual

if [ `whoami` == "biorob" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
else
  module add apps/R/3.1.2
  export R_LIBS=/home/m/mllofriualon/R-library/
fi

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.RunIndividualByNumber $logPath $individual

