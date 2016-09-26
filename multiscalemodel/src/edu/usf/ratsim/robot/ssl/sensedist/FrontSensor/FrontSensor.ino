#include <math.h>

const int SFSensor = A2;  
const int SLSensor = A1;  
const int SRSensor = A3;    

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
  int sum=0;
  for (int j=2; j<8; j++){
    sum=sum+n[j];
  }
  sum=sum/6;
  
  return n[5];
}

float convierte(float medida) {

  float sens[9]={386, 293, 238, 195, 165, 145, 127, 115, 103};
  int i=1;
  float conv=0;
  for (int i=0; i<8; i++ ){
    if ((medida<=sens[i])&&(medida>sens[i+1])){
    //conv=(i+3)-(medida-sens[i+1])/(sens[i]-sens[i+1]);
    conv=(i+3)-(medida-sens[i+1])/(sens[i]-sens[i+1]);
    }
  }

  if (medida<104){
    conv=10;
  }

  if (medida>385){
    conv=2;
  }
  
  if (medida==195){
    conv=5;
  }
  
  return conv;
}

