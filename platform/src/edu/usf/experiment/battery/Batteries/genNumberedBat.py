#!/usr/bin/env python

import sys

if len(sys.argv) < 6:
	print "ERROR: command format is:"
	print "./getNumberedBat.py FILENAME COLUMN_NAME CONSTANT_STRING MINVAL MAXVAL"

filename = sys.argv[1]
columnName = sys.argv[2]
constantString = sys.argv[3]
minval = int(sys.argv[4])
maxval = int(sys.argv[5])

f = open(filename + '.bat','w')
f.write('variable ' + columnName +'\n')
for i in range(minval,maxval+1):
	f.write('"'+constantString + str(i)+'"\n')
f.close()
	


