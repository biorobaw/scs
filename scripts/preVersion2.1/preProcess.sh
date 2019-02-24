#!/bin/bash


experimentFile=$1
logPath=$2

mkdir -p $logPath

#module add apps/jre/1.8.0_121.x86 
#module unload apps/jre/1.7.0_80.x64
java -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar edu.usf.experiment.PreExperiment $experimentFile $logPath > $logPath/preProc.txt 2>&1
