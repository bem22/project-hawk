// Own libraries
#include <IMU.h>
#include <Arduino.h>
#include "TimedAction.h"

long loop_timer;
int blink = 0;
void setup() {
    Serial.begin(9600);
    pinMode(13, OUTPUT);
    IMU_init();
    loop_timer = micros();
}

void loop() {
    read_imu_data();
    Serial.println(get_angle_pitch());

    // Synchronize the loop at 250hz
    while(micros() - loop_timer < 4000);
    loop_timer = micros();
}