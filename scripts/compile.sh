#!/bin/bash

#SBATCH -J ratsim

RATSIM=/work/rat_simulator/
JAVA_LIBS=/work/java_libs/

#RATSIM=/home/m/mllofriualon/work/rat_simulator
#JAVA_LIBS=/home/m/mllofriualon/java_libs/

#RATSIM=/home/m/mllofriualon/rat_simulator
#JAVA_LIBS=/home/m/mllofriualon/java_libs/
#JAVAC=/etc/alternatives/java_sdk_1.7.0/bin/javac
JAVAC=javac

rm -rf target
mkdir target
$JAVAC -sourcepath $RATSIM/src/ -d target -classpath "/work/experiment/bin/:/work/rat_simulator/deps/commons-io-2.4.jar:/work/rat_simulator/deps/jts-1.8.jar:/work/rat_simulator/deps/protobuf-java-2.5.0.jar:/work/rat_simulator/deps/jts-1.8.0.zip:/work/rat_simulator/deps/j3dport/gluegen-rt.jar:/work/rat_simulator/deps/j3dport/gluegen.jar:/work/rat_simulator/deps/j3dport/j3dcore.jar:/work/rat_simulator/deps/j3dport/j3dutils.jar:/work/rat_simulator/deps/j3dport/joal.jar:/work/rat_simulator/deps/j3dport/jocl.jar:/work/rat_simulator/deps/j3dport/jogl-all.jar:/work/rat_simulator/deps/j3dport/jogl-test.jar:/work/rat_simulator/deps/j3dport/vecmath.jar:/work/experiment/bin:/work/experiment/deps/commons-io-2.4.jar:/work/experiment/deps/reflections-0.9.9-RC1-uberjar.jar:/work/experiment/deps/guava-18.0.jar:/work/experiment/deps/slf4j-api-1.7.10.jar:/work/experiment/deps/javassist.jar:/work/experiment/deps/jts-1.8.jar:/work/experiment/deps/j3dport/gluegen-rt.jar:/work/experiment/deps/j3dport/gluegen.jar:/work/experiment/deps/j3dport/j3dcore.jar:/work/experiment/deps/j3dport/j3dutils.jar:/work/experiment/deps/j3dport/joal.jar:/work/experiment/deps/j3dport/jocl.jar:/work/experiment/deps/j3dport/jogl-all.jar:/work/experiment/deps/j3dport/vecmath.jar:/work/nslj/bin:/work/nslj/deps/tcljava.jar:/work/nslj/deps/jacl.jar:/work/nslj/deps/j3dport/gluegen-rt.jar:/work/nslj/deps/j3dport/gluegen.jar:/work/nslj/deps/j3dport/j3dcore.jar:/work/nslj/deps/j3dport/j3dutils.jar:/work/nslj/deps/j3dport/joal.jar:/work/nslj/deps/j3dport/jocl.jar:/work/nslj/deps/j3dport/jogl-all.jar:/work/nslj/deps/j3dport/vecmath.jar:/work/rat_simulator/deps/tcljava1.4.1/jacl.jar:/work/rat_simulator/deps/tcljava1.4.1/tcljava.jar" `find $RATSIM/src/ -iname *.java`
