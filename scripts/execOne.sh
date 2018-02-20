#!/bin/bash

#SBATCH --time=0:10:00
#SBATCH --cpus-per-task 2 
#SBATCH --qos=preempt
#SBATCH --mem=2000M
#SBATCH -p mri2016

logPath=$1
if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  individual=$2
else
  individual=$SLURM_ARRAY_TASK_ID
fi

echo "Individual " $individual

# Check for usf cluster
if ! $(hostname | grep --quiet "usf.edu"); then
  export PATH=/work/R-3.3.3/bin:$PATH
  export R_LIBS=/work/R-3.3.3/library/
else
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
  module add apps/jre/1.8.0_121.x86 
  module unload apps/jre/1.7.0_80.x64
  module list
  java -version
fi

java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar -Xmx1500m edu.usf.experiment.RunIndividualByNumber $logPath $individual

