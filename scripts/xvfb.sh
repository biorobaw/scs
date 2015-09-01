#$ -cwd
#$ -N xvfb
#$ -l h_rt=00:00:10
#$ -l location=tpa

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib64/:/home/m/mllofriualon/
export
Xvfb
pid=$!
sleep 5
kill $pid
