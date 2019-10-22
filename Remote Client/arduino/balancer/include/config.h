// Number of reads for setting the gyro error
const int error_no_reads = 2000;

/* 0x68 is the address for register 117 in MPU6050, 
     * WHO_AM_I register, which is the identifier for MPU6050
     */
const int MPU_addr = 0x68;

// LSB/g senitivity for the accelerometer
const float accel_sensitivity_LSB = 16384.0;

// LSB/s sensitivity for gyroscope: 131.0 for 250deg/sec -- 65.5 for 500deg/sec etc
const float gyro_sensitivity_LSB = 131.0;

const float pi = 3.14159;

const double acc_LSB_PI_180 = 938734.844; // accel_sensitivity_LSB * 180 / pi


[-1115,-1114] --> [0,19]	[127,128] --> [-16,4]	[1499,1500] --> [16377,16402]	[-46,-45] --> [0,3]	[-36,-35] --> [-1,2]	[-14,-13] --> [-2,1]