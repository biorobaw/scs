#!/bin/bash

logs=$1

cd $logs

mkdir figs

find . -iname "*.pdf" -exec ln  {} figs/ \;
