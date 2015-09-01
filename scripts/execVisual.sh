experiment=$1
logDir=$2
group=$3
indiv=$4

RATSIM=/work/rat_simulator/
JAVA_LIBS=/work/java_libs/

#RATSIM=/home/m/mllofriualon/work/rat_simulator
#JAVA_LIBS=/home/m/mllofriualon/java_libs/

JAVA=java
#JAVA=/etc/alternatives/java_sdk_1.7.0/bin/java
#sh scripts/compile.sh

$JAVA  -Xmx16000m -Djava.library.path=$JAVA_LIBS/j3d-1_5_2-linux-amd64/ -cp .:$JAVA_LIBS/nslj.jar:$JAVA_LIBS/commons-io-2.4.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/j3dcore.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/j3dutils.jar:$JAVA_LIBS/tcljava1.4.1/jacl.jar:$JAVA_LIBS/tcljava1.4.1/tcljava.jar:$JAVA_LIBS/jts-1.8.jar:$JAVA_LIBS/j3d-1_5_2-linux-amd64/vecmath.jar:bin/:src/ edu.usf.ratsim.experiment.Experiment $experiment $logDir $group $indiv

