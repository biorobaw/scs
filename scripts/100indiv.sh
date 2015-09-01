export DISPLAY=:0.0

experiment=$1
name=$2
group=$3
for i in `seq 0 99`; do
  ./scripts/execPinky.sh $experiment $name $group $i
done
