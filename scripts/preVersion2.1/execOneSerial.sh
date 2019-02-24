#!/bin/bash

logPath=$1
individual=$2

echo "Individual " $individual

ava -cp multiscalemodel/target/models-2.0.0-jar-with-dependencies.jar  edu.usf.experiment.RunIndividualByNumber $logPath $individual

