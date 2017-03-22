#include "PoluloMotor.h"


//Specify the links and initial tuning parameters

PoluloMotor::PoluloMotor(int encoderPin1, int encoderPin2, int enablePin, int dirPin1, int dirPin2, float ratio)
	: encoder(encoderPin1, encoderPin2, ratio)
//	, pidTuner(&Input, &Output)
{
	this->enablePin = enablePin;
	this->dirPin1 = dirPin1;
	this->dirPin2 = dirPin2;

	targetVel = 0;
  Output = 0;
  Input = encoder.getVel();
//
//  pidTuner.SetOutputStep(50);
//  pidTuner.SetControlType(1);
//  pidTuner.SetNoiseBand(1);

	pinMode(enablePin, OUTPUT);
	pinMode(dirPin1, OUTPUT);
	pinMode(dirPin2, OUTPUT);

	digitalWrite(enablePin, LOW);
	digitalWrite(dirPin1, LOW);
	digitalWrite(dirPin2, LOW);	
}

void PoluloMotor::printTunedKs(){
//  Serial.println("Constants");
//  Serial.println(pidTuner.GetKp());
//  Serial.println(pidTuner.GetKi());
//  Serial.println(pidTuner.GetKd());
}

bool PoluloMotor::autoTune()
{
  digitalWrite(dirPin1, HIGH);
  digitalWrite(dirPin2, LOW);  
  analogWrite(enablePin, 100);

  delay (300);
  
  long startTime = millis();
  // Discard one read
  encoder.getVel();
  float avgVel = encoder.getVel();
  while (millis() - startTime < 300){
    avgVel = .5 * encoder.getVel() + .5 * avgVel;
    delay(20);
  }
       
  ffModelVel = avgVel;    
  Serial.println(ffModelVel);

  analogWrite(enablePin, 0);
  digitalWrite(dirPin1, LOW);
  digitalWrite(dirPin2, LOW);
  delay(300);
  return true;
}

void PoluloMotor::setTargetVel(float vel)
{
	targetVel = vel;
}

int sign(float num){
	if (num > 0)
		return 1;
	else if (num < 0)
		return -1;
	else 
		return 0;
}

void PoluloMotor::pid()
{
  Input = encoder.getVel();
  
  // Linear feedforward model - max power / max vel 
  float ff = targetVel * 100;

  float ctrlSignal = ff + Output;
  // Direction setting
  if (targetVel == 0){
    digitalWrite(dirPin1, HIGH);
    digitalWrite(dirPin2, HIGH); 
  } else if (ctrlSignal > 0){
    digitalWrite(dirPin1, LOW);
    digitalWrite(dirPin2, HIGH);  
  } else if (ctrlSignal < 0) {
    digitalWrite(dirPin1, HIGH);
    digitalWrite(dirPin2, LOW); 
  }
//  Serial.println("Control variables");
//  Serial.println(Input);
//  Serial.println(targetVel);
//  Serial.println(Output);
//  Serial.println(ff);

  analogWrite(enablePin, min(abs(ctrlSignal), maxPWM));
}
