#!/bin/bash

calibrationFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

#idMessage=`sbatch ./scripts/compile.sh`
#compileId=`echo $idMessage | cut -d " " -f 4`
ant compile

#idMessage=`sbatch --dependency=afterok:$compileId scripts/preProcessCalibration.sh $calibrationFile $logPath`
#preprocId=`echo $idMessage | cut -d " " -f 4`
./scripts/preProcessCalibration.sh $calibrationFile $logPath

idMessage=`sbatch -a $fromIndiv-$toIndiv ./scripts/execOneCalib.sh $logPath`
#idMessage=`sbatch --dependency=afterok:$preprocId -a $fromIndiv-$toIndiv ./scripts/execOneCalib.sh $logPath`
slurmId=`echo $idMessage | cut -d " " -f 4`

sbatch --dependency=afterok:$slurmId scripts/postProcessCalib.sh $logPath

