// Own libraries
#include <IMU.h>
#include <Arduino.h>
#include "TimedAction.h"
#include "Timed.h"

TimedAction time1 = TimedAction(3000, printer);

long loop_timer;
int blink = 0;
void setup() {
    Serial.begin(9600);
    loop_timer = micros();
    pinMode(13, OUTPUT);
}

void loop() {


    // Synchronize the loop at 250hz
    while(micros() - loop_timer < 400);
    loop_timer = micros();
}

