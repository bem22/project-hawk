// Number of reads for setting the gyro error
const int gyro_error_no_reads = 2000;

/* 0x68 is the address for register 117 in MPU6050, 
     * WHO_AM_I register, which is the identifier for MPU6050
     */
const int MPU_addr = 0x68;

// LSB/g senitivity for the accelerometer
const float accel_sensitivity_LSB = 16384.0;

// LSB/s sensitivity for gyroscope: 131.0 for 250deg/sec -- 65.5 for 500deg/sec etc
const float gyro_sensitivity_LSB = 131.0;
