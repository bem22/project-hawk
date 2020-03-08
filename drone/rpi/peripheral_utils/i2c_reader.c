//
// Created by bem22 on 04/03/2020.
//

#include "i2c_reader.h"
#define BYTE_TO_BINARY_PATTERN "%c%c%c%c%c%c%c%c \n"
#define BYTE_TO_BINARY(byte)  \
  (byte & 0x80 ? '1' : '0'), \
  (byte & 0x40 ? '1' : '0'), \
  (byte & 0x20 ? '1' : '0'), \
  (byte & 0x10 ? '1' : '0'), \
  (byte & 0x08 ? '1' : '0'), \
  (byte & 0x04 ? '1' : '0'), \
  (byte & 0x02 ? '1' : '0'), \
  (byte & 0x01 ? '1' : '0')
int file_i2c;
int length;
unsigned char buffer[60] = {0};

//------ OPEN the i2c BUS -----
char *filename = (char*)"/dev/i2c-1";



char* read_voltage() {
    if((file_i2c = open(filename, O_RDWR)) < 0) {
        //ERROR HANDLING: you can check errno to see what went wrong
        printf("Failed to open the i2c bus");
        return "";
    }

    int addr = 0x40; //<<<The i2c address of the current sensor

    if (ioctl(file_i2c, I2C_SLAVE, addr) < 0) {
        printf("Failed to acquire bus access");
        return "";
    }

    buffer[0] = 0;

    if ((write(file_i2c, buffer, 1)) !=1 ) {
        printf("Error writing to i2c slave\n");
    }

    //----- READ BYTES -----
    length = 4;			//<<< Number of bytes to read
    if (read(file_i2c, buffer, length) != length)		//read() returns the number of bytes actually read, if it doesn't match then an error occurred (e.g. no response from the device)
    {
        //ERROR HANDLING: i2c transaction failed
        printf("Failed to read from the i2c bus.\n");
    } else {
        for(int i=0; i<=10; i++) {
            printf(BYTE_TO_BINARY_PATTERN, BYTE_TO_BINARY(buffer[i]));
        }
    }

    return "12.3";
}

char* get_battery_voltage() {
    read_voltage();
    return "9.3";
}