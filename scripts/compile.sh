#!/bin/bash

#SBATCH -p development,circe

rm -rf bin
mkdir bin 
javac -sourcepath ./multiscalemodel/src/:./platform/src -d bin -cp "./experiment/src/:./experiment/bin/:./multiscalemodel/target:./multiscalemodel/target/classes:./deps/*:./deps/j3dport/*" `find . -iname *.java`
