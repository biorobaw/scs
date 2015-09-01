#!/bin/bash

#SBATCH --array=0-255
#SBATCH -J ratsim
# #SBATCH --dependency=singleton

# execute ploting after this job
#sbatch --dependency=afterok:$SLURM_JOB_ID scripts/plotRuntimes.sh $log

groupSize=64

experiment=$1
logDir=$2
#numIndividuals=$3
numIndividual=`echo $SLURM_ARRAY_TASK_ID % $groupSize| bc`
numGroup=`echo $SLURM_ARRAY_TASK_ID / $groupSize | bc`

#RATSIM=/home/m/mllofriualon/work/rat_simulator
#JAVA_LIBS=/home/m/mllofriualon/java_libs/

RATSIM=/home/m/mllofriualon/rat_simulator
JAVA_LIBS=/home/m/mllofriualon/java_libs/

JAVA=java
JAVA=/etc/alternatives/java_sdk_1.7.0/bin/java
#sh scripts/compile.sh

#export PATH=/work/R-3.1.1/bin:$PATH
export PATH=/home/m/mllofriualon/R-3.0.2/bin:$PATH

Xvfb :$SLURM_ARRAY_TASK_ID -screen 2 1600x1200x16 +extension GLX &
xvfb_pid=$!
export DISPLAY=:$SLURM_ARRAY_TASK_ID.2 
#export DISPLAY=0:0
$JAVA  -Xmx16000m -Djava.library.path=$JAVA_LIBS/j3d-1_5_2-linux-amd64/ -cp .:$JAVA_LIBS/nslj.jar:$JAVA_LIBS/commons-io-2.4.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/j3dcore.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/j3dutils.jar:$JAVA_LIBS/tcljava1.4.1/jacl.jar:$JAVA_LIBS/tcljava1.4.1/tcljava.jar:$JAVA_LIBS/jts-1.8.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/vecmath.jar:bin/:src/ edu.usf.ratsim.experiment.Experiment $experiment $logDir $numGroup $numIndividual # $SGE_TASK_ID

kill $xvfb_pid
