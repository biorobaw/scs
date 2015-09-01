#!/bin/bash

#SBATCH -J ratsim
#SBATCH --dependency=singleton

export PATH=/home/m/mllofriualon/R-3.0.2/bin:$PATH

logdir=$1

cp src/edu/usf/ratsim/experiment/plot/multifeeders/plotRuntimes.r logs/$logdir/

cd logs/$logdir/
Rscript plotRuntimes.r > plot.out
