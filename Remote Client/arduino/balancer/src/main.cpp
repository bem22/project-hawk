// Own libraries
#include <IMU.h>
#include <Arduino.h>
#include "TimedAction.h"
#include "Timed.h"

TimedAction time1 = TimedAction(3000, printer);

void setup() {
    Serial.begin(9600);
    IMU_init();
}

void loop() {
    time1.check();
}

