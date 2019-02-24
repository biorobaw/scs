#!/bin/bash

configFile=$1
baseDir=$2

mvn package


#create log folder structure:
python ${SCS_FOLDER}/scripts/python/logFolderGenerator.py ${baseDir} ${configFile}

#get number of lines in configFile:
numConfigs=`wc -l ${configFile}`-1

#set individuals to run:
fromIndiv=0
toIndiv=${numConfigs}

#execute each line
idMessage=`sbatch -a $fromIndiv-$toIndiv ./scripts/execOne.sh $configFile $baseDir`
#ratsId=`echo $idMessage | cut -d " " -f 4`
#sbatch --qos=preempt -p mri2016 --dependency=afterok:$ratsId scripts/postProcess.sh $logPath