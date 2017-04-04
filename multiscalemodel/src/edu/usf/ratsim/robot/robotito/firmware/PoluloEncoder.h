
#ifndef PoluloEncoder_h
#define PoluloEncoder_h

#include "Arduino.h"
#include <Encoder.h>

class PoluloEncoder {
  public:
    PoluloEncoder(int pin1, int pin2, float ratio);
    float getVel();
    long getTics() { return ticsLastRead; };
  private:
    Encoder encoder;
    long timeLastRead;
    long ticsLastRead;
    float averageVel;
    float alpha = .9;
    float ticks_milli_2_turns_sec;
    float TICS_PER_AXIX_TURN = 12;
};

#endif
