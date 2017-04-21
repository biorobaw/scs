
columns startPosition ratpath enabledFeeders P1 P2 nFoodStopCondition feederOrder

constant SOURCE_FOLDERS
"./platform/src/;./multiscalemodel/src/;./platform/bin/;./multiscalemodel/bin/;./micronsl/bin/;./deps/*;./deps/j3dport/*"

constant MAIN_CLASS
edu.usf.experiment.Experiment

constant EXPERIMENT_XML
"./multiscalemodel/src/edu/usf/ratsim/experiment/xml/tspSimpleRunsFullPathLocal.xml"

calculated LOG_FOLDER 
concat "logs/TSPInter/p" P1 "-q" P2

constant GROUP 
Control

constant RAT_NUMBER
1

variable startPosition
-0.28223,-0.44841,0

variable ratpath
InterLabFunctionality/Jan2016/paths/1

variable enabledFeeders 
18,21,3,5

variable P1
0.1
0.2
0.3
0.4
0.5
0.6
0.7
0.8
0.9
1.0

variable P2
0.1
0.2
0.3
0.4
0.5
0.6
0.7
0.8
0.9
1.0


calculated nFoodStopCondition
lenList enabledFeeders 

combine allXall
