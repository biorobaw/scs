#!/usr/bin/python

import serial

if __name__ == "__main__":
  
  ser = serial.Serial('/dev/ttyACM0')
  
  # Discard first line
  ser.readline()
  
  while True:
    line = ser.readline()
    vals = line.split()
    vals = [float(x) for x in vals]
    print vals
