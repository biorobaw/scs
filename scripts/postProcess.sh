#!/bin/bash

#SBATCH -p development

logPath=$1

module add apps/R/3.1.2

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PostExperiment $logPath > $logPath/postProc.txt 2>&1

