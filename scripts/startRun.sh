#!/bin/bash

xml=$1 
logs=$2
group=$3
individual=$4

roslaunch orb_slam2 orb_slam.launch &

sleep 10

java -Dgnu.io.rxtx.SerialPorts=/dev/teensy:/dev/uno \
	-Djava.library.path=/usr/lib/jni/ -classpath    \
	"./bin/:./deps/*" edu.usf.experiment.Experiment $xml $logs $group $individual &

sleep 2

roslaunch ssl_driver ssl_driver.launch

#roslaunch multiscalemodel/src/edu/usf/ratsim/robot/ssl/model.launch
