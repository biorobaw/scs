#!/bin/sh

experiment=$1
logPath=$2
numIndividuals=$3

./scripts/execPinky.sh $experiment $logPath $SLURM_ARRAY_TASK_ID 
