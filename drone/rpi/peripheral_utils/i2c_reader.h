
//
// Created by bem22 on 04/03/2020.
//

#ifndef SERVER_I2C_READER_H
#define SERVER_I2C_READER_H
#include <fcntl.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <linux/i2c-dev.h>


char* read_voltage();
char* get_battery_voltage();

/*=========================================================================
    CONFIG REGISTER (R/W)
    -----------------------------------------------------------------------*/
#define INA260_REG_CONFIG                      (0x00)
/*---------------------------------------------------------------------*/
#define INA260_CONFIG_RESET                    (0x8000)  // Reset Bit

#define INA260_CONFIG_AVGRANGE_MASK      	   (0x0E00)  // Averaging mode Mask
#define INA260_CONFIG_AVGRANGE_1               (0x0000)  // Average mode 1
#define INA260_CONFIG_AVGRANGE_4               (0x0200)  // Average mode 4
#define INA260_CONFIG_AVGRANGE_16              (0x0400)  // Average mode 16
#define INA260_CONFIG_AVGRANGE_64              (0x0600)  // Average mode 64
#define INA260_CONFIG_AVGRANGE_128             (0x0800)  // Average mode 128
#define INA260_CONFIG_AVGRANGE_256             (0x0A00)  // Average mode 256
#define INA260_CONFIG_AVGRANGE_512             (0x0C00)  // Average mode 512
#define INA260_CONFIG_AVGRANGE_1024            (0x0E00)  // Average mode 1024

#define INA260_CONFIG_BVOLTAGETIME_MASK        (0x01C0)  // Bus Voltage Conversion Time Mask
#define INA260_CONFIG_BVOLTAGETIME_140US       (0x0000)  // 140us
#define INA260_CONFIG_BVOLTAGETIME_1100US      (0x0100)  // 1.1ms
#define INA260_CONFIG_BVOLTAGETIME_8244US      (0x01C0)  // 8.244ms

#define INA260_CONFIG_SCURRENTTIME_MASK        (0x0038)  // Shunt Current Conversion Time Mask
#define INA260_CONFIG_SCURRENTTIME_140US       (0x0000)  // 140us
#define INA260_CONFIG_SCURRENTTIME_1100US      (0x0020)  // 1.1ms
#define INA260_CONFIG_SCURRENTTIME_8244US      (0x0038)  // 8.244ms

#define INA260_CONFIG_MODE_MASK                (0x0007)  // Operating Mode Mask
#define INA260_CONFIG_MODE_POWERDOWN           (0x0000)
#define INA260_CONFIG_MODE_SVOLT_TRIGGERED     (0x0001)
#define INA260_CONFIG_MODE_BVOLT_TRIGGERED     (0x0002)
#define INA260_CONFIG_MODE_SANDBVOLT_TRIGGERED (0x0003)
#define INA260_CONFIG_MODE_ADCOFF              (0x0004)
#define INA260_CONFIG_MODE_SVOLT_CONTINUOUS    (0x0005)
#define INA260_CONFIG_MODE_BVOLT_CONTINUOUS    (0x0006)
#define INA260_CONFIG_MODE_SANDBVOLT_CONTINUOUS (0x0007)


/*=========================================================================*/

/*=========================================================================
    SHUNT VOLTAGE REGISTER (R)
    -----------------------------------------------------------------------*/
#define INA260_REG_SHUNTVOLTAGE                (0x01)
/*=========================================================================*/

/*=========================================================================
    BUS VOLTAGE REGISTER (R)
    -----------------------------------------------------------------------*/
#define INA260_REG_BUSVOLTAGE                  (0x02)
/*=========================================================================*/

/*=========================================================================
    POWER REGISTER (R)
    -----------------------------------------------------------------------*/
#define INA260_REG_POWER                       (0x03)
/*=========================================================================*/

/*=========================================================================
    CURRENT REGISTER (R)
    -----------------------------------------------------------------------*/
#define INA260_REG_CURRENT                     (0x01)
/*=========================================================================*/

/*=========================================================================
CALIBRATION REGISTER (R/W)
    -----------------------------------------------------------------------*/
#define INA260_MASK_ENABLE                     (0x06)
/*=========================================================================*/


#endif //SERVER_I2C_READER_H
