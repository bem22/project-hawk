#include "IMU.h"
#include <Arduino.h>

void printer() {
    Serial.flush();
    read_imu_data();
    Serial.println(getX_gyro() - getX_gyro_error());
    Serial.println(getY_gyro() - getY_gyro_error());
    Serial.println(getZ_gyro() - getZ_gyro_error());
}