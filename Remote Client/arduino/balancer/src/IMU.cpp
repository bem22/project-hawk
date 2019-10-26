#include "config.h"
#include <Wire.h>
#include <Arduino.h>
#include "IMU.h"

/* Declare variables for read and calculated
 * values from the MPU 
 */

int a_raw_x, a_raw_y, a_raw_z; 

// These are set by get_ax/get_ay/get_az
float ax, ay, az;

int temp;

int g_raw_x, g_raw_y, g_raw_z;

// These are set by get_gx/get_gy/get_gz
float gx, gy, gz;

unsigned int acc_total_vector;

float angle_pitch_acc, angle_roll_acc, angle_pitch_acc_raw, angle_roll_acc_raw;
float angle_pitch_gyro, angle_roll_gyro, angle_yaw_gyro;
float angle_pitch = 0, angle_roll = 0, angle_yaw = 0;


long int gex = 0, gey = 0, gez = 0;
int aex, aey, aez;

int init_angle_flag = 0;
// Initialize the connection with the MPU through i2c and initialize all variables
void IMU_init() {
    Wire.begin();

    // Set i2c to MPU6050 to 400kHz
    Wire.setClock(400000);

    Wire.beginTransmission(MPU_addr);
    
    // PWR_MGMT_1 register
    Wire.write(0x6B);

    // Set CLKSEL to 0 (selects the 8MHz internal oscilator)
    Wire.write(0b00000000);

    Wire.endTransmission(false);

    // 0x1B is register 27 in MPU6050 which sets the full scale range for GYRO
    Wire.write(0x1B);


    // 250 deg/sec max
    Wire.write(0b00000100);
    
    Wire.endTransmission(false);

    // 0x1C is register 27 in MPU6050 which sets the full scale range for ACCELEROMETER
    Wire.write(0x1C); 
    // +/- 4g max
    Wire.write(0b00000100);
    
    calc_acc_error();
    calc_gyro_error();
}

// This function will set the gex gey gez values from the mean of readings from the MPU
void calc_gyro_error () {
    // Read 2000 values and get the mean
    for(int i = 0; i < error_no_reads; i++) {
        read_imu_raw_data();
        gex += g_raw_x;
        gey += g_raw_y;
        gez += g_raw_z;
        delay(2);
    }

    gex /= error_no_reads;
    gey /= error_no_reads;
    gez /= error_no_reads;
}

void calc_acc_error () {

    // Read 2000 values and get the mean
    for(int i = 0; i < error_no_reads; i++) {
        read_imu_data();
        aex += a_raw_x;
        aey += a_raw_y;
        aez += a_raw_z;
        // Delay is important so we don't read the same value twice   
        delay(2);
    }

    aex = aex / error_no_reads;
    aey = aey / error_no_reads;
    aez = aez / error_no_reads - accel_sensitivity_LSB;

}

void compute_acc_angles() {
    calc_acc_pitch_angle();
    calc_acc_roll_angle();
    calc_acc_yaw_angle();
}

void compute_gyro_angles() {
    if(init_angle_flag) {
        angle_roll_gyro += (float) gx * refresh_constant;
        angle_pitch_gyro += (float) gy * refresh_constant;
        angle_yaw_gyro += (float) gz * refresh_constant;

    } else {
        angle_roll_gyro = angle_roll_acc;
        angle_pitch_gyro = angle_pitch_acc;
        init_angle_flag = 1;
    }
}

// We apply the complementary filter
void compute_final_angles() {
    angle_pitch = (float) (angle_pitch_acc * 0.02) + (angle_pitch_gyro * 0.98);
    angle_roll = (float) (angle_roll_acc * 0.02) + (angle_roll_gyro * 0.98);
}

void read_imu_data() {
    /* 
     * This function uses the raw gyro and accelerometer data.
     * Each value is filtered by the error obtained by averaging
     * the error in x reads (x = error_no_reads) 
     */

    read_imu_raw_data();

    ax = (float) (a_raw_x - aex) / accel_sensitivity_LSB;
    ay = (float) (a_raw_y - aey) / accel_sensitivity_LSB;
    az = (float) (a_raw_z - aez) / accel_sensitivity_LSB;
    gx = (float) (g_raw_x - gex) / gyro_sensitivity_LSB;
    gy = (float) (g_raw_y - gey) / gyro_sensitivity_LSB;
    gz = (float) (g_raw_z - gez) / gyro_sensitivity_LSB;

    compute_acc_angles();
    compute_gyro_angles();

    compute_final_angles();
}

void read_imu_raw_data() {
    
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

float get_acc_sensitivity() {
    return accel_sensitivity_LSB;
}

float get_gyro_sensitivity() {
    return gyro_sensitivity_LSB;
}

void calc_acc_total_vector() {
    acc_total_vector = sqrt((ax*ax) + (ay*ay) + (az*az));
}


void calc_acc_roll_angle() {
    angle_roll_acc = atan2(ay, az) * TO_DEG_CONST;
}

void calc_acc_pitch_angle() {
    angle_pitch_acc = atan2(-1 * ax, sqrt(ay * ay + az * az)) * TO_DEG_CONST;
}

//TODO: Approximate the yaw drift and fill this 
void calc_acc_yaw_angle() {

}

float get_acc_pitch_angle() {
    return angle_pitch_acc;    
}

float get_acc_roll_angle() {
    return angle_roll_acc;
}

float get_gyro_roll_angle() {
    return angle_roll_gyro;
}

float get_gyro_pitch_angle() {
    return angle_pitch_gyro;
}

float get_gyro_yaw_angle() {
    return angle_yaw_gyro;
}

float get_angle_roll() {
    return angle_roll;
}

float get_angle_pitch() {
    return angle_pitch;
}

/*
 * DEBUGGING functions
 */
