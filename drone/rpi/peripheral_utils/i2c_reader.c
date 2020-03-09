//
// Created by bem22 on 04/03/2020.
//

char battery_voltage[10];
char* answer[10] = {0};
#include "i2c_reader.h"
#include "../drone_utils/state.h"

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

char settings = 0b1;

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

    // Set up the Calibration Register

    buffer[0] = 5;
    buffer[1] = 0b00100000;
    buffer[2] = 0b01111000;

    // Write to th device
    if ((write(file_i2c, buffer, 3)) !=3) {
        printf("Error writing to i2c slavez\n");
    }


    // Set up the configuration register
    buffer[0] = 0;
    buffer[1] = 0b00000001;
    buffer[2] = 0b10011111;

    // Write to the device
    if ((write(file_i2c, buffer, 3)) !=3) {
        printf("Error writing to i2c slavez\n");
    }

    int a = 1;
    if(a) {
        // Read the configuration register (0)

        buffer[0] = 0;
        buffer[1] = 0;
        buffer[2] = 0;
        if ((write(file_i2c, buffer, 1)) != 1) {
            printf(":(\n");
        }

        //----- READ BYTES -----
        length = 2;            //<<< Number of bytes to read
        if (read(file_i2c, buffer, length) !=
            length)        //read() returns the number of bytes actually read, if it doesn't match then an error occurred (e.g. no response from the device)
        {
            //ERROR HANDLING: i2c transaction failed
            printf("Failed to read from the i2c bus.\n");
        } else {
            printf("Configuration Register \n");
            for (int i = 0; i < length; i++) {
                printf(BYTE_TO_BINARY_PATTERN, BYTE_TO_BINARY(buffer[i]));
            }
            printf("\n\n");
        }
    }


    if(a) {
        // Read the Calibration Register (5)
        buffer[0] = 5;
        buffer[1] = 0;
        buffer[2] = 0;

        if ((write(file_i2c, buffer, 1)) != 1) {
            printf(":(\n");
        }

        //----- READ BYTES -----
        length = 2;			//<<< Number of bytes to read
        if (read(file_i2c, buffer, length) != length)		//read() returns the number of bytes actually read, if it doesn't match then an error occurred (e.g. no response from the device)
        {
            //ERROR HANDLING: i2c transaction failed
            printf("Failed to read from the i2c bus.\n");
        } else {
            printf("Calibration Register \n");
            for(int i=0; i<length; i++) {
                printf(BYTE_TO_BINARY_PATTERN, BYTE_TO_BINARY(buffer[i]));
            }
        }
    }


    // Read Bus Voltage Register (2)

    buffer[0] = 2;
    buffer[1] = 0;
    buffer[2] = 0;

    if ((write(file_i2c, buffer, 1)) != 1) {
        printf(":(\n");
    }

    //----- READ BYTES -----
    length = 2;			//<<< Number of bytes to read
    if (read(file_i2c, buffer, length) != length)		//read() returns the number of bytes actually read, if it doesn't match then an error occurred (e.g. no response from the device)
    {
        //ERROR HANDLING: i2c transaction failed
        printf("Failed to read from the i2c bus.\n");
    }

    int rawReading = buffer[0];
    rawReading = rawReading << 5;

    buffer[1] = buffer[1] >> 3;

    rawReading += buffer[1];

    printf("%f", rawReading * 0.00399);



    snprintf(answer, 8, "%f", rawReading * 0.00399);

    return answer;
}

char* get_battery_voltage() {
    snprintf(battery_voltage, 8, "%s", read_voltage());

    return battery_voltage;
}