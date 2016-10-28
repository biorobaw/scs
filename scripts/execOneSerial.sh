#!/bin/bash

logPath=$1
individual=$2

echo "Individual " $individual

java -cp "./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*" edu.usf.experiment.RunIndividualByNumber $logPath $individual

