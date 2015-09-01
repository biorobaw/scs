#!/bin/bash

#$ -cwd
#$ -N taxi
#$ -m b
#$ -m e
#$ -t 1-100

experiment=$1
logDir=$2
individual=$3
#individual=$SGE_TASK_ID

RATSIM=/work/rat_simulator/
JAVA_LIBS=/work/java_libs/

#Xvfb :$individual -screen 2 1600x1200x16 +extension GLX &
#xvfb_pid=$!
#export DISPLAY=:0


#RATSIM=/home/m/mllofriualon/work/rat_simulator
#JAVA_LIBS=/home/m/mllofriualon/java_libs/

JAVA=java
#JAVA=/etc/alternatives/java_sdk_1.7.0/bin/java
#sh scripts/compile.sh

export PATH=/work/R-3.1.1/bin:$PATH
export R_LIBS=/work/R-3.1.1/library/

$JAVA -Xmx16000m -cp /work/experiment/src/:/work/experiment/bin/:/work/rat_simulator/target:/work/rat_simulator/target/classes:/work/rat_simulator/deps/commons-io-2.4.jar:/work/rat_simulator/deps/jts-1.8.jar:/work/rat_simulator/deps/protobuf-java-2.5.0.jar:/work/rat_simulator/deps/jts-1.8.0.zip:/work/rat_simulator/deps/j3dport/gluegen-rt.jar:/work/rat_simulator/deps/j3dport/gluegen.jar:/work/rat_simulator/deps/j3dport/j3dcore.jar:/work/rat_simulator/deps/j3dport/j3dutils.jar:/work/rat_simulator/deps/j3dport/joal.jar:/work/rat_simulator/deps/j3dport/jocl.jar:/work/rat_simulator/deps/j3dport/jogl-all.jar:/work/rat_simulator/deps/j3dport/jogl-test.jar:/work/rat_simulator/deps/j3dport/vecmath.jar:/work/experiment/bin:/work/experiment/deps/commons-io-2.4.jar:/work/experiment/deps/reflections-0.9.9-RC1-uberjar.jar:/work/experiment/deps/guava-18.0.jar:/work/experiment/deps/slf4j-api-1.7.10.jar:/work/experiment/deps/javassist.jar:/work/experiment/deps/jts-1.8.jar:/work/experiment/deps/j3dport/gluegen-rt.jar:/work/experiment/deps/j3dport/gluegen.jar:/work/experiment/deps/j3dport/j3dcore.jar:/work/experiment/deps/j3dport/j3dutils.jar:/work/experiment/deps/j3dport/joal.jar:/work/experiment/deps/j3dport/jocl.jar:/work/experiment/deps/j3dport/jogl-all.jar:/work/experiment/deps/j3dport/vecmath.jar:/work/nslj/bin:/work/nslj/deps/tcljava.jar:/work/nslj/deps/jacl.jar:/work/nslj/deps/j3dport/gluegen-rt.jar:/work/nslj/deps/j3dport/gluegen.jar:/work/nslj/deps/j3dport/j3dcore.jar:/work/nslj/deps/j3dport/j3dutils.jar:/work/nslj/deps/j3dport/joal.jar:/work/nslj/deps/j3dport/jocl.jar:/work/nslj/deps/j3dport/jogl-all.jar:/work/nslj/deps/j3dport/vecmath.jar:/work/rat_simulator/deps/tcljava1.4.1/jacl.jar:/work/rat_simulator/deps/tcljava1.4.1/tcljava.jar edu.usf.experiment.RunIndividualByNumber $experiment $logDir $individual

#kill $xvfb_pid
