#include "config.h"
#include <Wire.h>
#include <Arduino.h>
#include "IMU.h"

/* Declare variables for read and calculated
 * values from the MPU 
 */

int16_t a_raw_x, a_raw_y, a_raw_z; 

// These are set by get_ax/get_ay/get_az
float ax, ay, az;

int16_t temp;

int16_t g_raw_x, g_raw_y, g_raw_z;

// These are set by get_gx/get_gy/get_gz
float gx, gy, gz;

// gyro error - these are raw values
float gex, gey, gez;

// acc error on X axis - raw value
float aex;


// Initialize the connection with the MPU through i2c and initialize all variables
void IMU_init() {
    Wire.begin();

    // Set i2c to MPU6050 to 400kHz
    Wire.setClock(400000UL);

    Wire.beginTransmission(MPU_addr);
    
    // PWR_MGMT_1 register
    Wire.write(0x6B);

    // Set CLKSEL to 0 (selects the 8MHz internal oscilator)
    Wire.write(0b00000000);

    Wire.endTransmission(false);

    // 0x1B is register 27 in MPU6050 which sets the full scale range for GYRO
    Wire.write(0x1B);
    // 250 deg/sec max
    Wire.write(0b00000000);
    
    Wire.endTransmission(false);

    // 0x1C is register 27 in MPU6050 which sets the full scale range for ACCELEROMETER
    Wire.write(0x1C); 
    // +/- 2g max
    Wire.write(0b00000000);

    calc_gyro_error();
    calc_acc_error();
}

// This function will set the gex gey gez values from the mean of readings from the MPU
void calc_gyro_error () {
    // Set error to 0
    gex = gey = gez = 0;

    // Read 2000 values and get the mean
    for(int i = 0; i < error_no_reads; i++) {
        read_imu_data();
        gex += g_raw_x;
        gey += g_raw_y;
        gez += g_raw_z;
    }

    gex /= error_no_reads;
    gey /= error_no_reads;
    gez /= error_no_reads;

}

void calc_acc_error () {
    // Set error to 0
    aex = 0;

    // Read 2000 values and get the mean
    for(int i = 0; i < error_no_reads; i++) {
        read_imu_data();
        aex += a_raw_x;

        // Delay is important so we don't read the same value twice   
        delay(2);
    }

    aex = aex / error_no_reads;
}

void read_imu_data() {
    
    Wire.beginTransmission(MPU_addr);
    
    // Start with the ACCEL_XOUT_H (addr 59)
    Wire.write(0x3B);

    // Do not release transmission and set restart message
    Wire.endTransmission(false);

    // Request data from register 59 up to register 73 (0x3B - 0x48)
    Wire.requestFrom(MPU_addr, 14, true);


    /* The  code `read1 << 8 | read2` 
     * reads one register (high), shifts it 
     * by 8 bits and then sums it with read2
     */
    a_raw_x = Wire.read()<< 8 | Wire.read(); // 0x3B and 0x3C
    a_raw_y = Wire.read()<< 8 | Wire.read(); // 0x3D and 0x3E
    a_raw_z = Wire.read()<< 8 | Wire.read(); // 0x3F and 0x40
    temp = Wire.read()<< 8 | Wire.read(); // 0x41 and 0x42
    g_raw_x = Wire.read()<< 8 | Wire.read(); // 0x43 and 0x44
    g_raw_y = Wire.read()<< 8 | Wire.read(); // 0x45 and 0x46
    g_raw_z = Wire.read()<< 8 | Wire.read(); // 0x47 and 0x48
    
    Wire.endTransmission(true);
}

float get_gx() {
    gx = (float) (g_raw_x - gex) / gyro_sensitivity_LSB;
    return gx;
}

float get_gy() {
    gy = (float) (g_raw_y - gey) / gyro_sensitivity_LSB;
    return gy;
}

float get_gz() {
    gz = (float) (g_raw_z - gez) / gyro_sensitivity_LSB;
    return gz;
}

float get_ax() {
    ax = (float) a_raw_x / acc_LSB_PI_180;
    return ax;
}

float get_ay() {
    ay = (float) a_raw_y / acc_LSB_PI_180;
    return ay;
}

float get_az() {
    az = (float) a_raw_z / acc_LSB_PI_180;
    return az; 
}

int get_g_raw_x() {
    read_imu_data();
    return g_raw_x;
}

int get_g_raw_y() {
    return g_raw_y;
}

int get_g_raw_z() {
    return g_raw_z;
}

int get_a_raw_x() {
    return a_raw_x;
}

int get_a_raw_y() {
    return a_raw_y;
}

int get_a_raw_z() {
    return a_raw_z;
}

int get_gyro_error_x() {
    return gex;
}
int get_gyro_error_y() {
    return gey;
}
int get_gyro_error_z() {
    return gez;
}

int get_acc_error_x() {
    return aex;
}

float get_acc_sensitivity() {
    return accel_sensitivity_LSB;
}

float get_gyro_sensitivity() {
    return gyro_sensitivity_LSB;
}