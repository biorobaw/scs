#!/bin/bash

#SBATCH -p development,circe

rm -rf bin
mkdir bin 
javac -sourcepath ./multiscalemodel/src/:./platform/src:micronsl/src -d bin -cp "./experiment/src/:./experiment/bin/:./multiscalemodel/target:./multiscalemodel/target/classes:./deps/xbee-java-library-1.2.0.jar" ./multiscalemodel/src/edu/usf/ratsim/robot/robotito/XbeeTestReceive.java
