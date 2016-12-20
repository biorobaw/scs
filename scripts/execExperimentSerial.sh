#!/bin/bash

experimentFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

ant compile

./scripts/preProcess.sh $experimentFile $logPath

for i in $(seq $fromIndiv $toIndiv); do
	./scripts/execOne.sh $logPath $i
done

scripts/postProcess.sh $logPath

