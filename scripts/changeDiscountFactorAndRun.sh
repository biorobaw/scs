#!/bin/bash

#mkdir src/edu/usf/ratsim/experiment/xml/vardisc/ 

for i in `seq 0.05 0.05 0.95`; do
	sed "s/#DISCOUNT/$i/g" ./src/edu/usf/ratsim/experiment/xml/multiFeedersOneSubVarDiscount.xml > ./src/edu/usf/ratsim/experiment/xml/vardisc/multiFeedersOneSubVarDiscount$i.xml
	for j in `seq 1 5`; do
		qsub scripts/exec.sh /edu/usf/ratsim/experiment/xml/vardisc/multiFeedersOneSubVarDiscount$i.xml
	done
done
