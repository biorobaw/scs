constant SOURCE_FOLDERS
"./platform/src/:./multiscalemodel/src/:./bin/:./deps/*:./deps/j3dport/*"

constant MAIN_CLASS
edu.usf.experiment.Experiment

constant EXPERIMENT_XML
"./multiscalemodel/src/edu/usf/ratsim/experiment/xml/tspSimpleRuns.xml"

calculated LOG_FOLDER 
row

constant GROUP 
1

constant RAT_NUMBER
1

calculated nFoodStopCondition
lenList feederOrder 

combine allXall
