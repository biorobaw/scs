#!/bin/bash


experimentFile=$1
logPath=$2

mkdir -p $logPath

<<<<<<< HEAD
#module add apps/jre/1.8.0_121.x86 
#module unload apps/jre/1.7.0_80.x64
=======
module add apps/jre/1.8.0_121.x86 
module unload apps/jre/1.7.0_80.x64
>>>>>>> bug-algs

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.PreExperiment $experimentFile $logPath > $logPath/preProc.txt 2>&1
