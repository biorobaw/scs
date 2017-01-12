roslaunch orb_slam2 orb_slam.launch &

sleep 5;

rosrun ssl_driver slamProxy.py &

run ssl_driver slamProxy.py & java -Dgnu.io.rxtx.SerialPorts=/dev/teensy:/dev/uno -Djava.library.path=/usr/lib/jni/ -classpath "./bin/:./deps/*" edu.usf.ratsim.robot.ssl.SlamSetup
