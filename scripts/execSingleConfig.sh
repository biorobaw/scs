#!/bin/bash

#SBATCH --time=0:10:00
#SBATCH --cpus-per-task 2 
#SBATCH --qos=preempt
#SBATCH --mem=2000M
#SBATCH -p mri2016

configFile=$1
baseDir=$2

if [ -z "$SLURM_ARRAY_TASK_ID" ]; then
  configId=$3
else
  configId=$SLURM_ARRAY_TASK_ID
fi

echo "configId " $configId

# Check for usf cluster
if ! $(hostname | grep --quiet "usf.edu"); then
  #R is no longer used, but it was left just in case
  #export PATH=/work/R-3.3.3/bin:$PATH
  #export R_LIBS=/work/R-3.3.3/library/
else
  #module add apps/R/3.3.2
  #export R_LIBS=/home/m/mllofriualon/work/rlib
  #problem we now use java 10, we need newer jre
  #module add apps/jre/1.8.0_121.x86 
  #module unload apps/jre/1.7.0_80.x64
  #module list
  java -version
fi

java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar -Xmx1500m edu.usf.experiment.Experiment $configFile $configId $baseDir

