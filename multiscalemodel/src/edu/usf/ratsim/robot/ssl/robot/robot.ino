// Declare motor pins
#define enablePinLF 2
#define dirPinLF 3
#define brakePinLF 4

#define enablePinLB 5
#define dirPinLB 6
#define brakePinLB 7

#define enablePinRF 19
#define dirPinRF 18
#define brakePinRF 17

#define enablePinRB 16
#define dirPinRB 15
#define brakePinRB 14

#define speedPinLF 23
#define speedPinLB 22
#define speedPinRF 21
#define speedPinRB 20

// Velocities
//  90 = Stop
// 180 = Full Forward
//   0 = Full Reverse
double targetLFvel = 0;
double targetRFvel = 0;
double targetLBvel = 0;
double targetRBvel = 0;

char resetCtrls = 0;

//***********************************************************************************
void setup()
{
  // Set controller signal pins to output  
  pinMode(enablePinLF, OUTPUT);
  pinMode(enablePinLB, OUTPUT);
  pinMode(enablePinRF, OUTPUT);
  pinMode(enablePinRB, OUTPUT);

  pinMode(dirPinLF, OUTPUT);
  pinMode(dirPinLB, OUTPUT);
  pinMode(dirPinRF, OUTPUT);
  pinMode(dirPinRB, OUTPUT);

  pinMode(brakePinLF, OUTPUT);
  pinMode(brakePinLB, OUTPUT);
  pinMode(brakePinRF, OUTPUT);
  pinMode(brakePinRB, OUTPUT);

  // Enable Controllers
  digitalWrite(enablePinLF, HIGH);
  digitalWrite(enablePinLB, HIGH);
  digitalWrite(enablePinRF, HIGH);
  digitalWrite(enablePinRB, HIGH);

  // Enable Brakes
  digitalWrite(brakePinLF, HIGH);
  digitalWrite(brakePinLB, HIGH);
  digitalWrite(brakePinRF, HIGH);
  digitalWrite(brakePinRB, HIGH);

  // Set Speed to Zero
  analogWrite(speedPinLF, 30);
  analogWrite(speedPinLB, 30);
  analogWrite(speedPinRF, 30);
  analogWrite(speedPinRB, 30);

  // Start Serial Port
  Serial.begin(9600);
}

void loop()
{
  runComm();
}
//***********************************************************************************
// Write Speeds to Motors
void setSpeeds()
{
  if(resetCtrls == 'c')
    resetAllESC();
    
  // Bounds for PWM duty cycles, about 10% and 90% of 0 and 255
  static int lowPWM = 35, highPWM = 225;

//  targetLFvel = targetLBvel = targetRFvel = targetRBvel = 20;

  // Output speeds
  if (targetLFvel > 0 )
  {
    analogWrite(speedPinLF, map(abs(targetLFvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinLF, HIGH);
    digitalWrite(brakePinLF, LOW);
  }
  else if (targetLFvel < 0 )
  {
    analogWrite(speedPinLF, map(abs(targetLFvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinLF, LOW);
    digitalWrite(brakePinLF, LOW);
  }
  else
    digitalWrite(brakePinLF, HIGH);

  if (targetLBvel > 0 )
  {
    analogWrite(speedPinLB, map(abs(targetLBvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinLB, HIGH);
    digitalWrite(brakePinLB, LOW);
  }
  else if (targetLBvel < 0)
  {
    analogWrite(speedPinLB, map(abs(targetLBvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinLB, LOW);
    digitalWrite(brakePinLB, LOW);
  }
  else
    digitalWrite(brakePinLB, HIGH);

  if (targetRFvel > 0 )
  {
    analogWrite(speedPinRF, map(abs(targetRFvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinRF, LOW);
    digitalWrite(brakePinRF, LOW);
  }
  else if (targetRFvel < 0 )
  {
    analogWrite(speedPinRF, map(abs(targetRFvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinRF, HIGH);
    digitalWrite(brakePinRF, LOW);
  }
  else
    digitalWrite(brakePinRF, HIGH);

  if (targetRBvel > 0 )
  {
    analogWrite(speedPinRB, map(abs(targetRBvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinRB, LOW);
    digitalWrite(brakePinRB, LOW);
  }
  else if (targetRBvel < 0 )
  {
    analogWrite(speedPinRB, map(abs(targetRBvel), 0, 100, lowPWM, highPWM));
    digitalWrite(dirPinRB, HIGH);
    digitalWrite(brakePinRB, LOW);
  }
  else
    digitalWrite(brakePinRB, HIGH);
}
//***********************************************************************************

// Reset Controllers *****************************************************************
void resetAllESC()
{
  // First stop all robots
  digitalWrite(brakePinLF, HIGH);
  digitalWrite(brakePinLB, HIGH);
  digitalWrite(brakePinRF, HIGH);
  digitalWrite(brakePinRB, HIGH);

  delay(200);

  // Disable Controllers
  digitalWrite(enablePinLF, LOW);
  digitalWrite(enablePinLB, LOW);
  digitalWrite(enablePinRF, LOW);
  digitalWrite(enablePinRB, LOW);

  delay(200);
  
  // Enable Controllers
  digitalWrite(enablePinLF, HIGH);
  digitalWrite(enablePinLB, HIGH);
  digitalWrite(enablePinRF, HIGH);
  digitalWrite(enablePinRB, HIGH);       
}
//***********************************************************************************

char state = 't';

double targetLFvelSerial = 0;
double targetRFvelSerial = 0;
double targetLBvelSerial = 0;
double targetRBvelSerial = 0;

int resetCtrlsSerial = 0;

void runComm()
{
    switch (state)
    {
      case 't':                          // Wait for Start Marker
        if (char(Serial.read()) == char(250))
        {
          state = 'b';
        }
        break;
      case 'b':                         // Read Packet
        if (Serial.available() >= 6)
        {
          //Serial.println("Reading Commands");
          targetLFvelSerial = ((int)Serial.read() - 100);
          targetLBvelSerial = ((int)Serial.read() - 100);
          targetRFvelSerial = (int)Serial.read() - 100;
          targetRBvelSerial = ((int)Serial.read() - 100);
          resetCtrlsSerial = (int)Serial.read();

          if (char(Serial.read()) == char(255))          // Check for End Marker
          {
            state = 't';
            targetLFvel = targetLFvelSerial;
            targetLBvel = targetLBvelSerial;
            targetRFvel = targetRFvelSerial;
            targetRBvel = targetRBvelSerial;
            resetCtrls = resetCtrlsSerial;
            setSpeeds();
 
          }
          else
          {
            state = 't';
          }
          break;
        }
        state = 't';
    }
}






