void calc_gyro_error();
void calc_acc_error();

void read_imu_data();
void IMU_init();

float get_gx();
float get_gy();
float get_gz();

float get_ax();
float get_ay();
float get_az();

int get_g_raw_x();
int get_g_raw_y();
int get_g_raw_z();

int get_a_raw_x();
int get_a_raw_y();
int get_a_raw_z();

int get_gyro_error_x();
int get_gyro_error_y();
int get_gyro_error_z();

int get_acc_error_x();

float get_acc_sensitivity();
float get_gyro_sensitivity();
