#!/bin/bash

#SBATCH -p development,circe

logPath=$1

if [ `whoami` == "mllofriualon" ]; then
        module add apps/R/3.1.2
        export R_LIBS=/home/m/mllofriualon/R-library/
fi

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PostExperiment $logPath > $logPath/postProc.txt 2>&1

