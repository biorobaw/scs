#!/bin/bash


logPath=$1
if [ `whoami` == "martin" ]; then
  export PATH=/work/R-3.3.3/bin:$PATH
  export R_LIBS=/work/R-3.3.3/library/
else
  module add apps/R/3.3.2
  export R_LIBS=/home/m/mllofriualon/work/rlib
  module add apps/jre/1.8.0_121.x86  
  module unload apps/jre/1.7.0_80.x64
fi

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PostExperiment $logPath > $logPath/postProc.txt 2>&1

