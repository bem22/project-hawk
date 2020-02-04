#ifndef SERVER_STATE_H
#define SERVER_STATE_H

#include <stdbool.h>

#define MIN_CTRL 1000
#define MAX_CTRL 2000

typedef struct state {
    bool ARMED;
    unsigned short THROTTLE;
    unsigned short YAW;
    unsigned short PITCH;
    unsigned short ROLL;
    unsigned short AUX1;
    unsigned short AUX2;
    unsigned short AUX3;
    unsigned short AUX4;
} state;

state drone_state;

bool state_init();
bool state_check_parked();
bool state_is_armed();

#endif //SERVER_STATE_H

