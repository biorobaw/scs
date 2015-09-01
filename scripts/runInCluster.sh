#!/bin/bash

logdir=$1
numIndividuals=$2

./scripts/compile.sh

for i in `seq 1 $numIndividuals`; do
  qsub scripts/exec.sh /edu/usf/ratsim/experiment/xml/multiFeeders.xml $logdir $i
done
