#include "IMU.h"
#include <Arduino.h>
#include "config.h"

void printer() {
    Serial.flush();
    read_imu_data();
    Serial.println(get_ax()-get_acc_error_x());
    Serial.println(get_ay());
    Serial.println(get_az());
}