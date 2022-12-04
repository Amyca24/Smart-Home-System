#include <SoftwareSerial.h>
#include <Servo.h>
SoftwareSerial Blue(2, 3);
#include <SimpleDHT.h>
int pinDHT11 = 6;
SimpleDHT11 dht11;

String data;
Servo servo1;
Servo servo2;
Servo servo3;

long int poser = 0;             // Initial Servo1 position
int value;
long int dorpos = 0;            // Initial Servo2 position

int ledPin = 10;                // LED 
int pirPin = 4;                 // PIR Out pin 
int pirStat = 0;                // PIR status


void setup() {
pinMode(13, OUTPUT);  
digitalWrite(13, HIGH);
pinMode(11, OUTPUT);  
digitalWrite(11, HIGH);
pinMode(ledPin, OUTPUT);     
pinMode(pirPin, INPUT);     
Serial.begin(9600);
Blue.begin(9600);
servo1.attach(7);
servo2.attach(5);
servo3.attach(8);
} 
void loop() {

  //while (Blue.available()==0) ;

  {
  byte temperature = 0;
  byte humidity = 0;
if (dht11.read(pinDHT11, &temperature, &humidity, NULL))
{
return;
}

//Temperature sensor
  Blue.print((int)temperature);
  Blue.print("C");
  Blue.print(" & ");
  Blue.print((int)humidity);
  Blue.print("%");
  
}

  
 if(Blue.available() > 0)
  {

// 1st servo (Door)

    data = Blue.readString();
    Serial.println(data);
    if(data == "90" || data == "0")
    { 
      poser = data.toInt();
      servo1.write(poser); 
    }  

//2nd servo (Livingroom Window)

    if(data == "1")
    { 
      dorpos = data.toInt()*90;
      servo2.write(dorpos); 
    }
    if(data == "2")
    {
      dorpos = data.toInt()*0;
      servo2.write(dorpos);
    }
    
  delay(1000);

//3rd Servo (Bedroom Window)

if(data == "7")
    { 
      dorpos = data.toInt()*90;
      servo3.write(dorpos); 
    }
    if(data == "8")
    {
      dorpos = data.toInt()*0;
      servo3.write(dorpos);
    }
    
  delay(1000);

// 1st LED (Livingroom)

 if(data == "49")
       {
         digitalWrite(13, HIGH);
         Serial.println("LED ON");
        }  
  if(data == "48")
  {
   digitalWrite(13, LOW);
   Serial.println("LED OFF");
   
    }     

     delay(1000);

//2nd LED (Bedroom)
  if(data == "5")
  {
   digitalWrite(11, HIGH);
   Serial.println("LED ON");
  }  
  if(data == "6")
  {
   digitalWrite(11, LOW);
   Serial.println("LED OFF");
  }

//Movement Sensor(PIR sensor)

  }
 pirStat = digitalRead(pirPin); 
 if (pirStat == HIGH) {            // if motion detected
   digitalWrite(ledPin, HIGH);  // turn LED ON
   Serial.println("Hey I got you!!!");
 } 
 else {
   digitalWrite(ledPin, LOW); // turn LED OFF if we have no motion
 } 
}
