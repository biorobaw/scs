#include <math.h>

// Mid robot sensors
const int SFSensor = A2;  
const int SLSensor = A1;  
const int SRSensor = A3; 
// Lower sensors  
const int SLRSensor = A0;  
const int SLLSensor = A4;  

int SFValue = 0;        // value read from the pot
int SLValue = 0;        // value read from the pot
int SRValue = 0;        // value read from the pot

void setup() {
  // initialize serial communications at 9600 bps:
  Serial.begin(9600);  
}

void loop() {
  float m;
  float valor;
  
  m=leeSensor(SLSensor);
  valor=convierte(m);
  Serial.print(valor);
  Serial.print(" ");
  m=leeSensor(SFSensor);
  valor=convierte(m);
  Serial.print(valor);
  Serial.print(" ");
  m=leeSensor(SRSensor);
  valor=convierte(m);
  Serial.print(valor);
  Serial.print(" ");
  m=leeSensor(SLLSensor);
  valor=convierte(m);
  Serial.print(valor);
  Serial.print(" ");
  m=leeSensor(SLRSensor);
  valor=convierte(m);
  Serial.print(valor);
  Serial.print(" ");
  Serial.print("\n");
}

int leeSensor(int sensor) {
  float n[10];
  for (int i=0; i<10; i++) {
         n[i]=analogRead(sensor);
         delay(2);       
  }
  int min=1000;
  for (int i=0; i<10; i++) {
      for (int j=1;j<10;j++){
        if (n[j]<n[j-1]){
               min=n[j-1];
               n[j-1]=n[j];
               n[j]=min;
         }
       }
    }
  
  return n[5];
}

float convierte(float medida) {
  float volt = (medida/1023.0f) * 5;
  if (volt < .3)
    return 400;

  // Inverse of distance using eq
  float distinv = 0.08 * volt - 0.002;
  float dist = 1 / distinv - 0.42;
  // return milimeters
  return dist * 10;
}

