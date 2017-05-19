#!/bin/bash

#SBATCH --qos=preempt

logPath=$1
<<<<<<< HEAD
if [ `whoami` == "martin" ]; then
  export PATH=/work/R-3.3.3/bin:$PATH
  export R_LIBS=/work/R-3.3.3/library/
else
=======

if [ `whoami` == "mllofriualon" ]; then
>>>>>>> bug-algs
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
fi

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationPostExperiment $logPath > $logPath/postProc.txt 2>&1

