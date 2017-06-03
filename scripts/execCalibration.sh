#!/bin/bash

calibrationFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

echo Executing from $fromIndiv to $toIndiv

ant compile

./scripts/preProcessCalibration.sh $calibrationFile $logPath

idMessage=`sbatch -a $fromIndiv-$toIndiv ./scripts/execOneCalib.sh $logPath`
slurmId=`echo $idMessage | cut -d " " -f 4`

sbatch --dependency=afterok:$slurmId scripts/postProcessCalib.sh $logPath

