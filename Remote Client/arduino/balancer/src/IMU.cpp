#include "config.h"
#include <Wire.h>
#include "IMU.h"

int16_t ax, ay, az; 
int16_t temp;
int16_t gx, gy, gz;

// gyro error
float gex, gey, gez;


// Initialize the connection with the MPU through i2c and initialize all variables
void IMU_init() {
    Wire.begin();

    // Set i2c to MPU6050 to 400kHz
    Wire.setClock(400000UL);

    Wire.beginTransmission(MPU_addr);
    
    // PWR_MGMT_1 register
    Wire.write(0x6B);

    // Set CLKSEL to 0 (selects the 8MHz internal oscilator)
    Wire.write(0);

    Wire.endTransmission(true);

    calc_gyro_error();
}

// This function will set the gex gey gez values from the mean of readings from the MPU
void calc_gyro_error () {
    // Set error to 0
    gex = gey = gez = 0;

    // Read 2000 values and get the mean
    for(int i = 0; i < gyro_error_no_reads; i++) {
        read_imu_data();
        gex += gx;
        gey += gy;
        gez += gz;
    }

    gex /= gyro_error_no_reads;
    gey /= gyro_error_no_reads;
    gez /= gyro_error_no_reads;

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
    ax = Wire.read()<< 8 | Wire.read(); // 0x3B and 0x3C
    ay = Wire.read()<< 8 | Wire.read(); // 0x3D and 0x3E
    az = Wire.read()<< 8 | Wire.read(); // 0x3F and 0x40
    temp = Wire.read()<< 8 | Wire.read(); // 0x41 and 0x42
    gx = Wire.read()<< 8 | Wire.read(); // 0x43 and 0x44
    gy = Wire.read()<< 8 | Wire.read(); // 0x45 and 0x46
    gz = Wire.read()<< 8 | Wire.read(); // 0x47 and 0x48
    
    Wire.endTransmission(true);
}

int getX_gyro() {
    read_imu_data();
    return gx;
}

int getY_gyro() {
    return gy;
}

int getZ_gyro() {
    return gz;
}

int get_temp() {
    return temp;
}

int getX_acc() {
    return ax;
}

int getY_acc() {
    return ay;
}

int getZ_acc() {
    return az;
}

int getX_gyro_error() {
    return gex;
}
int getY_gyro_error() {
    return gey;
}
int getZ_gyro_error() {
    return gez;
}
