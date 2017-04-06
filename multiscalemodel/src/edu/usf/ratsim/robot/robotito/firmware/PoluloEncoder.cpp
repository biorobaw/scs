#include "PoluloEncoder.h"

PoluloEncoder::PoluloEncoder(int pin1, int pin2, float ratio) : encoder(pin1, pin2) 
{
  timeLastRead = millis();
  ticsLastRead = 0l;
  averageVel = 0;
  encoder.write(0);
  ticks_milli_2_turns_sec = 1000.0f / (TICS_PER_AXIX_TURN * ratio);
}

float PoluloEncoder::getVel()
{
  long newPos, newTime;
  newPos = encoder.read();
  newTime = millis();
  
  long posDiff = newPos - ticsLastRead;

  long timeDiff = newTime-timeLastRead;

  if (timeDiff > 0 && abs(posDiff)<100000){
    averageVel = ((float)posDiff)/(timeDiff) * ticks_milli_2_turns_sec;
    //encoder.write(0);
  }


  //Serial.println(averageVel);
  //Serial.println();

  timeLastRead = newTime;
  ticsLastRead = newPos;
  
  return averageVel;
}

