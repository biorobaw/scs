#ifndef PoluloMotor_h
#define PoluloMotor_h

#include "Arduino.h"
#include "PoluloEncoder.h"

class PoluloMotor {
  public:
    PoluloMotor(int encoderPin1, int encoderPin2, int enablePin, int dirPin1, int dirPin2, float ratio);
    void setTargetVel(float targetVel);
    float getTargetVel() { return targetVel; }
    long getTics(){ return encoder.getTics();}
    void pid();
    float getVel() {return encoder.getVel();}
    void setkP(float kp) { this->kp =  kp;}
    void setkI(float ki) { this->ki = ki;}
    void setkD(float kd) { this->kd = kd;}
    bool autoTune();
    void printTunedKs();
  private:
    PoluloEncoder encoder;
    
//    double kp = 8.36;
//    double ki = 0.67;
//    double kd = 26.14;

    double kp = 10;
    double ki = 2;
    double kd = 0.0;

    int maxPWM = 255;

    //Define Variables we'll be connecting to
    double Input, Output, targetVel;

    int enablePin;
    int dirPin1;
    int dirPin2;
    // Model Vel when inputed the calibrating pwm - default value 
    float ffModelVel = 6.0f;

};

#endif
