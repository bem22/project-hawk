// Number of reads for setting the gyro error
const int error_no_reads = 200;

/* 0x68 is the address for register 117 in MPU6050, 
     * WHO_AM_I register, which is the identifier for MPU6050
     */
const int MPU_addr = 0x68;

// 250Hz
const float refresh_constant = 0.004;

// LSB/g senitivity for the accelerometer 
const float accel_sensitivity_LSB = 8192.0;
// LSB/s sensitivity for gyroscope: 131.0 for 250deg/sec -- 65.5 for 500deg/sec etc
const float gyro_sensitivity_LSB = 65.5;

const float pi = 3.14159;

double TO_DEG_CONST = 57.297; //(180 / 3.1415)


