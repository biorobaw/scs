#!/bin/bash

#SBATCH -p development

experimentFile=$1
logPath=$2

module add apps/R/3.1.2

/usr/bin/java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PostExperiment $experimentFile $logPath > $logPath/postProc.txt 2>&1

