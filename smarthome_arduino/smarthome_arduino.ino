#include <SoftwareSerial.h>// import the serial library

SoftwareSerial Genotronex(10, 11); // RX, TX
int ledpin=13; // led on D13 will show blink on / off
int BluetoothData; // the data given from Computer
int tempData = 0;
int previousTempData = 0;
int tempPin = 1;
int delta = 0;
int deltaValue = 1;

void setup() {
  // put your setup code here, to run once:
  Genotronex.begin(9600);
  pinMode(ledpin,OUTPUT);
  Serial.begin(9600);
}

void loop() {

  
  int reading = analogRead(tempPin);
  float tempC = reading / 9.31;
  tempData = (int)tempC;
  delta = abs(previousTempData - tempData);
  
    if(delta >= deltaValue){
    Serial.println(tempData);
    Genotronex.write(tempData);

    }
  
    previousTempData = tempData;
    delay(1000);
  
  // put your main code here, to run repeatedly:
    while(Genotronex.available()){
    
    int c=Serial.read();
   Genotronex.println(c);
    
    BluetoothData=Genotronex.read();
   
   if(BluetoothData=='1'){   // if number 1 pressed ....
   digitalWrite(ledpin,1);
   Genotronex.println("LED  On D13 ON ! ");
   }
  if (BluetoothData=='0'){// if number 0 pressed ....
  digitalWrite(ledpin,0);
   Genotronex.println("LED  On D13 Off ! ");
  }
}
delay(100);// prepare for next data ...
}

