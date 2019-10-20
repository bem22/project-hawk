// Number of reads for setting the gyro error
const int gyro_error_no_reads = 2000;

// As per MPU6050 datasheet, the output for gyro is 65.5 deg/s (max 500 deg/s)
const float gyro_sensitivity = 65.5;

// As per MPU6050 datasheet, the output for accelerometer is
const float accel_sensitivity = 0;

/* 0x68 is the address for register 117 in MPU6050, 
     * WHO_AM_I register, which is the identifier for MPU6050
     */
const int MPU_addr = 0x68;