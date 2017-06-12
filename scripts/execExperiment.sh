#!/bin/bash

experimentFile=$1
logPath=$2
fromIndiv=$3
toIndiv=$4

#idMessage=`sbatch ./scripts/compile.sh`
#compileId=`echo $idMessage | cut -d " " -f 4`
ant compile


#idMessage=`sbatch --dependency=afterok:$compileId scripts/preProcess.sh $experimentFile $logPath`
#preprocId=`echo $idMessage | cut -d " " -f 4`
./scripts/preProcess.sh $experimentFile $logPath

#idMessage=`sbatch --dependency=afterok:$preprocId -a $fromIndiv-$toIndiv ./scripts/execOne.sh $logPath`
idMessage=`sbatch -a $fromIndiv-$toIndiv ./scripts/execOne.sh $logPath`
ratsId=`echo $idMessage | cut -d " " -f 4`
sbatch --qos=preempt -p mri2016 --dependency=afterok:$ratsId scripts/postProcess.sh $logPath

