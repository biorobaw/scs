#!/bin/bash

#SBATCH --time=0:30:00
#SBATCH --cpus-per-task 2 
#SBATCH --mem=8192
#SBATCH --qos=preempt
#SBATCH -p mri2016,cms2016,circe

logPath=$1
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$2
else
  individual=$SLURM_ARRAY_TASK_ID
fi

echo "Individual " $individual

if [ `whoami` == "martin" ]; then
  export PATH=/work/R-3.1.1/bin:$PATH
  export R_LIBS=/work/R-3.1.1/library/
else
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
  module add apps/jre/1.8.0_121.x86 
  module unload apps/jre/1.7.0_80.x64
fi
module list
java -version

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.RunIndividualByNumber $logPath $individual

