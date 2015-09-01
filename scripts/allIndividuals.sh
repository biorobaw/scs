export DISPLAY=:0.0

experiment=$1
logPath=$2
numIndividuals=$3
for i in `seq 0 $numIndividuals`; do
  ./scripts/execPinky.sh $experiment $logPath $i
done
