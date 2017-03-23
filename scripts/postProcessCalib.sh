#!/bin/bash

#SBATCH -p development

logPath=$1

if [ `whoami` == "mllofriualon" ]; then
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib

fi

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.CalibrationPostExperiment $logPath > $logPath/postProc.txt 2>&1

