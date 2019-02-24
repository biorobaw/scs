#!/bin/bash

configFile=$1
baseDir=$2

mvn package

#add python module to circe
module add apps/python/3.7.0

#create log folder structure:
python ${SCS_FOLDER}/scripts/python/logFolderGenerator.py ${baseDir} ${configFile}


#store command executed along with commit version and time
cmdHistory=${baseDir}/cmdHistory.txt
date >> ${cmdHistory}
git log --pretty=format:'%h' -n 1 >> ${cmdHistory} && echo " ${configFile}" >>${cmdHistory}

#get number of lines in configFile:
numLines=`wc -l ${configFile} | cut -f1 -d' '`


#set individuals to run:
#the last one is the number of lines in the file minus 2 (since its 0 based and the first line are headers)
fromIndiv=0
toIndiv=`expr ${numLines} - 2` 

#execute each line
outputFilePattern="${baseDir}/slurmOut/slurm-%A_%a.out"
idMessage=`sbatch -a $fromIndiv-$toIndiv -e=$outputFilePattern -o=$outputFilePattern ./scripts/execSingleConfig.sh $configFile $baseDir`
#ratsId=`echo $idMessage | cut -d " " -f 4`
#sbatch --qos=preempt -p mri2016 --dependency=afterok:$ratsId scripts/postProcess.sh $logPath